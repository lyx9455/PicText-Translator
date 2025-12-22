// main.js
import {createApp} from 'vue'
import App from './App.vue'
import router from "@/router/index.js";
import axios from "axios";
import 'element-plus/theme-chalk/dark/css-vars.css'

axios.defaults.baseURL = '/api'

const app = createApp(App)

app.use(router)

app.mount('#app')
