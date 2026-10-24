<script lang="ts">
  import { onMount } from 'svelte'
  import Navbar from './lib/components/layout/Navbar.svelte'
  import StatsBanner from './lib/components/layout/StatsBanner.svelte'
  import MovieGrid from './lib/components/movies/MovieGrid.svelte'
  import MovieModal from './lib/components/movies/MovieModal.svelte'
  import PeopleTable from './lib/components/people/PeopleTable.svelte'
  import PersonModal from './lib/components/people/PersonModal.svelte'
  import ToastContainer from './lib/components/common/ToastContainer.svelte'
  import Button from './lib/components/common/Button.svelte'
  import AlertBanner from './lib/components/common/AlertBanner.svelte'
  import { movieStore } from './lib/stores/movieStore'
  import { peopleStore } from './lib/stores/peopleStore'
  import { filteredMovies, filteredPeople, searchQuery } from './lib/stores/searchStore'
  import { RefreshCw } from '@lucide/svelte'

  let activeTab = $state<'movies' | 'people'>('movies')
  let movieModalOpen = $state(false)
  let personModalOpen = $state(false)

  onMount(() => {
    // Initial fetch from both microservices
    movieStore.load()
    peopleStore.load()
  })

  function handleRefreshAll() {
    movieStore.load()
    peopleStore.load()
  }
</script>

<div class="app-layout">
  <Navbar
    {activeTab}
    onTabChange={(tab) => activeTab = tab}
    onAddMovie={() => movieModalOpen = true}
    onAddPerson={() => personModalOpen = true}
  />

  <main class="main-content">
    <div class="content-container">
      <!-- Platform Header / Stats -->
      <div class="section-header">
        <div>
          <h1 class="page-title">
            {#if activeTab === 'movies'}
              Movie Catalog Management
            {:else}
              People & Talent Registry
            {/if}
          </h1>
          <p class="page-subtitle">
            Enterprise microservices platform managing cinematic releases, cast members, and production creators.
          </p>
        </div>

        <div class="header-actions">
          <Button variant="secondary" size="sm" onclick={handleRefreshAll} loading={$movieStore.loading || $peopleStore.loading}>
            <RefreshCw size={15} color="#2563EB" />
            <span>Sync Services</span>
          </Button>
        </div>
      </div>

      <!-- Quick Metrics Overview -->
      <StatsBanner />

      <!-- Service Connection Alerts (if any) -->
      {#if activeTab === 'movies' && $movieStore.error}
        <AlertBanner
          title="movie-service error"
          message="{$movieStore.error}. Verify movie-service is running on port 8081."
          variant="danger"
          actionText="Retry"
          onAction={() => movieStore.load()}
        />
      {/if}

      {#if activeTab === 'people' && $peopleStore.error}
        <AlertBanner
          title="people service error"
          message="{$peopleStore.error}. Verify backend microservices are running."
          variant="danger"
          actionText="Retry"
          onAction={() => peopleStore.load()}
        />
      {/if}

      <!-- Main View Tabs -->
      {#if activeTab === 'movies'}
        <MovieGrid
          movies={$filteredMovies}
          loading={$movieStore.loading}
          onAddMovie={() => movieModalOpen = true}
        />
      {:else}
        <PeopleTable
          people={$filteredPeople}
          loading={$peopleStore.loading}
          onAddPerson={() => personModalOpen = true}
        />
      {/if}
    </div>
  </main>

  <!-- Modals -->
  <MovieModal
    open={movieModalOpen}
    onclose={() => movieModalOpen = false}
  />

  <PersonModal
    open={personModalOpen}
    onclose={() => personModalOpen = false}
  />

  <!-- Toast Notification Stack -->
  <ToastContainer />
</div>

<style>
  .app-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background: var(--bg-primary);
  }

  .main-content {
    flex: 1;
    padding: 32px 24px 64px 24px;
  }

  .content-container {
    max-width: 1440px;
    margin: 0 auto;
    width: 100%;
  }

  .section-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 24px;
    flex-wrap: wrap;
  }

  .page-title {
    font-size: 1.85rem;
    font-weight: 800;
    color: var(--text-primary);
    letter-spacing: -0.025em;
  }

  .page-subtitle {
    font-size: 0.9375rem;
    color: var(--text-secondary);
    margin-top: 4px;
    max-width: 640px;
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  @media (max-width: 640px) {
    .main-content {
      padding: 20px 16px 48px 16px;
    }

    .page-title {
      font-size: 1.45rem;
    }
  }
</style>
