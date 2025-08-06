import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    // 前端开发服务器端口，这里假设是 5173
    port: 5173,
    proxy: {
      // '/api' 是一个自定义的代理前缀
      // 当你的请求路径以 /api 开头时，Vite会将其代理到 target 指定的地址
      '/api': {
        // 这是你的后端服务器地址
        target: 'http://localhost:9090',
        // 关键配置：将请求头中的 Origin 修改为目标地址，以解决跨域问题
        changeOrigin: true,
        // 可选配置：重写请求路径，去掉 /api 前缀
        // 例如，前端请求 /api/user/page 会被转发到 http://localhost:9090/user/page
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
