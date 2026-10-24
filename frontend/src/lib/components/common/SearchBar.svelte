<script lang="ts">
  import { Search, X } from '@lucide/svelte'
  import { searchQuery } from '../../stores/searchStore'

  interface Props {
    placeholder?: string
  }

  let { placeholder = 'Search movies, cast, creators...' }: Props = $props()

  function handleClear() {
    searchQuery.set('')
  }
</script>

<div class="search-container">
  <Search size={17} class="search-icon" />
  <input
    type="text"
    class="search-input"
    {placeholder}
    bind:value={$searchQuery}
    aria-label="Search"
  />
  {#if $searchQuery}
    <button type="button" class="clear-btn" onclick={handleClear} aria-label="Clear search">
      <X size={15} />
    </button>
  {/if}
</div>

<style>
  .search-container {
    position: relative;
    display: flex;
    align-items: center;
    width: 100%;
    max-width: 420px;
  }

  :global(.search-icon) {
    position: absolute;
    left: 14px;
    color: var(--text-muted);
    pointer-events: none;
  }

  .search-input {
    width: 100%;
    padding: 9px 38px 9px 40px;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-full);
    color: var(--text-primary);
    font-size: 0.875rem;
    font-family: inherit;
    transition: all var(--transition-fast);
    outline: none;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  }

  .search-input:focus {
    border-color: var(--border-focus);
    background: #FFFFFF;
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
  }

  .search-input::placeholder {
    color: var(--text-muted);
  }

  .clear-btn {
    position: absolute;
    right: 12px;
    background: transparent;
    border: none;
    color: var(--text-muted);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2px;
    border-radius: 50%;
  }

  .clear-btn:hover {
    color: var(--text-primary);
  }
</style>
