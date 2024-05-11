<template>
    <button class="bg-slate-600 text-white px-4 py-2 mt-2" @click="handleClick" v-if="!subtitleGenerator.isLoading">Convert for TikTok</button>
    <p v-else v-text="subtitleGenerator.loadingState"></p>
</template>

<script>
import SubtitleGenerator from '@/utils/SubtitleGenerator';

export default {
    data() {
        return {
            subtitleGenerator: new SubtitleGenerator()
        };
    },
    methods: {
        handleClick(event) {
            this.subtitleGenerator.generateSubtitles(event);
        }
    }
}
</script>
