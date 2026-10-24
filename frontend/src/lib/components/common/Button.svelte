<script lang="ts">
  import type { Snippet } from 'svelte'

  interface Props {
    variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
    size?: 'sm' | 'md' | 'lg'
    type?: 'button' | 'submit' | 'reset'
    form?: string
    disabled?: boolean
    loading?: boolean
    onclick?: (e: MouseEvent) => void
    children?: Snippet
  }

  let {
    variant = 'primary',
    size = 'md',
    type = 'button',
    form,
    disabled = false,
    loading = false,
    onclick,
    children
  }: Props = $props()
</script>

<button
  {type}
  {form}
  class="btn {variant} {size}"
  disabled={disabled || loading}
  {onclick}
>
  {#if loading}
    <span class="spinner {variant === 'secondary' || variant === 'ghost' ? 'spinner-blue' : ''}"></span>
  {/if}
  {#if children}
    {@render children()}
  {/if}
</button>

<style>
  .btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    font-weight: 600;
    font-family: inherit;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all var(--transition-fast);
    border: 1px solid transparent;
    text-decoration: none;
    line-height: 1;
  }

  .btn:disabled {
    opacity: 0.55;
    cursor: not-allowed;
  }

  /* Sizes */
  .sm {
    padding: 7px 14px;
    font-size: 0.8125rem;
  }

  .md {
    padding: 10px 18px;
    font-size: 0.875rem;
  }

  .lg {
    padding: 12px 24px;
    font-size: 1rem;
  }

  /* Variants */
  .primary {
    background: var(--accent-primary);
    color: #ffffff;
    box-shadow: 0 1px 3px rgba(37, 99, 235, 0.35);
  }

  .primary:hover:not(:disabled) {
    background: var(--accent-hover);
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
    transform: translateY(-1px);
  }

  .secondary {
    background: #ffffff;
    color: var(--text-primary);
    border-color: var(--border-subtle);
    box-shadow: var(--shadow-sm);
  }

  .secondary:hover:not(:disabled) {
    background: var(--bg-secondary);
    border-color: #CBD5E1;
  }

  .danger {
    background: var(--danger-bg);
    color: var(--danger-hover);
    border-color: #FECACA;
  }

  .danger:hover:not(:disabled) {
    background: var(--danger);
    color: #ffffff;
    border-color: var(--danger);
  }

  .ghost {
    background: transparent;
    color: var(--text-secondary);
  }

  .ghost:hover:not(:disabled) {
    background: var(--bg-secondary);
    color: var(--text-primary);
  }

  .spinner {
    width: 14px;
    height: 14px;
    border: 2px solid rgba(255, 255, 255, 0.4);
    border-top-color: #ffffff;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  .spinner-blue {
    border-color: rgba(37, 99, 235, 0.2);
    border-top-color: var(--accent-primary);
  }
</style>
