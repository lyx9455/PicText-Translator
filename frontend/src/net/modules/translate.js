// src/net/modules/translate.js
import {post} from "../http.js";

/**
 * 文本翻译接口
 * @param {Object} payload - 请求参数
 * @param {string} payload.text - 待翻译文本
 * @param {string} payload.sourceLanguage - 源语言，例如 'zh'
 * @param {string} payload.targetLanguage - 目标语言，例如 'en'
 * @param {string} [payload.formatType='text'] - 文本格式，可选
 * @param {string} [payload.scene='general'] - 场景，可选，例如 'general' 或 'e-commerce'
 * @param {string} [payload.context] - 上下文信息，可选
 * @returns {Promise<Object>} 返回后端 TranslateVO 对象
 */
async function text(payload) {
    return await post("/api/translate/text", payload);
}

/**
 * 图片翻译接口
 * @param {Object} payload - 请求参数
 * @param {string} payload.imageBase64 - 图片 Base64，必填
 * @param {string} payload.sourceLanguage - 源语言
 * @param {string} payload.targetLanguage - 目标语言
 * @param {string} [payload.field='general'] - 翻译领域，可选 'general' 或 'e-commerce'
 * @param {string} [payload.ext] - 扩展信息 JSON 字符串，可选
 * @returns {Promise<Object>} 返回图片翻译结果 Data 对象
 * {
 *   "RequestId": "D774D33D-F1CB-5A2C-A787-E0A2179239CE",
 *   "Code": 200,
 *   "Message": "Error Message",
 *   "Data": {
 *     "InPaintingUrl": "https://example.com/example.jpg",
 *     "TemplateJson": "Editor Template Json String",
 *     "FinalImageUrl": "https://example.com/example.jpg"
 *   }
 * }
 */
async function image(payload) {
    return await post("/api/translate/image", payload);
}

export const translate = {
    text,
    image
};
