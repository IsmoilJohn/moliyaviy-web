<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const submitting = ref(false)

async function handleSubmit() {
  submitting.value = true
  try {
    await auth.login(email.value, password.value)
    router.push({ name: 'dashboard' })
  } catch {
    // error message is already set on the store
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-brand">
        <span class="login-brand__mark">₸</span>
        <span class="login-brand__name">Moliyaviy</span>
      </div>
      <p class="login-subtitle">Финансовый помощник — войдите в аккаунт</p>

      <form class="login-form" @submit.prevent="handleSubmit">
        <label class="field">
          <span class="field__label">Email</span>
          <input v-model="email" type="email" autocomplete="username" required placeholder="you@example.com" />
        </label>

        <label class="field">
          <span class="field__label">Пароль</span>
          <input v-model="password" type="password" autocomplete="current-password" required placeholder="••••••••" />
        </label>

        <p v-if="auth.error" class="alert alert-error">{{ auth.error }}</p>

        <button type="submit" class="btn btn-primary btn-block" :disabled="submitting">
          {{ submitting ? 'Входим...' : 'Войти' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at 15% 20%, var(--color-primary-soft), transparent 45%),
    var(--color-bg);
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: 36px 32px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 800;
  font-size: 1.3rem;
}

.login-brand__mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: var(--color-primary);
  color: #fff;
  font-size: 1.2rem;
}

.login-subtitle {
  color: var(--color-text-muted);
  font-size: 0.9rem;
  margin-bottom: 20px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>
