<template>
  <div>

    <div v-for="(weekday, index) in weekdays" :key="index">
      <button @click="addWeekday(weekday)" :class="{ 'bg-blue-500': selectedWeekdays.includes(weekday) }">{{ weekday
        }}</button>
    </div>

    <div>
      <table class="w-full rounded-lg overflow-hidden shadow-lg">
        <thead>
          <tr class="bg-gray-100">
            <th class="py-2 px-4 border border-gray-300">Time</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(schedule, index) in ScheduleTime" :key="index">
            <td class="py-2 px-4 border border-gray-300">{{ schedule }}</td>
            <td class="py-2 px-4 border border-gray-300">
              <button @click="removeTime(index)" class="text-red-600">Remove</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="mt-4">
        <label for="newTime" class="block font-bold">Add Time</label>
        <input type="time" id="newTime" v-model="newTime" class="w-full border border-gray-300 p-2 rounded-md">
        <button @click="addTime" class="mt-2 px-4 py-2 bg-blue-500 text-white rounded-full shadow-md">Add</button>
      </div>
    </div>
    <button @click="saveData" class="mt-2 px-4 py-2 bg-green-400 text-white rounded-full shadow-md"> Save</button>
  </div>
</template>


<script>
export default {
  data() {
    return {
      weekdays: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      selectedWeekdays: [],
      ScheduleTime: [],
      newTime: '',
    };
  },
  methods: {
    addTime() {
      if (this.newTime) {
        this.ScheduleTime.push(this.newTime);
        this.newTime = '';
      }
    },
    addWeekday(weekday) {
      if (!this.selectedWeekdays.includes(weekday)) {
        this.selectedWeekdays.push(weekday);
      } else {
        const index = this.selectedWeekdays.indexOf(weekday);
        this.selectedWeekdays.splice(index, 1);
      }
    },
    removeTime(index) {
      this.ScheduleTime.splice(index, 1);
    },
    saveData() {
      const days = this.selectedWeekdays.join(", ");
      const times = this.ScheduleTime.join(", ");
      var formData = new FormData();
      formData.append('Days', days);
      formData.append('Time', times);
      formData.append('assigneduser', localStorage.getItem("userid"));
      fetch(`http://localhost:8080/api/addSchedule/`, {
        method: 'POST',
        body: formData
      })
        .then(response => response.json())
        .then(data => {
          console.log(data); // Log the API response
        })
        .catch(error => {
          console.error('Error:', error);
        });
    },
    async getSchedules() {
      var formData = new FormData();
      formData.append('id', localStorage.getItem("userid"));
      try {
        const response = await fetch(`http://localhost:8080/api/get/schedule/`, {
          method: 'POST',
          body: formData
        });
        const data = await response.json();
        console.log(data);
        if (data.schedules && data.schedules.length > 0) {
          const Weekdays = data.schedules[0].Weekdays || [];
          const Times = data.schedules[0].Times || [];
          Weekdays.forEach(weekday => {
            if (!this.selectedWeekdays.includes(weekday)) {
              this.selectedWeekdays.push(weekday);
            }
          });
          Times.forEach(time => {
            this.ScheduleTime.push(time);
          });
        }
      } catch (error) {
        console.error('Error:', error);
      }
    },
  },
  async mounted() {
    // Ensure localStorage has the user ID before calling getSchedules
    if (localStorage.getItem("userid")) {
      await this.getSchedules();
    } else {
      console.error('User ID not found in localStorage.');
    }
  }
}
</script>