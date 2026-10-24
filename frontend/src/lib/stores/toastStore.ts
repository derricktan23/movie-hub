import { writable } from 'svelte/store'
import type { Toast, ToastType } from '../types/toast'

function createToastStore() {
  const { subscribe, update } = writable<Toast[]>([])

  function show(message: string, type: ToastType = 'info', title?: string, duration = 4000) {
    const id = Math.random().toString(36).substring(2, 9)
    const toast: Toast = { id, message, type, title, duration }

    update((items) => [...items, toast])

    if (duration > 0) {
      setTimeout(() => {
        dismiss(id)
      }, duration)
    }
  }

  function dismiss(id: string) {
    update((items) => items.filter((t) => t.id !== id))
  }

  return {
    subscribe,
    show,
    dismiss,
    success: (msg: string, title?: string) => show(msg, 'success', title || 'Success'),
    error: (msg: string, title?: string) => show(msg, 'error', title || 'Error', 6000),
    info: (msg: string, title?: string) => show(msg, 'info', title || 'Information'),
    warning: (msg: string, title?: string) => show(msg, 'warning', title || 'Warning', 5000)
  }
}

export const toastStore = createToastStore()
