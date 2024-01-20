<template>
  <div class="clip-container">
    <div v-for="(clip, index) in clips" :key="index" class="clip-item">
      <h2 v-text="clip"></h2>
      <div>
        
        <video width="500" height="300" :src="'http://localhost:8080/api/getclips/' + clip" controls></video>
      </div>
      <div v-if="subtitlesExist[clip] === true">

      </div>

        <button  @click='naviegateToSettings(clip)'> Settings</button>
   

      
      <div v-if="subtitlesExist[clip] === false">
        <genSubTitels :data-name="clip"></genSubTitels>
        <p>Subtitles do not exist ❎</p>
      </div>
      <p v-else-if="subtitlesExist[clip] === true">Subtitles exist ✅</p>
      <p v-else>An error occurred while checking subtitles</p>

      <DeleteClip :data-name="clip"></DeleteClip>

      <DownloadSubs v-if="subtitlesExist[clip] === true" :data-name="clip"></DownloadSubs>
    </div>
  </div>
</template>

<style scoped>
.clip-container {
  display: flex;
  flex-wrap: wrap;
  padding-left: 10px;
}

.clip-item {
  flex: 0 0 calc(25% - 20px); /* Adjust the percentage as needed, and consider margins */
  margin: 10px;
}
</style>

<script setup>
import genSubTitels from './genSubTitels.vue';
import DeleteClip from './DeleteClip.vue';
import DownloadSubs from './DownloadSubtitles.vue';
import { ref, onMounted, computed } from 'vue';
import router from '../main.js'

const clips = ref([]);
const subtitlesExist = ref({});


const naviegateToSettings = async (Clip) => {
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

    await Promise.all(clipList.map(async (clipName) => {
      const subExist = await subExists(clipName);
      subtitlesExist.value[clipName] = subExist;
    }));
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
  try {
    const subtitelsExistsResponse = await fetch("http://localhost:8080/api/checksubtitle/" + clipName);
    const subtitelsExists = await subtitelsExistsResponse.json();
    console.log(subtitelsExists.exists);
    return subtitelsExists.exists === true;
  } catch (error) {
    console.error("Error checking subtitles:", error);
    return false;
  }
};

</script>