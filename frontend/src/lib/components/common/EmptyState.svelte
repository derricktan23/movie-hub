<script lang="ts">
  import type { Component } from 'svelte'
  import Button from './Button.svelte'
  import { SearchX } from '@lucide/svelte'

  interface Props {
    title: string
    description: string
    icon?: Component<{ size?: number; color?: string }>
    isSearch?: boolean
    searchQuery?: string
    actionText?: string
    onAction?: () => void
    onClearSearch?: () => void
  }

  let {
    title,
    description,
    icon: Icon,
    isSearch = false,
    searchQuery = '',
    actionText,
    onAction,
    onClearSearch
  }: Props = $props()
</script>

<div class="empty-state glass-panel animate-fade-in">
  {#if isSearch}
    <SearchX size={44} color="#64748B" />
    <h3>{title}</h3>
    <p>
      {#if searchQuery}
        No results matched your search query "<strong>{searchQuery}</strong>".
      {:else}
        {description}
      {/if}
    </p>
    {#if onClearSearch}
      <Button variant="secondary" size="sm" onclick={onClearSearch}>
        Clear Search Filter
      </Button>
    {/if}
  {:else}
    {#if Icon}
      <Icon size={44} color="#2563EB" />
    {/if}
    <h3>{title}</h3>
    <p>{description}</p>
    {#if actionText && onAction}
      <Button variant="primary" size="md" onclick={onAction}>
        <span>{actionText}</span>
      </Button>
    {/if}
  {/if}
</div>

<style>
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 64px 24px;
    gap: 16px;
    max-width: 520px;
    margin: 40px auto;
  }

  .empty-state h3 {
    font-size: 1.25rem;
    font-weight: 700;
    color: var(--text-primary);
  }

  .empty-state p {
    font-size: 0.9375rem;
    color: var(--text-secondary);
    line-height: 1.5;
  }
</style>
