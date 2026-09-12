export type TransactionType = 'INCOME' | 'EXPENSE'

export interface User {
  id: string
  email: string
  fullName: string | null
  createdAt: string
}

export interface Category {
  id: string
  name: string
  type: TransactionType
  color: string | null
  createdAt: string
  updatedAt: string
}

export interface Transaction {
  id: string
  categoryId: string
  categoryName: string
  type: TransactionType
  amount: number
  transactionDate: string
  comment: string | null
  createdAt: string
  updatedAt: string
}

export interface CategoryExpense {
  categoryId: string
  categoryName: string
  amount: number
  percentage: number
}

export interface CategoryLimitStatus {
  categoryId: string
  categoryName: string
  limit: number
  spent: number
  remaining: number
  exceeded: boolean
}

export interface DashboardData {
  month: string
  totalIncome: number
  totalExpense: number
  balance: number
  expensesByCategory: CategoryExpense[]
  limits: CategoryLimitStatus[]
}

export interface LoginResponse {
  token: string
  tokenType: string
  user: User
}

export interface ApiError {
  message: string
}
