<script setup>
import { computed } from 'vue'
import { Icon } from '@iconify/vue'

const props = defineProps({
    label: {
        type: String,
        required: true
    },
    value: {
        type: [Number, String],
        required: true
    },
    unit: {
        type: String,
        default: ''
    },
    change: {
        type: [Number, String, null],
        default: null
    }
})

const numericChange = computed(() => {
    return typeof props.change === 'string' ? parseFloat(props.change) : props.change
})

const changeDirection = computed(() => {
    if (numericChange.value > 0) return 'gain'
    if (numericChange.value < 0) return 'loss'
    return 'no_change'
})

const changeIcon = computed(() => {
    if (changeDirection.value === 'gain') return 'mdi:arrow-up'
    if (changeDirection.value === 'loss') return 'mdi:arrow-down'
    return ''
})

const changeClass = computed(() => {
    if (changeDirection.value === 'gain') return 'stat-change weight-gain'
    if (changeDirection.value === 'loss') return 'stat-change weight-loss'
    return 'stat-change no-change'
})

const displayValue = computed(() => {
    if (typeof props.value === 'number') {
        if (props.label === 'Workouts Completed') return props.value
        if (props.label === 'Total Weight Lifted') return props.value.toFixed(2)
        if (props.label === 'Current Weight') return props.value.toFixed(1)
    }
    return props.value
})
</script>
<template>
  <div class="stat-card">
    <div class="stat-label">{{ label }}</div>
    <div class="stat-value">
      {{ displayValue }} <span v-if="unit">{{ unit }}</span>
    </div>

    <div v-if="numericChange !== null && changeDirection !== 'no_change'" :class="changeClass">
      <Icon :icon="changeIcon" v-if="changeIcon" />
      <span>
        {{ numericChange > 0 ? '+' : '' }}{{ numericChange }} {{ unit }}
      </span>
    </div>
  </div>
</template>



<style scoped>
    .stat-card {
        background: white;
        border-radius: 8px;
        padding: 16px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        transition: transform 0.2s ease, box-shadow 0.2s ease;
    }

    .stat-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }

    .stat-label {
        font-size: 14px;
        color: #757575;
        margin-bottom: 8px;
    }

    .stat-value {
        font-size: 32px;
        font-weight: 700;
        margin: 0;
    }

    .stat-change {
        font-size: 14px;
        display: flex;
        align-items: center;
        margin-top: 4px;
        gap: 4px;
    }

    .weight-loss { 
        color: #4CAF50; 
    }

    .weight-gain {
        color: #f44336; 
    }
    .no-change {
        color: #757575; 
    }

</style>