<template>
  <button class="bg-red-600 text-white px-4 py-2 mt-2" @click="back2Home">Back</button>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const back2Home = async () => {
  try {
    await router.push({ name: 'Home' });
  } catch (error) {
    console.error(error);
  }
};
</script>
