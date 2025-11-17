// src/net/modules/translate.js
import { post } from "../http.js";

/**
 * textPayload: {
 *   text, sourceLanguage, targetLanguage,
 *   formatType = 'text', scene = 'general', context?
 * }
 */
async function text(payload) {
    const body = await post("/api/translate/text", payload);
    // backend returns TranslateVO in body
    return body;
}

export const translate = {
    text
};
