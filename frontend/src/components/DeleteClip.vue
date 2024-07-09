<template>
  <button  class=" bg-red-500 text-white px-4 py-2 mt-2" id="DeleteButton" @click="deleteClip">Delete</button>
</template>

<script setup>

async function deleteClip(event) {
  try {
    const clipname = event.target.dataset.name;
    console.log(clipname);
    
    const response = await fetch(`http://localhost:8080/api/deleteclip/${clipname}`, {
      method: 'DELETE',

    });

    if (response.ok) {
      const div = event.target.parentElement;
      div.remove();
    } else {
      console.error('Failed to delete clip');
    }
  } catch (error) {
    console.error('An error occurred:', error);
  }
}
</script>
