<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';
import { API_PATHS } from '../api_paths';
import { useUserStore } from '../stores/userStore';
import { Icon } from '@iconify/vue';
import AddClientModal from '@/components/AddClientModal.vue';

const userStore = useUserStore();
const route = useRoute();
const router = useRouter();

const addClientModalState = ref(false);

const students = ref([]);
const searchQuery = ref('');
const isLoading = ref(true);
const error = ref(null);

const sortColumn = ref('');
const sortOrder = ref(1); // 1 asc, -1 desc

const fetchStudents = async () => {
    isLoading.value = true;
    error.value = null;
    try {
        const response = await axios.get(API_PATHS.GET_CLIENTS + userStore.getUserId, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        });
        students.value = response.data;
        console.log("Fetched students:", students.value);
    } catch (err) {
        console.error("Error fetching students:", err);
    } finally {
        isLoading.value = false;
    }
};

const sortBy = (column) => {
    if (sortColumn.value === column) {
        sortOrder.value = sortOrder.value * -1;
    } else {
        sortColumn.value = column;
        sortOrder.value = 1; // default is asc
    }
};

const getSortIcon = (column) => {
    if (sortColumn.value !== column) {
        return 'line-md:arrow-down'; // default icon
    } else {
        if (sortOrder.value === 1) {
            return 'line-md:arrow-up'; 
        } else {
            return 'line-md:arrow-down';
        }
    }
};

const sortedAndFilteredStudents = computed(() => {
    let currentStudents = [...students.value]

    // apply search filter
    const query = searchQuery.value.toLowerCase().trim();
    if (query) {
        currentStudents = currentStudents.filter(student =>
            student.name.toLowerCase().includes(query) ||
            student.email.toLowerCase().includes(query) ||
            String(student.id).includes(query)
        );
    }

    // then apply sort
    if (sortColumn.value !== '') {
        currentStudents = currentStudents.sort((a, b) => {
            let valA, valB;

            if (sortColumn.value === "latestActivity") {
                const dateA = a.latestActivity === "Never" ? new Date(0) : parseDateString(a.latestActivity);
                const dateB = b.latestActivity === "Never" ? new Date(0) : parseDateString(b.latestActivity);
                return (dateA.getTime() - dateB.getTime()) * sortOrder.value;
            } else if (sortColumn.value === "height" || sortColumn.value === "currentWeight") {
                valA = parseFloat(a[sortColumn.value]) || 0; // default values in case its null or undefined
                valB = parseFloat(b[sortColumn.value]) || 0;
            } else if (sortColumn.value === "id") {
                valA = parseInt(a[sortColumn.value]);
                valB = parseInt(b[sortColumn.value]);
            }
            else {
                valA = String(a[sortColumn.value]).toLowerCase();
                valB = String(b[sortColumn.value]).toLowerCase();
            }

            if (valA < valB) return -1 * sortOrder.value;
            if (valA > valB) return 1 * sortOrder.value;
            return 0;
        });
    }

    return currentStudents;
});

const parseDateString = (dateString) => {
    const [day, month, year] = dateString.split('/').map(Number);
    return new Date(year, month - 1, day);
};


const viewInfo = (studentId) => {
    router.push(`/students/${studentId}`);
    console.log("View info for student:", studentId);
};

const notifyStudent = async (studentId) => {
    console.log("Notify student:", studentId);
    try {
        // still need the logic 
        alert(`Notification sent to student ${studentId}! (Simulated)`);
    } catch (err) {
        console.error("Error sending notification:", err);
        alert("Failed to send notification.");
    }
};

const removeStudent = async (studentId) => {
    if (confirm(`Are you sure you want to remove student ${studentId}? This cannot be undone.`)) {
        try {
            await axios.delete(API_PATHS.REMOVE_CLIENT(studentId), {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            });
            students.value = students.value.filter(s => s.id !== studentId);
            alert(`Student ${studentId} removed successfully!`);
        } catch (err) {
            console.error("Error removing student:", err);
            error.value = "Failed to remove student. Please try again.";
        }
    }
};

const addNewStudent = () => {
    router.push('/professor/add-student');
    console.log("Add new student clicked");
};

onMounted(() => {
    fetchStudents();
});

const toggleAddClientModal = () => {
    addClientModalState.value = !addClientModalState.value;
    console.log("Toggling Add Client Modal:", addClientModalState.value);
};

const handleClientAssigned = () => {
    // has to fetch again because of the other data
    fetchStudents();
    addClientModalState.value = false; 
};

</script>

