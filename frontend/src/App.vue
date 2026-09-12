<script setup lang="ts">
import { RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

function handleLogout() {
  auth.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="app-shell">
    <header v-if="auth.isAuthenticated && route.name !== 'login'" class="topbar">
      <div class="topbar__inner">
        <div class="brand">
          <span class="brand__mark">₸</span>
          <span class="brand__name">Moliyaviy</span>
        </div>
        <div class="topbar__user">
          <span class="topbar__email">{{ auth.user?.email }}</span>
          <button class="btn btn-ghost" type="button" @click="handleLogout">Выйти</button>
        </div>
      </div>
    </header>

    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.topbar {
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 10;
}

.topbar__inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 800;
  font-size: 1.1rem;
}

.brand__mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: var(--color-primary);
  color: #fff;
  font-size: 1.05rem;
}

.topbar__user {
  display: flex;
  align-items: center;
  gap: 14px;
}

.topbar__email {
  color: var(--color-text-muted);
  font-size: 0.88rem;
}

@media (max-width: 480px) {
  .topbar__email {
    display: none;
  }
}

.app-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>
