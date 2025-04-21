<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'

    const router = useRouter()

    const searchText = ref('')
    const clients = ref([])

    const sortColumn= ref('')
    const sortOrder = ref(1) // 1 ascending, -1 descending

    const sortBy = (column) => {
        if (sortColumn.value === column){
            sortOrder.value = sortOrder.value * -1
        } else {
            sortColumn.value = column
            sortOrder.value = 1 // default to ascending
        }
    }

    const getSortIcon = (column) => {
        if (sortColumn.value !== column) {
            return 'line-md:arrow-down'
        } else {
            if (sortOrder.value === 1) {
                return 'line-md:arrow-up'
            } else {
                return 'line-md:arrow-down'
            }
        }
    }


    const clientList = computed(() => {
        let clientsFiltered = [...clients.value];

        if(searchText.value.length !== 0){
            clientsFiltered = clients.value.filter(client => {
                return client.name.toLowerCase().includes(searchText.value.toLowerCase())
            })
        }

        if(sortColumn.value !==''){
            clientsFiltered = clientsFiltered.sort((a, b) => {
                if (sortColumn.value === "lastActivity") {
                    const dateA = new Date(a.lastActivity)
                    const dateB = new Date(b.lastActivity)
                    return (dateA - dateB) * sortOrder.value
                }
                if (a[sortColumn.value] < b[sortColumn.value]) return -1 * sortOrder.value
                if (a[sortColumn.value] > b[sortColumn.value]) return 1 * sortOrder.value

                return 0
            })
        }

        return clientsFiltered
    })      

    onMounted(() => {
        // Fetch clients from backend
        // For now, test using static data, pop by copilot
        clients.value = [
            { id: 1, name: 'John Doe', height: '180 cm', weight: '75 kg', lastActivity: '2023-10-01' },
            { id: 3, name: 'Mike Johnson', height: '175 cm', weight: '80 kg', lastActivity: '2023-10-03' },
            { id: 2, name: 'Jane Smith', height: '165 cm', weight: '60 kg', lastActivity: '2023-10-02' },
            { id: 4, name: 'Emily Davis', height: '170 cm', weight: '65 kg', lastActivity: '2023-10-04' },
        ]
    })


</script>

<template>
    <div class="clients-page">
        <div class="clients-header">
            <h1>Clients</h1>
            <button class="add-client-button">+ Add new client</button>
        </div>

        <div class="client-list-container">
            <h2>Client List</h2>
            <div class="search-bar">
                <input type="text" placeholder="Search for Client..." v-model="searchText" />
            </div>

            <div class="table">
                <table class="clients-table">
                    <thead>
                        <tr>
                            <th @click="sortBy('id')">
                                <span class="th-content">
                                    Client ID
                                    <Icon class="icon" :icon="getSortIcon('id')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('name')">
                                <span class="th-content">
                                    Client Name
                                    <Icon class="icon" :icon="getSortIcon('name')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('height')">
                                <span class="th-content">
                                    Height
                                    <Icon class="icon" :icon="getSortIcon('height')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('weight')">
                                <span class="th-content">
                                    Weight
                                    <Icon class="icon" :icon="getSortIcon('weight')" width="15" height="15" />
                                </span>
                            </th>
                            <th @click="sortBy('lastActivity')">
                                <span class="th-content">
                                    Last Activity
                                    <Icon class="icon" :icon="getSortIcon('lastActivity')" width="15" height="15" />
                                </span>
                            </th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="client in clientList" :key="client.id">
                            <td>
                                <div class="client-row">
                                    <div class="avatar"></div>
                                    <div class="client-id">{{ client.id }}</div>
                                </div>
                            </td>
                            <td>{{ client.name }}</td>
                            <td>{{ client.height }}</td>
                            <td>{{ client.weight }}</td>
                            <td>{{ client.lastActivity }}</td>
                            <td class="buttons">
                                <button class="view-button">View Info</button>
                                <button class="notify-button">Notify</button>
                                <button class="remove-button">Remove</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .th-content {
        display: inline-flex;
        align-items: center;
        gap: 5px;
    }
    .clients-page {
        margin: 0 auto;
        padding: 20px;
        max-width: 1500px;
    }

    .clients-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .clients-header h1 {
        font-size: 2rem;
        font-weight: 700;
    }

    .add-client-button {
        background-color: var(--button-lighter);
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        cursor: pointer;
    }

    .client-list-container {
        background-color: var(--background-white-color);
        border-radius: 10px;
        padding: 20px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .client-list-container h2 {
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
    }

    .table {
        overflow-x: auto;
    }

    .clients-table {
        width: 100%;
        border-collapse: collapse;
    }

    .clients-table th {
        background-color: var(--button-lighter);
        color: white;
        padding: 10px;
        text-align: left;
        cursor: pointer;
        position: relative;
    }

    .clients-table th .icon{
    }

    .clients-table td {
        padding: 10px;
        border-bottom: 1px solid #e0e0e0;
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

    .buttons {
        display: flex;
        gap: 10px;
    }

    .view-button {
        background-color: #4169E1;
        color: white;
        border: none;
        border-radius: 4px;
        padding: 5px 10px;
        font-size: 12px;
        cursor: pointer;
    }

    .notify-button {
        background-color: #FFD700;
        color: black;
        border: none;
        border-radius: 4px;
        padding: 5px 10px;
        font-size: 12px;
        cursor: pointer;
    }
    
    .remove-button {
        background-color: #DC143C;
        color: white;
        border: none;
        border-radius: 4px;
        padding: 5px 10px;
        font-size: 12px;
        cursor: pointer;
    }

</style>