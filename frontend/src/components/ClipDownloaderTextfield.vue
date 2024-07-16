<template>
    <div>
      <input v-model="url" type="text" placeholder="Enter URL here"   />
      <button @click="download">Download</button>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  
  export default {
    setup() {
      const url = ref('');  // Reactive reference for the URL
      const loadingState = ref(false);  // Proper camelCase naming
  
      async function download() {
        if (!url.value) {
          alert("Please enter a URL before downloading.");  // User feedback
          return;
        }
  
        loadingState.value = true;  // Indicate that the download process has started
        try {
          const userId = localStorage.getItem('userid');  // Retrieve user ID from local storage
          const formData = new FormData();
          formData.append('userid', userId);
          formData.append('url', url.value);
  
          console.log(`Starting download for user ${userId} from URL: ${url.value}`);
  
          const response = await fetch("http://localhost:8080/api/twitch/downloadclip", {
            method: 'POST',
            body: formData,
          });
  
          if (!response.ok) throw new Error('Failed to fetch clips');  // Error handling for non-2xx responses
  
          const data = await response.json();
          console.log('Download response:', data);
  
          // Implement what should happen with the data here (e.g., display a message, handle the data)
        } catch (error) {
          console.error("Error fetching clips:", error);
          alert("Error during download: " + error.message);  // User feedback
        } finally {
          loadingState.value = false;  // Reset loading state irrespective of success or failure
        }
      }
  
      return { url, download };
    }
  };
  </script>
  