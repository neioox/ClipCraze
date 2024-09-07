<template>
  <div>
    <h1 class="font-bold bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 bg-clip-text text-transparent p-6 text-7xl">Settings</h1>

    <div>

      <h2 class="text-2xl font-bold">Original</h2>
      <div class="w-full flex justify-center">
        <video class="mx-auto rounded-lg" width="500" height="300" :src="'http://localhost:8080/api/getclips/' + clip" controls></video>
      </div>

      <div class="w-full flex flex-col items-center">
        <h2 class="text-xl font-bold w-full flex justify-center">With subtitles</h2>
        <div class="w-full flex justify-center">
          <video width="500" height="300" :src="'http://localhost:8080/api/getclips/' + clipWithSubs" controls></video>
        </div>

        <h2 class="text-xl font-bold w-full flex justify-center mt-4">Cropped for Short Videos</h2>
        <div class="w-full flex justify-center">
          <video width="500" height="300" :src="'http://localhost:8080/api/getclips/' + shortClip" controls></video>
        </div>
      </div>

      <div>
        <GenSubTitels :data-name="clipID"></GenSubTitels>
        <button class="bg-slate-600 text-white px-4 py-2 mt-2" @click="generateDescription">Generate description</button>
        <button class="bg-slate-600 text-white px-4 py-2 mt-2" @click="editSubtitles">Edit Subtitles</button>
      </div>

      <div>
        <textarea v-model="desc"></textarea>
      </div>
      <button class="bg-slate-600 text-white px-4 py-2 mt-2" @click="recommendHashtags">Recommend hashtags</button>
      <div>
        <textarea v-model="hashtags">#test #sample</textarea>
      </div>

      <button class="bg-red-600 text-white px-4 py-2 mt-2" @click="back2Home">Back</button>
    </div>

    <SubTitelsEditBox></SubTitelsEditBox>
    <DeleteClip :data-name="clipID"></DeleteClip>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import router from '../main.js';
import DeleteClip from '@/components/DeleteClip.vue';
import GenSubTitels from '@/components/genSubTitels.vue';
import SubTitelsEditBox from '@/components/SubTitlesEditBox.vue';

const clipID = useRoute().params.id;
const clip = ref(clipID);
const clipWithSubs = ref(clipID.replace('.mp4', '_w_subs.mp4'));
const shortClip = ref(clipID.replace('.mp4', '_final.mp4'));
const desc = ref("");
const checkSubtitle = ref(false);

const hashtags = ref("#test #sample");

const back2Home = async () => {
  try {
    await router.push({ name: 'Home' });
  } catch (error) {
    console.error(error);
  }
};

</script>
