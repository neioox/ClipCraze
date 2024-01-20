import { createApp } from 'vue'
import App from './App.vue'

import { createRouter, createWebHashHistory } from "vue-router"
import SettingsView from '@/views/SettingsView.vue';

import Home from "@/Home.vue"



const app = createApp(App)




const routes = [
    { path: "/", component: Home , name: "Home"},
    { path: "/clip-settings/:id", component: SettingsView, name: "SettingsView" , props: true }
]


const router = createRouter({
    history: createWebHashHistory(),
    routes: routes,
    linkActiveClass: "active"
})

app.use(router)
app.mount('#app')



//to use the router in antother file to push
export default router;