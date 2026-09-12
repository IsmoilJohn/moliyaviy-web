#!/usr/bin/env python3
"""
One-off importer: Telegram Desktop HTML chat export -> moliyaviy-web transactions.

Run this on the machine where the export lives and where the moliyaviy-web
backend is reachable (default http://localhost:8080).

Dependencies:
    pip install beautifulsoup4 requests

Step 1 - PREVIEW (no writes, just parses and prints what it found):
    python import_telegram_transactions.py "C:\\Users\\user\\Downloads\\Telegram Desktop\\ChatExport_2026-09-12"

Check the printed rows and the INCOME/EXPENSE totals carefully. Nothing is
sent to the API until you pass --import.

Step 2 - IMPORT (only after you're happy with the preview):
    python import_telegram_transactions.py "C:\\Users\\user\\Downloads\\Telegram Desktop\\ChatExport_2026-09-12" ^
        --import --email you@example.com --password "your-password"

Notes / judgment calls made while writing this (review these against your
data in the preview step):
  - An amount is only recognized when it has thousands-grouping dots, e.g.
    "50.000" or "5.650.300" (matches the two examples you gave). A bare
    "500" with no dot is treated as "no clear amount" and the message is
    skipped, per your instruction #3 - safer than guessing on ambiguous
    small numbers that might just be a quantity, not money.
  - Dates like "29.09.2023" never match the amount pattern because the
    groups after the first dot are only 2 digits, not 3.
  - Every category is "Прочее" (Other), picked separately for INCOME and
    EXPENSE since the schema has one "Прочее" per type.
  - Import is not idempotent: running --import twice creates duplicate
    transactions. This is a one-off migration script, not something meant
    to be re-run.
"""

import argparse
import re
import sys
from dataclasses import dataclass
from datetime import date, datetime
from pathlib import Path

try:
    from bs4 import BeautifulSoup
except ImportError:
    sys.exit("Missing dependency. Run: pip install beautifulsoup4 requests")

try:
    import requests
except ImportError:
    sys.exit("Missing dependency. Run: pip install beautifulsoup4 requests")

EXPORT_FILES = ["messages.html", "messages2.html", "messages3.html", "messages4.html"]

INCOME_KEYWORDS = [
    "начислен", "начислили", "начислена",
    "зарплата", "зарплату", "зп",
    "премия", "премию",
    "получил", "получила", "получено",
    "поступил", "поступила", "поступление",
    "зачислен", "зачислили",
    "доход",
]

SKIP_PATTERNS = [
    re.compile(r"баланс.*составляет", re.IGNORECASE),
    re.compile(r"текущий баланс", re.IGNORECASE),
]

# Requires at least one thousands-grouping dot (e.g. 50.000, 5.650.300).
# Deliberately does NOT match plain small numbers like "500" - see docstring.
# The lookbehind/lookahead guards stop it from matching a sub-run embedded
# inside a longer digit-dot sequence, e.g. picking "9.202" out of a date
# like "29.09.2023" - it only matches a maximal, standalone run.
AMOUNT_RE = re.compile(r"(?<!\d)(?<!\.)\d{1,3}(?:\.\d{3})+(?!\d)")

DEFAULT_CATEGORY_NAME = "Прочее"


@dataclass
class ParsedTransaction:
    transaction_date: date
    amount: int
    type: str
    comment: str
    raw_text: str
    source_file: str


def classify_type(text: str) -> str:
    lower = text.lower()
    return "INCOME" if any(kw in lower for kw in INCOME_KEYWORDS) else "EXPENSE"


def should_skip(text: str) -> bool:
    return any(p.search(text) for p in SKIP_PATTERNS)


def build_comment(text: str, amount_span: tuple[int, int]) -> str:
    start, end = amount_span
    stripped = text[:start] + text[end:]
    stripped = re.sub(r"\bсум\b", "", stripped, flags=re.IGNORECASE)
    stripped = re.sub(r"\s+", " ", stripped).strip(" ,.-:")
    return stripped[:500]


def parse_title_datetime(title: str) -> datetime | None:
    # e.g. "29.09.2023 11:56:20 UTC+05:00" -> drop the "UTC+05:00" part
    head = title.split(" UTC")[0].strip()
    try:
        return datetime.strptime(head, "%d.%m.%Y %H:%M:%S")
    except ValueError:
        return None


def parse_file(path: Path) -> list[ParsedTransaction]:
    soup = BeautifulSoup(path.read_text(encoding="utf-8"), "html.parser")
    results = []

    for msg in soup.select("div.message"):
        classes = msg.get("class", [])
        if "service" in classes:
            continue

        text_div = msg.select_one(".text")
        date_div = msg.select_one(".date.details")
        if text_div is None or date_div is None:
            continue

        text = text_div.get_text(separator=" ", strip=True)
        if not text or should_skip(text):
            continue

        match = AMOUNT_RE.search(text)
        if not match:
            continue

        title = date_div.get("title", "")
        dt = parse_title_datetime(title)
        if dt is None:
            continue

        amount = int(match.group(0).replace(".", ""))
        comment = build_comment(text, match.span())
        tx_type = classify_type(text)

        results.append(ParsedTransaction(
            transaction_date=dt.date(),
            amount=amount,
            type=tx_type,
            comment=comment,
            raw_text=text,
            source_file=path.name,
        ))

    return results


