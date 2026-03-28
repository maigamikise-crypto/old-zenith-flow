<script setup>
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';
import baseService from '@/service/baseService';

const props = defineProps({
  executionId: {
    type: Number,
    required: true
  },
  symbol: {
    type: String,
    default: ''
  },
  stockName: {
    type: String,
    default: ''
  }
});

const chartContainer = ref(null);
let chartInstance = null;

const fetchDataAndDraw = async () => {
  if (!props.executionId) return;
  
  try {
    const response = await baseService.get(`/backtest/${props.executionId}/daily-results`);
    const data = response.data;
    
    if (!data || data.length === 0) {
      console.warn('No data for execution', props.executionId);
      return;
    }

    const dates = data.map(item => item.date);
    const strategyValues = data.map(item => item.totalAssets);
    const benchmarkValues = data.map(item => item.benchmarkValue);
    
    const titleText = props.stockName 
      ? `回测结果: ${props.stockName} (${props.symbol})` 
      : `Backtest Results (${props.symbol})`;

    const option = {
      title: {
        text: titleText,
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['Strategy', 'Benchmark'],
        bottom: 0
      },
      xAxis: {
        type: 'category',
        data: dates
      },
      yAxis: {
        type: 'value',
        scale: true
      },
      series: [
        {
          name: 'Strategy',
          type: 'line',
          data: strategyValues,
          smooth: true,
          showSymbol: false,
          itemStyle: {
            color: '#ef5350' // A股红
          },
          lineStyle: {
            width: 2
          }
        },
        {
          name: 'Benchmark',
          type: 'line',
          data: benchmarkValues,
          smooth: true,
          showSymbol: false,
          itemStyle: {
            color: '#66bb6a' // A股绿
          },
          lineStyle: {
            type: 'dashed',
            width: 2
          }
        }
      ]
    };

    if (!chartInstance) {
      chartInstance = echarts.init(chartContainer.value);
    }
    chartInstance.setOption(option);
  } catch (error) {
    console.error('Failed to fetch chart data:', error);
  }
};

onMounted(() => {
  fetchDataAndDraw();
  window.addEventListener('resize', () => chartInstance && chartInstance.resize());
});

watch(() => [props.executionId, props.stockName], () => {
  fetchDataAndDraw();
});
</script>

<template>
  <div class="chart-wrapper">
    <div ref="chartContainer" class="chart"></div>
  </div>
</template>

<style scoped>
.chart-wrapper {
  width: 100%;
  height: 500px;
  margin-top: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px;
}
.chart {
  width: 100%;
  height: 100%;
}
</style>

