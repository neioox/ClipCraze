<template>
  <div>
    <h2>Finsihed Clips</h2>

    


    <div v-for="(clip, index) in clips" :key="index">
      <video class="w-500 h-300 rounded-lg" :src="'http://localhost:8080/api/getclips/' + clip" controls></video>



    </div>
  </div>
</template>

<script>

import { onMounted } from 'vue';  // Import necessary lifecycle hook from 'vue'

export default {
  data() {
    return {
      clips: []  // Using 'clips' to store data
    }
  },

  methods: {
    async getClipsAssignedToUser() {
      if (this.clips.length === 0) {
        const userId = localStorage.getItem("userid"); // Handle potential null values appropriately
        if (!userId) {
          console.error('User ID is not available');
          return; // Early return if user ID is not found
        }



        try {
          const response = await fetch(`http://localhost:8080/api/get/user/${userId}/Clips/Finished`);
          const clipList = await response.json();
          
        
          clipList.forEach((clip => 
        

            this.clips.push(clip)

        ));          

          console.log(clipList)
        } catch (error) {
          console.error("Error fetching clips:", error);
        }
      }
    }
  },

  mounted() {
    this.getClipsAssignedToUser();  // Use this to reference component instance methods
  }
}
</script>
