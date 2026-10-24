<script lang="ts">
  import Modal from './Modal.svelte'
  import Button from './Button.svelte'
  import { AlertTriangle } from '@lucide/svelte'

  interface Props {
    open: boolean
    title: string
    itemName: string
    loading?: boolean
    onclose: () => void
    onconfirm: () => void
  }

  let { open = false, title, itemName, loading = false, onclose, onconfirm }: Props = $props()
</script>

<Modal {open} {title} {onclose}>
  <div class="confirm-content">
    <div class="warning-icon">
      <AlertTriangle size={24} color="#EF4444" />
    </div>
    <p class="warning-text">
      Are you sure you want to delete <strong>"{itemName}"</strong>? This will permanently remove the record from the database.
    </p>
  </div>

  {#snippet footer()}
    <Button variant="ghost" onclick={onclose} disabled={loading}>
      Cancel
    </Button>
    <Button variant="danger" onclick={onconfirm} {loading}>
      Confirm Delete
    </Button>
  {/snippet}
</Modal>

<style>
  .confirm-content {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    padding: 6px 0;
  }

  .warning-icon {
    background: #FEF2F2;
    border: 1px solid #FECACA;
    padding: 10px;
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .warning-text {
    font-size: 0.9375rem;
    color: var(--text-secondary);
    line-height: 1.5;
  }

  .warning-text strong {
    color: var(--text-primary);
  }
</style>
