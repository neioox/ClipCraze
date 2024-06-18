<template>
  <div>
    <nav class="flex items-center justify-between flex-wrap bg-blue-500 p-6">
      <!-- Your navigation content -->
    </nav>
    <div class="flex items-center justify-center h-screen">
      <div class="w-full max-w-xs">
        <form class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4" @submit.prevent="login">
          <div class="mb-4">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="username">
              Username
            </label>
            <input
              v-model="username"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="username" type="text" placeholder="Enter your username" />
          </div>
          <div class="mb-6">
            <label class="block text-gray-700 text-sm font-bold mb-2" for="password">
              Password
            </label>
            <input
              v-model="password"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              id="password" type="password" placeholder="Enter your password" />
          </div>
          <div class="flex items-center justify-between">
            <button
              class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
              type="submit">
              Log In
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <back-button></back-button>
</template>

<script>

import backButton from '@/components/backButton.vue';
export default {
  data() {
    return {
      username: '',
      password: ''
    };
  },
  methods: {
    async login() {
      try {
        const formData = new FormData();
        formData.append('username', this.username);
        formData.append('password', this.password);
        
        const response = await fetch(`http://localhost:8080/api/validate/login`, {
          method: 'POST',
          body: formData
        });
        
        if (!response.ok) {
          throw new Error('Invalid credentials');
        }

        const data = await response.json();
        console.log(data)
        localStorage.setItem("username", this.username);
        localStorage.setItem("userid", data.userID);
        localStorage.setItem("token", data.token);
        
 
        this.$router.push({ name: 'Dashboard' });
      } catch (error) {
        console.error('Error:', error.message);
        // Handle error here
      }
    }
  },
  components: {
    'back-button': backButton
  },
};
</script>
