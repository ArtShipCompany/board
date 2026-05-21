import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],

  define: {
    global: 'globalThis',
  },

  server: {
    port: 5173,
    host: '0.0.0.0',

    proxy: {
      '/api': {
        target: 'http://backend:8081',
        changeOrigin: true,
        secure: false,
      },

      '/ws': {
        target: 'http://backend:8081',
        changeOrigin: true,
        secure: false,
        ws: true,
      },
    },
  },
});