<script setup lang="ts">
import { onMounted, ref } from 'vue'
import TheWelcome from '../components/TheWelcome.vue'
import { apiClient } from '@/api/client'

const backendStatus = ref<'checking' | 'ok' | 'unreachable'>('checking')

onMounted(async () => {
  try {
    const { data } = await apiClient.get<{ status: string }>('/health')
    backendStatus.value = data.status === 'ok' ? 'ok' : 'unreachable'
  } catch {
    backendStatus.value = 'unreachable'
  }
})
</script>

<template>
  <main>
    <p class="backend-status">Backend: {{ backendStatus }}</p>
    <TheWelcome />
  </main>
</template>

<style scoped>
.backend-status {
  text-align: center;
  font-size: 0.9rem;
  color: var(--color-text);
}
</style>
