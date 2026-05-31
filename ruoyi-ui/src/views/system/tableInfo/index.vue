<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="桌台名称" prop="tableName">
        <el-input v-model="queryParams.tableName" placeholder="请输入桌台名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="桌台状态">
        <el-select v-model="queryParams.tableStatus" placeholder="请选择状态" clearable>
          <el-option label="空闲" value="0"/>
          <el-option label="已预约" value="1"/>
          <el-option label="使用中" value="2"/>
          <el-option label="维护中" value="3"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" @click="handleUpdate" :disabled="single">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" @click="handleDelete" :disabled="multiple">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="桌台ID" align="center" prop="tableId" width="80"/>
      <el-table-column label="桌台名称" align="center" prop="tableName"/>
      <el-table-column label="桌台编号" align="center" prop="tableCode"/>
      <el-table-column label="桌台类型" align="center" prop="typeName"/>
      <el-table-column label="所属门店" align="center" prop="storeName"/>
      <el-table-column label="容纳人数" align="center" prop="capacity"/>
      <el-table-column label="每小时价格" align="center" prop="hourlyRate">
        <template slot-scope="scope">¥{{ scope.row.hourlyRate }}</template>
      </el-table-column>
      <el-table-column label="桌台状态" align="center" prop="tableStatus">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.tableStatus === '0'">空闲</el-tag>
          <el-tag type="warning" v-else-if="scope.row.tableStatus === '1'">已预约</el-tag>
          <el-tag type="danger" v-else-if="scope.row.tableStatus === '2'">使用中</el-tag>
          <el-tag type="info" v-else>维护中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
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
        <el-form-item label="桌台名称" prop="tableName">
          <el-input v-model="form.tableName" placeholder="请输入桌台名称"/>
        </el-form-item>
        <el-form-item label="桌台编号" prop="tableCode">
          <el-input v-model="form.tableCode" placeholder="请输入桌台编号"/>
        </el-form-item>
        <el-form-item label="桌台类型" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择类型" @change="handleTypeChange">
            <el-option v-for="type in typeList" :key="type.typeId" :label="type.typeName" :value="type.typeId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="所属门店" prop="storeId">
          <el-select v-model="form.storeId" placeholder="请选择门店">
            <el-option v-for="store in storeList" :key="store.storeId" :label="store.storeName" :value="store.storeId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="20"/>
        </el-form-item>
        <el-form-item label="每小时价格" prop="hourlyRate">
          <el-input-number v-model="form.hourlyRate" :min="0" :precision="2" placeholder="请输入价格"/>
        </el-form-item>
        <el-form-item label="押金" prop="deposit">
          <el-input-number v-model="form.deposit" :min="0" :precision="2" placeholder="请输入押金"/>
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入位置/区域"/>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTableInfo, getTableInfo, addTableInfo, updateTableInfo, delTableInfo } from "@/api/system/tableInfo";
import { getAllStores } from "@/api/system/store";
import { getAllTypes } from "@/api/system/tableType";

export default {
  name: "TableInfo",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      tableList: [],
      storeList: [],
      typeList: [],
      title: "",
      open: false,
      queryParams: { pageNum: 1, pageSize: 10, tableName: null, tableStatus: null },
      form: {},
      rules: {
        tableName: [{ required: true, message: "桌台名称不能为空", trigger: "blur" }],
        tableCode: [{ required: true, message: "桌台编号不能为空", trigger: "blur" }],
        typeId: [{ required: true, message: "请选择桌台类型", trigger: "change" }],
        storeId: [{ required: true, message: "请选择门店", trigger: "change" }]
      }
    };
  },
  created() {
    this.getList();
    this.getStoreList();
    this.getTypeList();
  },
  methods: {
    getList() {
      this.loading = true;
      listTableInfo(this.queryParams).then(response => {
        this.tableList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getStoreList() {
      getAllStores().then(response => { this.storeList = response.data; });
    },
    getTypeList() {
      getAllTypes().then(response => { this.typeList = response.data; });
    },
    handleTypeChange(typeId) {
      const type = this.typeList.find(t => t.typeId === typeId);
      if (type) {
        this.form.typeName = type.typeName;
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(val) {
      this.ids = val.map(item => item.tableId);
      this.single = val.length !== 1;
      this.multiple = val.length < 1;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加桌台";
    },
    handleUpdate(row) {
      this.reset();
      const tableId = row.tableId || this.ids;
      getTableInfo(tableId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改桌台";
      });
    },
    handleDelete(row) {
      const tableIds = row.tableId || this.ids;
      this.$modal.confirm('是否确认删除桌台编号为"' + tableIds + '"的数据项？').then(function() {
        return delTableInfo(tableIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.tableId != null) {
            updateTableInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTableInfo(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    reset() {
      this.form = {
        tableId: null, tableName: null, tableCode: null, typeId: null, typeName: null,
        storeId: null, storeName: null, capacity: 4, hourlyRate: 0, deposit: 0,
        tableStatus: "0", currentPlayers: 0, location: null, sort: 0, status: "0", remark: null
      };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    }
  }
};
</script>
