<!-- app.vue -->
<template>

<RouterView>

<div id="app">
    
   
  <Header></Header>

  <div class="flex flex-col items-center mt-10">
    <img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Smilies/Robot.png" alt="Robot" width="100" height="100" />
    <h1 class="font-bold bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 bg-clip-text text-transparent p-6 text-7xl">ClipCraze</h1>
  </div>
    <ReceiveClips></ReceiveClips>
    <ClipsList></ClipsList>
    
     <router-view></router-view>

    
  </div>

  </RouterView>
</template>

<script setup>
// Import your components
import ClipsList from './components/ClipsList.vue';
import ReceiveClips from './components/ReceiveClips.vue';
import Login from './views/Login.vue';
import router from './main.js'
import Header from './components/Header.vue';

if (localStorage.getItem("username") || localStorage.getItem("token") ) {

const token = localStorage.getItem("token");

var formData = new FormData();
formData.append('token', token);


fetch(`http://localhost:8080/api/checkToken`, {
method: 'POST',
body: formData
})
.then(function (response) {
  return response.json();
})
.then(async function (data) {

 
 
    if(data.response === "Token ist ungültig"){
   
  try {


    if (!window.location.href.includes("Login") || !window.location.href.includes("Register")) {
      
      await router.push({ name: 'Login' });
    }
  } catch (error) {
    console.error(error);
  }
  }

})
.catch(function (error) {
  console.error('Error:', error);
});

 }



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
