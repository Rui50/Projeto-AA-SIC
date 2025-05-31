<script setup>
    import { ref, onMounted, computed } from 'vue'
    import { useRoute } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import WorkoutCompleted from '@/components/WorkoutCompleted.vue'
    
    const route = useRoute()

    // temp data, structure will prob change, populate auto with copilot
    const workout = ref({
        name: 'Workout Title',
        isActive: true,
        madeBy: 'You',
        duration: 45,
        exercises: [
            { name: 'Exercise 1', sets: 3, muscle: 'Legs', status: 'completed', professorNote: 'Good job!', sets: [
                { setNumber: 1, previousWeight: 50, weight: 60, reps: 10, restTime: 2, status: 'completed' },
                { setNumber: 2, previousWeight: 55, weight: 65, reps: 8, restTime: 2, status: 'not' },
                { setNumber: 3, previousWeight: 60, weight: 70, reps: 6, restTime: 2, status: 'not' },
            ] },
            { name: 'Exercise 2', sets: 4, muscle: 'Chest', status: 'not completed', sets: [
                { setNumber: 1, previousWeight: 40, weight: null, reps: null, restTime: 1.5 },
                { setNumber: 2, previousWeight: null, weight: null, reps: null, restTime: 1.5 },
                { setNumber: 3, previousWeight: null, weight: null, reps: null, restTime: 1.5 },
                { setNumber: 4, previousWeight: null, weight: null, reps: null, restTime: 1.5 },
            ] },
            { name: 'Exercise 3', sets: 2, muscle: 'Back', status:'not completed', sets:[
                { setNumber: 1, previousWeight:null , weight:null , reps:null , restTime:null},
                { setNumber: 2 , previousWeight:null , weight:null , reps:null , restTime:null},
            ]},
        ],
        exerciseCount: 5,
    })

    // depois isto fica computed
    const currentExerciseIndex = ref(0)

    const currentExercise = computed(() => {
        return workout.value.exercises[currentExerciseIndex.value];
    })

    const selectExercise = (index) => {
        currentExerciseIndex.value = index
    }

    // falta design no lado esquerdo de completed e assim, falta ainda decidir como organizar workouts na backend

    // tentar fazer com que o lado direito tenha um tamanho fixo, porque senao exercicios com sets diferentes mexem com a ui
</script>


<template>
    <div class="workout-page">
        <div class="workout-header">
            <h1> {{ workout.name }}</h1>
            <!-- use an actual timer later -->
            <div class="timer">
                <span class="timer-value">00:00</span>
            </div>
        </div>

        <div class="workout-data">
            <div class="workout-data-item">
                <Icon icon="meteor-icons:list" width="24" height="24" />
                <span> {{ workout.exerciseCount }} Exercises</span>
            </div>
            <div class="workout-data-item">
                <Icon icon="mdi-light:clock" width="24" height="24" />
                <span> {{ workout.duration }} Minutes</span>
            </div>
            <!-- tentar arranjar outra coisa para meter aqui -->
            <div class="workout-data-item">
                <Icon icon="ion:person-outline" width="24" height="24" />                
                <span> Made by {{ workout.madeBy }}</span>
            </div>
        </div>  

        <!-- Can add a progress bar here -->

        <div class="workout-main-layour">
            <!-- Exercise list on the left side -->
             <div class="exercise-list">
                <div 
                    v-for="(exercise, index) in workout.exercises"
                    :key="index"
                    class="exercise-card"
                    :class="{ 'active': index === currentExerciseIndex }"
                    @click="selectExercise(index)"
                    >
                    <div class="exercise-name">{{ exercise.name }}</div>
                    <div class="exercise-muscle"> {{ exercise.muscle }}</div>
                    <div class="exercise-status" 
                        :class="{
                            'completed': exercise.status === 'completed',
                        }">
                    </div>
                </div>
             </div>


             <!-- Exercise "execution" on the right -->
            <div class="exercise-details">
                <div class="exercise-header">
                    <div class="ex-name">{{ currentExercise.name }}</div>
                    <div class="ex-muscle">{{ currentExercise.muscle }}</div>
                </div>
                <div class="professor-note" v-if="currentExercise.professorNote">
                    <div class="professor-note-header">
                        <Icon icon="ph:note-thin" width="24" height="24" />
                        <span>Professor's Note</span>
                    </div>
                    <div class="professor-note-content">
                        <span>{{ currentExercise.professorNote }}</span>
                    </div>
                </div>


                <div class="set-details">
                    <h2>Sets</h2>
                    <table class="sets-table">
                        <thead>
                            <tr>
                                <th>Set</th>
                                <th>Previous</th>
                                <!-- Make the kg responsive to the page units -->
                                <th>Weight (kg)</th>
                                <th>Reps</th>
                                <th>Rest time (recommended)</th>
                                <th>Completed</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="set in currentExercise.sets" :key="set.id" :class="{ 'completed': set.status === 'completed' }">
                                <td>{{ set.setNumber }}</td>
                                <td>{{ set.previousWeight }}</td>
                                <td><input type="text" v-model="set.weight"></td>
                                <td><input type="text" v-model="set.reps"></td>
                                <td>{{ set.restTime }} min</td>
                                <td>
                                    <Icon v-if="set.status === 'completed'" icon="mdi-light:check-circle" width="24" height="24" />
                                    <button v-else class="btn-complete" @click="completeSet(index)">Complete</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="add-wrapper">
                        <button class="add-set" @click="addSet">+ Add Set</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="workout-btns">
            <button class="btn cancel">Cancel</button>
            <button class="btn complete">Complete Workout</button>
        </div>

        <WorkoutCompleted/>
    </div>
