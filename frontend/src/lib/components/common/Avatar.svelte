<script lang="ts">
  interface Props {
    name: string
    size?: 'sm' | 'md' | 'lg'
  }

  let { name, size = 'md' }: Props = $props()

  const initials = $derived.by(() => {
    const trimmed = name?.trim() || ''
    if (!trimmed) return 'P'
    const parts = trimmed.split(/\s+/)
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
    }
    return trimmed.substring(0, 2).toUpperCase()
  })
</script>

<div class="avatar-circle size-{size}" title={name} aria-label={name}>
  {initials}
</div>

<style>
  .avatar-circle {
    border-radius: 50%;
    background: #EFF6FF;
    border: 1px solid #DBEAFE;
    color: var(--accent-primary);
    font-weight: 700;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    user-select: none;
  }

  .size-sm {
    width: 28px;
    height: 28px;
    font-size: 0.6875rem;
  }

  .size-md {
    width: 36px;
    height: 36px;
    font-size: 0.8125rem;
  }

  .size-lg {
    width: 48px;
    height: 48px;
    font-size: 1rem;
  }
</style>
