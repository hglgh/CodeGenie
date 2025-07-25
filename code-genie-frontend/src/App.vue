<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
</script>

<template>
  <BasicLayout />
</template>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body {
  height: 100%;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #f0f8ff;
}

#app {
  height: 100%;
}

/* 全局文字在绿蓝渐变背景上的优化 */
h1,
h2,
h3,
h4,
h5,
h6 {
  color: #00695c;
  font-weight: 600;
}

p {
  color: #37474f;
  line-height: 1.6;
}

/* 链接样式优化 */
a {
  color: #00838f;
  transition: color 0.3s ease;
}

a:hover {
  color: #0097a7;
}
</style>
