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
        },
        changeDirection: {
            type: String,
            default: null
        }
    })

    const changeIcon = computed(() => {
        if (props.changeDirection === 'gain') {
            return 'mdi:arrow-up'; 
        } else if (props.changeDirection === 'loss') {
            return 'mdi:arrow-down'; 
        }
        return ''; 
    });

    const changeClass = computed(() => {
        if (props.changeDirection === 'gain') {
            return 'stat-change weight-gain';
        } else if (props.changeDirection === 'loss') {
            return 'stat-change weight-loss';
        }
        return 'stat-change';
    });

    const displayValue = computed(() => {
        if (typeof props.value === 'number') {
            if (props.label === 'Workouts Completed') {
                return props.value; 
            } else if (props.label === 'Total Weight Lifted') {
                return props.value.toFixed(2); //
            } else if (props.label === 'Current Weight') {
                return props.value.toFixed(1); 
            }
        }
        return props.value; 
    });

</script>

<template>
    <div class="stat-card">
        <div class="stat-label">
            {{ label }}
        </div>
        <div class="stat-value">
            {{ displayValue }} <span v-if="unit">{{ unit }}</span>
        </div>
        <div v-if="change !== null && changeDirection !== 'no_change'" :class="changeClass">
            <Icon :icon="changeIcon" v-if="changeIcon" />
            <span>{{ change }} {{ unit }}</span>
            <span v-if="changeDirection === 'gain'">Gain</span>
            <span v-else-if="changeDirection === 'loss'">Loss</span>
        </div>
         <div v-else-if="change !== null && changeDirection === 'no_change'" class="stat-change no-change">
            <span>No Change</span>
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