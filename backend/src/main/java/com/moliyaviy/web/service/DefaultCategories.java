package com.moliyaviy.web.service;

import com.moliyaviy.web.entity.TransactionType;
import java.util.List;

final class DefaultCategories {

    static final List<Seed> SEEDS = List.of(
            new Seed("Еда", TransactionType.EXPENSE, "#f97316"),
            new Seed("Транспорт", TransactionType.EXPENSE, "#3b82f6"),
            new Seed("Коммуналка", TransactionType.EXPENSE, "#0ea5e9"),
            new Seed("Развлечения", TransactionType.EXPENSE, "#a855f7"),
            new Seed("Здоровье", TransactionType.EXPENSE, "#ef4444"),
            new Seed("Прочее", TransactionType.EXPENSE, "#6b7280"),
            new Seed("Зарплата", TransactionType.INCOME, "#22c55e"),
            new Seed("Подработка", TransactionType.INCOME, "#14b8a6"),
            new Seed("Прочее", TransactionType.INCOME, "#6b7280")
    );

    private DefaultCategories() {
    }

    record Seed(String name, TransactionType type, String color) {
    }

}