<template>
    <div class="clients-page-container">
        <div class="header">
            <h1>Students</h1>
            <button @click="toggleAddClientModal" class="add-button">+ Add new student</button>
        </div>

        <div class="client-list-card">
            <div class="search-bar">
                <input
                    type="text"
                    v-model="searchQuery"
                    placeholder="Search for Student..."
                />
            </div>

            <div v-if="isLoading" class="loading-message">
                Loading students...
            </div>
            <div v-else-if="error" class="error-message">
                {{ error }}
            </div>
            <div v-else-if="students.length === 0 && !isLoading" class="empty-list-message">
                No students found for this professor.
            </div>
            <div v-else class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th @click="sortBy('id')">
                                <span class="th-content">
                                    Student ID
                                    <Icon class="icon" :icon="getSortIcon('id')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('name')">
                                <span class="th-content">
                                    Student Name
                                    <Icon class="icon" :icon="getSortIcon('name')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('email')">
                                <span class="th-content">
                                    Email
                                    <Icon class="icon" :icon="getSortIcon('email')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('height')">
                                <span class="th-content">
                                    Height
                                    <Icon class="icon" :icon="getSortIcon('height')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('currentWeight')"> <span class="th-content">
                                    Weight
                                    <Icon class="icon" :icon="getSortIcon('currentWeight')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('latestActivity')"> <span class="th-content">
                                    Latest Activity
                                    <Icon class="icon" :icon="getSortIcon('latestActivity')" width="15" height="15" />
                                </span>
                            </th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="student in sortedAndFilteredStudents" :key="student.id">
                            <td>
                                {{ student.id }}
                            </td>
                            <td>{{ student.name }}</td>
                            <td>{{ student.email }}</td>
                            <td>{{ student.height ? student.height + ' cm' : 'N/A' }}</td>
                            <td>{{ student.currentWeight ? student.currentWeight + ' kg' : 'N/A' }}</td>
                            <td>{{ student.latestActivity }}</td>
                            <td class="actions">
                                <button @click="viewInfo(student.id)" class="action-button view-info">View Info</button>
                                <button @click="notifyStudent(student.id)" class="action-button notify">Notify</button>
                                <button @click="removeStudent(student.id)" class="action-button delete">Delete</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <AddClientModal 
            :modalState="addClientModalState"
            @close="toggleAddClientModal" 
            @assign-client="handleClientAssigned" />
        />
    </div>
</template>

<style scoped>
    .clients-page-container {
        padding: 20px;
        font-family: sans-serif;
        background-color: var(--background-white-color);
        min-height: 100vh;
    }

    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .header h1 {
        color: #333;
        margin: 0;
        font-size: 2rem; 
        font-weight: 700; 
    }

    .add-button {
        background-color: var(--button-lighter);
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 16px;
        transition: background-color 0.2s;
    }

    .add-button:hover {
        filter: brightness(1.1); 
    }

    .client-list-card { 
        background-color: var(--background-white-color); 
        border-radius: 10px;
        padding: 20px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); 
    }

    .client-list-card h2 {
        font-size: 1.5rem;
        margin-bottom: 20px;
    }

    .search-bar {
        margin-bottom: 20px;
    }

    .search-bar input {
        width: 100%;
        padding: 10px;
        border: 1px solid #cccccc; 
        border-radius: 5px;
        font-size: 16px;
    }

    .loading-message, .error-message, .empty-list-message {
        text-align: center;
        padding: 20px;
        color: #666;
    }

    .error-message {
        color: #dc3545;
    }

    .table-responsive {
        overflow-x: auto;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 0;
    }

    th, td {
        text-align: left;
        padding: 12px 15px;
        border-bottom: 1px solid #e0e0e0; 
    }

    th {
        background-color: var(--button-lighter);
        color: white; 
        font-weight: bold;
        cursor: pointer; 
        position: relative;
    }

    tr:hover {
        background-color: #f1f1f1;
    }

    .th-content {
        display: inline-flex;
        align-items: center;
        gap: 5px;
    }

    .clients-table th .icon {
        vertical-align: middle;
    }

    .client-row {
        display: flex;
        align-items: center;
    }

    .avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background-color: #cccccc;
        margin-right: 10px;
        cursor: pointer;
    }

    .actions { 
        display: flex;
        gap: 10px;
    }

    .actions button {
        border: none;
        border-radius: 4px;
        padding: 8px 12px; 
        font-size: 14px;
        cursor: pointer;
        transition: background-color 0.2s, opacity 0.2s;
    }

    .action-button.view-info {
        background-color: #4169E1; 
        color: white;
    }
    .action-button.view-info:hover {
        filter: brightness(1.1);
    }

    .action-button.notify { 
        background-color: #FFD700; 
        color: black; 
    }
    .action-button.notify:hover {
        filter: brightness(1.1);
    }

    .action-button.delete { 
        background-color: #DC143C;
        color: white;
    }
    .action-button.delete:hover {
        filter: brightness(1.1);
    }
</style>