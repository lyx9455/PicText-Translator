<template>
  <div class="text-translate-container">
    <el-card class="translate-card">
      <h2>文本翻译</h2>

      <div class="translate-wrapper">

        <!-- 左侧：源文本 -->
        <div class="translate-col">

          <el-select v-model="sourceLanguage" placeholder="选择源语言" class="lang-select">
            <el-option label="中文" value="zh" />
            <el-option label="英文" value="en" />
            <el-option label="日语" value="ja" />
            <el-option label="韩语" value="ko" />
          </el-select>

          <el-input
              type="textarea"
              v-model="sourceText"
              placeholder="请输入需要翻译的文本"
              rows="10"
          ></el-input>
        </div>

        <!-- 切换按钮 -->
        <div class="translate-switch">
          <el-button circle @click="switchLanguage">
            <el-icon><Switch /></el-icon>
          </el-button>
        </div>

        <!-- 右侧：翻译结果 -->
        <div class="translate-col">

          <el-select v-model="targetLanguage" placeholder="选择目标语言" class="lang-select">
            <el-option label="英文" value="en" />
            <el-option label="中文" value="zh" />
            <el-option label="日语" value="ja" />
            <el-option label="韩语" value="ko" />
          </el-select>

          <el-input
              type="textarea"
              :value="translatedText"
              rows="10"
              readonly
          ></el-input>

          <div v-if="loading" class="loading-text">正在翻译，请稍候...</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Switch } from '@element-plus/icons-vue'
import { ref, watch } from 'vue'
import { translate } from '@/net'

// 输入与输出文本
const sourceText = ref('')
const translatedText = ref('')

// 语言
const sourceLanguage = ref('zh')
const targetLanguage = ref('en')

// loading 状态
const loading = ref(false)

// 交换语言
const switchLanguage = () => {
  const tmpLang = sourceLanguage.value
  sourceLanguage.value = targetLanguage.value
  targetLanguage.value = tmpLang

  const tmpText = sourceText.value
  sourceText.value = translatedText.value
  translatedText.value = tmpText
}

// 防抖
let timer = null
watch(sourceText, () => {
  clearTimeout(timer)

  if (!sourceText.value.trim()) {
    translatedText.value = ''
    return
  }

  timer = setTimeout(() => {
    translateText()
  }, 400)
})

// 调用后端翻译
const translateText = async () => {
  loading.value = true
  try {
    const res = await translate.text({
      text: sourceText.value,
      sourceLanguage: sourceLanguage.value,
      targetLanguage: targetLanguage.value,
      formatType: 'text',
      scene: 'general'
    })
    translatedText.value = res.translatedText
  } catch (e) {
    // 错误已被拦截器提示，这里不用处理
  } finally {
    loading.value = false
  }
}
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

.translate-switch {
  display: flex;
  justify-content: center;
  align-items: center;
}

.lang-select {
  margin-bottom: 10px;
}

.loading-text {
  margin-top: 8px;
  font-size: 13px;
  color: #888;
}
</style>
