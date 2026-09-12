<script setup lang="ts">
import type { Category, CategoryExpense } from '@/types/models'
import { formatCurrency } from '@/lib/format'

const props = defineProps<{
  items: CategoryExpense[]
  categories: Category[]
}>()

const PALETTE = ['#6c5ce7', '#f97316', '#0ea5e9', '#22c55e', '#eab308', '#ec4899', '#14b8a6', '#a855f7']

function colorFor(categoryId: string, index: number): string {
  const category = props.categories.find((c) => c.id === categoryId)
  return category?.color || PALETTE[index % PALETTE.length] || '#6c5ce7'
}
</script>

<template>
  <div class="panel">
    <h2 class="panel__title">Расходы по категориям</h2>
    <p v-if="items.length === 0" class="muted">Нет расходов за этот месяц</p>
    <ul v-else class="breakdown-list">
      <li v-for="(item, index) in items" :key="item.categoryId" class="breakdown-row">
        <div class="breakdown-row__top">
          <span class="breakdown-row__name">
            <span class="dot" :style="{ background: colorFor(item.categoryId, index) }" />
            {{ item.categoryName }}
          </span>
          <span class="breakdown-row__amount">{{ formatCurrency(item.amount) }}</span>
        </div>
        <div class="breakdown-row__bar-line">
          <div class="bar">
            <div
              class="bar__fill"
              :style="{ width: item.percentage + '%', background: colorFor(item.categoryId, index) }"
            />
          </div>
          <span class="breakdown-row__pct">{{ item.percentage }}%</span>
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.breakdown-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.breakdown-row__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 0.9rem;
}

.breakdown-row__name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  flex-shrink: 0;
}

.breakdown-row__amount {
  font-weight: 700;
  color: var(--color-text-muted);
  white-space: nowrap;
}

.breakdown-row__bar-line {
  display: flex;
  align-items: center;
  gap: 10px;
}

.breakdown-row__bar-line .bar {
  flex: 1;
}

.breakdown-row__pct {
  font-size: 0.78rem;
  color: var(--color-text-faint);
  font-weight: 600;
  width: 38px;
  text-align: right;
  flex-shrink: 0;
}
</style>
