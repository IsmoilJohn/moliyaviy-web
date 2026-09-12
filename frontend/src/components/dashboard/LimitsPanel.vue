<script setup lang="ts">
import type { CategoryLimitStatus } from '@/types/models'
import { formatCurrency } from '@/lib/format'

defineProps<{ limits: CategoryLimitStatus[] }>()

function progressPercent(limit: CategoryLimitStatus): number {
  if (limit.limit <= 0) return 0
  return Math.min(100, Math.round((limit.spent / limit.limit) * 100))
}
</script>

<template>
  <div class="panel">
    <h2 class="panel__title">Лимиты по категориям</h2>
    <p v-if="limits.length === 0" class="muted">Лимиты пока не заданы</p>
    <ul v-else class="limits-list">
      <li v-for="limit in limits" :key="limit.categoryId" class="limit-row">
        <div class="limit-row__top">
          <span class="limit-row__name">
            {{ limit.categoryName }}
            <span v-if="limit.exceeded" class="badge badge--danger">Превышен</span>
          </span>
          <span class="limit-row__figures">{{ formatCurrency(limit.spent) }} / {{ formatCurrency(limit.limit) }}</span>
        </div>
        <div class="bar">
          <div
            class="bar__fill"
            :class="limit.exceeded ? 'bar__fill--danger' : 'bar__fill--ok'"
            :style="{ width: progressPercent(limit) + '%' }"
          />
        </div>
        <span class="limit-row__remaining" :class="{ 'text-danger': limit.remaining < 0 }">
          {{ limit.remaining >= 0 ? 'Остаток' : 'Превышение' }}: {{ formatCurrency(Math.abs(limit.remaining)) }}
        </span>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.limits-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.limit-row__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.limit-row__name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.limit-row__figures {
  color: var(--color-text-muted);
  font-weight: 600;
  font-size: 0.83rem;
  white-space: nowrap;
}

.limit-row__remaining {
  display: block;
  margin-top: 6px;
  font-size: 0.8rem;
  color: var(--color-text-muted);
  font-weight: 600;
}
</style>
