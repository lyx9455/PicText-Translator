<template>
  <div class="image-translate">
    <!-- 图片选择 -->
    <input type="file" @change="onFileChange" accept="image/*" />

    <!-- 语言选择 -->
    <div class="controls">
      <select v-model="sourceLanguage">
        <option value="zh">中文</option>
        <option value="en">英文</option>
        <option value="ja">日语</option>
      </select>
      <span>→</span>
      <select v-model="targetLanguage">
        <option value="en">英文</option>
        <option value="zh">中文</option>
        <option value="ja">日语</option>
      </select>
      <button @click="doTranslate" :disabled="loading">
        {{ loading ? "翻译中..." : "翻译图片" }}
      </button>
    </div>

    <!-- 原图预览 -->
    <div v-if="imageBase64">
      <h4>原图预览：</h4>
      <img :src="`data:image/png;base64,${imageBase64}`" alt="原图" style="max-width:300px;" />
    </div>

    <!-- 翻译结果 -->
    <div v-if="resultImageUrl">
      <h4>翻译结果：</h4>
      <img :src="resultImageUrl" alt="翻译结果" style="max-width:300px;" />
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { translate } from "@/net";

// 图片 Base64
const imageBase64 = ref("");
const resultImageUrl = ref("");

// 翻译语言
const sourceLanguage = ref("zh");
const targetLanguage = ref("en");

// loading 状态
const loading = ref(false);

// 选择图片
function onFileChange(e) {
  const file = e.target.files[0];
  if (!file) return;

  if (file.size >= 10 * 1024 * 1024) {
    ElMessage.warning("文件不能超过 10MB");
    return;
  }

  const reader = new FileReader();
  reader.onload = () => {
    imageBase64.value = reader.result.split(",")[1] || "";
  };
  reader.readAsDataURL(file);
}

// 翻译图片
async function doTranslate() {
  if (!imageBase64.value) {
    ElMessage.warning("请先选择图片");
    return;
  }

  loading.value = true;
  resultImageUrl.value = "";

  try {
    const res = await translate.image({
      imageBase64: imageBase64.value,
      sourceLanguage: sourceLanguage.value,
      targetLanguage: targetLanguage.value,
      field: "general",
      ext: JSON.stringify({ needEditorData: "false" })
    });
    resultImageUrl.value = res.finalImageUrl || "";

    if (resultImageUrl.value) {
      ElMessage.success("图片翻译完成");
    } else {
      ElMessage.warning("翻译返回结果为空");
    }
  } catch (err) {
    console.error(err);
    ElMessage.error(err.message || "图片翻译失败");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.image-translate {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.controls {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
