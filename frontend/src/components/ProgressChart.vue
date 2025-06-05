<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { Bar, Line } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, PointElement } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, PointElement);

const props = defineProps({
    chartType: {
        type: String,
        required: true,
    },
    chartData: {
        type: Array,
        required: true,
    }
});

const chartRef = ref(null);

const chartConfig = ref({
    labels: [],
    datasets: []
});

const chartOptions = computed(() => ({
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
            display: true,
            position: 'top',
            labels: {
                font: {
                    size: 14,
                    family: 'Inter, sans-serif'
                },
                color: '#333'
            }
        },
        title: {
            display: true,
            text: chartTitle.value,
            font: {
                size: 18,
                weight: 'bold',
                family: 'Inter, sans-serif'
            },
            color: '#333'
        },
        tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            titleFont: {
                size: 14,
                weight: 'bold',
                family: 'Inter, sans-serif'
            },
            bodyFont: {
                size: 13,
                family: 'Inter, sans-serif'
            },
            padding: 10,
            cornerRadius: 5
        }
    },
    scales: {
        x: {
            type: 'category',
            title: {
                display: true,
                text: 'Date',
                font: {
                    size: 14,
                    weight: 'bold',
                    family: 'Inter, sans-serif'
                },
                color: '#555'
            },
            ticks: {
                font: {
                    size: 12,
                    family: 'Inter, sans-serif'
                },
                color: '#666'
            },
            grid: {
                display: false
            }
        },
        y: {
            title: {
                display: true,
                text: getYAxisLabel(props.chartType),
                font: {
                    size: 14,
                    weight: 'bold',
                    family: 'Inter, sans-serif'
                },
                color: '#555'
            },
            beginAtZero: true,
            ticks: {
                font: {
                    size: 12,
                    family: 'Inter, sans-serif'
                },
                color: '#666',
                callback: function(value) {
                    if (props.chartType === 'volumePerWorkoutPlan') { // depois trocar para lb tambem dependendo da metrica do user
                        return value + ' kg';
                    }
                    return value;
                }
            },
            grid: {
                color: 'rgba(0, 0, 0, 0.08)' 
            }
        }
    }
}));

const chartTitle = computed(() => {
    switch (props.chartType) {
        case 'volumePerWorkoutPlan':
            return 'Volume Per Workout';
        /*
        case 'bodyWeight':
            return 'Body Weight Progress'
        case 'totalVolume':
            return 'Total Volume Lifted'
        case 'workoutsCompleted':
            return 'Workouts Completed Over Time'*/
        default:
            return 'Performance Chart';
    }
});

const getYAxisLabel = (type) => {
    switch (type) {
        //case 'bodyWeight': return 'Weight (kg)';
        //case 'totalVolume':
        case 'volumePerWorkoutPlan': return 'Volume (kg)'; 
        //case 'workoutsCompleted': return 'Count';
        default: return 'Value';
    }
};

const processChartData = () => {
    if (!props.chartData || props.chartData.length === 0) {
        chartConfig.value = {
            labels: [],
            datasets: []
        };
        return;
    }

    const labels = props.chartData.map(item => item.date);
    const dataValues = props.chartData.map(item => item.value);

    let datasetLabel = '';
    let backgroundColor = '';
    let borderColor = '';
    let chartTypeForDisplay = Bar; // default bar chart

    switch (props.chartType) {
        case 'volumePerWorkoutPlan': 
            datasetLabel = 'Volume Lifted (kg)';
            backgroundColor = 'rgba(78, 106, 245, 0.6)';
            borderColor = 'rgba(78, 106, 245, 1)'; 
            break;
        /*
        case 'bodyWeight':
            datasetLabel = 'Body Weight (kg)';
            backgroundColor = 'rgba(75, 192, 192, 0.6)'; // Greenish-blue for lines
            borderColor = 'rgba(75, 192, 192, 1)';
            chartTypeForDisplay = Line;
            break;
        case 'totalVolume':
        case 'workoutsCompleted':
            datasetLabel = props.chartType === 'totalVolume' ? 'Total Volume (kg)' : 'Workouts Completed';
            backgroundColor = 'rgba(255, 159, 64, 0.6)'; // Orange
            borderColor = 'rgba(255, 159, 64, 1)';
            break;
        */
    }

    chartConfig.value = {
        labels: labels,
        datasets: [{
            label: datasetLabel,
            data: dataValues,
            backgroundColor: backgroundColor,
            borderColor: borderColor,
            borderWidth: 1,
            type: chartTypeForDisplay === Line ? 'line' : 'bar',
            pointRadius: chartTypeForDisplay === Line ? 5 : 0, 
            pointBackgroundColor: chartTypeForDisplay === Line ? borderColor : undefined, 
            pointBorderColor: '#fff', 
            pointHoverRadius: chartTypeForDisplay === Line ? 8 : undefined, 
            tension: chartTypeForDisplay === Line ? 0.4 : 0,
            fill: false, 
            barPercentage: 0.8,
            categoryPercentage: 0.8 
        }]
    };
};

watch(() => [props.chartData, props.chartType], () => {
    processChartData();
}, { immediate: true }); 
</script>

<template>
    <div class="progress-chart">
        <component
            :is="chartConfig.datasets.length > 0 && chartConfig.datasets[0].type === 'line' ? Line : Bar"
            ref="chartRef"
            :data="chartConfig"
            :options="chartOptions"
        />
    </div>
</template>

<style scoped>
.progress-chart {
    min-height: 300px;
    position: relative;
    height: 400px; 
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    box-sizing: border-box; 
}

.progress-chart canvas {
    max-width: 100%;
    height: 100%; 
}
</style>