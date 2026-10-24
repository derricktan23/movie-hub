<script lang="ts">
  import { AlertCircle, AlertTriangle, Info } from '@lucide/svelte'
  import Button from './Button.svelte'

  interface Props {
    title: string
    message: string
    variant?: 'danger' | 'warning' | 'info'
    actionText?: string
    onAction?: () => void
  }

  let {
    title,
    message,
    variant = 'danger',
    actionText,
    onAction
  }: Props = $props()
</script>

<div class="alert-banner {variant} animate-fade-in" role="alert">
  <div class="icon-wrapper">
    {#if variant === 'danger'}
      <AlertCircle size={20} color="#EF4444" />
    {:else if variant === 'warning'}
      <AlertTriangle size={20} color="#F59E0B" />
    {:else}
      <Info size={20} color="#3B82F6" />
    {/if}
  </div>

  <div class="alert-details">
    <strong>{title}:</strong>
    <span>{message}</span>
  </div>

  {#if actionText && onAction}
    <Button variant={variant === 'danger' ? 'danger' : 'secondary'} size="sm" onclick={onAction}>
      {actionText}
    </Button>
  {/if}
</div>

<style>
  .alert-banner {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 14px 18px;
    margin-bottom: 24px;
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-sm);
  }

  .icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .alert-details {
    flex: 1;
    font-size: 0.875rem;
    line-height: 1.4;
  }

  .alert-details strong {
    margin-right: 6px;
  }

  .danger {
    background: #FEF2F2;
    border: 1px solid #FECACA;
    color: #991B1B;
  }

  .danger strong {
    color: #7F1D1D;
  }

  .warning {
    background: #FFFBEB;
    border: 1px solid #FDE68A;
    color: #92400E;
  }

  .warning strong {
    color: #78350F;
  }

  .info {
    background: #EFF6FF;
    border: 1px solid #DBEAFE;
    color: #1E40AF;
  }

  .info strong {
    color: #1E3A8A;
  }
</style>
