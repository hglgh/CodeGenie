<template>
  <a-layout class="basic-layout">
    <!-- 头部导航 -->
    <a-layout-header class="header">
      <GlobalHeader />
    </a-layout-header>

    <!-- 内容区域 -->
    <a-layout-content class="content">
      <router-view />
    </a-layout-content>

    <!-- 底部版权 -->
    <a-layout-footer class="footer">
      <GlobalFooter />
    </a-layout-footer>
  </a-layout>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(
    135deg,
    #fefefe 0%,
    #f8faf8 20%,
    #f0f6f0 40%,
    #e8f2e8 60%,
    #e0ede0 80%,
    #d8e8d8 100%
  );
  position: relative;
}

.basic-layout::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.5) 0%, transparent 70%),
    radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.3) 0%, transparent 80%),
    radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.2) 0%, transparent 90%);
  pointer-events: none;
  z-index: 0;
}

.header {
  padding: 0;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(20px);
  box-shadow: 0 1px 3px rgba(200, 230, 201, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  position: relative;
}

.content {
  flex: 1;
  padding: 24px;
  min-height: calc(100vh - 64px - 70px); /* 减去header和footer高度 */
  position: relative;
  z-index: 1;
}

.footer {
  padding: 0;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 -1px 3px rgba(200, 230, 201, 0.15);
  position: relative;
  z-index: 1;
}

/* 内容区域的子元素样式 */
.content :deep(main) {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(77, 208, 225, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.4);
  position: relative;
  z-index: 1;
}

.content :deep(.about) {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(15px);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(77, 208, 225, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.4);
  position: relative;
  z-index: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content {
    padding: 16px;
  }

  .content :deep(main),
  .content :deep(.about) {
    padding: 24px;
    border-radius: 12px;
  }
}

@media (max-width: 576px) {
  .content {
    padding: 12px;
  }

  .content :deep(main),
  .content :deep(.about) {
    padding: 20px;
    border-radius: 8px;
  }
}
</style>
