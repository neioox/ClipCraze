<!-- app.vue -->
<template>

<RouterView>

<div id="app">
    <h1 class="font-bold , drop-shadow-sm, text-5xl" >CLIPCRAZE</h1>
    <ClipsList></ClipsList>
    <ReceiveClips></ReceiveClips>
     <router-view></router-view>

    
  </div>

  </RouterView>
</template>

<script setup>
// Import your components
import ClipsList from './components/ClipsList.vue';
import ReceiveClips from './components/ReceiveClips.vue';
import Login from './views/Login.vue';


</script>

<script>

export default {
  components: {
    ReceiveClips,
    ClipsList,
    Login
    
  }
};
</script>

<style>
#app {
  font-family: 'Avenir', 'Helvetica', 'Arial', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

button {
  border: none;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  background-color: #59ce8f;
  color: #ffffff;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #4ea87f;
}
</style>
