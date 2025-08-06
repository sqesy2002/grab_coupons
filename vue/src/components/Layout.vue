<template>
    <div class="app-layout">
        <!-- 顶部栏 -->
        <header class="header">
            <div class="logo">抢券系统</div>
        </header>

        <div class="main-container">
            <!-- 左侧导航栏 -->
            <aside class="sidebar">
                <nav>
                    <ul>
                        <li>
                            <router-link to="/user">用户信息</router-link>
                        </li>
                        <li>
                            <router-link to="/coupon">券种类信息</router-link>
                        </li>
                        <li>
                            <router-link to="/grab">开始抢券</router-link>
                        </li>
                    </ul>
                </nav>
            </aside>

            <!-- 右侧主内容区 -->
            <main class="content">
                <!-- 路由匹配到的组件将渲染在这里 -->
                <router-view />
            </main>
        </div>
    </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router';
</script>

<style scoped>
.app-layout {
    display: flex;
    flex-direction: column;
    height: 100vh;
    background-color: #f0f2f5;
}

.header {
    height: 60px;
    background-color: #ffffff;
    box-shadow: 0 2px 8px #f0f1f2;
    display: flex;
    align-items: center;
    padding: 0 20px;
    z-index: 10;
}

.logo {
    font-size: 20px;
    font-weight: 600;
    color: #333;
}

.main-container {
    display: flex;
    flex: 1;
    overflow: hidden; /* 防止内容溢出 */
}

.sidebar {
    width: 200px;
    background-color: #ffffff;
    padding-top: 20px;
    box-shadow: 2px 0 8px #f0f1f2;
}

.sidebar nav ul {
    list-style: none;
    padding: 0;
    margin: 0;
}

.sidebar nav li a {
    display: block;
    padding: 12px 20px;
    text-decoration: none;
    color: #333;
    transition: background-color 0.3s;
}

.sidebar nav li a:hover {
    background-color: #e6f7ff;
}

/* vue-router提供的激活状态class */
.sidebar nav li a.router-link-active {
    background-color: #1890ff;
    color: #ffffff;
}

.content {
    flex: 1;
    padding: 20px;
    overflow-y: auto; /* 如果内容超长，则允许滚动 */
}
</style>