<!-- app.vue -->
<template>

<RouterView>

<div id="app">
    
   
  <Header></Header>

    
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
