<script setup>
    import { ref, watch } from 'vue'

    const props = defineProps({
        initialScheduleType: {
            type: String,
            required: true
        },
        initialScheduledDays: {
            type: Array,
            default: () => []
        }
    })

    const emit = defineEmits(['update-scheduleType', 'update-scheduledDays'])

    const scheduleType = ref(props.initialScheduleType)
    const scheduledDays = ref(props.initialScheduledDays)

    const weekdays = ref([
        { name: 'M', full: 'MONDAY', selected: false },
        { name: 'T', full: 'TUESDAY', selected: false },
        { name: 'W', full: 'WEDNESDAY', selected: false },
        { name: 'T', full: 'THURSDAY', selected: false },
        { name: 'F', full: 'FRIDAY', selected: false },
        { name: 'S', full: 'SATURDAY', selected: false },
        { name: 'S', full: 'SUNDAY', selected: false }
    ]);

    // watch for changes in scheduleType and emit updates
    watch(() => props.initialScheduleType, (newVal) => {
        scheduleType.value = newVal;
    }, { immediate: true });

    watch(() => props.initialScheduledDays, (newVal) => {
        weekdays.value.forEach(day => {
            day.selected = newVal.includes(day.full);
        });   
    }, { immediate: true });

    watch (scheduleType, (newType) => {
        emit('update-scheduleType', newType);
    });

    watch (weekdays, (newDays) => {
        const selectedDays = newDays.
            filter(day => day.selected)
            .map(day => day.full)
            .sort();
        emit('update-scheduledDays', selectedDays);
    }, { deep: true });


</script>

<template>
    <div class="edit-card">
        <h2>Schedule Type</h2>
        <div class="schedule-types">
            <div
                class="schedule-type"
                :class="{ 'active': scheduleType === 'Free' }"
                @click="scheduleType = 'Free'"
            >
                <div class="schedule-type-name">Freedom</div>
                <p>Execute the workout when I have the free time for it</p>
            </div>

            <div
                class="schedule-type"
                :class="{ 'active': scheduleType === 'Fixed' }"
                @click="scheduleType = 'Fixed'"
            >
                <div class="schedule-type-name">Fixed</div>
                <p>Select the day/s of the week for this workout</p>
            </div>
        </div>

        <div v-if="scheduleType === 'Fixed'">
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
</template>

<style scoped>
    .edit-card {
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
    .schedule-types {
        display: flex;
        gap: 1rem;
        margin-bottom: 1rem;
    }

    .schedule-type {
        flex: 1;
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

    .schedule-type-name {
        font-weight: 700;
        margin-bottom: 0.5rem;
    }

    p {
        font-size: 0.9rem;
        color: #6b7280;
    }

    .days-title {
        font-weight: 600;
        font-size: 1rem;
    }

    .days-select-section {
        display: flex;
        gap: 1rem;
        margin-top: 1rem;
    }

    .days-selection {
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
</style>