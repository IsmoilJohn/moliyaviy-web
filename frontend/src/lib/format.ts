const currencyFormatter = new Intl.NumberFormat('ru-RU', { maximumFractionDigits: 0 })

export function formatCurrency(amount: number): string {
  return `${currencyFormatter.format(amount)} сум`
}

export function formatAmount(amount: number): string {
  return currencyFormatter.format(amount)
}

export function currentMonthValue(): string {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
}

export function todayIso(): string {
  const now = new Date()
  const yyyy = now.getFullYear()
  const mm = String(now.getMonth() + 1).padStart(2, '0')
  const dd = String(now.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}

export function formatMonthLabel(monthValue: string): string {
  const [year, month] = monthValue.split('-').map(Number)
  if (!year || !month) return monthValue
  const date = new Date(year, month - 1, 1)
  const label = date.toLocaleDateString('ru-RU', { month: 'long', year: 'numeric' })
  return label.charAt(0).toUpperCase() + label.slice(1)
}

export function formatDate(dateValue: string): string {
  const [year, month, day] = dateValue.split('-').map(Number)
  if (!year || !month || !day) return dateValue
  const date = new Date(year, month - 1, day)
  return date.toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric' })
}