def parse_all(export_dir: Path) -> list[ParsedTransaction]:
    all_transactions = []
    for filename in EXPORT_FILES:
        file_path = export_dir / filename
        if not file_path.exists():
            print(f"WARNING: {file_path} not found, skipping", file=sys.stderr)
            continue
        parsed = parse_file(file_path)
        print(f"{filename}: parsed {len(parsed)} transactions")
        all_transactions.extend(parsed)

    all_transactions.sort(key=lambda t: (t.transaction_date, t.raw_text))
    return all_transactions


def print_preview(transactions: list[ParsedTransaction], limit: int = 30) -> None:
    print()
    print(f"=== Preview: first {min(limit, len(transactions))} of {len(transactions)} parsed transactions ===")
    print(f"{'Date':<12} {'Amount':>14} {'Type':<8} Comment")
    for t in transactions[:limit]:
        print(f"{t.transaction_date} {t.amount:>14,} {t.type:<8} {t.comment[:90]}".replace(",", " "))

    income_total = sum(t.amount for t in transactions if t.type == "INCOME")
    expense_total = sum(t.amount for t in transactions if t.type == "EXPENSE")
    income_count = sum(1 for t in transactions if t.type == "INCOME")
    expense_count = sum(1 for t in transactions if t.type == "EXPENSE")

    print()
    print("=== Totals across ALL parsed transactions (not just the preview above) ===")
    print(f"INCOME:  {income_count:>5} transactions, total {income_total:,}".replace(",", " "))
    print(f"EXPENSE: {expense_count:>5} transactions, total {expense_total:,}".replace(",", " "))
    print()
    print("Nothing has been imported yet. Re-run with --import once this looks right.")


def login(api_url: str, email: str, password: str) -> str:
    resp = requests.post(f"{api_url}/api/auth/login", json={"email": email, "password": password}, timeout=10)
    resp.raise_for_status()
    return resp.json()["token"]


def fetch_default_category_ids(api_url: str, token: str) -> dict[str, str]:
    resp = requests.get(f"{api_url}/api/categories", headers={"Authorization": f"Bearer {token}"}, timeout=10)
    resp.raise_for_status()
    categories = resp.json()

    ids_by_type = {
        c["type"]: c["id"]
        for c in categories
        if c["name"] == DEFAULT_CATEGORY_NAME
    }
    missing = {"INCOME", "EXPENSE"} - ids_by_type.keys()
    if missing:
        sys.exit(f"Could not find a '{DEFAULT_CATEGORY_NAME}' category for: {', '.join(sorted(missing))}. "
                  f"Did you delete the default categories?")
    return ids_by_type


def import_transactions(api_url: str, token: str, category_ids: dict[str, str],
                         transactions: list[ParsedTransaction]) -> None:
    headers = {"Authorization": f"Bearer {token}"}
    created, failed = 0, []

    for i, t in enumerate(transactions, start=1):
        payload = {
            "categoryId": category_ids[t.type],
            "type": t.type,
            "amount": t.amount,
            "transactionDate": t.transaction_date.isoformat(),
            "comment": t.comment or None,
        }
        resp = requests.post(f"{api_url}/api/transactions", json=payload, headers=headers, timeout=10)
        if resp.status_code == 201:
            created += 1
        else:
            failed.append((t, resp.status_code, resp.text))

        if i % 50 == 0 or i == len(transactions):
            print(f"  ... {i}/{len(transactions)} processed ({created} created, {len(failed)} failed)")

    print()
    print(f"Done. Created {created}/{len(transactions)} transactions.")
    if failed:
        print(f"{len(failed)} FAILED:")
        for t, status, body in failed[:20]:
            print(f"  [{status}] {t.transaction_date} {t.amount} {t.type} '{t.comment[:60]}' -> {body[:200]}")
        if len(failed) > 20:
            print(f"  ... and {len(failed) - 20} more")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("export_dir", type=Path, help="Path to the ChatExport_* folder")
    parser.add_argument("--api-url", default="http://localhost:8080", help="Backend base URL")
    parser.add_argument("--email", help="Account email (required with --import)")
    parser.add_argument("--password", help="Account password (required with --import)")
    parser.add_argument("--import", dest="do_import", action="store_true",
                         help="Actually POST the transactions. Without this flag, only a preview is printed.")
    parser.add_argument("--preview-limit", type=int, default=30, help="How many rows to print in preview mode")
    args = parser.parse_args()

    if not args.export_dir.is_dir():
        sys.exit(f"Not a directory: {args.export_dir}")

    transactions = parse_all(args.export_dir)

    if not args.do_import:
        print_preview(transactions, limit=args.preview_limit)
        return

    if not args.email or not args.password:
        sys.exit("--import requires --email and --password")

    print(f"Importing {len(transactions)} transactions to {args.api_url} ...")
    token = login(args.api_url, args.email, args.password)
    category_ids = fetch_default_category_ids(args.api_url, token)
    import_transactions(args.api_url, token, category_ids, transactions)


if __name__ == "__main__":
    main()
