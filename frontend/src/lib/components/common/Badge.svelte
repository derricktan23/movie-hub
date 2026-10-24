<script lang="ts">
  interface Props {
    role?: string
    variant?: 'actor' | 'director' | 'producer' | 'composer' | 'default'
  }

  let { role = 'default', variant }: Props = $props()

  const computedVariant = $derived.by(() => {
    if (variant) return variant
    const upper = role.toUpperCase()
    if (upper.includes('ACTOR')) return 'actor'
    if (upper.includes('DIRECTOR')) return 'director'
    if (upper.includes('PRODUCER')) return 'producer'
    if (upper.includes('COMPOSER')) return 'composer'
    return 'default'
  })
</script>

<span class="badge {computedVariant}">
  {role}
</span>

<style>
  .badge {
    display: inline-flex;
    align-items: center;
    padding: 3px 10px;
    border-radius: var(--radius-full);
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.04em;
    text-transform: uppercase;
    white-space: nowrap;
    border: 1px solid transparent;
    transition: all var(--transition-fast);
  }

  /* Enterprise Blue & Crisp Pastel Pill Palette */
  .actor {
    background: #EFF6FF;
    color: #1E40AF;
    border-color: #DBEAFE;
  }

  .director {
    background: #FEF3C7;
    color: #92400E;
    border-color: #FDE68A;
  }

  .producer {
    background: #ECFDF5;
    color: #065F46;
    border-color: #A7F3D0;
  }

  .composer {
    background: #F5F3FF;
    color: #5B21B6;
    border-color: #DDD6FE;
  }

  .default {
    background: #F1F5F9;
    color: #334155;
    border-color: #E2E8F0;
  }
</style>
