<template>
  <div>
    <Header></Header>
    <div v-if="userLoggedin">
      <h1 class="font-bold bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 bg-clip-text text-transparent p-6 text-7xl">Account</h1>
      <h1 class="text-5xl bg-violet-400 py-2 px-4 rounded focus:outline-none text-white focus:shadow-outline">
        username: {{ userName }}
      </h1>
      
      <div>
        <p>Discord WEBHOOK link</p>
        <input v-model="url" type="text" placeholder="Enter Discord WEBHOOK url here" />
        <button @click="setWebhook">set</button>
      </div>


      <div>

        <p>Get clips from the last ___ days</p>
        <input v-model="dayPeriod" type="number" placeholder="Enter Day periode of the clips" />

        <button @click="setPeriodeOfClips">set</button>
      </div>


      <button @click="logout" class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-full focus:outline-none focus:shadow-outline">
        Logout
      </button>
    </div>

    <div v-else>
      <h1 class="font-bold bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 bg-clip-text text-transparent p-6 text-7xl">You are not Loggedin</h1>

      <a href="#register" class="bg-violet-500 hover:bg-violet-700 text-white font-bold py-2 px-4 rounded-full focus:outline-none focus:shadow-outline">
        Register
      </a>
      <a href="#login" class="bg-violet-500 hover:bg-violet-700 text-white font-bold py-2 px-4 rounded-full focus:outline-none focus:shadow-outline">
        Login
      </a>
    </div>
  </div>
</template>

<script>
import Header from '@/components/Header.vue';
import { ref, onMounted } from 'vue';

export default {
  components: {
    Header,
  },
  setup() {
    const userLoggedin = ref(false);
    const userName = ref("");
    const url = ref("");
    const dayPeriod = ref("");

    const checkUserToken = () => {
      const userToken = localStorage.getItem("token");
      if (userToken) {
        userLoggedin.value = true;
        userName.value = localStorage.getItem("username");
      }
    };

    const fetchSettings = async () => {
      try {
        const userId = localStorage.getItem('userid');
        const response = await fetch(`http://localhost:8080/api/getSettings/${userId}`);
        if (!response.ok) throw new Error('Failed to fetch settings');
        
        const data = await response.json();
        url.value = data.webhook;
        dayPeriod.value = data.dayPeriodOfTheClip;

        console.log("URL: " + url.value);
      } catch (error) {
        console.error("Error fetching settings:", error);
      }
    };



  const addSettings = async (form) => {



    try {



    const response = await fetch("http://localhost:8080/api/addSettings", {
      method: 'POST',
      body: form,
    });

    if (!response.ok) throw new Error('Failed to set webhook');

    const data = await response.json();
    console.log('add Settings response:', data);
  } catch (error) {
      console.error("Error :", error);
    }

  }

    const setWebhook = async () => {


        const userId = localStorage.getItem('userid');
        const formData = new FormData();
        formData.append('id', userId);
        formData.append('webhook', url.value);

        addSettings(formData);

    };

    const setPeriodeOfClips = async () => {





        const userId = localStorage.getItem('userid');
        const formData = new FormData();
        formData.append('id', userId);
        formData.append('dayPeriodeOfClip', dayPeriod.value);

        addSettings(formData);





    }


    const logout = () => {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      userLoggedin.value = false;
      userName.value = "";
    };

    onMounted(async () => {
      checkUserToken();
      if (userLoggedin.value) {
        await fetchSettings();
      }
    });

    return {
      userLoggedin,
      userName,
      url,
      setWebhook,
      logout
    };
  }
};
</script>