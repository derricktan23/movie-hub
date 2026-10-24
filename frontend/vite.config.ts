import { svelte } from '@sveltejs/vite-plugin-svelte'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [svelte()],
  server: {
    port: 5173,
    proxy: {
      '/api/movies': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/movies/, '')
      },
      '/api/ppl': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/ppl/, '')
      }
    }
  }
})

