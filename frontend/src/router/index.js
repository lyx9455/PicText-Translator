import { createRouter, createWebHistory } from 'vue-router'
import { unauthorized } from "@/net/index.js";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/Login.vue')
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/views/welcome/Register.vue')
                },
                {
                    path: 'forget',
                    name: 'welcome-forget',
                    component: () => import('@/views/welcome/Forget.vue')
                }
            ]
        },
        {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue'),
            redirect: '/index/text-translate', // 默认进入文本翻译页
            children: [
                {
                    path: 'text-translate',
                    name: 'text-translate',
                    component: () => import('@/views/translate/TextTranslate.vue')
                },
                {
                    path: 'image-translate',
                    name: 'image-translate',
                    component: () => import('@/views/translate/ImageTranslate.vue')
                }
            ]
        }
    ]
})

// 登录状态守卫
router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorized()
    if (to.name?.startsWith('welcome') && !isUnauthorized) {
        next('/index')
    } else if (to.fullPath.startsWith('/index') && isUnauthorized) {
        next('/')
    } else {
        next()
    }
})

export default router
