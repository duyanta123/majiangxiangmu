<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="玩家姓名" prop="playerName">
        <el-input v-model="queryParams.playerName" placeholder="请输入玩家姓名" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="手机号码" prop="playerPhone">
        <el-input v-model="queryParams.playerPhone" placeholder="请输入手机号码" clearable @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" @click="handleAdd">新增会员</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" @click="handleUpdate" :disabled="single">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" @click="handleDelete" :disabled="multiple">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="memberList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="会员ID" align="center" prop="memberId" width="80"/>
      <el-table-column label="会员卡号" align="center" prop="cardNo" width="120"/>
      <el-table-column label="玩家姓名" align="center" prop="playerName"/>
      <el-table-column label="手机号码" align="center" prop="playerPhone" width="120"/>
      <el-table-column label="会员等级" align="center" prop="levelName"/>
      <el-table-column label="累计积分" align="center" prop="totalPoints"/>
      <el-table-column label="可用积分" align="center" prop="availablePoints"/>
      <el-table-column label="累计消费" align="center" prop="totalConsumption"><template slot-scope="scope">¥{{ scope.row.totalConsumption }}</template></el-table-column>
      <el-table-column label="入会时间" align="center" prop="memberSince" width="150"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope"><dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/></template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-coins" @click="handlePoints(scope.row)">积分管理</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="玩家姓名" prop="playerName"><el-input v-model="form.playerName" placeholder="请输入玩家姓名"/></el-form-item>
        <el-form-item label="手机号码" prop="playerPhone"><el-input v-model="form.playerPhone" placeholder="请输入手机号码"/></el-form-item>
        <el-form-item label="会员等级" prop="levelId">
          <el-select v-model="form.levelId" placeholder="请选择等级" @change="handleLevelChange">
            <el-option v-for="l in levelList" :key="l.levelId" :label="l.levelName" :value="l.levelId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="会员卡号" prop="cardNo"><el-input v-model="form.cardNo" placeholder="自动生成"/></el-form-item>
        <el-form-item label="可用积分" prop="availablePoints"><el-input-number v-model="form.availablePoints" :min="0"/></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status"><el-radio label="0">正常</el-radio><el-radio label="1">停用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="积分管理" :visible.sync="pointsOpen" width="450px" append-to-body>
      <el-form ref="pointsForm" :model="pointsForm" label-width="80px">
        <el-form-item label="会员" :label-width="80"><span>{{ pointsForm.playerName }}</span></el-form-item>
        <el-form-item label="当前积分" :label-width="80"><span>{{ pointsForm.currentPoints }}</span></el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="pointsForm.changeType">
            <el-radio label="0">增加积分</el-radio><el-radio label="1">扣减积分</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分数量" prop="points"><el-input-number v-model="pointsForm.points" :min="1" :max="99999"/></el-form-item>
        <el-form-item label="操作说明" prop="description"><el-input v-model="pointsForm.description" type="textarea"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPoints">确 定</el-button><el-button @click="pointsOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMember, getMember, addMember, updateMember, delMember, addPoints, deductPoints } from "@/api/system/member";
import { getAllLevels } from "@/api/system/memberLevel";

export default {
  name: "Member",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0,
      memberList: [], levelList: [], title: "", open: false, pointsOpen: false,
      queryParams: { pageNum: 1, pageSize: 10, playerName: null, playerPhone: null },
      form: {}, pointsForm: {}, rules: { playerName: [{ required: true, message: "玩家姓名不能为空", trigger: "blur" }] }
    };
  },
  created() { this.getList(); this.getLevelList(); },
  methods: {
    getList() { this.loading = true; listMember(this.queryParams).then(r => { this.memberList = r.rows; this.total = r.total; this.loading = false; }); },
    getLevelList() { getAllLevels().then(r => { this.levelList = r.data; }); },
    handleLevelChange(levelId) {
      const level = this.levelList.find(l => l.levelId === levelId);
      if (level) this.form.levelName = level.levelName;
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(val) { this.ids = val.map(i => i.memberId); this.single = val.length !== 1; this.multiple = val.length < 1; },
    handleAdd() { this.reset(); this.open = true; this.title = "新增会员"; },
    handleUpdate(row) {
      this.reset();
      const id = row.memberId || this.ids;
      getMember(id).then(r => { this.form = r.data; this.open = true; this.title = "修改会员"; });
    },
    handlePoints(row) {
      this.pointsForm = { memberId: row.memberId, playerName: row.playerName, currentPoints: row.availablePoints || 0, changeType: "0", points: 0, description: "" };
      this.pointsOpen = true;
    },
    handleDelete(row) {
      const ids = row.memberId || this.ids;
      this.$modal.confirm('是否确认删除？').then(() => delMember(ids)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.memberId != null) { updateMember(this.form).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList(); }); }
          else { addMember(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList(); }); }
        }
      });
    },
    submitPoints() {
      const { memberId, changeType, points, description } = this.pointsForm;
      if (changeType === "0") {
        addPoints(memberId, points, description).then(() => { this.$modal.msgSuccess("积分增加成功"); this.pointsOpen = false; this.getList(); });
      } else {
        deductPoints(memberId, points, description).then(() => { this.$modal.msgSuccess("积分扣减成功"); this.pointsOpen = false; this.getList(); }).catch(() => {});
      }
    },
    reset() {
      this.form = { memberId: null, playerId: null, playerName: null, playerPhone: null, levelId: null, levelName: null, cardNo: null, availablePoints: 0, status: "0", remark: null };
      this.resetForm("form");
    }
  }
};
</script>
