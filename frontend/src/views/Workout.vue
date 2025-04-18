<script setup>
    import { ref, onMounted } from 'vue'
    import { useRoute } from 'vue-router'
    import { Icon } from '@iconify/vue'
    
    const route = useRoute()

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
            { name:'Exercise 1', sets :3 , muscle:'Legs'},
            { name: 'Exercise 2', sets: 4, muscle: 'Chest' },
            { name: 'Exercise 3', sets: 2, muscle: 'Back' },
            { name: 'Exercise 1', sets: 3, muscle: 'Legs' },
            { name: 'Exercise 2', sets: 4, muscle: 'Chest' },
            { name: 'Exercise 3', sets: 2, muscle: 'Back' },
            { name: 'Exercise 1', sets: 3, muscle: 'Legs' },
        ],
        exerciseCount: 5,
    })

    // depois isto fica computed
    const currentExerciseIndex = ref(0)
    const currentExercise = ref(workout.value.exercises[currentExerciseIndex.value])

    const selectExercise = (index) => {
        currentExerciseIndex.value = index
    }

    // falta design no lado esquerdo de completed e assim, falta ainda decidir como organizar workouts na backend

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
            </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
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
        font-size: 1.5rem;
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
        max-width: 1300px;
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
