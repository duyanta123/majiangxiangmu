<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="类型名称" prop="typeName">
        <el-input v-model="queryParams.typeName" placeholder="请输入类型名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="门店" prop="storeId">
        <el-select v-model="queryParams.storeId" placeholder="请选择门店" clearable>
          <el-option v-for="store in storeList" :key="store.storeId" :label="store.storeName" :value="store.storeId"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0"/>
          <el-option label="停用" value="1"/>
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

    <el-table v-loading="loading" :data="tableTypeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="类型ID" align="center" prop="typeId" width="80"/>
      <el-table-column label="类型名称" align="center" prop="typeName"/>
      <el-table-column label="类型编码" align="center" prop="typeCode"/>
      <el-table-column label="所属门店" align="center" prop="storeName"/>
      <el-table-column label="描述" align="center" prop="description" show-overflow-tooltip/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
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

    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入类型名称"/>
        </el-form-item>
        <el-form-item label="类型编码" prop="typeCode">
          <el-input v-model="form.typeCode" placeholder="请输入类型编码"/>
        </el-form-item>
        <el-form-item label="所属门店" prop="storeId">
          <el-select v-model="form.storeId" placeholder="请选择门店">
            <el-option v-for="store in storeList" :key="store.storeId" :label="store.storeName" :value="store.storeId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述"/>
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
import { listTableType, getTableType, addTableType, updateTableType, delTableType } from "@/api/system/tableType";
import { getAllStores } from "@/api/system/store";

export default {
  name: "TableType",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      tableTypeList: [],
      storeList: [],
      title: "",
      open: false,
      queryParams: { pageNum: 1, pageSize: 10, typeName: null, storeId: null, status: null },
      form: {},
      rules: {
        typeName: [{ required: true, message: "类型名称不能为空", trigger: "blur" }],
        typeCode: [{ required: true, message: "类型编码不能为空", trigger: "blur" }],
        storeId: [{ required: true, message: "请选择门店", trigger: "change" }]
      }
    };
  },
  created() {
    this.getList();
    this.getStoreList();
  },
  methods: {
    getList() {
      this.loading = true;
      listTableType(this.queryParams).then(response => {
        this.tableTypeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getStoreList() {
      getAllStores().then(response => {
        this.storeList = response.data;
      });
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
      this.ids = val.map(item => item.typeId);
      this.single = val.length !== 1;
      this.multiple = val.length < 1;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加桌台类型";
    },
    handleUpdate(row) {
      this.reset();
      const typeId = row.typeId || this.ids;
      getTableType(typeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改桌台类型";
      });
    },
    handleDelete(row) {
      const typeIds = row.typeId || this.ids;
      this.$modal.confirm('是否确认删除类型编号为"' + typeIds + '"的数据项？').then(function() {
        return delTableType(typeIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.typeId != null) {
            updateTableType(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTableType(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    reset() {
      this.form = { typeId: null, typeName: null, typeCode: null, storeId: null, storeName: null, description: null, status: "0" };
      this.resetForm("form");
    },
    cancel() {
      this.open = false;
      this.reset();
    }
  }
};
</script>
