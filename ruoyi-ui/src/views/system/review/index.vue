<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="门店" prop="storeId">
        <el-select v-model="queryParams.storeId" placeholder="请选择门店" clearable>
          <el-option v-for="s in storeList" :key="s.storeId" :label="s.storeName" :value="s.storeId"/>
        </el-select>
      </el-form-item>
      <el-form-item label="显示状态">
        <el-select v-model="queryParams.isShow" placeholder="请选择" clearable>
          <el-option label="显示" value="0"/><el-option label="隐藏" value="1"/>
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" @click="handleAdd">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" @click="handleDelete" :disabled="multiple">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reviewList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="门店" align="center" prop="storeName"/>
      <el-table-column label="玩家" align="center" prop="playerName"/>
      <el-table-column label="评分" align="center" prop="rating">
        <template slot-scope="scope"><el-rate v-model="scope.row.rating" disabled text-color="#ff9900"/></template>
      </el-table-column>
      <el-table-column label="评价内容" align="center" prop="content" show-overflow-tooltip/>
      <el-table-column label="回复内容" align="center" prop="replyContent" show-overflow-tooltip/>
      <el-table-column label="显示" align="center" prop="isShow">
        <template slot-scope="scope"><el-switch v-model="scope.row.isShow" active-value="0" inactive-value="1" @change="handleShowChange(scope.row)"/></template>
      </el-table-column>
      <el-table-column label="评价时间" align="center" prop="createTime" width="180"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-chat-dot-round" @click="handleReply(scope.row)">回复</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="门店" prop="storeId"><el-select v-model="form.storeId" placeholder="请选择门店">
            <el-option v-for="s in storeList" :key="s.storeId" :label="s.storeName" :value="s.storeId"/>
          </el-select></el-form-item>
        <el-form-item label="玩家" prop="playerName"><el-input v-model="form.playerName" placeholder="请输入玩家姓名"/></el-form-item>
        <el-form-item label="评分" prop="rating"><el-rate v-model="form.rating" show-text/></el-form-item>
        <el-form-item label="评价内容" prop="content"><el-input v-model="form.content" type="textarea" rows="3"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button><el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="回复评价" :visible.sync="replyOpen" width="500px" append-to-body>
      <el-form ref="replyForm" :model="replyForm" label-width="80px">
        <el-form-item label="回复内容"><el-input v-model="replyForm.replyContent" type="textarea" rows="4"/></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReply">确 定</el-button><el-button @click="replyOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReview, getReview, addReview, updateReview, delReview } from "@/api/system/review";
import { getAllStores } from "@/api/system/store";

export default {
  name: "Review",
  data() {
    return {
      loading: true, ids: [], single: true, multiple: true, showSearch: true, total: 0,
      reviewList: [], storeList: [], title: "", open: false, replyOpen: false,
      queryParams: { pageNum: 1, pageSize: 10, storeId: null, isShow: null },
      form: {}, replyForm: {}, rules: {}
    };
  },
  created() { this.getList(); this.getStoreList(); },
  methods: {
    getList() { this.loading = true; listReview(this.queryParams).then(r => { this.reviewList = r.rows; this.total = r.total; this.loading = false; }); },
    getStoreList() { getAllStores().then(r => { this.storeList = r.data; }); },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(val) { this.ids = val.map(i => i.reviewId); this.single = val.length !== 1; this.multiple = val.length < 1; },
    handleAdd() { this.reset(); this.open = true; this.title = "添加评价"; },
    handleReply(row) { this.replyForm = { reviewId: row.reviewId, replyContent: row.replyContent }; this.replyOpen = true; },
    handleShowChange(row) { updateReview({ reviewId: row.reviewId, isShow: row.isShow }).then(() => { this.$modal.msgSuccess("修改成功"); }); },
    handleDelete(row) {
      const ids = row.reviewId || this.ids;
      this.$modal.confirm('是否确认删除？').then(() => delReview(ids)).then(() => { this.getList(); this.$modal.msgSuccess("删除成功"); }).catch(() => {});
    },
    submitReply() {
      updateReview(this.replyForm).then(() => { this.$modal.msgSuccess("回复成功"); this.replyOpen = false; this.getList(); });
    },
    submitForm() {
      if (this.form.reviewId != null) { updateReview(this.form).then(() => { this.$modal.msgSuccess("修改成功"); this.open = false; this.getList(); }); }
      else { addReview(this.form).then(() => { this.$modal.msgSuccess("新增成功"); this.open = false; this.getList(); }); }
    },
    reset() {
      this.form = { reviewId: null, storeId: null, storeName: null, playerId: null, playerName: null, rating: 5, content: null, isShow: "0" };
      this.resetForm("form");
    },
    cancel() { this.open = false; this.reset(); }
  }
};
</script>
