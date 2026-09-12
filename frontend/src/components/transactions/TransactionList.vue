<script setup lang="ts">
import { computed, ref } from 'vue'
import { apiClient } from '@/api/client'
import type { Transaction } from '@/types/models'
import { formatCurrency, formatDate } from '@/lib/format'

const props = withDefaults(
  defineProps<{
    transactions: Transaction[]
    loading?: boolean
    limit?: number
  }>(),
  { limit: 15 },
)
const emit = defineEmits<{ deleted: [] }>()

const deletingId = ref<string | null>(null)

const visible = computed(() => props.transactions.slice(0, props.limit))

async function handleDelete(id: string) {
  deletingId.value = id
  try {
    await apiClient.delete(`/transactions/${id}`)
    emit('deleted')
  } finally {
    deletingId.value = null
  }
}
</script>

<template>
  <div class="panel">
    <div class="panel__header">
      <h2 class="panel__title">Последние транзакции</h2>
      <span v-if="transactions.length > limit" class="muted panel__count">
        показано {{ limit }} из {{ transactions.length }}
      </span>
    </div>

    <p v-if="loading" class="muted">Загрузка...</p>
    <p v-else-if="visible.length === 0" class="muted">Транзакций пока нет</p>

    <ul v-else class="scroll-list tx-list">
      <li v-for="tx in visible" :key="tx.id" class="tx-row">
        <div class="tx-row__main">
          <span class="badge" :class="tx.type === 'INCOME' ? 'badge--income' : 'badge--expense'">
            {{ tx.type === 'INCOME' ? 'Доход' : 'Расход' }}
          </span>
          <div class="tx-row__info">
            <span class="tx-row__category">{{ tx.categoryName }}</span>
            <span v-if="tx.comment" class="tx-row__comment">{{ tx.comment }}</span>
          </div>
        </div>
        <div class="tx-row__side">
          <span class="tx-row__date">{{ formatDate(tx.transactionDate) }}</span>
          <span class="tx-row__amount" :class="tx.type === 'INCOME' ? 'text-income' : 'text-danger'">
            {{ tx.type === 'INCOME' ? '+' : '−' }}{{ formatCurrency(tx.amount) }}
          </span>
          <button
            class="btn-danger-ghost"
            type="button"
            :disabled="deletingId === tx.id"
            title="Удалить"
            @click="handleDelete(tx.id)"
          >
            ✕
          </button>
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.panel__header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
}

.panel__count {
  font-size: 0.78rem;
}

.tx-list {
  gap: 4px;
}

.tx-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 4px;
  border-bottom: 1px solid var(--color-surface-muted);
}

.tx-row:last-child {
  border-bottom: none;
}

.tx-row__main {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.tx-row__info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.tx-row__category {
  font-weight: 600;
  font-size: 0.9rem;
}

.tx-row__comment {
  font-size: 0.78rem;
  color: var(--color-text-faint);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 220px;
}

.tx-row__side {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.tx-row__date {
  font-size: 0.78rem;
  color: var(--color-text-faint);
  white-space: nowrap;
}

.tx-row__amount {
  font-weight: 700;
  font-size: 0.9rem;
  white-space: nowrap;
}

@media (max-width: 480px) {
  .tx-row {
    flex-direction: column;
    align-items: stretch;
    gap: 6px;
  }

  .tx-row__side {
    justify-content: space-between;
  }
}

.text-income {
  color: var(--color-income);
}
</style>
