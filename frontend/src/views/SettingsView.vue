<template>
  <div>
    <h1>SETTINGS</h1>

    <!-- Your other template code goes here -->

    <h2>original</h2>
    <video width="500" height="300" :src="'http://localhost:8080/api/getclips/' + clip" controls></video>

    <div>
      <h2>With subtitles</h2>
      <video v-if="checkSubtitle == true" width="500" height="300" :src="'http://localhost:8080/api/getclips/' + clipWithSubs" controls></video>
      <p v-else>Does not exist yet</p>

      <h2>Cropped for Short Videos</h2>
      <video v-if="checkSubtitle == true" width="500" height="300" :src="'http://localhost:8080/api/getclips/' + shortClip" controls></video>
      <p v-else>Does not exist yet</p>
    </div>

    <div>
      <GenSubTitels :data-name="clipID"></GenSubTitels>
    </div>

    <button @click="generateDescription">Generate description</button>
    <button @click="editSubtitles">Edit Subtitles</button>
    <div>
      <textarea v-model="description">
        Sample description
      </textarea>
    </div>
    <button @click="recommendHashtags">Recommend hashtags</button>
    <div>
      <textarea v-model="hashtags">
        #test #sample
      </textarea>
    </div>

    <button class="back" @click="back2Home">Back</button>
  </div>

  
  <SubTitelsEditBox></SubTitelsEditBox>
  <DeleteClip :data-name="clipID"></DeleteClip>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import router from '../main.js';
import { subExists } from '@/components/ClipsList.vue';
import DeleteClip from '@/components/DeleteClip.vue';
import GenSubTitels from '@/components/genSubTitels.vue';
import SubTitelsEditBox from '@/components/SubTitlesEditBox.vue'


const clipID = useRoute().params.id;

const clip = ref(clipID);
const clipWithSubs = ref(clipID.replace('.mp4', '_w_subs.mp4'));
const shortClip = ref(clipID.replace('.mp4', '_w_subs_4_tiktok.mp4'));

const checkSubtitle = ref();

const description = ref('Sample description');

const back2Home = async () => {
  try {
    await router.push({ name: 'Home' });
  } catch (error) {
    console.error(error);
  }
};
const generateDescription = async () => {
  console.log('Generating description...');

  var requestOptions = {
  method: 'POST',
  redirect: 'follow'
};

fetch("http://localhost:8080/api/generateText/Minecraft,%20Twitch,%20BastiGHG", requestOptions)
  .then(response => response.text())
  .then(result => console.log(result))
  .catch(error => console.log('error', error));
};



const editSubtitles = () => {
  // Implement your logic for editing subtitles
  console.log('Editing subtitles...');
};

onMounted(async () =>{
  checkSubtitle.value = await subExists(clipID);
});

</script>
