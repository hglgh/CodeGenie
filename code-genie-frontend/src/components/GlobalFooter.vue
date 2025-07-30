<template>
  <div class="global-footer">
    <div class="footer-content">
      <span>© 2025 程序员</span>
      <a
        href="https://github.com/hglgh"
        target="_blank"
        rel="noopener noreferrer"
        class="author-link"
      >
        扈江离
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
// 无需额外逻辑
</script>

<style scoped>
.global-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 70px;
  background: transparent;
}

.footer-content {
  color: #00695c;
  font-size: 14px;
  text-align: center;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.author-link {
  color: #004d40;
  text-decoration: none;
  margin-left: 4px;
  transition: all 0.3s;
  font-weight: 600;
}

.author-link:hover {
  color: #00332a;
  text-decoration: underline;
  text-shadow: 0 1px 3px rgba(255, 255, 255, 0.9);
}

/* 响应式设计 */
@media (max-width: 576px) {
  .footer-content {
    font-size: 12px;
  }
}
</style>
