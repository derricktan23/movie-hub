<script lang="ts">
  import type { Toast } from '../../types/toast'
  import { toastStore } from '../../stores/toastStore'
  import { CheckCircle2, AlertCircle, Info, AlertTriangle, X } from '@lucide/svelte'

  interface Props {
    toast: Toast
  }

  let { toast }: Props = $props()
</script>

<div class="toast-item {toast.type} animate-fade-in" role="alert">
  <div class="toast-icon">
    {#if toast.type === 'success'}
      <CheckCircle2 size={18} color="#10B981" />
    {:else if toast.type === 'error'}
      <AlertCircle size={18} color="#EF4444" />
    {:else if toast.type === 'warning'}
      <AlertTriangle size={18} color="#F59E0B" />
    {:else}
      <Info size={18} color="#2563EB" />
    {/if}
  </div>

  <div class="toast-content">
    {#if toast.title}
      <h4 class="toast-title">{toast.title}</h4>
    {/if}
    <p class="toast-message">{toast.message}</p>
  </div>

  <button type="button" class="toast-close" onclick={() => toastStore.dismiss(toast.id)} aria-label="Dismiss">
    <X size={15} />
  </button>
</div>

<style>
  .toast-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-md);
    padding: 14px 16px;
    box-shadow: var(--shadow-lg);
    min-width: 320px;
    max-width: 440px;
    pointer-events: auto;
  }

  .toast-icon {
    flex-shrink: 0;
    margin-top: 1px;
  }

  .toast-content {
    flex: 1;
  }

  .toast-title {
    font-size: 0.875rem;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 2px;
  }

  .toast-message {
    font-size: 0.8125rem;
    color: var(--text-secondary);
    line-height: 1.4;
  }

  .toast-close {
    background: transparent;
    border: none;
    color: var(--text-muted);
    cursor: pointer;
    padding: 2px;
    border-radius: var(--radius-sm);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .toast-close:hover {
    color: var(--text-primary);
  }

  .success { border-left: 4px solid var(--success); }
  .error { border-left: 4px solid var(--danger); }
  .warning { border-left: 4px solid var(--warning); }
  .info { border-left: 4px solid var(--accent-primary); }
</style>
