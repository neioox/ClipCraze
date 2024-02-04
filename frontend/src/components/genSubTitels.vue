
<script setup>
import { ref } from 'vue';

const subsExist = ref(false);
const responses = ref([]);
const isLoading = ref(false);
const loadingState =ref();

async function generateSubtitles(event) {
    let subtitlesExist = false;
    let attempts = 0;
    const maxAttempts = 15; // Set your max attempts here

    while (!subtitlesExist && attempts < maxAttempts) {
        try {
            //set loading state to true
            isLoading.value = true;
            console.log(event);
            const clipName = event.target.dataset.name;
            console.log(clipName);

            loadingState.value = "Checking if subtitles exist..."

            let subtitlesExistsResponse = await fetch("http://localhost:8080/api/checksubtitle/" + clipName);
            let subtitlesExists = await subtitlesExistsResponse.json();

            subsExist.value = subtitlesExists.Exists;        

            loadingState.value = "Response: " + subtitlesExists;

            responses.value.push(subtitlesExists); // Use .value to access the underlying value
            console.log("test")
            console.log(subsExist.value)
            //if subs don't exist generate them 
           
            if (!subsExist.value) {
                loadingState.value = "Generating Subtitles with AI...";
                // Generate subtitles 
                const response = await fetch("http://localhost:8080/api/genSubtitle/" + clipName, {method:  "POST"
                });
                responses.value.push(await response.json()); // Use .value to access the underlying value
            } else {

                console.log(subtitlesExist)
                subtitlesExist = true;

                loadingState.value = "Adding the Subtitles to the video..."
                // Render it to a new video file 
                const formattingConvertResponse = await fetch("http://localhost:8080/api/addsubtitles2vid/" + clipName , {method: "POST"});
                responses.value.push(await formattingConvertResponse.json()); // Use .value to access the underlying value

                loadingState.value = "Cropping it for TikTok..."
                const Convert4Tiktok = await fetch("http://localhost:8080/api/crop4tiktok/" + clipName.replace(".mp4", "_w_subs.mp4"), {method: "POST"});
                responses.value.push(await Convert4Tiktok.json()); // Use .value to access the underlying value

                loadingState.value = "Done!"
                //set loading to false
                isLoading.value = false;
            }
        } catch (error) {
            console.error("Error generating subtitles:", error);
            // You can handle the error here, show a message to the user, or log it.
        }

        attempts++;
        if (!subtitlesExist) {
            // Wait for 1 second before the next attempt
            await new Promise(resolve => setTimeout(resolve, 1000));
        }
    }

    if (attempts === maxAttempts) {
        console.error("Max attempts reached. Subtitles could not be generated.");
        loadingState.value="Max attempts reached. Subtitles could not be generated."
    }
}





</script>

<template>
    <button @click="generateSubtitles" v-if="isLoading == false">Convert for Tiktok</button>
    <p v-else v-text="loadingState"></p>

</template>
