<template>
<button @click="getdownloadLink">Download</button>
</template>


<script setup> 

async function getdownloadLink(event){
const clipname = event.target.dataset.name;

window.open("http://localhost:8080/api/getsubtitels/"+ clipname, "_blank");

}
</script>