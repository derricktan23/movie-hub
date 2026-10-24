<script lang="ts">
  import { toastStore } from '../../stores/toastStore'
  import ToastItem from './Toast.svelte'
</script>

<aside class="toast-container" aria-live="polite">
  {#each $toastStore as toast (toast.id)}
    <ToastItem {toast} />
  {/each}
</aside>

<style>
  .toast-container {
    position: fixed;
    bottom: 24px;
    right: 24px;
    z-index: 9999;
    display: flex;
    flex-direction: column;
    gap: 10px;
    pointer-events: none;
  }
</style>
