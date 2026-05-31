<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon bg-blue"><i class="el-icon-money"></i></div>
          <div class="stat-content">
            <p class="stat-value">¥{{ formatMoney(stats.todayReservationRevenue) }}</p>
            <p class="stat-label">今日预约收入</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon bg-green"><i class="el-icon-shopping-cart"></i></div>
          <div class="stat-content">
            <p class="stat-value">¥{{ formatMoney(stats.todayConsumptionRevenue) }}</p>
            <p class="stat-label">今日消费收入</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon bg-yellow"><i class="el-icon-user"></i></div>
          <div class="stat-content">
            <p class="stat-value">{{ stats.todayActivePlayers }}</p>
            <p class="stat-label">今日活跃玩家</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon bg-purple"><i class="el-icon-user-plus"></i></div>
          <div class="stat-content">
            <p class="stat-value">{{ stats.todayConsumers }}</p>
            <p class="stat-label">今日消费人数</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <div class="chart-card">
          <h3>近7日收入趋势</h3>
          <div id="revenueChart" style="height: 250px;"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <h3>近7日会员趋势</h3>
          <div id="memberChart" style="height: 250px;"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <h3>会员消费排行</h3>
          <el-table :data="rankingList" style="width: 100%" max-height="250">
            <el-table-column prop="playerName" label="姓名" width="80"/>
            <el-table-column prop="totalConsumption" label="累计消费">
              <template slot-scope="scope">¥{{ formatMoney(scope.row.totalConsumption) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <div class="chart-card">
          <h3>周数据统计</h3>
          <el-row :gutter="10">
            <el-col :span="6" class="mini-stat">
              <p class="mini-value">¥{{ formatMoney(stats.weekReservationRevenue) }}</p>
              <p class="mini-label">预约收入</p>
            </el-col>
            <el-col :span="6" class="mini-stat">
              <p class="mini-value">¥{{ formatMoney(stats.weekConsumptionRevenue) }}</p>
              <p class="mini-label">消费收入</p>
            </el-col>
            <el-col :span="6" class="mini-stat">
              <p class="mini-value">{{ stats.weekActivePlayers || 0 }}</p>
              <p class="mini-label">活跃玩家</p>
            </el-col>
            <el-col :span="6" class="mini-stat">
              <p class="mini-value">{{ stats.weekNewMembers || 0 }}</p>
              <p class="mini-label">新增会员</p>
            </el-col>
          </el-row>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <h3>月数据统计</h3>
          <el-row :gutter="10">
            <el-col :span="6" class="mini-stat">
              <p class="mini-value">¥{{ formatMoney(stats.monthReservationRevenue) }}</p>
              <p class="mini-label">预约收入</p>
            </el-col>
            <el-col :span="6" class="mini-stat">
              <p class="mini-value">¥{{ formatMoney(stats.monthConsumptionRevenue) }}</p>
              <p class="mini-label">消费收入</p>
            </el-col>
            <el-col :span="6" class="mini-stat">
              <p class="mini-value">{{ stats.monthNewMembers || 0 }}</p>
              <p class="mini-label">新增会员</p>
            </el-col>
            <el-col :span="6" class="mini-stat">
              <p class="mini-value">{{ stats.monthNewPlayers || 0 }}</p>
              <p class="mini-label">新增玩家</p>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <div class="chart-card">
          <h3>门店概览</h3>
          <el-table :data="storeOverview" style="width: 100%">
            <el-table-column prop="storeCount" label="门店数量"/>
            <el-table-column prop="tableCount" label="桌台数量"/>
            <el-table-column prop="memberCount" label="会员数量"/>
            <el-table-column prop="playerCount" label="玩家数量"/>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboard, getRevenueTrend, getMemberTrend, getMemberRanking } from "@/api/system/analysis";

export default {
  name: "AnalysisDashboard",
  data() {
    return {
      stats: {},
      rankingList: [],
      storeOverview: [],
      revenueTrend: [],
      memberTrend: [],
      revenueChart: null,
      memberChart: null
    };
  },
  mounted() {
    this.loadData();
  },
  beforeDestroy() {
    if (this.revenueChart) {
      this.revenueChart.dispose();
      this.revenueChart = null;
    }
    if (this.memberChart) {
      this.memberChart.dispose();
      this.memberChart = null;
    }
  },
  methods: {
    loadData() {
      Promise.all([
        getDashboard(),
        getRevenueTrend({}),
        getMemberTrend({}),
        getMemberRanking(10)
      ]).then(([dashboardRes, revenueRes, memberRes, rankingRes]) => {
        if (dashboardRes.code === 200) {
          const data = dashboardRes.data;
          this.stats = {
            ...(data.todayStats || {}),
            ...(data.weekStats || {}),
            ...(data.monthStats || {})
          };
          this.storeOverview = data.storeOverview ? [data.storeOverview] : [];
        }
        if (revenueRes.code === 200) {
          this.revenueTrend = revenueRes.data || [];
          this.renderRevenueChart();
        }
        if (memberRes.code === 200) {
          this.memberTrend = memberRes.data || [];
          this.renderMemberChart();
        }
        if (rankingRes.code === 200) {
          this.rankingList = rankingRes.data?.consumptionRanking || [];
        }
      }).catch(error => {
        console.error('加载数据失败:', error);
      });
    },
    formatMoney(val) {
      return (val || 0).toLocaleString();
    },
    renderRevenueChart() {
      if (!this.revenueTrend || this.revenueTrend.length === 0) {
        return;
      }
      const chartDom = document.getElementById('revenueChart');
      if (!chartDom) return;

      if (this.revenueChart) {
        this.revenueChart.dispose();
      }
      this.revenueChart = echarts.init(chartDom);

      const dates = this.revenueTrend.map(i => {
        if (i.date && i.date.includes('-')) {
          return i.date.substring(5);
        }
        return i.date;
      });
      const reservationData = this.revenueTrend.map(i => i.reservationAmount || 0);
      const consumptionData = this.revenueTrend.map(i => i.consumptionAmount || 0);

      this.revenueChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            let result = params[0].name + '<br/>';
            params.forEach(param => {
              result += param.marker + param.seriesName + ': ¥' + param.value.toLocaleString() + '<br/>';
            });
            return result;
          }
        },
        grid: {
          left: 50,
          right: 20,
          top: 30,
          bottom: 30
        },
        xAxis: {
          type: 'category',
          data: dates,
          boundaryGap: false
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: function(value) {
              return value >= 1000 ? (value / 1000).toFixed(1) + 'k' : value;
            }
          }
        },
        series: [
          {
            name: '预约收入',
            type: 'line',
            data: reservationData,
            smooth: true,
            itemStyle: { color: '#667eea' },
            areaStyle: { color: 'rgba(102,126,234,0.1)' }
          },
          {
            name: '消费收入',
            type: 'line',
            data: consumptionData,
            smooth: true,
            itemStyle: { color: '#11998e' },
            areaStyle: { color: 'rgba(17,153,142,0.1)' }
          }
        ]
      });
    },
    renderMemberChart() {
      if (!this.memberTrend || this.memberTrend.length === 0) {
        return;
      }
      const chartDom = document.getElementById('memberChart');
      if (!chartDom) return;

      if (this.memberChart) {
        this.memberChart.dispose();
      }
      this.memberChart = echarts.init(chartDom);

      const dates = this.memberTrend.map(i => {
        if (i.date && i.date.includes('-')) {
          return i.date.substring(5);
        }
        return i.date;
      });
      const newMembersData = this.memberTrend.map(i => i.newMembers || 0);
      const activeMembersData = this.memberTrend.map(i => i.activeMembers || 0);

      this.memberChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            let result = params[0].name + '<br/>';
            params.forEach(param => {
              result += param.marker + param.seriesName + ': ' + param.value + '人<br/>';
            });
            return result;
          }
        },
        grid: {
          left: 50,
          right: 20,
          top: 30,
          bottom: 30
        },
        xAxis: {
          type: 'category',
          data: dates,
          boundaryGap: false
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [
          {
            name: '新增会员',
            type: 'line',
            data: newMembersData,
            smooth: true,
            itemStyle: { color: '#fc4a1a' }
          },
          {
            name: '活跃会员',
            type: 'line',
            data: activeMembersData,
            smooth: true,
            itemStyle: { color: '#f7b733' }
          }
        ]
      });
    }
  }
};
</script>

<style scoped>
.stat-card { display: flex; align-items: center; background: #fff; padding: 15px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.05); }
.stat-icon { width: 50px; height: 50px; border-radius: 8px; display: flex; align-items: center; justify-content: center; margin-right: 15px; }
.stat-icon i { font-size: 24px; color: #fff; }
.stat-content { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; color: #333; margin: 0; }
.stat-label { font-size: 14px; color: #999; margin: 5px 0 0; }
.bg-blue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.bg-green { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
.bg-yellow { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); }
.bg-purple { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }
.chart-card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.05); }
.chart-card h3 { margin: 0 0 15px; font-size: 16px; color: #333; }
.mini-stat { text-align: center; }
.mini-value { font-size: 20px; font-weight: bold; color: #333; margin: 0; }
.mini-label { font-size: 12px; color: #999; margin: 5px 0 0; }
</style>
