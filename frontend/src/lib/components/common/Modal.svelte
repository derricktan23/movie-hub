<script lang="ts">
  import type { Snippet } from 'svelte'
  import { X } from '@lucide/svelte'

  interface Props {
    open: boolean
    title: string
    onclose: () => void
    children?: Snippet
    footer?: Snippet
  }

  let { open = false, title, onclose, children, footer }: Props = $props()

  function handleBackdrop(e: MouseEvent) {
    if (e.target === e.currentTarget) {
      onclose()
    }
  }

  function handleKeydown(e: KeyboardEvent) {
    if (e.key === 'Escape' && open) {
      onclose()
    }
  }
</script>

<svelte:window onkeydown={handleKeydown} />

{#if open}
  <!-- svelte-ignore a11y_click_events_have_key_events -->
  <!-- svelte-ignore a11y_no_static_element_interactions -->
  <div class="modal-backdrop" onclick={handleBackdrop}>
    <div class="modal-card animate-fade-in" role="dialog" aria-modal="true" aria-labelledby="modal-title">
      <div class="modal-header">
        <h2 id="modal-title">{title}</h2>
        <button type="button" class="close-btn" onclick={onclose} aria-label="Close dialog">
          <X size={18} />
        </button>
      </div>

      <div class="modal-body">
        {#if children}
          {@render children()}
        {/if}
      </div>

      {#if footer}
        <div class="modal-footer">
          {@render footer()}
        </div>
      {/if}
    </div>
  </div>
{/if}

<style>
  .modal-backdrop {
    position: fixed;
    inset: 0;
    z-index: 999;
    background: rgba(15, 23, 42, 0.45);
    backdrop-filter: blur(6px);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
  }

  .modal-card {
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-xl);
    width: 100%;
    max-width: 540px;
    max-height: 90vh;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  .modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 18px 24px;
    border-bottom: 1px solid var(--border-subtle);
    background: #FFFFFF;
  }

  .modal-header h2 {
    font-size: 1.15rem;
    font-weight: 700;
    color: var(--text-primary);
  }

  .close-btn {
    background: transparent;
    border: none;
    color: var(--text-muted);
    cursor: pointer;
    padding: 6px;
    border-radius: var(--radius-sm);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: color var(--transition-fast), background var(--transition-fast);
  }

  .close-btn:hover {
    color: var(--text-primary);
    background: var(--bg-secondary);
  }

  .modal-body {
    padding: 24px;
    overflow-y: auto;
    flex: 1;
    background: #FFFFFF;
  }

  .modal-footer {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 12px;
    padding: 16px 24px;
    background: #F8FAFC;
    border-top: 1px solid var(--border-subtle);
  }
</style>
