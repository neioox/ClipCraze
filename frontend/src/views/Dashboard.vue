<template>
  <Header></Header>
  <div class="flex flex-col items-center mt-10">
    <h1 class="font-bold bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 bg-clip-text text-transparent p-6 text-7xl">Dashboard</h1>
  </div>
  <h1>Welcome {{ username }}</h1>

  <div>
    <h2 class="text-xl font-bold mb-4">Streamer List</h2>

    <!-- Table to display streamers -->
    <table class="w-full rounded-lg overflow-hidden shadow-lg">
  <thead class="bg-gray-100">
    <tr>
      <th class="py-4 px-6 text-left">Streamer</th>
      <th class="py-4 px-6 text-left">Actions</th>
    </tr>
  </thead>
  <tbody>
    <tr v-for="(streamer, index) in streamers" :key="index" class="hover:bg-gray-50">
      <td class="py-3 px-6 border-b border-gray-200 font-bold">{{ streamer }}</td>
      <td class="py-3 px-6 border-b border-gray-200">
        <button @click="removeStreamer(index)" class="text-white bg-red-500 hover:bg-red-600 transition-colors duration-150 px-4 py-2 rounded-full shadow">
          Remove
        </button>
      </td>
    </tr>
  </tbody>
</table>


    <!-- Input to add new streamer -->
    <div class="mt-4">
      <label for="newStreamer" class="block font-bold">Add Streamer</label>
      <input type="text" id="newStreamer" v-model="newStreamer" class="w-full border border-gray-300 p-2 rounded-md shadow-md">
      <button @click="addStreamer" class="mt-2 px-4 py-2 bg-blue-500 text-white rounded-full">Add</button>
    </div>
  </div>
  <shedule-panel></shedule-panel>
  <back-button></back-button>
</template>

<script>
import ShedulePanel from '@/components/ShedulePanel.vue';
import BackButton from '@/components/backButton.vue';
import Header from '@/components/Header.vue';


export default {
  data() {
    return {
      username: '',
      streamers: [], // Initialize streamers array to store streamer names
      newStreamer: '' // Initialize newStreamer data property to bind to the input field
    };
  },

  methods: {
    async getStreamers() {
      if (this.streamers.length === 0) {
        const formData = new FormData();
        formData.append('id', localStorage.getItem("userid"));

        const response = await fetch('http://localhost:8080/api/get/streamer', {
          method: 'POST',
          body: formData
        });

        if (!response.ok) {
          throw new Error('Invalid');
        }

        const data = await response.json();
        this.streamers = data.streamers;
      }
    },

    async addStreamer2DB(streamername, userid) {
      try {
        const formData = new FormData();
        formData.append('name', streamername);
        formData.append('assigneduser', userid);

        const response = await fetch('http://localhost:8080/api/addStreamer', {
          method: 'POST',
          body: formData
        });

        if (!response.ok) {
          throw new Error('Invalid');
        }

        const data = await response.json();
        console.log(data.response);
      } catch (error) {
        console.error('Error:', error.message);
        // Handle error here
      }
    },

    addStreamer() {
      if (this.newStreamer.trim() !== '') {
        const streamer = this.newStreamer.trim();
        this.streamers.push(streamer);
        const userid = localStorage.getItem("userid");
        if (userid) {
          this.addStreamer2DB(streamer, userid);
        }

        this.newStreamer = '';
      }
    },

    async removeStreamer(index) {
      const streamername = this.streamers[index];
      const userid = localStorage.getItem("userid");

      try {
        const formData = new FormData();
        formData.append('streamername', streamername);
        formData.append('id', userid);

        this.streamers.splice(index, 1);

        const response = await fetch('http://localhost:8080/api/delete/streamer', {
          method: 'DELETE',
          body: formData
        });

        if (!response.ok) {
          throw new Error('Invalid');
        }

        const data = await response.json();
        console.log(data.response);
      } catch (error) {
        console.error('Error:', error.message);
        // Handle error here
      } 
    }
  },

  components: {
    'shedule-panel': ShedulePanel,
    'back-button': BackButton, 
    Header
  },

  async mounted() {
    this.username = localStorage.getItem('username') || 'Guest';
    await this.getStreamers();
  }
};
</script>
