<template>
  <div>
    <h1 class= "text-4xl font-bold ">Settings ⚙️</h1>

    <!-- Your other template code goes here -->

    <div style="background-color: yellowgreen;">
    
    </div>

    <h2 class="text-2xl font-bold">original</h2>
    <div class="w-full flex justify-center"> <!-- Assuming a full-width container, adjust as needed -->
    <video class="mx-auto rounded-lg" width="500" height="300" :src="'http://localhost:8080/api/getclips/' + clip" controls></video>
</div>
    <div>
      <h2 class=" text-xl font-bold">With subtitles</h2>
      <video v-if="checkSubtitle == true" width="500" height="300" :src="'http://localhost:8080/api/getclips/' + clipWithSubs" controls></video>
      <p v-else>Does not exist yet</p>

      <h2 class=" text-xl font-bold">Cropped for Short Videos</h2>
      <video v-if="checkSubtitle == true" width="500" height="300" :src="'http://localhost:8080/api/getclips/' + shortClip" controls></video>
      <p v-else>Does not exist yet</p>
    </div>

    <div>
      <GenSubTitels :data-name="clipID"></GenSubTitels>
   
      <button class="bg-slate-600 text-white px-4 py-2 mt-2" @click="generateDescription">Generate description</button>
     <button class="bg-slate-600 text-white px-4 py-2 mt-2" @click="editSubtitles">Edit Subtitles</button>
    </div>

   
    <div>
      <textarea class="" v-model="desc"> </textarea>
    </div>
    <button class="bg-slate-600 text-white px-4 py-2 mt-2" @click="recommendHashtags">Recommend hashtags</button>
    <div>
      <textarea v-model="hashtags">
        #test #sample
      </textarea>
    </div>

    <button class="bg-red-600 text-white px-4 py-2 mt-2" @click="back2Home">Back</button>
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
const desc  = ref("")
const checkSubtitle = ref();



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

fetch("http://localhost:8080/api/generateText/test", requestOptions)
  .then(response => response.json())
  .then(result => {
    desc.value = result.description
    console.log("test");
    console.log(desc.value);
  })
  .catch(error => console.log('error', error));
}





const editSubtitles = () => {
  // Implement your logic for editing subtitles
  console.log('Editing subtitles...');
};

onMounted(async () =>{
  checkSubtitle.value = await subExists(clipID);
  console.log(checkSubtitle.value)
});

</script>
