<script setup>
;import { ref } from 'vue';

const LoadingState = ref(false)


//Download all clips of the last 7 days 

async function receiveClips() {
  try {
    LoadingState.value = true;

    const response = await fetch("http://localhost:8080/api/requestClips");
    const data = await response;
    // Handle the response data as needed
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
