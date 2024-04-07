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
import router from './main.js'


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
