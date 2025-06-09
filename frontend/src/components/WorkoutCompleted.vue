<script setup>
    import { ref, computed } from 'vue';
    import { Icon } from '@iconify/vue';
    
    import { useToast } from 'vue-toastification'
    const toast = useToast()

    const props = defineProps({
        popupState: {
            type: Boolean,
            default: false
        },
        workoutExecutionData: {
            type: Object,
            default: null
        },
        elapsedTime: {
            type: Number,
            default: 0
        }
    });

    const emit = defineEmits(['close', 'save']);

    const feedbackNote = ref('');
    const workoutName = ref(props.workoutExecutionData?.workoutPlan?.name || 'Workout Name');


    const workoutDate = computed(() => {
        if (props.workoutExecutionData?.executionDate) {
            const date = new Date(props.workoutExecutionData.executionDate);
            return date.toLocaleDateString('en-US', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit'
            });
        }
        return 'N/A';
    });

    const formattedDuration = computed(() => {
        const totalSeconds = Math.floor(props.elapsedTime / 1000);
        const hours = Math.floor(totalSeconds / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);
        const seconds = totalSeconds % 60;

        const parts = [];
        if (hours > 0) parts.push(`${hours}h`);
        if (minutes > 0) parts.push(`${minutes}m`);
        if (seconds > 0 || (hours === 0 && minutes === 0)) parts.push(`${seconds}s`);

        return parts.join(' ');
    });

    const totalExercises = computed(() => {
        return props.workoutExecutionData?.exerciseExecutions?.length || 0;
    });

    const completedExercises = computed(() => {
        let count = 0;
        if (props.workoutExecutionData?.exerciseExecutions) {
            props.workoutExecutionData.exerciseExecutions.forEach(exerciseExecution => {
                const plannedSets = exerciseExecution.exerciseData?.plannedSets || [];

                const originalPlannedSets = plannedSets.filter(set => !set.isAdded);
                const addedCompletedSets = plannedSets.filter(set => set.isAdded && set.completed);

                if (originalPlannedSets.length > 0) {
                    const allOriginalPlannedCompleted = originalPlannedSets.every(set => set.completed);
                    if (allOriginalPlannedCompleted) {
                        count++;
                    }
                } else if (addedCompletedSets.length > 0) {
                    count++;
                }
            });
        }
        return count;
    });

    const totalSets = computed(() => {
        return props.workoutExecutionData?.exerciseExecutions?.reduce((total, ex) => {
            // doesnt count added sets
            return total + (ex.exerciseData?.plannedSets?.filter(set => !set.isAdded).length || 0);
        }, 0) || 0;
    });

    // sets completed (including both planned and added sets)
    const completedSets = computed(() => {
        return props.workoutExecutionData?.exerciseExecutions?.reduce((total, ex) => {
            // sum all sets that are .completed=true
            return total + (ex.exerciseData?.plannedSets?.filter(set => set.completed).length || 0);
        }, 0) || 0;
    });


    const totalWeightLifted = computed(() => {
        return props.workoutExecutionData?.exerciseExecutions?.reduce((total, ex) => {
            return total + (ex.exerciseData?.plannedSets?.reduce((sum, set) => {
                return sum + (set.completed ? (set.weightPerformed || 0) * (set.repsPerformed || 0) : 0);
            }, 0) || 0);
        }, 0) || 0;
    });


    const closePopup = () => {
        emit('close');
    };

    const saveWorkout = () => {
        emit('save', feedbackNote.value);
        feedbackNote.value = '';
        toast.success('Workout Completed Successfully!');
    };
