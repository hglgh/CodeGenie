<template>
  <div class="global-header">
    <!-- Logo和标题 -->
    <div class="logo-section">
      <img src="/logo.svg" alt="Logo" class="logo" />
      <span class="site-title">Code Genie</span>
    </div>

    <!-- 导航菜单 -->
    <a-menu
      v-model:selectedKeys="selectedKeys"
      mode="horizontal"
      class="nav-menu"
      :items="menuItems"
      @click="handleMenuClick"
    />

    <!-- 用户区域 -->
    <div class="user-section">
      <a-button type="primary" @click="handleLogin"> 登录 </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'

const router = useRouter()
const route = useRoute()

// 当前选中的菜单项
const selectedKeys = ref<string[]>([])

// 菜单配置
const menuItems = computed<MenuProps['items']>(() => [
  {
    key: '/',
    label: '首页',
    title: '首页',
  },
  {
    key: '/about',
    label: '关于',
    title: '关于',
  },
])

// 菜单点击处理
const handleMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}

// 登录按钮处理
const handleLogin = () => {
  console.log('登录功能待实现')
}

// 监听路由变化更新选中状态
import { watch } from 'vue'
watch(
  () => route.path,
  (newPath) => {
    selectedKeys.value = [newPath]
  },
  { immediate: true },
)
</script>

<style scoped>
.global-header {
  display: flex;
  align-items: center;
  height: 64px;
  padding: 0 24px;
  background: transparent;
}

.logo-section {
  display: flex;
  align-items: center;
  margin-right: 40px;
  flex-shrink: 0;
}

.logo {
  height: 32px;
  width: 32px;
  margin-right: 12px;
}

.site-title {
  font-size: 18px;
  font-weight: 600;
  color: #00695c;
  white-space: nowrap;
  text-shadow: 0 1px 3px rgba(255, 255, 255, 0.9);
}

.nav-menu {
  flex: 1;
  border-bottom: none;
  line-height: 64px;
  background: transparent;
}

.nav-menu :deep(.ant-menu-item) {
  color: #00695c;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

.nav-menu :deep(.ant-menu-item:hover) {
  color: #004d40;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 6px;
}

.nav-menu :deep(.ant-menu-item-selected) {
  color: #004d40;
  background: rgba(255, 255, 255, 0.35);
  border-bottom-color: #00695c;
  border-radius: 6px 6px 0 0;
}

.user-section {
  flex-shrink: 0;
  margin-left: 24px;
}

.user-section :deep(.ant-btn-primary) {
  background: rgba(102, 187, 106, 0.9);
  border-color: rgba(102, 187, 106, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(102, 187, 106, 0.3);
  color: white;
}

.user-section :deep(.ant-btn-primary:hover) {
  background: rgba(76, 175, 80, 0.95);
  border-color: rgba(76, 175, 80, 0.95);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .global-header {
    padding: 0 16px;
  }

  .logo-section {
    margin-right: 20px;
  }

  .site-title {
    display: none;
  }

  .user-section {
    margin-left: 16px;
  }
}

@media (max-width: 576px) {
  .global-header {
    padding: 0 12px;
  }

  .logo-section {
    margin-right: 12px;
  }

  .user-section {
    margin-left: 12px;
  }
}
</style>
