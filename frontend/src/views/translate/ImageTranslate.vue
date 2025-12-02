<template>
  <div class="chat-container">
    <!-- 聊天消息区：展示翻译后的图片 -->
    <div class="message-area">
      <div v-for="(m, idx) in messages" :key="idx" class="msg msg-ai">
        <img :src="m.url" :alt="`翻译结果图片 ${idx + 1}`" class="msg-img" />
      </div>
    </div>

    <!-- 语言选择栏 -->
    <div class="lang-select">
      <select v-model="sourceLanguage">
        <option value="zh">中文</option>
        <option value="en">英文</option>
        <option value="ja">日语</option>
      </select>
      <span>→</span>
      <select v-model="targetLanguage">
        <option value="zh">中文</option>
        <option value="en">英文</option>
        <option value="ja">日语</option>
      </select>
    </div>

    <!-- 底部输入栏 -->
    <div class="chat-input-bar">
      <!-- 附件图标按钮 -->
      <div class="icon-btn" @click="triggerFileInput" title="上传图片">📎</div>
      <input ref="fileInput" type="file" accept="image/*" hidden @change="onFileChange" />

      <!-- 输入区：用于粘贴、拖拽 和 预览草稿 -->
      <div
          class="input-area"
          @paste="onPaste"
          @dragover.prevent
          @drop.prevent="onDrop"
          @click="focusPaste"
      >
        <!-- 占位符 -->
        <div v-if="!draftPreview" class="placeholder">点击并 Ctrl+V 粘贴图片，或上传 / 拖拽</div>

        <!-- 草稿层 -->
        <div v-if="draftPreview" class="draft-wrap">
          <img :src="draftPreview" :alt="'待翻译图片预览'" class="draft-img" />
          <!-- 草稿右上角 × 删除 -->
          <div class="draft-close" @click.stop="removeDraft">×</div>
        </div>

        <textarea ref="pasteBox" class="hidden-paste"></textarea>
      </div>

      <!-- 翻译按钮 -->
      <button class="send-btn" :disabled="!draftBase64 || loading" @click="sendTranslate">
        {{ loading ? "翻译中…" : "翻译" }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { translate } from "@/net";

const messages = reactive([]);

const draftBase64 = ref("");
const draftPreview = ref("");
const loading = ref(false);

const sourceLanguage = ref("zh");
const targetLanguage = ref("en");

const fileInput = ref(null);
const pasteBox = ref(null);

/* ---- 工具函数 ---- */
function triggerFileInput() {
  fileInput.value && fileInput.value.click();
}
function focusPaste() {
  pasteBox.value && pasteBox.value.focus();
}
function fileToBase64(file) {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = () => {
      const dataUrl = reader.result.toString();
      resolve({
        base64: dataUrl.split(",")[1],
        dataUrl,
      });
    };
    reader.readAsDataURL(file);
  });
}

async function handleIncomingFile(file) {
  if (!file) return;
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning("文件不能超过 10MB");
    return;
  }
  const { base64, dataUrl } = await fileToBase64(file);
  draftBase64.value = base64;
  draftPreview.value = dataUrl;
}

async function onFileChange(e) {
  const file = e.target.files?.[0];
  e.target.value = "";
  await handleIncomingFile(file);
}

async function onDrop(e) {
  await handleIncomingFile(e.dataTransfer.files[0]);
}

async function onPaste(e) {
  for (const item of e.clipboardData.items) {
    if (item.type.startsWith("image/")) {
      const f = item.getAsFile();
      await handleIncomingFile(f);
      return;
    }
  }
}

/* 删除草稿 */
function removeDraft() {
  draftBase64.value = "";
  draftPreview.value = "";
}

/* 翻译并显示为聊天消息 */
async function sendTranslate() {
  if (!draftBase64.value) return;

  loading.value = true;

  try {
    const res = await translate.image({
      imageBase64: draftBase64.value,
      sourceLanguage: sourceLanguage.value,
      targetLanguage: targetLanguage.value,
      field: "general",
      ext: JSON.stringify({ needEditorData: "false" }),
    });

    const finalUrl = res?.finalImageUrl || res?.FinalImageUrl || res?.data?.finalImageUrl || "";
    if (!finalUrl) {
      ElMessage.warning("翻译返回为空");
      return;
    }

    messages.push({ url: finalUrl });
    removeDraft(); // 清空草稿
    ElMessage.success("翻译完成");
  } catch (err) {
    console.error(err);
    ElMessage.error(err?.message || "翻译失败");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 10px;
}

.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.msg-img {
  max-width: 360px;
  border-radius: 6px;
  border: 1px solid #ddd;
}

/* ---- 语言选择栏 ---- */
.lang-select {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 12px;
  font-size: 14px;
}

/* ---- 输入区 ---- */
.chat-input-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  border-top: 1px solid #eee;
  background: #fafafa;
}

.icon-btn {
  cursor: pointer;
  padding: 6px;
  font-size: 18px;
}

.input-area {
  flex: 1;
  min-height: 70px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  padding: 8px;
  background: #fff;
  position: relative;
  display: flex;
  align-items: center;
}

.placeholder {
  color: #909399;
  font-size: 14px;
}

.hidden-paste {
  opacity: 0;
  width: 1px;
  height: 1px;
  position: absolute;
}

/* 草稿预览图 */
.draft-wrap {
  position: relative;
}
.draft-img {
  max-width: 160px;
  border-radius: 6px;
  border: 1px solid #eee;
}
.draft-close {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ff4d4f;
  color: white;
  width: 18px;
  height: 18px;
  text-align: center;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  line-height: 18px;
}

.send-btn {
  background: #409eff;
  color: white;
  border: none;
  padding: 8px 14px;
  border-radius: 6px;
  cursor: pointer;
}
.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
