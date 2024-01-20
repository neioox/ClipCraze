<template>

<div id="EditBox">
    
    <div v-for="(sub, index) in SRTFile" :key="index" class="clip-item">
        <h2 v-text="sub.id"></h2>   
        <h2 v-text="sub.timestamp"></h2>
        <textarea v-model="sub.text" class="modern-text" @input="($event) => onTextFieldChange($event, index)"></textarea>
    </div>
    <button @click="onSaveChanges"> Save Changes</button>
</div>

</template>

<style scoped>
.modern-text {
    font-family: Arial, sans-serif;
    color: #333;
    font-size: 1.2em;
    line-height: 1.6;
    margin-bottom: 1em;
    text-align: justify;
    text-justify: inter-word;
}
</style>


<script setup>



import { useRoute } from 'vue-router';
import { ref } from 'vue';

const clipID = useRoute().params.id;
const SRTFile = ref("");
const changedSubs = new Map();


const getSubtitels = async (name) =>{

try {
    
 const response = await fetch("http://localhost:8080/api/getSubsAsJson/"+ name);
 const data = await response.json();

    SRTFile.value = data;
// console.log(data);

} catch (error) { 
}

};

//save the changed text to a map 
const onTextFieldChange =  (event, index) => {
    
    const newtext = event.target.value;
    const id = index;
    
    changedSubs.set(id, newtext)
}

async function onSaveChanges (event) {
    const keys = changedSubs.keys();
    const appUrl =  window.location.href.split("/");
    const filenameVar = appUrl[5];

    for (const key of keys) {


        console.log(key)
        try {
            let url = "/api/editSub";
            let data = {
                id: String(key), // replace with actual id
                text: changedSubs.get(key), // replace with actual text
                filename: filenameVar // replace with actual filename
            };
                console.log(data)


            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
             
             body: JSON.stringify(data) // add data to the body of the request
            });

            if (!response.ok) {
                console.error('Failed to edit subtitle');
            }
        } catch (error) {
            console.log(error);
        }
    }
}



getSubtitels(clipID)
</script>