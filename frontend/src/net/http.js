// src/net/http.js
import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router/index.js";

const authItemName = "authorize";

/* ---------------- Token 工具 ---------------- */
function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
    if (!str) return null;

    const authObj = JSON.parse(str);
    if (new Date(authObj.expire) <= new Date()) {
        deleteAccessToken();
        ElMessage.warning("登录状态已过期，请重新登录！");
        return null;
    }
    return authObj.token;
}

function storeAccessToken(remember, token, expire) {
    const authObj = { token, expire };
    const str = JSON.stringify(authObj);
    if (remember) localStorage.setItem(authItemName, str);
    else sessionStorage.setItem(authItemName, str);
}

function deleteAccessToken(redirect = false) {
    localStorage.removeItem(authItemName);
    sessionStorage.removeItem(authItemName);
    if (redirect) router.push({ name: "welcome-login" });
}

/* ---------------- axios 实例 ---------------- */
const instance = axios.create({
    timeout: 10000
});

/* 请求拦截器 - 自动注入 Authorization */
instance.interceptors.request.use(config => {
    const token = takeAccessToken();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

/* 响应拦截器 - 统一解包 data.data，处理 401/429 */
instance.interceptors.response.use(
    res => {
        const data = res.data;

        // 成功
        if (data?.code === 200) {
            return data.data;
        }

        // 401：区分 登录失败 vs 登录过期
        if (data?.code === 401) {
            const token = takeAccessToken();

            if (token) {
                // 已登录状态下的 401 → 登录过期
                ElMessage.warning("登录状态已过期，请重新登录！");
                deleteAccessToken(true);
            } else {
                // 未登录状态下的 401 → 登录失败
                ElMessage.error(data?.message || "用户名或密码错误");
            }

            return Promise.reject(new Error("UNAUTHORIZED"));
        }

        // 其他业务错误
        ElMessage.warning(data?.message || "请求失败");
        return Promise.reject(new Error(data?.message || "REQUEST_ERROR"));
    },
    err => {
        const status = err.response?.status;

        if (status === 429) {
            ElMessage.error(err.response?.data?.message || "请求过于频繁");
        } else {
            ElMessage.error(err.response?.data?.message || "发生了一些错误");
        }

        return Promise.reject(err);
    }
);

/* ---------------- 便捷方法 ---------------- */
async function post(url, data) {
    return instance.post(url, data);
}

async function get(url) {
    return instance.get(url);
}

/* ---------------- JWT / 用户工具 ---------------- */
function parseJwt(token) {
    try {
        const base64Url = token.split(".")[1];
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split("")
                .map(c => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
                .join("")
        );
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Invalid JWT Token:", e);
        return null;
    }
}

function getUsername() {
    const token = takeAccessToken();
    if (!token) return null;
    const payload = parseJwt(token);
    return payload?.name || null;
}

export {
    instance,
    post,
    get,
    takeAccessToken,
    storeAccessToken,
    deleteAccessToken,
    getUsername
};
