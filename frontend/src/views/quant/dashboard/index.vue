<script setup>
import { ref, onMounted, computed } from 'vue';
import baseService from '@/service/baseService';
import BacktestChart from '../components/BacktestChart.vue';
import TrainingPanel from '../components/TrainingPanel.vue';

const currentTab = ref('backtest'); // 'backtest' | 'train'

const executions = ref([]);
const selectedExecutionId = ref(null);
const newSymbol = ref('600519');
const isRunning = ref(false);

// AI Models (with details)
const availableModels = ref([]);
const selectedModelName = ref('');

const fetchModels = async () => {
  try {
    const response = await baseService.get('/backtest/models');
    availableModels.value = response.data; // now returns list of objects
    
    if (availableModels.value.length > 0 && !selectedModelName.value) {
      selectedModelName.value = availableModels.value[0].modelName;
    }
  } catch (error) {
    console.error('Failed to fetch models:', error);
  }
};

const fetchHistory = async () => {
  try {
    const response = await baseService.get('/backtest/list');
    executions.value = response.data;
    // Select the first one by default if available and nothing selected
    if (executions.value.length > 0 && !selectedExecutionId.value) {
      selectedExecutionId.value = executions.value[0].id;
    }
  } catch (error) {
    console.error('Failed to fetch history:', error);
  }
};

const startBacktest = async () => {
  if (!newSymbol.value) return;
  isRunning.value = true;
  try {
    // 传入 selectedModelName
    await baseService.post(`/backtest/start?symbol=${newSymbol.value}&strategy=ai&model=${selectedModelName.value}`);
    await fetchHistory();
    if (executions.value.length > 0) {
      selectedExecutionId.value = executions.value[0].id;
    }
  } catch (error) {
    console.error('Failed to start backtest:', error);
    alert('Backtest failed: ' + error.message);
  } finally {
    isRunning.value = false;
  }
};

const selectExecution = (id) => {
  selectedExecutionId.value = id;
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleString();
};

// Computed property to get the full execution object
const selectedExecution = computed(() => {
  return executions.value.find(e => e.id === selectedExecutionId.value);
});

onMounted(() => {
  fetchHistory();
  fetchModels();
});
</script>

<template>
  <div class="dashboard">
    <div class="header">
      <h1>Zenith Flow Dashboard</h1>
      <div class="tabs">
        <button 
          :class="{ active: currentTab === 'backtest' }" 
          @click="currentTab = 'backtest'">
          Backtest
        </button>
        <button 
          :class="{ active: currentTab === 'train' }" 
          @click="currentTab = 'train'">
          Train Model
        </button>
      </div>
    </div>

    <!-- Tab: Backtest -->
    <div class="container" v-if="currentTab === 'backtest'">
      <!-- Sidebar: History List -->
      <div class="sidebar">
        <div class="control-panel">
          <div class="input-row">
          <input v-model="newSymbol" placeholder="Symbol (e.g. 600519)" />
          </div>
          <div class="input-row">
             <select v-model="selectedModelName" class="model-select">
                <option value="" disabled>Select Model</option>
                <option v-for="m in availableModels" :key="m.modelName" :value="m.modelName">
                  {{ m.modelName }} ({{ m.algoType }}) - {{ new Date(m.createdAt).toLocaleDateString() }}
                </option>
             </select>
          </div>
          <button @click="startBacktest" :disabled="isRunning" class="run-btn">
            {{ isRunning ? 'Running...' : 'Run AI Backtest' }}
          </button>
        </div>

        <div class="history-list">
          <h3>History</h3>
          <div 
            v-for="exec in executions" 
            :key="exec.id"
            :class="['history-item', { active: selectedExecutionId === exec.id }]"
            @click="selectExecution(exec.id)"
          >
            <div class="item-header">
              <div class="symbol-container">
                <span class="symbol-code">{{ exec.symbol }}</span>
                <span class="symbol-name" v-if="exec.stockName">{{ exec.stockName }}</span>
              </div>
              <span class="return" :class="{ positive: exec.totalReturn > 0, negative: exec.totalReturn < 0 }">
                {{ (exec.totalReturn * 100).toFixed(2) }}%
              </span>
            </div>
            <div class="item-meta">
              {{ exec.strategyName }} | {{ formatDate(exec.createdAt) }}
            </div>
          </div>
        </div>
      </div>

      <!-- Main Content: Chart & Details -->
      <div class="main-content">
        <div v-if="selectedExecutionId">
          <BacktestChart 
            :executionId="selectedExecutionId" 
            :symbol="selectedExecution?.symbol"
            :stockName="selectedExecution?.stockName"
          />
          
          <div class="details-card" v-if="selectedExecution">
            <h3>Performance Metrics</h3>
            <div class="metrics-grid">
              <div class="metric">
                <label>Final Assets</label>
                <span>{{ selectedExecution.finalAssets }}</span>
              </div>
              <div class="metric">
                <label>Total Return</label>
                <span :class="{ 'text-positive': selectedExecution.totalReturn > 0, 'text-negative': selectedExecution.totalReturn < 0 }">
                  {{ (selectedExecution.totalReturn * 100).toFixed(2) }}%
                </span>
              </div>
              <div class="metric">
                <label>Period</label>
                <span>{{ selectedExecution.startDate }} ~ {{ selectedExecution.endDate }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="placeholder">
          Select a backtest record to view details.
        </div>
      </div>
    </div>

    <!-- Tab: Train -->
    <div class="container" v-if="currentTab === 'train'">
      <TrainingPanel />
    </div>

  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  /*height: 90vh;*/ /* Let container control height */
  padding: 20px;
}

.header {
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tabs button {
  background: transparent;
  border: none;
  color: #888;
  font-size: 1.1em;
  margin-left: 20px;
  cursor: pointer;
  padding-bottom: 5px;
}

.tabs button.active {
  color: #646cff;
  border-bottom: 2px solid #646cff;
}

.container {
  display: flex;
  flex: 1;
  gap: 20px;
  /*overflow: hidden;*/
}

.sidebar {
  width: 300px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #eee;
  padding-right: 20px;
}

.control-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.input-row {
    display: flex;
}

.control-panel input, .model-select {
  flex: 1;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ddd;
  /*background: #222;*/
  /*color: #fff;*/
}

.run-btn {
  padding: 10px;
  background: #646cff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.run-btn:disabled {
  background: #ccc;
}

.history-list {
  flex: 1;
  /*overflow-y: auto;*/
}

.history-item {
  padding: 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.history-item:hover {
  background: #f5f5f5;
}

.history-item.active {
  background: #e6e6e6;
  border-left: 4px solid #646cff;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  margin-bottom: 4px;
}

.symbol-container {
  display: flex;
  flex-direction: column;
}

.symbol-code {
  font-size: 1em;
}

.symbol-name {
  font-size: 0.8em;
  color: #666;
}

/* A股颜色: 红涨绿跌 */
.return.positive { color: #ef5350; }
.return.negative { color: #66bb6a; }

.text-positive { color: #ef5350; }
.text-negative { color: #66bb6a; }

.item-meta {
  font-size: 0.8em;
  color: #888;
}

.main-content {
  flex: 1;
  /*overflow-y: auto;*/
}

.details-card {
  margin-top: 20px;
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 10px;
}

.metric {
  display: flex;
  flex-direction: column;
}

.metric label {
  font-size: 0.85em;
  color: #888;
  margin-bottom: 4px;
}

.metric span {
  font-size: 1.2em;
  font-weight: bold;
}

.placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #666;
}
</style>

