<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="玩家姓名" prop="playerName">
        <el-input v-model="queryParams.playerName" placeholder="请输入玩家姓名" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="预约状态">
        <el-select v-model="queryParams.reservationStatus" placeholder="请选择状态" clearable>
          <el-option label="待确认" value="0"/><el-option label="已确认" value="1"/>
          <el-option label="已完成" value="2"/><el-option label="已取消" value="3"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" @click="handleUpdate" :disabled="single">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" @click="handleDelete" :disabled="multiple">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reservationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="预约编号" align="center" prop="reservationCode" width="150"/>
      <el-table-column label="玩家姓名" align="center" prop="playerName"/>
      <el-table-column label="手机号码" align="center" prop="playerPhone" width="120"/>
      <el-table-column label="门店" align="center" prop="storeName"/>
      <el-table-column label="桌台" align="center" prop="tableName"/>
      <el-table-column label="预约日期" align="center" prop="reserveDate" width="100"/>
      <el-table-column label="时段" align="center" prop="startTime" width="100"/>
      <el-table-column label="预约状态" align="center" prop="reservationStatus">
        <template slot-scope="scope">
          <el-tag type="warning" v-if="scope.row.reservationStatus === '0'">待确认</el-tag>
          <el-tag type="success" v-else-if="scope.row.reservationStatus === '1'">已确认</el-tag>
          <el-tag type="primary" v-else-if="scope.row.reservationStatus === '2'">已完成</el-tag>
          <el-tag type="info" v-else>已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付状态" align="center" prop="payStatus">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.payStatus === '0'">未支付</el-tag>
          <el-tag type="primary" v-else>已支付</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="玩家姓名" prop="playerName"><el-input v-model="form.playerName" placeholder="请输入玩家姓名"/></el-form-item>
        <el-form-item label="手机号码" prop="playerPhone"><el-input v-model="form.playerPhone" placeholder="请输入手机号码"/></el-form-item>
        <el-form-item label="门店" prop="storeId"><el-select v-model="form.storeId" placeholder="请选择门店" @change="handleStoreChange">
            <el-option v-for="s in storeList" :key="s.storeId" :label="s.storeName" :value="s.storeId"/>
          </el-select></el-form-item>
        <el-form-item label="桌台" prop="tableId"><el-select v-model="form.tableId" placeholder="请选择桌台">
            <el-option v-for="t in tableList" :key="t.tableId" :label="t.tableName" :value="t.tableId"/>
          </el-select></el-form-item>
        <el-form-item label="预约日期" prop="reserveDate"><el-date-picker v-model="form.reserveDate" type="date" placeholder="选择日期"/></el-form-item>
        <el-form-item label="开始时间" prop="startTime"><el-time-select v-model="form.startTime" placeholder="开始时间" start="09:00" step="00:30" end="23:00"/></el-form-item>
        <el-form-item label="结束时间" prop="endTime"><el-time-select v-model="form.endTime" placeholder="结束时间" start="09:00" step="00:30" end="23:30"/></el-form-item>
        <el-form-item label="预约状态" prop="reservationStatus">
          <el-radio-group v-model="form.reservationStatus">
            <el-radio label="0">待确认</el-radio><el-radio label="1">已确认</el-radio>
            <el-radio label="2">已完成</el-radio><el-radio label="3">已取消</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" placeholder="请输入备注"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReservation, getReservation, addReservation, updateReservation, delReservation } from "@/api/system/reservation";
import { getAllStores } from "@/api/system/store";
import { getAllTables } from "@/api/system/tableInfo";

export default {
  name: "Reservation",
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0,
      reservationList: [], storeList: [], tableList: [], title: "", open: false,
      queryParams: { pageNum: 1, pageSize: 10, playerName: null, reservationStatus: null },
      form: {}, rules: { playerName: [{ required: true, message: "玩家姓名不能为空", trigger: "blur" }] }
    };
  },
  created() { this.getList(); this.getStoreList(); },
  methods: {
    getList() {
      this.loading = true;
      listReservation(this.queryParams).then(r => { this.reservationList = r.rows; this.total = r.total; this.loading = false; });
    },
    getStoreList() { getAllStores().then(r => { this.storeList = r.data; }); },
    getTableList(storeId) { getAllTables().then(r => { this.tableList = r.data.filter(t => t.storeId === storeId); }); },
    handleStoreChange(storeId) { this.getTableList(storeId); },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(val) { this.ids = val.map(i => i.reservationId); this.single = val.length !== 1; this.multiple = val.length < 1; },
    handleAdd() { this.reset(); this.open = true; this.title = "添加预约"; },
    handleUpdate(row) {
      this.reset();
      const id = row.reservationId || this.ids;
      getReservation(id).then(r => { this.form = r.data; this.open = true; this.title = "修改预约"; });
    },
    handleDelete(row) {
      const ids = row.reservationId || this.ids;
      this.$modal.confirm('是否确认删除？').then(() => delReservation(ids)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.reservationId != null) {
            updateReservation(this.form).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList(); });
          } else {
            addReservation(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList(); });
          }
        }
      });
    },
    reset() {
      this.form = { reservationId: null, playerName: null, playerPhone: null, storeId: null, storeName: null, tableId: null, tableName: null, reserveDate: null, startTime: null, endTime: null, playHours: null, reservationStatus: "0", payStatus: "0", remark: null };
      this.resetForm("form");
    },
    cancel() { this.open = false; this.reset(); }
  }
};
</script>
