<script setup>
import { ref } from 'vue';

const LoadingState = ref(false)


//Download all clips of the last 7 days 

async function receiveClips() {
  try {
    LoadingState.value = true;

    // Get the formParam id from local storage
        const userid = localStorage.getItem('userid');




        const formData = new FormData();
        formData.append('id', userid);

        console.log(userid)


    const response = await fetch("http://localhost:8080/api/requestClips", {
      method: 'POST',
      body: formData, 
    });

    const data = await response.json(); 
    console.log(data);
    LoadingState.value = false;
  } catch (error) {
    console.error("Error fetching clips:", error);
    LoadingState.value = false;
  }
}

</script>

<template>
  <button v-if="LoadingState == false" @click="receiveClips">Receive new Clips</button>
  <p v-else>Loading Clips...</p>
</template>
