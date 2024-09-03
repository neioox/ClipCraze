<template>

  <button v-if="selectedClips.length > 0" @click="addClipstoQueue">Add clips to queue</button>
  <button class="accent-red-600" @click="deleteAllClips">delete ALL clips</button>



  <div class="flex flex-wrap pl-10">
    <div v-for="(clip, index) in clips" :key="index" class="w-1/4 m-4"
      :class="{ 'border-4  border-purple-600 shadow-2xl rounded-md': isSelected(clip) }"
      @click="toggleSelectClip(clip)">

      <h2 class="text-xl, font-bold" v-text="clip"></h2>
      <div>
        <video class="w-500 h-300 rounded-lg" :src="'http://localhost:8080/api/getclips/' + clip" controls></video>
      </div>
      <div v-if="subtitlesExist[clip] === true"></div>
      <button @click='navigateToSettings(clip)' class=" bg-slate-700 text-white px-4 py-2 mt-2">Settings</button>
      <div v-if="subtitlesExist[clip] === false">
        <genSubTitles :data-name="clip"></genSubTitles>
        <p class="text-red-400">Subtitles do not exist ❎</p>
      </div>
      <p v-else-if="subtitlesExist[clip] === true" class="text-green-500">Subtitles exist ✅</p>
      <p v-else class="text-red-500">An error occurred while checking subtitles</p>
      <DeleteClip :data-name="clip"></DeleteClip>
      <DownloadSubs v-if="subtitlesExist[clip] === true" :data-name="clip"></DownloadSubs>
      
      </div>
  </div>
</template>


<script setup>

import DeleteClip from './DeleteClip.vue';
import DownloadSubs from './DownloadSubtitles.vue';
import { ref, onMounted, computed } from 'vue';
import router from '../main.js'

const clips = ref([]);
const subtitlesExist = ref({});



const selectedClips = ref([]);

function isSelected(clip) {
  return selectedClips.value.includes(clip);
}

function toggleSelectClip(clip) {
  const index = selectedClips.value.indexOf(clip);
  if (index !== -1) {
    selectedClips.value.splice(index, 1);  // Remove clip from selected clips if it's already selected
  } else {
    selectedClips.value.push(clip);  // Add clip to selected clips if it's not already selected
  }
}


const addClipstoQueue = async () => {
  if (selectedClips.value.length > 0) {
    const formData = new FormData();
    selectedClips.value.forEach(clip => {
      formData.append('clip', clip);  
    });
    const response = await fetch('http://localhost:8080/api/createQueue/', {
      method: 'POST',
      body: formData
    });
    if (!response.ok) {
      throw new Error('Invalid');
    }
    const data = await response.json();
    console.log(data.response);
  }

}



const deleteAllClips = async () => {

    const response = await fetch('http://localhost:8080/api/deleteAllClips/', {
      method: 'DELETE'
    });
    if (!response.ok) {
      throw new Error('Invalid');
    }
    const data = await response.json();
    console.log(data.response);


}



const navigateToSettings = async (Clip) => {
  try {
    await router.push({ name: 'SettingsView', params: { id: Clip } });
  } catch (error) {
    console.error(error);
  }
};


//pulling all clips 

const recieveclipList = async () => {
  try {
    const response = await fetch("http://localhost:8080/api/clips");
    const clipList = await response.json();
    clips.value = clipList;
    // console.log(clipList)

    localStorage.setItem("clips", clipList)
    await Promise.all(clipList.map(async (clipName) => {
      const subExist = await subExists(clipName);
      subtitlesExist.value[clipName] = subExist;
    }));



    let subtitlesExistsResponse = await fetch("http://localhost:8080/api/checksubtitle/" + clipName);
    let subtitlesExists = await subtitlesExistsResponse.json();

    console.log(subtitlesExists)
  } catch (error) {
    console.error("Error fetching clips:", error);
  }
};


const editedClips = computed(() => {
  return clips.value.map((clip) => clip.replace(".mp4", "_w_subs.mp4"));
});


onMounted(() => {
  recieveclipList();
});

</script>



<script>


export const subExists = async (clipName) => {

  const subtitelsExistsResponse = await fetch("http://localhost:8080/api/checksubtitle/" + clipName);
  const subtitelsExists = await subtitelsExistsResponse.json();
  console.log(subtitelsExists);
  return subtitelsExists.Exists;

};

</script>