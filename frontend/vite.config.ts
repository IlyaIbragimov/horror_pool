import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const backendUrl = process.env.VITE_DEV_BACKEND_URL ?? 'http://localhost:8080';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/horrorpool': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
    },
  },
});