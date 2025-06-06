<template>
  <div class="schedule-card">
    <div class="schedule-header">
      <h3>Weekly Schedule</h3>
      <!--<a href="#" class="calendar-link">View Calendar</a>-->
    </div>
    <div class="schedule-grid">
      <div
        v-for="(day, index) in weekDays"
        :key="index"
        :class="['day-box', { active: day.isToday }]"
      >
        <div class="day-title">{{ day.short }}</div>
        <div class="day-date">{{ day.date }}</div>
        <div class="day-events">
          <template v-if="day.events.length">
            <div 
              class="day-event" 
              v-for="(event, idx) in day.events" 
              :key="event.id || idx"
              @click="goToWorkoutPlan(event.id)"
            >
              <span class="dot"></span>
              {{ event.name }}
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { API_PATHS } from '../api_paths';	
import { useRouter } from 'vue-router'

const router = useRouter()

const dayShorts = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
const dayFullNames = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

const events = ref({});

function getMonday(d) {
  const date = new Date(d);
  const day = date.getDay();
  const diff = (day === 0 ? -6 : 1) - day;
  date.setDate(date.getDate() + diff);
  return date;
}

function getWeekDays() {
  const today = new Date();
  const monday = getMonday(today);

  // Prepara um dicionário com as chaves normalizadas (ex: tudo minúsculo)
  const eventsMap = {};
  Object.keys(events.value).forEach(k => {
    eventsMap[k.toLowerCase()] = events.value[k];
  });

  const week = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    const short = dayShorts[i];
    const fullName = dayFullNames[i];
    // Usa a chave minúscula para garantir que encontra!
    const key = fullName.toLowerCase();
    week.push({
      date: d.getDate(),
      short,
      fullName,
      isToday:
        d.getDate() === today.getDate() &&
        d.getMonth() === today.getMonth() &&
        d.getFullYear() === today.getFullYear(),
      events: eventsMap[key] || [],
    });
  }
  return week;
}

// Atualiza weekDays quando events muda
const weekDays = ref(getWeekDays());
watch(events, () => {
  weekDays.value = getWeekDays();
});

// Busca eventos ao montar
onMounted(async () => {
  try {
    const res = await axios.get(API_PATHS.WeeklySchedule, {
      withCredentials: true
    });
    events.value = res.data;
  } catch (e) {
    console.error(e);
  }
});

// Navegação para o plano de treino
function goToWorkoutPlan(id) {
  if (id) {    
    router.push(`/workout/${id}`);
  }
}
</script>

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
  flex: 1 1 auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.day-event {
  color: #3e56f6;
  font-weight: 500;
  display: flex;
  align-items: center;
  font-size: 0.98rem;
  justify-content: center;
  cursor: pointer;
}
.day-event:hover {
  background-color: #d0d4ff;
  color: #1a2a9c;
  border-radius: 6px;
}
.day-event .dot {
  width: 8px;
  height: 8px;
  background: #3e56f6;
  border-radius: 50%;
  margin-right: 4px;
  display: inline-block;
}
.day-event--empty {
  color: #bbb;
  font-weight: 400;
  font-size: 0.96rem;
  opacity: 0.8;
}
</style>
