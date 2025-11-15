<template>
  <div class="index-container">
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <div class="logo" @click="toggleMenu">
        <el-icon v-if="isCollapse"><Menu /></el-icon>
        <span v-else>图灵译</span>
      </div>

      <el-menu :default-active="active" :collapse="isCollapse" @select="onSelect">
        <el-menu-item index="text-translate">
          <el-icon><Edit /></el-icon><span>文本翻译</span>
        </el-menu-item>
        <el-menu-item index="image-translate">
          <el-icon><Picture /></el-icon><span>图像翻译</span>
        </el-menu-item>
      </el-menu>

      <div class="user-section">
        <el-dropdown trigger="click">
          <span class="user-info">
            <el-avatar size="small" src="https://api.dicebear.com/7.x/identicon/svg?seed=TuringAI" />
            <span v-if="!isCollapse" class="username">{{ username }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="toggleDarkMode">
                {{ isDark ? '切换到浅色' : '切换到深色' }}
              </el-dropdown-item>
              <el-dropdown-item divided @click="userLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </aside>

    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { getUsername, logout } from "@/net/index.js";
import { useDark, useToggle } from "@vueuse/core";
import router from "@/router/index.js";
import { Edit, Picture, Menu } from "@element-plus/icons-vue";

const active = ref("text-translate");
const username = getUsername();
const isCollapse = ref(false);
const isDark = useDark();
const toggleDark = useToggle(isDark);

function onSelect(index) {
  active.value = index;
  router.push(`/index/${index}`);
}

function toggleMenu() {
  isCollapse.value = !isCollapse.value;
}

function toggleDarkMode() {
  toggleDark();
}

function userLogout() {
  logout(() => router.push("/"));
}
</script>


<style scoped>
.index-container {
  display: flex;
  height: 100vh;
  background: var(--el-bg-color);
}

.sidebar {
  width: 200px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color);
  transition: width 0.25s ease;
}
.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  cursor: pointer;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.el-menu {
  flex: 1;
  border-right: none;
  background: transparent !important;
  padding-top: 10px;
}

.el-menu-item {
  border-radius: 10px;
  height: 48px;
}
.el-menu-item span {
  margin-left: 10px;
  font-size: 14px;
}
.el-menu-item.is-active {
  background: var(--el-fill-color-light) !important;
}
.el-menu-item:hover {
  background: var(--el-fill-color-lighter);
}

.user-section {
  border-top: 1px solid var(--el-border-color-lighter);
  padding: 12px;
  display: flex;
  justify-content: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}
.username {
  font-size: 13px;
  font-weight: 600;
}
.sidebar.collapsed .username {
  display: none;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: var(--el-bg-color);
}
</style>
