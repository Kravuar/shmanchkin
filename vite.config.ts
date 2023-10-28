import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
  resolve: {
    alias: {
      "@": "/frontend",
      "assets": "/frontend/assets",
    }
  },
  plugins: [react()],
})
