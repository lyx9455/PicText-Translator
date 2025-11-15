<template>
  <div class="text-translate-container">
    <el-card class="translate-card">
      <h2>文本翻译</h2>

      <div class="translate-wrapper">
        <!-- 左侧源语言 -->
        <div class="translate-col">
          <div class="col-header">{{ sourceLangLabel }}</div>
          <el-input
              type="textarea"
              v-model="sourceText"
              placeholder="请输入文本"
              rows="10"
          ></el-input>
        </div>

        <!-- 中间切换按钮 -->
        <div class="translate-switch">
          <el-button icon="ArrowRight" circle @click="switchLanguage"></el-button>
        </div>

        <!-- 右侧目标语言 -->
        <div class="translate-col">
          <div class="col-header">{{ targetLangLabel }}</div>
          <el-input
              type="textarea"
              :value="translatedText"
              rows="10"
              readonly
          ></el-input>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

// 数据
const sourceText = ref('')
const translatedText = ref('')
const isChineseToEnglish = ref(true)

const sourceLangLabel = computed(() => (isChineseToEnglish.value ? '中文' : '英文'))
const targetLangLabel = computed(() => (isChineseToEnglish.value ? '英文' : '中文'))

// 切换方向
const switchLanguage = () => {
  isChineseToEnglish.value = !isChineseToEnglish.value
  const temp = sourceText.value
  sourceText.value = translatedText.value
  translatedText.value = temp
}

// 模拟翻译防抖
let timer = null
watch(sourceText, (newVal) => {
  clearTimeout(timer)
  timer = setTimeout(() => {
    if (!newVal.trim()) {
      translatedText.value = ''
      return
    }
    // 模拟翻译
    translatedText.value = isChineseToEnglish.value
        ? `[EN] ${newVal}`
        : `[ZH] ${newVal}`
  }, 500) // 500ms 防抖
})
</script>

<style scoped>
.text-translate-container {
  padding: 20px;
}

.translate-card {
  max-width: 900px;
  margin: 0 auto;
}

.translate-card h2 {
  margin-bottom: 20px;
  font-weight: 600;
}

.translate-wrapper {
  display: flex;
  gap: 16px;
  align-items: center;
}

.translate-col {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.col-header {
  font-weight: 500;
  margin-bottom: 8px;
}

.translate-switch {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