</template>

<style scoped>

    .add-wrapper {
        display: flex;
        justify-content: center;
    }

    .add-set {
        display: flex;
        text-align: center;
        padding: 0.5rem 1rem;
        margin-top: 1rem;
        color: var(--button-primary);
        background: none;
        border: none;
        cursor: pointer;
        font-weight: 600;
        font-size: 1rem;
    }

    .btn {
        padding: 0.8rem 1.2rem;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 1rem;
        font-weight: 500;
        transition: background-color 0.3s ease;
        color: white;
    }

    .btn:hover {
        /**opacity: 0.9;*/
    }

    .btn.cancel {
        background-color: #C75450;
    }

    .btn.cancel:hover {
        background-color: #A6393B;
    }

    .btn.complete {
        background-color: var(--button-primary);
    }

    .btn.complete:hover {
        background-color: #1E3A8A;
    }

    .btn-complete {
        background-color: var(--button-lighter);
        border: none;
        border-radius: 5px;
        padding: 0.5rem 1rem;
        cursor: pointer;
        color: white;
    }

    h2 {
        font-size: 1.5rem;
        font-weight: 700;
    }

    .set-details {
        margin-top: 1rem;
    }

    .sets-table {
        width: 100%;
        margin-top: 1rem;
        border-collapse: collapse;
    }

    .sets-table th, .sets-table td {
        padding: 0.5rem;
        text-align: center;
        padding: 10px;;
        border-bottom: 1px solid #ddd;
    }

    .sets-table th {
        font-weight: 600;
        background-color: #F3F4F6;
        color: #111827;
    }

    .sets-table tr:last-child td {
        border-bottom: none;
    }

    .sets-table tr.completed {
        background-color: rgba(84, 214, 89, 0.3);
    }

    .sets-table input{
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #E5E7EB;
        border-radius: 4px;
        font-size: 1rem;
    }

    .workout-btns {
        display: flex;
        justify-content: flex-end;
        gap: 1rem;
        margin-top: 2rem;
    }

    .professor-note {
        background-color: #FEF3C7;
        border-left: 4px solid #F6A71E;
        padding: 1rem;
        margin-bottom: 20px;
        border-radius: 8px;
    }

    .professor-note-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-weight: 600;
        margin-bottom: 0.5rem;
    }

    .professor-note-content {
        font-size: 0.9rem;
        color: #111827;
    }

    .exercise-header {
        display: flex;
        justify-content: flex-start;
        gap: 1rem;
        align-items: center;
        margin-bottom: 1rem;
    }

    .ex-name {
        font-size: 1.8rem;
        font-weight: 700;
    }

    .ex-muscle {
        font-size: 0.8rem;
        background-color: rgb(158, 158, 158);
        padding: 0.4rem 1.5rem;
        color: white;
        border-radius: 1rem;
    }

    .exercise-details {
        background-color: var(--background-white-color);
        border-radius: 8px;
        padding: 1rem;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .workout-main-layour {
        display: grid;
        grid-template-columns: 1fr 2fr;
        gap: 2rem;
    }

    .exercise-list {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        max-height: 65vh;
        overflow-y: auto;
        padding-right: 0.8rem;
        scroll-behavior: smooth;
        scrollbar-width: thin; 
    }

    .exercise-list::-webkit-scrollbar {
        width: 8px;
    }

    .exercise-list::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 10px;
    }

    .exercise-list::-webkit-scrollbar-thumb {
        background: #c1c1c1;
        border-radius: 10px;
    }

    .exercise-list::-webkit-scrollbar-thumb:hover {
        background: #a8a8a8;
    }

    

    .exercise-card {
        background-color: var(--background-white-color);
        border-radius: 8px;
        padding: 1rem;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        cursor: pointer;
        transition: background-color 0.3s ease;
        position: relative;
    }

    .exercise-card:hover {
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
    }

    .exercise-card.active {
        border-left: 5px solid var(--accent-color);
    }


    .exercise-name {

        font-size: 1.2rem;
        font-weight: 600;
        color: var(--text-dark-gray);
    }

    .exercise-muscle {
        font-size: 1rem;
        color: var(--text-light-gray);
        margin-top: 0.5rem;
    }

    .exercise-status {
        position: absolute;
        top: 50%;
        right: 1rem;
        width: 20px;
        height: 20px;
        border-radius: 50%;
        border: 2px solid var(--accent-color);
    }

    .workout-page{
        max-width: 1500px;
        margin: 0 auto;
        padding: 2rem;
    }

    .workout-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }
    
    .workout-header h1 {
        font-size: 2rem;
        font-weight: 700;
        color: var(--accent-color);
    }

    .workout-data {
        display: flex;
        align-items: center;
        gap: 2rem;
        margin-bottom: 2rem;
        justify-content: flex-start;
    }

    .workout-data-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 1rem;
        color: var(--text-light-gray);
    }
</style>
