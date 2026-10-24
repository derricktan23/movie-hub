<script lang="ts">
  import { movieStore } from '../../stores/movieStore'
  import { peopleStore } from '../../stores/peopleStore'
  import { Film, Users, Award, Video } from '@lucide/svelte'

  const movieCount = $derived($movieStore.items.length)
  const peopleCount = $derived($peopleStore.items.length)
  const directorCount = $derived($peopleStore.items.filter((p) => p.role.toUpperCase().includes('DIRECTOR')).length)
  const actorCount = $derived($peopleStore.items.filter((p) => p.role.toUpperCase().includes('ACTOR')).length)
</script>

<section class="stats-banner" aria-label="Platform Statistics">
  <div class="stat-card glass-panel">
    <div class="stat-icon film">
      <Film size={20} />
    </div>
    <div class="stat-info">
      <span class="stat-value">{movieCount}</span>
      <span class="stat-label">Catalog Titles</span>
    </div>
  </div>

  <div class="stat-card glass-panel">
    <div class="stat-icon users">
      <Users size={20} />
    </div>
    <div class="stat-info">
      <span class="stat-value">{peopleCount}</span>
      <span class="stat-label">People in Registry</span>
    </div>
  </div>

  <div class="stat-card glass-panel">
    <div class="stat-icon director">
      <Video size={20} />
    </div>
    <div class="stat-info">
      <span class="stat-value">{directorCount}</span>
      <span class="stat-label">Directors</span>
    </div>
  </div>

  <div class="stat-card glass-panel">
    <div class="stat-icon actor">
      <Award size={20} />
    </div>
    <div class="stat-info">
      <span class="stat-value">{actorCount}</span>
      <span class="stat-label">Actors & Stars</span>
    </div>
  </div>
</section>

<style>
  .stats-banner {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 16px;
    margin-bottom: 32px;
  }

  .stat-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
    transition: transform var(--transition-fast), box-shadow var(--transition-fast), border-color var(--transition-fast);
  }

  .stat-card:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
    border-color: #CBD5E1;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .stat-icon.film {
    background: #EFF6FF;
    color: #2563EB;
  }

  .stat-icon.users {
    background: #ECFDF5;
    color: #059669;
  }

  .stat-icon.director {
    background: #FEF3C7;
    color: #D97706;
  }

  .stat-icon.actor {
    background: #F5F3FF;
    color: #7C3AED;
  }

  .stat-info {
    display: flex;
    flex-direction: column;
  }

  .stat-value {
    font-size: 1.5rem;
    font-weight: 800;
    line-height: 1.1;
    color: var(--text-primary);
  }

  .stat-label {
    font-size: 0.8125rem;
    font-weight: 500;
    color: var(--text-secondary);
    margin-top: 3px;
  }
</style>
