<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="玩家姓名" prop="playerName">
        <el-input v-model="queryParams.playerName" placeholder="请输入玩家姓名" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="支付状态">
        <el-select v-model="queryParams.payStatus" placeholder="请选择" clearable>
          <el-option label="未支付" value="0"/><el-option label="已支付" value="1"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" @click="handleUpdate" :disabled="single">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" @click="handleDelete" :disabled="multiple">删除</el-button></el-col>
      <el-col :span="1.5"><el-button type="warning" plain icon="el-icon-download" @click="handleExport">导出</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="consumeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="消费编号" align="center" prop="consumeNo" width="180"/>
      <el-table-column label="玩家姓名" align="center" prop="playerName"/>
      <el-table-column label="门店" align="center" prop="storeName"/>
      <el-table-column label="桌台费" align="center" prop="tableFee"><template slot-scope="scope">¥{{ scope.row.tableFee }}</template></el-table-column>
      <el-table-column label="套餐费" align="center" prop="packageFee"><template slot-scope="scope">¥{{ scope.row.packageFee }}</template></el-table-column>
      <el-table-column label="其他费用" align="center" prop="otherFee"><template slot-scope="scope">¥{{ scope.row.otherFee }}</template></el-table-column>
      <el-table-column label="总金额" align="center" prop="totalAmount"><template slot-scope="scope"><b>¥{{ scope.row.totalAmount }}</b></template></el-table-column>
      <el-table-column label="支付状态" align="center" prop="payStatus">
        <template slot-scope="scope"><el-tag type="success" v-if="scope.row.payStatus === '1'">已支付</el-tag><el-tag type="warning" v-else>未支付</el-tag></template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180"/>
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
        <el-form-item label="门店" prop="storeId"><el-select v-model="form.storeId" placeholder="请选择门店">
            <el-option v-for="s in storeList" :key="s.storeId" :label="s.storeName" :value="s.storeId"/>
          </el-select></el-form-item>
        <el-form-item label="桌台" prop="tableId"><el-select v-model="form.tableId" placeholder="请选择桌台">
            <el-option v-for="t in tableList" :key="t.tableId" :label="t.tableName" :value="t.tableId"/>
          </el-select></el-form-item>
        <el-form-item label="桌台费" prop="tableFee"><el-input-number v-model="form.tableFee" :min="0" :precision="2"/></el-form-item>
        <el-form-item label="套餐费" prop="packageFee"><el-input-number v-model="form.packageFee" :min="0" :precision="2"/></el-form-item>
        <el-form-item label="其他费用" prop="otherFee"><el-input-number v-model="form.otherFee" :min="0" :precision="2"/></el-form-item>
        <el-form-item label="总金额" prop="totalAmount"><el-input-number v-model="form.totalAmount" :min="0" :precision="2" readonly/></el-form-item>
        <el-form-item label="支付状态" prop="payStatus">
          <el-radio-group v-model="form.payStatus"><el-radio label="0">未支付</el-radio><el-radio label="1">已支付</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listConsume, getConsume, addConsume, updateConsume, delConsume } from "@/api/system/consume";
import { getAllStores } from "@/api/system/store";
import { getAllTables } from "@/api/system/tableInfo";

export default {
  name: "Consume",
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0,
      consumeList: [], storeList: [], tableList: [], title: "", open: false,
      queryParams: { pageNum: 1, pageSize: 10, playerName: null, payStatus: null },
      form: {}, rules: {}
    };
  },
  created() { this.getList(); this.getStoreList(); },
  watch: { 'form.tableFee': 'calcTotal', 'form.packageFee': 'calcTotal', 'form.otherFee': 'calcTotal' },
  methods: {
    calcTotal() { this.form.totalAmount = (this.form.tableFee || 0) + (this.form.packageFee || 0) + (this.form.otherFee || 0); },
    getList() { this.loading = true; listConsume(this.queryParams).then(r => { this.consumeList = r.rows; this.total = r.total; this.loading = false; }); },
    getStoreList() { getAllStores().then(r => { this.storeList = r.data; }); },
    getTableList() { getAllTables().then(r => { this.tableList = r.data; }); },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(val) { this.ids = val.map(i => i.consumeId); this.single = val.length !== 1; this.multiple = val.length < 1; },
    handleAdd() { this.reset(); this.getTableList(); this.open = true; this.title = "添加消费"; },
    handleUpdate(row) {
      this.reset(); this.getTableList();
      const id = row.consumeId || this.ids;
      getConsume(id).then(r => { this.form = r.data; this.open = true; this.title = "修改消费"; });
    },
    handleDelete(row) {
      const ids = row.consumeId || this.ids;
      this.$modal.confirm('是否确认删除？').then(() => delConsume(ids)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    },
    handleExport() { this.download('/system/consume/export', { ...this.queryParams }, `消费记录_${new Date().getTime()}.xlsx`); },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.consumeId != null) { updateConsume(this.form).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList(); }); }
          else { addConsume(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList(); }); }
        }
      });
    },
    reset() {
      this.form = { consumeId: null, playerId: null, playerName: null, storeId: null, storeName: null, tableId: null, tableName: null, tableFee: 0, packageFee: 0, otherFee: 0, totalAmount: 0, payStatus: "0", remark: null };
      this.resetForm("form");
    },
    cancel() { this.open = false; this.reset(); }
  }
};
</script>
