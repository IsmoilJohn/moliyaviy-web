<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import axios from 'axios'
import { apiClient } from '@/api/client'
import type { Category, TransactionType } from '@/types/models'
import { todayIso } from '@/lib/format'

const props = defineProps<{ categories: Category[] }>()
const emit = defineEmits<{ created: [] }>()

const type = ref<TransactionType>('EXPENSE')
const categoryId = ref('')
const amount = ref<number | null>(null)
const date = ref(todayIso())
const comment = ref('')
const submitting = ref(false)
const errorMessage = ref<string | null>(null)
const successMessage = ref<string | null>(null)

const filteredCategories = computed(() => props.categories.filter((c) => c.type === type.value))

watch(
  filteredCategories,
  (list) => {
    if (!list.some((c) => c.id === categoryId.value)) {
      categoryId.value = list[0]?.id ?? ''
    }
  },
  { immediate: true },
)

async function handleSubmit() {
  errorMessage.value = null
  successMessage.value = null

  if (!categoryId.value || !amount.value || amount.value <= 0 || !date.value) {
    errorMessage.value = 'Заполните сумму, категорию и дату'
    return
  }

  submitting.value = true
  try {
    await apiClient.post('/transactions', {
      categoryId: categoryId.value,
      type: type.value,
      amount: amount.value,
      transactionDate: date.value,
      comment: comment.value.trim() || null,
    })
    successMessage.value = 'Транзакция добавлена'
    amount.value = null
    comment.value = ''
    emit('created')
  } catch (e) {
    errorMessage.value =
      axios.isAxiosError(e) && e.response?.data?.message
        ? e.response.data.message
        : 'Не удалось сохранить транзакцию'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="panel">
    <h2 class="panel__title">Новая транзакция</h2>
    <form class="tx-form" @submit.prevent="handleSubmit">
      <div class="type-switch">
        <button
          type="button"
          class="type-switch__btn"
          :class="{ 'type-switch__btn--active-expense': type === 'EXPENSE' }"
          @click="type = 'EXPENSE'"
        >
          Расход
        </button>
        <button
          type="button"
          class="type-switch__btn"
          :class="{ 'type-switch__btn--active-income': type === 'INCOME' }"
          @click="type = 'INCOME'"
        >
          Доход
        </button>
      </div>

      <div class="field-row">
        <label class="field">
          <span class="field__label">Сумма</span>
          <input v-model.number="amount" type="number" min="0" step="0.01" placeholder="0" required />
        </label>
        <label class="field">
          <span class="field__label">Дата</span>
          <input v-model="date" type="date" required />
        </label>
      </div>

      <label class="field">
        <span class="field__label">Категория</span>
        <select v-model="categoryId" required>
          <option v-for="c in filteredCategories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
      </label>

      <label class="field">
        <span class="field__label">Комментарий</span>
        <input v-model="comment" type="text" placeholder="Необязательно" maxlength="500" />
      </label>

      <p v-if="errorMessage" class="form-message form-message--error">{{ errorMessage }}</p>
      <p v-if="successMessage" class="form-message form-message--success">{{ successMessage }}</p>

      <button type="submit" class="btn btn-primary btn-block" :disabled="submitting">
        {{ submitting ? 'Сохранение...' : 'Добавить' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.tx-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.type-switch {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 8px;
  background: var(--color-surface-muted);
  border-radius: var(--radius-sm);
  padding: 4px;
}

.type-switch__btn {
  border: none;
  background: transparent;
  padding: 9px 12px;
  border-radius: 6px;
  font-weight: 700;
  font-size: 0.85rem;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.15s ease;
}

.type-switch__btn--active-expense {
  background: var(--color-expense-soft);
  color: var(--color-expense);
}

.type-switch__btn--active-income {
  background: var(--color-income-soft);
  color: var(--color-income);
}
</style>
