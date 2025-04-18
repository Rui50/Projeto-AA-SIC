<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'

    const props = defineProps({
        label: {
            type: String,
            required: true
        },
        value: {
            type: Number,
            required: true
        }
    })

    const router = useRouter()

</script>

<template>
    <div class="stat-card">
        <div class="stat-label">
            {{ label }} kg  <!-- later {{ unit }}-->
        </div>
        <div class="stat-value">
            {{ value }} kg
        </div>
        <span v-if="change > 0"></span>
        <span v-else-if="change < 0"></span>
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
</style>