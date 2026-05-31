<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="等级名称" prop="levelName">
        <el-input v-model="queryParams.levelName" placeholder="请输入等级名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option label="正常" value="0"/><el-option label="停用" value="1"/>
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

    <el-table v-loading="loading" :data="levelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="等级ID" align="center" prop="levelId" width="80"/>
      <el-table-column label="等级名称" align="center" prop="levelName"/>
      <el-table-column label="等级编码" align="center" prop="levelCode"/>
      <el-table-column label="最低积分" align="center" prop="minPoints"/>
      <el-table-column label="最高积分" align="center" prop="maxPoints"/>
      <el-table-column label="折扣" align="center" prop="discount"><template slot-scope="scope">{{ scope.row.discount * 10 }}折</template></el-table-column>
      <el-table-column label="描述" align="center" prop="description" show-overflow-tooltip/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope"><dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/></template>
      </el-table-column>
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
        <el-form-item label="等级名称" prop="levelName"><el-input v-model="form.levelName" placeholder="请输入等级名称"/></el-form-item>
        <el-form-item label="等级编码" prop="levelCode"><el-input v-model="form.levelCode" placeholder="请输入等级编码"/></el-form-item>
        <el-form-item label="最低积分" prop="minPoints"><el-input-number v-model="form.minPoints" :min="0"/></el-form-item>
        <el-form-item label="最高积分" prop="maxPoints"><el-input-number v-model="form.maxPoints" :min="0"/></el-form-item>
        <el-form-item label="折扣" prop="discount"><el-input-number v-model="form.discount" :min="0" :max="1" :precision="2"/></el-form-item>
        <el-form-item label="描述" prop="description"><el-input v-model="form.description" type="textarea"/></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status"><el-radio label="0">正常</el-radio><el-radio label="1">停用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMemberLevel, getMemberLevel, addMemberLevel, updateMemberLevel, delMemberLevel } from "@/api/system/memberLevel";

export default {
  name: "MemberLevel",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0,
      levelList: [], title: "", open: false,
      queryParams: { pageNum: 1, pageSize: 10, levelName: null, status: null },
      form: {}, rules: { levelName: [{ required: true, message: "等级名称不能为空", trigger: "blur" }], levelCode: [{ required: true, message: "等级编码不能为空", trigger: "blur" }] }
    };
  },
  created() { this.getList(); },
  methods: {
    getList() { this.loading = true; listMemberLevel(this.queryParams).then(r => { this.levelList = r.rows; this.total = r.total; this.loading = false; }); },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(val) { this.ids = val.map(i => i.levelId); this.single = val.length !== 1; this.multiple = val.length < 1; },
    handleAdd() { this.reset(); this.open = true; this.title = "添加等级"; },
    handleUpdate(row) {
      this.reset();
      const id = row.levelId || this.ids;
      getMemberLevel(id).then(r => { this.form = r.data; this.open = true; this.title = "修改等级"; });
    },
    handleDelete(row) {
      const ids = row.levelId || this.ids;
      this.$modal.confirm('是否确认删除？').then(() => delMemberLevel(ids)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.levelId != null) { updateMemberLevel(this.form).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList(); }); }
          else { addMemberLevel(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList(); }); }
        }
      });
    },
    reset() {
      this.form = { levelId: null, levelName: null, levelCode: null, minPoints: 0, maxPoints: null, discount: 1, description: null, status: "0", sort: 0 };
      this.resetForm("form");
    },
    cancel() { this.open = false; this.reset(); }
  }
};
</script>
