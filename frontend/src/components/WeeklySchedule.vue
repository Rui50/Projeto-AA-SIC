<template>
  <div class="schedule-card">
    <div class="schedule-header">
      <h3>Weekly Schedule</h3>
    </div>
    <div class="schedule-grid">
      <div
        v-for="(day, index) in weekDays"
        :key="index"
        :class="['day-box', { active: day.isToday }]"
      >
        <div class="day-title">{{ day.short }}</div>
        <div class="day-date">{{ day.date }}</div>
        <div v-if="day.events.length" class="day-events">
          <div class="day-event" v-for="(event, idx) in day.events" :key="idx">
            <span class="dot"></span>
            {{ event }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// Day short names and full names, Monday = 0
const dayShorts = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
const dayFullNames = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

// Your input data: key is the day name
const events = {
  Monday:    ['Yoga'],
  Tuesday:   ['Leg Day', 'Core'],
  Wednesday: ['Crossfit'],
  Friday:    ['Cardio', 'Swimming', 'HIIT'],
};

// Utility to get the start of the week (Monday)
function getMonday(d) {
  const date = new Date(d);
  const day = date.getDay();
  const diff = (day === 0 ? -6 : 1) - day; // Sunday (0) becomes -6, Monday (1) becomes 0, etc
  date.setDate(date.getDate() + diff);
  return date;
}

function getWeekDays() {
  const today = new Date();
  const monday = getMonday(today);

  const week = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    // Find the name for the current day index (0 = Mon, 6 = Sun)
    const short = dayShorts[i];
    const fullName = dayFullNames[i];
    week.push({
      date: d.getDate(),
      short,
      fullName,
      isToday:
        d.getDate() === today.getDate() &&
        d.getMonth() === today.getMonth() &&
        d.getFullYear() === today.getFullYear(),
      events: events[fullName] || [],
    });
  }
  return week;
}

const weekDays = ref(getWeekDays());
</script>

<!-- CSS can stay the same as your original -->
<style scoped>
.schedule-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 2px 2px 10px #0001;
  padding: 18px 22px;
}
.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.schedule-header h3 {
  font-size: 1.22rem;
  margin: 0;
  font-weight: 700;
}
.calendar-link {
  color: #3e56f6;
  font-weight: 500;
  font-size: 1.04rem;
  text-decoration: none;
}
.schedule-grid {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}
.day-box {
  width: 150px;
  height: 175px;
  background: #fff;
  border: 2px solid #bbb;
  border-radius: 10px;
  text-align: left;
  padding: 7px 6px;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: border 0.2s, background 0.2s;
}
.day-box.active {
  background: #e6e8ff;
  border-color: #3e56f6;
}
.day-title {
  color: #888;
  font-size: 1.07rem;
  font-weight: 500;
  text-align: center;
}
.day-date {
  font-size: 1.02rem;
  font-weight: 600;
  margin: 2px 0 0 2px;
  text-align: center;
}
.day-events {
  margin-top: 5px;
}
.day-event {
  color: #3e56f6;
  font-weight: 500;
  display: flex;
  align-items: center;
  font-size: 0.98rem;
  justify-content: center;
}
.day-event .dot {
  width: 8px;
  height: 8px;
  background: #3e56f6;
  border-radius: 50%;
  margin-right: 4px;
  display: inline-block;
}
</style>
