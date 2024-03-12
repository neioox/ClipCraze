<template>
    <div class="container mx-auto mt-5">
      <form class="max-w-md mx-auto bg-white p-8 rounded shadow-md">
        <div class="mb-4">
          <label for="username" class="block text-gray-700 text-sm font-bold mb-2">Username</label>
          <input
            type="text"
            id="username"
            placeholder="Enter username"
            class="w-full px-3 py-2 border rounded focus:outline-none focus:shadow-outline"
          />
        </div>
        <div class="mb-4">
          <label for="email" class="block text-gray-700 text-sm font-bold mb-2">Email</label>
          <input
            type="email"
            id="email"
            placeholder="Enter email"
            class="w-full px-3 py-2 border rounded focus:outline-none focus:shadow-outline"
          />
        </div>
        <div class="mb-6">
          <label for="password" class="block text-gray-700 text-sm font-bold mb-2">Password</label>
          <input
            type="password"
            id="password"
            placeholder="Enter password"
            class="w-full px-3 py-2 border rounded focus:outline-none focus:shadow-outline"
          />
        </div>
        <button
          type="submit"
          class="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        >
          Register
        </button>
      </form>
    </div>
  </template>
  

<script>

window.onload = function() {
  document.querySelector('form').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent the form from submitting

    // Get the form values
    var username = document.getElementById('username').value;
    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;

    var formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);
    formData.append('group', 'user');

    fetch(`http://localhost:8080/api/addUser/`, {
    method: 'POST',
     body: formData
    
    })
    .then(function(response) {
      return response.json();
    })
    .then(function(data) {
      console.log(data); // Log the API response
    })
    .catch(function(error) {
      console.error('Error:', error);
    });
  });
};


</script>