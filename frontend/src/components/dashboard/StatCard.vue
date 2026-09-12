<script setup lang="ts">
import { computed } from 'vue'
import { formatCurrency } from '@/lib/format'

const props = withDefaults(
  defineProps<{
    label: string
    value: number
    tone?: 'income' | 'expense' | 'neutral'
  }>(),
  { tone: 'neutral' },
)

const toneClass = computed(() => `stat-card--${props.tone}`)
</script>

<template>
  <div class="stat-card" :class="toneClass">
    <span class="stat-card__label">{{ label }}</span>
    <span class="stat-card__value">{{ formatCurrency(value) }}</span>
  </div>
</template>

<style scoped>
.stat-card {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  padding: 22px 24px;
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  gap: 8px;
  border-top: 3px solid var(--color-border);
}

.stat-card__label {
  font-size: 0.8rem;
  color: var(--color-text-muted);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.stat-card__value {
  font-size: 1.6rem;
  font-weight: 800;
  color: var(--color-text);
  word-break: break-word;
}

.stat-card--income {
  border-top-color: var(--color-income);
}

.stat-card--income .stat-card__value {
  color: var(--color-income);
}

.stat-card--expense {
  border-top-color: var(--color-expense);
}

.stat-card--expense .stat-card__value {
  color: var(--color-expense);
}
</style>
