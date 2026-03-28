<script setup>
import { ref } from 'vue';
import baseService from '@/service/baseService';

const form = ref({
  name: 'model_v1',
  algo: 'rf',
  symbols: ''
});

const isTraining = ref(false);
const logs = ref([]);

const startTraining = async () => {
  if (!form.value.name) {
    alert('Please enter a model name');
    return;
  }
  
  isTraining.value = true;
  logs.value.push(`Sending training request for model: ${form.value.name}...`);
  
  try {
    const response = await baseService.post('/train/start', form.value);
    logs.value.push(`Server Response: ${response.data}`);
    logs.value.push('Training is running in background. Check Java console logs for details.');
    logs.value.push('(Once finished, the model will be automatically loaded)');
  } catch (error) {
    logs.value.push(`Error: ${error.message}`);
  } finally {
    isTraining.value = false;
  }
};
</script>

<template>
  <div class="training-panel">
    <h2>Train New AI Model</h2>
    
    <div class="form-group">
      <label>Model Name</label>
      <input v-model="form.name" placeholder="e.g. rf_hs300_v1" />
      <small>Unique name for the model file</small>
    </div>
    
    <div class="form-group">
      <label>Algorithm</label>
      <select v-model="form.algo">
        <option value="rf">Random Forest (随机森林)</option>
        <option value="lr">Logistic Regression (逻辑回归)</option>
      </select>
    </div>
    
    <div class="form-group">
      <label>Target Symbols (Optional)</label>
      <input v-model="form.symbols" placeholder="e.g. 600519,000001 (Leave empty for ALL)" />
      <small>Comma separated list of stock codes. Leave empty to train on all available data.</small>
    </div>
    
    <button @click="startTraining" :disabled="isTraining" class="train-btn">
      {{ isTraining ? 'Requesting...' : 'Start Training' }}
    </button>
    
    <div class="logs" v-if="logs.length > 0">
      <h3>Logs</h3>
      <div v-for="(log, index) in logs" :key="index" class="log-line">{{ log }}</div>
    </div>
  </div>
</template>

<style scoped>
.training-panel {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  color: #fff;
}

.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 5px;
  font-weight: bold;
  color: #ddd;
}

.form-group input, .form-group select {
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #444;
  background: #222;
  color: #fff;
  font-size: 1em;
}

.form-group small {
  margin-top: 5px;
  color: #888;
  font-size: 0.8em;
}

.train-btn {
  width: 100%;
  padding: 12px;
  background: #646cff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1.1em;
  cursor: pointer;
  transition: background 0.2s;
}

.train-btn:hover:not(:disabled) {
  background: #535bf2;
}

.train-btn:disabled {
  background: #444;
  cursor: not-allowed;
}

.logs {
  margin-top: 30px;
  background: #111;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #333;
}

.log-line {
  font-family: monospace;
  margin-bottom: 5px;
  color: #aaa;
}
</style>

