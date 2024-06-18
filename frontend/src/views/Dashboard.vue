<template>
  <h2>Dashboard</h2>
  <h1>Welcome {{ username }}</h1>

  <div>
    <h2 class="text-xl font-bold mb-4">Streamer List</h2>

    <!-- Table to display streamers -->
    <table class="w-full border-collapse border border-gray-300">
      <thead>
        <tr class="bg-gray-100">
          <th class="py-2 px-4 border border-gray-300">Streamer</th>
          <th class="py-2 px-4 border border-gray-300">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(streamer, index) in streamers" :key="index">
          <td class="py-2 px-4 border border-gray-300">{{ streamer }}</td>
          <td class="py-2 px-4 border border-gray-300">
            <button @click="removeStreamer(index)" class="text-red-600">Remove</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Input to add new streamer -->
    <div class="mt-4">
      <label for="newStreamer" class="block">Add Streamer:</label>
      <input type="text" id="newStreamer" v-model="newStreamer" class="w-full border border-gray-300 p-2 rounded-md">
      <button @click="addStreamer" class="mt-2 px-4 py-2 bg-blue-500 text-white rounded-md">Add</button>
    </div>
  </div>
  <shedule-panel></shedule-panel>
  <back-button></back-button>
</template>

<script>
import ShedulePanel from '@/components/ShedulePanel.vue';
import BackButton from '@/components/backButton.vue';

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
    'back-button': BackButton
  },

  async mounted() {
    this.username = localStorage.getItem('username') || 'Guest';
    await this.getStreamers();
  }
};
</script>
