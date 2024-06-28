<template>
  <div class="container mx-auto mt-5">
    <form @submit.prevent="registerUser" class="max-w-md mx-auto bg-white p-8 rounded shadow-md">
      <div class="mb-4">
        <label for="username" class="block text-gray-700 text-sm font-bold mb-2">Username</label>
        <input
          type="text"
          id="username"
          v-model="username"
          placeholder="Enter username"
          class="w-full px-3 py-2 border rounded focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-4">
        <label for="email" class="block text-gray-700 text-sm font-bold mb-2">Email</label>
        <input
          type="email"
          id="email"
          v-model="email"
          placeholder="Enter email"
          class="w-full px-3 py-2 border rounded focus:outline-none focus:shadow-outline"
        />
      </div>
      <div class="mb-6">
        <label for="password" class="block text-gray-700 text-sm font-bold mb-2">Password</label>
        <input
          type="password"
          id="password"
          v-model="password"
          placeholder="Enter password"
          class="w-full px-3 py-2 border rounded focus:outline-none focus:shadow-outline"
        />
      </div>
      <button
        type="submit"
       class="bg-purple-500 hover:bg-purple-700 text-white font-bold py-2 px-4 rounded-full focus:outline-none focus:shadow-outline"
      >
        Register
      </button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      email: '',
      password: ''
    };
  },
  methods: {
    async registerUser() {
      // Construct form data
      const formData = new FormData();
      formData.append('username', this.username);
      formData.append('password', this.password);
      formData.append('group', 'user');

      try {
        const response = await fetch(`http://localhost:8080/api/addUser/`, {
          method: 'POST',
          body: formData
        });

        const data = await response.json();
        console.log(data); // Log the API response

        // Navigate to Login page after successful registration
        this.$router.push({ name: 'Login' });
      } catch (error) {
        console.error('Error:', error);
      }
    }
  } 
};
</script>
