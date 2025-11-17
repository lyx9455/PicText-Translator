// src/net/modules/auth.js
import { post, get, storeAccessToken, deleteAccessToken } from "../http.js";
import { ElMessage } from "element-plus";

/**
 * login payload: { username, password, remember }
 * returns: body (the data object returned by backend)
 */
async function login({ username, password, remember = false }) {
    const body = await post("/api/auth/login", { username, password });
    // backend returns { token, expire, username, ... } in body
    storeAccessToken(remember, body.token, body.expire);
    ElMessage.success(`登录成功，欢迎 ${body.username} 来到我们的系统`);
    return body;
}

async function register({ username, password, email, code }) {
    const body = await post("/api/auth/register", { username, password, email, code });
    ElMessage.success("注册成功，欢迎加入我们");
    return body;
}

async function askCode({ email, type = "register" }) {
    // GET /api/auth/ask-code?email=xxx&type=register|reset
    const url = `/api/auth/ask-code?email=${encodeURIComponent(email)}&type=${encodeURIComponent(type)}`;
    await get(url);
    ElMessage.success(`验证码已发送到邮箱: ${email}`);
}

async function resetConfirm({ email, code }) {
    await post("/api/auth/reset-confirm", { email, code });
    ElMessage.success("验证码验证通过");
}

async function resetPassword({ email, code, password }) {
    await post("/api/auth/reset-password", { email, code, password });
    ElMessage.success("密码重置成功，请重新登录");
}

async function logout() {
    try {
        await get("/api/auth/logout");
    } finally {
        // 即便请求失败，也清本地状态
        deleteAccessToken();
    }
}

export const auth = {
    login,
    register,
    askCode,
    resetConfirm,
    resetPassword,
    logout
};
