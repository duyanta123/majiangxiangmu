<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="门店名称" prop="storeName">
        <el-input v-model="queryParams.storeName" placeholder="请输入门店名称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="门店编码" prop="storeCode">
        <el-input v-model="queryParams.storeCode" placeholder="请输入门店编码" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="城市" prop="city">
        <el-input v-model="queryParams.city" placeholder="请输入城市" clearable @keyup.enter.native="handleQuery"/>
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

    <el-table v-loading="loading" :data="storeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="门店ID" align="center" prop="storeId" width="80"/>
      <el-table-column label="门店名称" align="center" prop="storeName"/>
      <el-table-column label="门店编码" align="center" prop="storeCode"/>
      <el-table-column label="所在城市" align="center" prop="city"/>
      <el-table-column label="详细地址" align="center" prop="address"/>
      <el-table-column label="联系电话" align="center" prop="phone"/>
      <el-table-column label="营业时间" align="center" prop="businessHours"/>
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

    <!-- 添加或修改门店弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="门店名称" prop="storeName">
          <el-input v-model="form.storeName" placeholder="请输入门店名称"/>
        </el-form-item>
        <el-form-item label="门店编码" prop="storeCode">
          <el-input v-model="form.storeCode" placeholder="请输入门店编码"/>
        </el-form-item>
        <el-form-item label="所在省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份"/>
        </el-form-item>
        <el-form-item label="所在城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市"/>
        </el-form-item>
        <el-form-item label="所在区县" prop="district">
          <el-input v-model="form.district" placeholder="请输入区县"/>
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址"/>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话"/>
        </el-form-item>
        <el-form-item label="营业时间" prop="businessHours">
          <el-input v-model="form.businessHours" placeholder="请输入营业时间，如：09:00-23:00"/>
        </el-form-item>
        <el-form-item label="门店简介" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入门店简介"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注"/>
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
import { listStore, getStore, delStore, addStore, updateStore } from "@/api/system/store";

export default {
  name: "Store",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 门店列表
      storeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        storeName: null,
        storeCode: null,
        city: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        storeName: [
          { required: true, message: "门店名称不能为空", trigger: "blur" }
        ],
        storeCode: [
          { required: true, message: "门店编码不能为空", trigger: "blur" }
        ],
        city: [
          { required: true, message: "所在城市不能为空", trigger: "blur" }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询门店列表 */
    getList() {
      this.loading = true;
      listStore(this.queryParams).then(response => {
        this.storeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(val) {
      this.ids = val.map(item => item.storeId);
      this.single = val.length !== 1;
      this.multiple = val.length < 1;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增门店";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const storeId = row.storeId || this.ids;
      getStore(storeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改门店";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const storeIds = row.storeId || this.ids;
      this.$modal.confirm('是否确认删除门店编号为"' + storeIds + '"的数据项？').then(function() {
        return delStore(storeIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.storeId != null) {
            updateStore(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addStore(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 重置表单 */
    reset() {
      this.form = {
        storeId: null,
        storeName: null,
        storeCode: null,
        province: null,
        city: null,
        district: null,
        address: null,
        latitude: null,
        longitude: null,
        phone: null,
        businessHours: null,
        description: null,
        coverImage: null,
        images: null,
        status: "0",
        sort: 0,
        remark: null
      };
      this.resetForm("form");
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    }
  }
};
</script>
