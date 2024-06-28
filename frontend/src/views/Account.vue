<template>
    <div>
      <Header></Header>
      <div v-if="userLoggedin">
        <h1 class="font-bold bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 bg-clip-text text-transparent p-6 text-7xl">Account</h1>
        <h1 class="text-5xl bg-violet-400 py-2 px-4 rounded focus:outline-none text-white focus:shadow-outline">
          username: {{ userName }}
        </h1>
        <button @click="logout" class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-full focus:outline-none focus:shadow-outline">
          Logout
        </button>
      </div>
  
      <div v-else>
        <h1 class="font-bold bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 bg-clip-text text-transparent p-6 text-7xl">You are not Loggedin</h1>

        <a href="#register" class="bg-violet-500 hover:bg-violet-700 text-white font-bold py-2 px-4 rounded-full focus:outline-none focus:shadow-outline">
          Register
        </a>
        <a href="#login" class="bg-violet-500 hover:bg-violet-700 text-white font-bold py-2 px-4 rounded-full focus:outline-none focus:shadow-outline">
          Login
        </a>
      </div>
    </div>
  </template>
  
  <script>
  import Header from '@/components/Header.vue';
  
  export default {
    components: {
      Header,
    },
    data() {
      return {
        userLoggedin: false,
        userName: "",
      };
    },
    created() {
      this.checkUserToken();
    },
    methods: {
      checkUserToken() {
        var userToken = localStorage.getItem("token");
        if (userToken) {
          this.userLoggedin = true;
          this.userName = localStorage.getItem("username");
        }
      },
      logout() {
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        this.userLoggedin = false;
        this.userName = "";
      },
    },
  };
  </script>
  