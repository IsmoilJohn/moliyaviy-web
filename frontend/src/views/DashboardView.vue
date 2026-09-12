<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { apiClient } from '@/api/client'
import type { Category, Transaction, DashboardData } from '@/types/models'
import { currentMonthValue, formatMonthLabel } from '@/lib/format'
import StatCard from '@/components/dashboard/StatCard.vue'
import CategoryBreakdown from '@/components/dashboard/CategoryBreakdown.vue'
import LimitsPanel from '@/components/dashboard/LimitsPanel.vue'
import TransactionForm from '@/components/transactions/TransactionForm.vue'
import TransactionList from '@/components/transactions/TransactionList.vue'

const month = ref(currentMonthValue())
const dashboard = ref<DashboardData | null>(null)
const categories = ref<Category[]>([])
const transactions = ref<Transaction[]>([])

const loadingDashboard = ref(false)
const loadingTransactions = ref(false)
const errorMessage = ref<string | null>(null)

async function loadDashboard() {
  loadingDashboard.value = true
  errorMessage.value = null
  try {
    const { data } = await apiClient.get<DashboardData>('/dashboard', { params: { month: month.value } })
    dashboard.value = data
  } catch {
    errorMessage.value = 'Не удалось загрузить статистику за месяц'
  } finally {
    loadingDashboard.value = false
  }
}

async function loadCategories() {
  const { data } = await apiClient.get<Category[]>('/categories')
  categories.value = data
}

async function loadTransactions() {
  loadingTransactions.value = true
  try {
    const { data } = await apiClient.get<Transaction[]>('/transactions')
    transactions.value = data
  } finally {
    loadingTransactions.value = false
  }
}

function refreshAfterChange() {
  loadDashboard()
  loadTransactions()
}

watch(month, loadDashboard)

onMounted(() => {
  loadCategories()
  loadDashboard()
  loadTransactions()
})
</script>

<template>
  <div class="dashboard">
    <div class="dashboard__header">
      <div>
        <h1>Дашборд</h1>
        <p class="muted">{{ formatMonthLabel(month) }}</p>
      </div>
      <input v-model="month" type="month" class="month-picker" />
    </div>

    <p v-if="errorMessage" class="alert alert-error">{{ errorMessage }}</p>

    <section class="stat-grid">
      <StatCard label="Доходы" :value="dashboard?.totalIncome ?? 0" tone="income" />
      <StatCard label="Расходы" :value="dashboard?.totalExpense ?? 0" tone="expense" />
      <StatCard
        label="Баланс"
        :value="dashboard?.balance ?? 0"
        :tone="(dashboard?.balance ?? 0) >= 0 ? 'income' : 'expense'"
      />
    </section>

    <div class="dashboard__grid">
      <div class="dashboard__col">
        <CategoryBreakdown :items="dashboard?.expensesByCategory ?? []" :categories="categories" />
        <LimitsPanel :limits="dashboard?.limits ?? []" />
      </div>
      <div class="dashboard__col">
        <TransactionForm :categories="categories" @created="refreshAfterChange" />
        <TransactionList
          :transactions="transactions"
          :loading="loadingTransactions"
          @deleted="refreshAfterChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  max-width: 1180px;
  width: 100%;
  margin: 0 auto;
  padding: 28px 24px 60px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.dashboard__header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.dashboard__header h1 {
  font-size: 1.6rem;
}

.month-picker {
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 9px 12px;
  background: var(--color-surface);
  font-weight: 600;
  outline: none;
}

.month-picker:focus {
  border-color: var(--color-primary);
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.dashboard__grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 20px;
  align-items: start;
}

.dashboard__col {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

@media (max-width: 860px) {
  .stat-grid {
    grid-template-columns: 1fr;
  }

  .dashboard__grid {
    grid-template-columns: 1fr;
  }
}
</style>
