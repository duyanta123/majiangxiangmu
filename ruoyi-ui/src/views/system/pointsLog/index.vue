<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="玩家姓名" prop="playerName">
        <el-input v-model="queryParams.playerName" placeholder="请输入玩家姓名" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="变动类型">
        <el-select v-model="queryParams.changeType" placeholder="请选择" clearable>
          <el-option label="增加" value="0"/><el-option label="扣减" value="1"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" @click="handleDelete" :disabled="multiple">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="logList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="记录ID" align="center" prop="logId" width="80"/>
      <el-table-column label="玩家姓名" align="center" prop="playerName"/>
      <el-table-column label="手机号码" align="center" prop="playerPhone" width="120"/>
      <el-table-column label="变动类型" align="center" prop="changeType">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.changeType === '0'">增加</el-tag>
          <el-tag type="danger" v-else>扣减</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="积分变动" align="center" prop="points">
        <template slot-scope="scope"><span :class="scope.row.changeType === '0' ? 'text-success' : 'text-danger'">{{ scope.row.changeType === '0' ? '+' : '-' }}{{ scope.row.points }}</span></template>
      </el-table-column>
      <el-table-column label="变动前" align="center" prop="balanceBefore"/>
      <el-table-column label="变动后" align="center" prop="balanceAfter"/>
      <el-table-column label="说明" align="center" prop="description" show-overflow-tooltip/>
      <el-table-column label="变动时间" align="center" prop="createTime" width="180"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>
  </div>
</template>

<script>
import { listPointsLog, delPointsLog } from "@/api/system/pointsLog";

export default {
  name: "PointsLog",
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0,
      logList: [],
      queryParams: { pageNum: 1, pageSize: 10, playerName: null, changeType: null }
    };
  },
  created() { this.getList(); },
  methods: {
    getList() { this.loading = true; listPointsLog(this.queryParams).then(r => { this.logList = r.rows; this.total = r.total; this.loading = false; }); },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(val) { this.ids = val.map(i => i.logId); this.single = val.length !== 1; this.multiple = val.length < 1; },
    handleDelete(row) {
      const ids = row.logId || this.ids;
      this.$modal.confirm('是否确认删除？').then(() => delPointsLog(ids)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    }
  }
};
</script>