</script>
<template>
    <div v-if="popupState" class="workout-completed-overlay">
        <div class="workout-completed-container">
            <div class="workout-completed-header">
                <h2>Workout Completed</h2>
                <button class="close-button" @click="closePopup">X</button>
            </div>

            <div class="workout-details-summary">
                <div class="workout-details-header-top">
                    <div class="workout-name-date">
                        <h3>{{ workoutName }}</h3>
                        <p class="workout-date">{{ workoutDate }}</p>
                    </div>
                    <div class="workout-duration">
                        <Icon icon="mdi:clock-outline" width="24" height="24" />
                        <span>{{ formattedDuration }}</span>
                    </div>
                </div>

                <div class="workout-stats-grid">
                    <div class="stat-item">
                        <Icon icon="mdi:dumbbell" width="24" height="24" />
                        <span class="stat-value">{{ totalWeightLifted.toFixed(0) }} kg</span> <span class="stat-label">Total Volume</span>
                    </div>
                    <div class="stat-item">
                        <Icon icon="ic:outline-format-list-numbered" width="24" height="24" />
                        <span class="stat-value">{{ completedSets }} / {{ totalSets }}</span>
                        <span class="stat-label">Sets Completed</span>
                    </div>
                    <div class="stat-item">
                        <Icon icon="mdi:run-fast" width="24" height="24" />
                        <span class="stat-value">{{ completedExercises }} / {{ totalExercises }}</span>
                        <span class="stat-label">Exercises Done</span>
                    </div>
                </div>

                <div class="workout-feedback">
                    <h3>Your Feedback / Notes</h3>
                    <textarea v-model="feedbackNote" placeholder="Any notes about this workout? Feeling, performance, etc."></textarea>
                </div>
            </div>

            <div class="workout-completed-actions">
                <button class="btn btn-save" @click="saveWorkout">Save Workout</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* Overlay styles */
.workout-completed-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    backdrop-filter: blur(5px); 
}

.workout-completed-container {
    background-color: #ffffff;
    border-radius: 12px;
    padding: 2.5rem;
    width: 90%;
    max-width: 600px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(-20px); }
    to { opacity: 1; transform: translateY(0); }
}

.workout-completed-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #eee;
    padding-bottom: 1rem;
    margin-bottom: 1rem;
}

.workout-completed-header h2 {
    margin: 0;
    font-size: 1.8rem;
    color: #333;
    font-weight: 700;
}

.close-button {
    background: none;
    border: none;
    font-size: 1.8rem;
    color: #888;
    cursor: pointer;
    transition: color 0.2s ease;
}

.close-button:hover {
    color: #555;
}

.workout-details-summary {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.workout-details-header-top {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 1rem;
}

.workout-name-date h3 {
    margin: 0;
    font-size: 1.5rem;
    color: #212529;
    font-weight: 600;
}

.workout-date {
    margin: 0.2rem 0 0;
    color: #6c757d;
    font-size: 0.95rem;
}

.workout-duration {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background-color: #e9f5ff;
    padding: 0.6rem 1.2rem;
    border-radius: 25px;
    font-size: 1.1rem;
    font-weight: 600;
    color: #007bff;
    box-shadow: 0 2px 6px rgba(0, 123, 255, 0.1);
}

.workout-stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 1rem;
    margin-bottom: 1.5rem;
    text-align: center;
}

.stat-item {
    background-color: #f8f9fa;
    border: 1px solid #e0e0e0;
    border-radius: 10px;
    padding: 1.2rem 1rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.95rem;
    color: #555;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item .iconify {
    color: #007bff;
    font-size: 1.8rem;
}

.stat-value {
    font-size: 1.4rem;
    font-weight: 700;
    color: #333;
}

.stat-label {
    font-size: 0.85rem;
    color: #777;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.workout-feedback {
    margin-top: 1rem;
}

.workout-feedback h3 {
    font-size: 1.3rem;
    color: #333;
    margin-bottom: 0.8rem;
    font-weight: 600;
}

.workout-feedback textarea {
    width: 100%;
    min-height: 100px;
    padding: 1rem;
    border: 1px solid #ced4da;
    border-radius: 8px;
    font-size: 1rem;
    font-family: inherit;
    resize: vertical;
    box-sizing: border-box; 
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.workout-feedback textarea:focus {
    border-color: #007bff;
    outline: none;
    box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.workout-completed-actions {
    text-align: center;
    padding-top: 1.5rem;
    border-top: 1px solid #eee;
}

.btn-save {
    background-color: #28a745;
    color: white;
    border: none;
    padding: 0.9rem 2rem;
    border-radius: 8px;
    cursor: pointer;
    font-size: 1.1rem;
    font-weight: 600;
    transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.btn-save:hover {
    background-color: #218838;
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(40, 167, 69, 0.3);
}

.btn-save:active {
    transform: translateY(0);
}

@media (max-width: 500px) {
    .workout-completed-container {
        padding: 1.5rem;
    }

    .workout-completed-header h2 {
        font-size: 1.5rem;
    }

    .workout-duration {
        font-size: 1rem;
        padding: 0.5rem 1rem;
    }

    .stat-value {
        font-size: 1.2rem;
    }
}
</style>