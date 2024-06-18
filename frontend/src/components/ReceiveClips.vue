<script setup>
import { ref } from 'vue';
import SubtitleGenerator from '@/utils/SubtitleGenerator';

// Corrected the casing to 'LoadingState' consistently
const LoadingState = ref(false)






  


const subtitleGenerator = new SubtitleGenerator();

async function handleGenerateSubtitles(event) {
 
  let clips = localStorage.getItem("clips")
  
  clips = clips.split(", ")
  console.log(clips);
  
      if (clips.length <= 0 ) {
        
      

      for (const clip of clips) {
    try {
      LoadingState.value = true;  // Correct casing
      await subtitleGenerator.generateSubtitles(event, clip);
    
    
    
    } catch (error) {
      console.error("Error in generating subtitles:", error);
    } finally {
      LoadingState.value = false;  // Correct casing
    }
  }
  }
}

//Download all clips of the last 7 days 
async function receiveClips() {
  try {
    LoadingState.value = true;  // Correct casing

    const userid = localStorage.getItem('userid');
    const formData = new FormData();
    formData.append('id', userid);

    console.log(userid);

    const response = await fetch("http://localhost:8080/api/requestClips", {
      method: 'POST',
      body: formData,
    });

    const data = await response.json();
    console.log(data);
    LoadingState.value = false;  // Correct casing
  } catch (error) {
    console.error("Error fetching clips:", error);
    LoadingState.value = false;  // Correct casing
  }
}

</script>

<template>
  <div v-if="!LoadingState">  <!-- Corrected condition -->
    <button @click="receiveClips">Receive new Clips</button>
    <button @click="handleGenerateSubtitles">Convert ALL for TikTok</button>
  </div>
  <p v-else>Loading Clips...</p>
</template>
