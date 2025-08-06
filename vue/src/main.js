import { createApp } from 'vue'
import App from './App.vue'
// 1. 引入你创建的 router
import router from './router'

const app = createApp(App)

// 2. 在挂载应用前，告诉Vue应用实例使用路由
app.use(router)

app.mount('#app')
