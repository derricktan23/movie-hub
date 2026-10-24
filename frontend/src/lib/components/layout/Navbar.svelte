<script lang="ts">
  import { Film, Users, Plus, Clapperboard } from '@lucide/svelte'
  import SearchBar from '../common/SearchBar.svelte'
  import Button from '../common/Button.svelte'

  interface Props {
    activeTab: 'movies' | 'people'
    onTabChange: (tab: 'movies' | 'people') => void
    onAddMovie: () => void
    onAddPerson: () => void
  }

  let { activeTab = 'movies', onTabChange, onAddMovie, onAddPerson }: Props = $props()
</script>

<header class="navbar">
  <div class="nav-container">
    <div class="brand">
      <div class="brand-icon">
        <Clapperboard size={20} color="#ffffff" strokeWidth={2.2} />
      </div>
      <div class="brand-text">
        <span class="brand-title">MovieHub</span>
        <span class="brand-tag">ENTERPRISE</span>
      </div>
    </div>

    <nav class="nav-tabs" aria-label="Main Navigation">
      <button
        type="button"
        class="nav-tab {activeTab === 'movies' ? 'active' : ''}"
        onclick={() => onTabChange('movies')}
      >
        <Film size={16} />
        <span>Movie Catalog</span>
      </button>

      <button
        type="button"
        class="nav-tab {activeTab === 'people' ? 'active' : ''}"
        onclick={() => onTabChange('people')}
      >
        <Users size={16} />
        <span>People Directory</span>
      </button>
    </nav>

    <div class="nav-search">
      <SearchBar />
    </div>

    <div class="nav-actions">
      {#if activeTab === 'movies'}
        <Button variant="primary" size="sm" onclick={onAddMovie}>
          <Plus size={16} />
          <span>Add Movie</span>
        </Button>
      {:else}
        <Button variant="primary" size="sm" onclick={onAddPerson}>
          <Plus size={16} />
          <span>Add Person</span>
        </Button>
      {/if}
    </div>
  </div>
</header>

<style>
  .navbar {
    position: sticky;
    top: 0;
    z-index: 100;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border-bottom: 1px solid var(--border-subtle);
    padding: 0 24px;
    height: 68px;
    display: flex;
    align-items: center;
  }

  .nav-container {
    max-width: 1440px;
    width: 100%;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 24px;
  }

  .brand {
    display: flex;
    align-items: center;
    gap: 12px;
    text-decoration: none;
    cursor: pointer;
  }

  .brand-icon {
    width: 38px;
    height: 38px;
    border-radius: var(--radius-md);
    background: var(--accent-primary);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 8px rgba(37, 99, 235, 0.35);
  }

  .brand-text {
    display: flex;
    flex-direction: column;
  }

  .brand-title {
    font-size: 1.25rem;
    font-weight: 800;
    letter-spacing: -0.02em;
    color: var(--text-primary);
    line-height: 1.1;
  }

  .brand-tag {
    font-size: 0.625rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    color: var(--accent-primary);
  }

  .nav-tabs {
    display: flex;
    align-items: center;
    gap: 4px;
    background: #F1F5F9;
    padding: 4px;
    border-radius: var(--radius-full);
    border: 1px solid var(--border-subtle);
  }

  .nav-tab {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 7px 16px;
    border-radius: var(--radius-full);
    background: transparent;
    border: 1px solid transparent;
    color: var(--text-secondary);
    font-size: 0.875rem;
    font-weight: 600;
    font-family: inherit;
    cursor: pointer;
    transition: all var(--transition-fast);
  }

  .nav-tab:hover {
    color: var(--text-primary);
  }

  .nav-tab.active {
    background: #FFFFFF;
    color: var(--accent-primary);
    border-color: #E2E8F0;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  }

  .nav-search {
    flex: 1;
    display: flex;
    justify-content: center;
  }

  .nav-actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  @media (max-width: 900px) {
    .nav-search {
      display: none;
    }
  }

  @media (max-width: 680px) {
    .nav-tabs span {
      display: none;
    }
    .brand-tag {
      display: none;
    }
  }
</style>
