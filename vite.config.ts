import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
    root: path.resolve(__dirname, 'frontend'),
    build: {
        outDir: path.resolve(__dirname, 'frontend/dist'),
    },
    plugins: [react()],
    server: {
        port: 3000,
        proxy: {
            '/api': {
                target: 'http://localhost:8080/',
                changeOrigin: true,
                secure: false,
            }
        }
    }
})