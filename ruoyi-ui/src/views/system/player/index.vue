<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="玩家昵称" prop="nickName">
        <el-input v-model="queryParams.nickName" placeholder="请输入玩家昵称" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="手机号码" prop="phoneNumber">
        <el-input v-model="queryParams.phoneNumber" placeholder="请输入手机号码" clearable @keyup.enter.native="handleQuery"/>
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

    <el-table v-loading="loading" :data="playerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="玩家ID" align="center" prop="playerId" width="80"/>
      <el-table-column label="玩家昵称" align="center" prop="nickName"/>
      <el-table-column label="手机号码" align="center" prop="phoneNumber"/>
      <el-table-column label="玩家类型" align="center" prop="playerType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_player_type" :value="scope.row.playerType"/>
        </template>
      </el-table-column>
      <el-table-column label="累计消费" align="center" prop="totalConsumption">
        <template slot-scope="scope">
          ¥{{ scope.row.totalConsumption.toFixed(2) }}
        </template>
      </el-table-column>
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

    <!-- 添加或修改玩家弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="玩家昵称" prop="nickName">
          <el-input v-model="form.nickName" placeholder="请输入玩家昵称"/>
        </el-form-item>
        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="请输入手机号码"/>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="0">男</el-radio>
            <el-radio label="1">女</el-radio>
            <el-radio label="2">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="玩家类型" prop="playerType">
          <el-select v-model="form.playerType" placeholder="请选择玩家类型">
            <el-option label="散客" value="0"/>
            <el-option label="会员" value="1"/>
          </el-select>
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
import { listPlayer, getPlayer, delPlayer, addPlayer, updatePlayer } from "@/api/system/player";

export default {
  name: "Player",
  dicts: ['sys_player_type', 'sys_normal_disable'],
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
      // 玩家列表
      playerList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nickName: null,
        phoneNumber: null,
        status: null,
        playerType: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        nickName: [
          { required: true, message: "玩家昵称不能为空", trigger: "blur" }
        ],
        phoneNumber: [
          { required: true, message: "手机号码不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询玩家列表 */
    getList() {
      this.loading = true;
      listPlayer(this.queryParams).then(response => {
        this.playerList = response.rows;
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
      this.ids = val.map(item => item.playerId);
      this.single = val.length !== 1;
      this.multiple = val.length < 1;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增玩家";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const playerId = row.playerId || this.ids;
      getPlayer(playerId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改玩家";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const playerIds = row.playerId || this.ids;
      this.$modal.confirm('是否确认删除玩家编号为"' + playerIds + '"的数据项？').then(function() {
        return delPlayer(playerIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.playerId != null) {
            updatePlayer(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPlayer(this.form).then(response => {
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
        playerId: null,
        nickName: null,
        phoneNumber: null,
        avatar: null,
        gender: "2",
        birthday: null,
        status: "0",
        playerType: "0",
        totalConsumption: 0,
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
