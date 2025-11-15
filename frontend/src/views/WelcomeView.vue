<script setup>

</script>

<template>
  <div class="welcome-container">
    <div class="left-banner">
      <!--      <el-image style="width: 100%; height: 100%;" fit="cover" -->
      <!--                src="http://i1.hdslb.com/bfs/archive/60f35dd1d23bea22e317500bbcf9406ebf8f31c3.jpg" referrer-policy="no-referrer">-->
      <!--      </el-image>-->
      <img src="http://i1.hdslb.com/bfs/archive/60f35dd1d23bea22e317500bbcf9406ebf8f31c3.jpg"
           alt="背景"
           class="background-image"
           referrerpolicy="no-referrer"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">图灵译 · AI智能图文翻译平台</div>
      <div style="margin-top: 10px">让语言的界限，不再成为沟通的障碍</div>
      <div style="margin-top: 5px">基于SpringBoot3+Vue3构建，集成智能文本与图像翻译功能，为学习者与开发者提供高效的语言理解与协作工具。
      </div>
    </div>
    <div class="right-card">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<style scoped>
.welcome-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  overflow: hidden;
  background-color: var(--el-bg-color);
}

.left-banner {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.background-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}

.right-card {
  width: 400px;
  z-index: 1;
  background-color: var(--el-bg-color);
}
</style>