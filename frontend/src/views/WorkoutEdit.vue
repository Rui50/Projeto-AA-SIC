<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import Navbar from '@/components/Navbar.vue'

    const router = useRouter()

    // nao esquecer de por o delete nesta pagina

    const workoutName = ref('Workout Title')
    
    // temporary
    const editName = () => {
        const newName = prompt('Enter new workout name:', workoutName.value);
        if (newName !== null && newName.trim() !== '') {
            workoutName.value = newName.trim();
        }
    };

    const scheduleType = ref('freedom');

    const weekdays = ref([
        { name: 'M', selected: false },
        { name: 'T', selected: false },
        { name: 'W', selected: false },
        { name: 'T', selected: false },
        { name: 'F', selected: false },
        { name: 'S', selected: false },
        { name: 'S', selected: false }
    ]);

    const exercises= ref([
        { name: 'Pernas', sets: '3', reps: '12-15', weight: '18-20kg', rest: '60s' },
        { name: 'Pernas', sets: '3', reps: '12-15', weight: '18-20kg', rest: '60s' },
        { name: 'Pernas', sets: '3', reps: '12-15', weight: '18-20kg', rest: '60s' }
    ])
    // fazer sempre que mudar o nome do workout guardar? ou so quando selecionar o guardar geral
    // ao passar o rato por cima de um dos dias talvez expandir o dia para dizer o texto completo
    // fazer as box shadows "mais fortes"
</script>

<template>
    <div class="edit-workout">
        <div class="edit-workout-header">
           <div class="workout-name">
                <Icon icon="mdi:edit" width="24" height="24" @click="editName" class="pen-icon"/>
                <h1> {{ workoutName }}</h1>
           </div>
           <div class="workout-edit-actions">
                <button class="button-cancel">Cancel</button>
                <button class="button-save">Save Changes</button>
           </div>
        </div>

        <div class="edit-card">
            <h2>Schedule Type</h2>
            <div class="schedule-types">
                <div 
                    class="schedule-type"
                    :class="{ 'active': scheduleType === 'freedom' }"
                    @click="scheduleType = 'freedom'"
                >
                    <div class="schedule-type-name">Freedom</div>
                    <p>Execute the workout when I have the free time for it</p>
                </div>

                <div 
                    class="schedule-type"
                    :class="{ 'active': scheduleType === 'fixed' }"
                    @click="scheduleType = 'fixed'"
                >
                    <div class="schedule-type-name">Fixed</div>
                    <p>Select the day/s of the week for this workout</p>
                </div>
            </div>

            <div v-if="scheduleType === 'fixed'">
                <div class="days-title">Select the days of the week for this workout</div>
                <div class="days-select-section">  
                    <div 
                        class="days-selection"
                        v-for="(day, index) in weekdays" 
                        :key="index"
                        :class="{ 'active': day.selected }"
                        @click="day.selected = !day.selected"
                    >
                        <div class="days">{{ day.name }}</div>
                    </div>
                </div>
            </div> 
        </div>

        <div class="edit-card">
            <div class="exercises-header">
                <h2>Exercises</h2>
                <button class="add-exercise-button">+ Add Exercise</button>
            </div>

            <div class="exercises-list">

            </div>
        </div>
    </div>
    


</template>

<style scoped> 
    .edit-workout{
        max-width: 1500px;
        width: 100%;
        margin: 0 auto;
        padding: 20px;
    }

    .edit-workout-header{
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 2rem;
    }

    .workout-name{
        display: flex;
        align-items: center;
        gap: 1rem;

    }

    .pen-icon{
        cursor: pointer;
        color: #495057;
    }

    .pen-icon:hover {
        color: #4b5563;
    }

    .workout-edit-actions{
        display: flex;
        gap: 1rem;
    }

    .button-cancel {
        padding: 0.6rem 1rem;
        border-radius: 5px;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
        border: none;
        background-color: #f6f8fa;
        border: 1px solid #d1d5db;
        color: #24292e;
        transition: background-color 0.2s;
    }

    
    .button-save {
        padding: 0.5rem 1rem;
        border-radius: 5px;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
        border: none;
        background-color: var(--button-lighter);
        color: white;       
        transition: background-color 0.2s;
    }

    .button-cancel:hover {
        background-color: #e1e4e8;
    }
    .button-save:hover {
        background-color: var(--button-primary);
    }   

    h1 {
        color: #000000;
    }

    .edit-card{
        background-color: white;
        border-radius: 10px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        padding: 2rem;
        margin-bottom: 2rem;
    }

    h2 {
        font-size: 1.3rem;
        font-weight: 600;
        margin-bottom: 1rem;
    }
    .schedule-types{
        display: flex;
        gap: 1rem;
        margin-bottom: 1rem;

    }

    .schedule-type {
        flex: 1; /**To take up all the space */
        border: 1px solid #d1d5db;
        border-radius: 8px;
        padding: 10px;
        cursor: pointer;
        position: relative;
        transition: border-color 0.3s ease;
    }
    
    .schedule-type.active {
        border: 1px solid #4f46e5;
    }

    .schedule-type-name{
        font-weight: 700;
        margin-bottom: 0.5rem;
    }

    p {
        font-size: 0.9rem;
        color: #6b7280;
    }

    .days-title{
        font-weight: 600;
        font-size: 1rem;
    }

    .days-select-section{
        display: flex;
        gap: 1rem;
        margin-top: 1rem;
    }
    

    .days-selection{
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 0.8rem;
        cursor: pointer;
        background-color: #f3f4f6;
        color: #4b5563;
    }

    .days-selection.active {
        background-color: var(--button-lighter);
        color: white;
    }

    .days {
        font-weight: 700;
    }

    .exercises-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .add-exercise-button {
        background-color: var(--button-lighter);
        color: white;
        padding: 0.5rem 1rem;
        border-radius: 5px;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
        border: none;
    }

</style>