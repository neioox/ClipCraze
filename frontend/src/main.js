import { createApp } from 'vue'
import App from './App.vue'

import { createRouter, createWebHashHistory } from "vue-router"
import SettingsView from '@/views/SettingsView.vue';
import Login from './views/Login.vue';
import Home from "@/Home.vue"
import './styles/app.css';
import Register from './views/Register.vue';
import Dashboard from './views/Dashboard.vue'

const app = createApp(App)




const routes = [
    { path: "/", component: Home , name: "Home"},
    { path: "/clip-settings/:id", component: SettingsView, name: "SettingsView" , props: true },
    { path: "/Login", component: Login, name:"Login"}, 
    {path: "/Register", component: Register, name: "Register"},
    {path: "/Dashboard", component: Dashboard, name: "Dashboard"}
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