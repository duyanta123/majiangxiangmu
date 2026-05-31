<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <div class="ai-card">
          <h3><i class="el-icon-date"></i> AI智能排班</h3>
          <el-form :model="scheduleForm" inline>
            <el-form-item label="门店">
              <el-select v-model="scheduleForm.storeId" placeholder="请选择门店">
                <el-option v-for="s in stores" :key="s.storeId" :label="s.storeName" :value="s.storeId"/>
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker v-model="scheduleForm.date" type="date" format="yyyy-MM-dd" value-format="yyyy-MM-dd"/>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="generateSchedule" :loading="scheduleLoading">生成排班</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="scheduleList" style="margin-top: 20px;" v-if="scheduleList.length > 0">
            <el-table-column prop="timeSlot" label="时段" width="120"/>
            <el-table-column prop="employeeName" label="员工" width="80"/>
            <el-table-column prop="position" label="岗位" width="80"/>
            <el-table-column prop="workload" label="工作量" width="70"/>
            <el-table-column prop="aiSuggestion" label="AI建议"/>
          </el-table>
          <el-empty v-else description="点击生成排班按钮获取排班建议" style="margin-top: 40px;"/>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="ai-card">
          <h3><i class="el-icon-data-line"></i> AI门店分析</h3>
          <el-form :model="analyzeForm" inline>
            <el-form-item label="门店">
              <el-select v-model="analyzeForm.storeId" placeholder="请选择门店">
                <el-option v-for="s in stores" :key="s.storeId" :label="s.storeName" :value="s.storeId"/>
              </el-select>
            </el-form-item>
            <el-button type="primary" @click="runAnalysis" :loading="analyzeLoading">运行分析</el-button>
          </el-form>
          <div v-if="analysisResult" style="margin-top: 20px;">
            <el-row :gutter="10">
              <el-col :span="12">
                <el-card shadow="hover">
                  <div class="stat-item">
                    <span class="label">完成订单</span>
                    <span class="value">{{ analysisResult.completedOrders || 0 }}</span>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card shadow="hover">
                  <div class="stat-item">
                    <span class="label">消费次数</span>
                    <span class="value">{{ analysisResult.totalConsumes || 0 }}</span>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="12" style="margin-top: 10px;">
                <el-card shadow="hover">
                  <div class="stat-item">
                    <span class="label">总收入</span>
                    <span class="value">¥{{ analysisResult.totalRevenue || 0 }}</span>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="12" style="margin-top: 10px;">
                <el-card shadow="hover">
                  <div class="stat-item">
                    <span class="label">活跃玩家</span>
                    <span class="value">{{ analysisResult.activePlayers || 0 }}</span>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <h4 style="margin-top: 20px; color: #409EFF;"><i class="el-icon-lightbulb"></i> AI建议</h4>
            <ul class="suggestion-list">
              <li v-for="(s, i) in analysisResult.suggestions" :key="i">{{ s }}</li>
            </ul>
          </div>
          <div v-else style="margin-top: 40px; text-align: center; color: #999;">
            <i class="el-icon-bar-chart" style="font-size: 48px; margin-bottom: 10px;"/>
            <p>请选择门店并点击运行分析</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <div class="ai-card">
          <h3><i class="el-icon-present"></i> AI营销推荐</h3>
          <el-form :model="recommendForm" inline>
            <el-form-item label="玩家ID">
              <el-input v-model="recommendForm.playerId" placeholder="请输入玩家ID" style="width: 200px;"/>
            </el-form-item>
            <el-button type="primary" @click="generateNewRecommendations">生成推荐</el-button>
          </el-form>
          <el-table :data="recommendationList" style="margin-top: 20px;" v-if="recommendationList.length > 0">
            <el-table-column prop="recType" label="推荐类型" width="100"/>
            <el-table-column prop="recContent" label="推荐内容"/>
            <el-table-column prop="recReason" label="推荐理由" show-overflow-tooltip/>
          </el-table>
          <el-empty v-else description="输入玩家ID后生成个性化推荐" style="margin-top: 40px;"/>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="ai-card">
          <h3><i class="el-icon-chat-dot-round"></i> AI语音助手 <el-tag size="mini" type="success">RAG知识库</el-tag></h3>
          <div class="chat-container">
            <div class="chat-history" ref="chatHistoryRef">
              <div v-for="(msg, i) in chatHistory" :key="i" :class="['chat-message', msg.type]">
                <div class="message-content">{{ msg.content }}</div>
              </div>
              <div v-if="isAiTyping" class="chat-message ai">
                <div class="message-content typing">
                  <i class="el-icon-loading"></i> AI正在思考...
                  <span v-if="currentRetrieval" class="retrieval-info">
                    已检索知识库：{{ currentRetrieval }}
                  </span>
                </div>
              </div>
            </div>
            <div class="chat-input-area">
              <el-input
                v-model="chatInput"
                placeholder="请输入您的问题，如：会员积分怎么兑换？"
                @keyup.enter.native="sendMessage"
                :disabled="isAiTyping"
              />
              <el-button type="primary" @click="sendMessage" :loading="isAiTyping" :disabled="!chatInput || isAiTyping">
                <i class="el-icon-send"></i> 发送
              </el-button>
            </div>
            <div v-if="apiError" class="api-error" style="margin-top: 10px; color: #F56C6C; font-size: 12px;">
              {{ apiError }}
            </div>
            <div class="knowledge-tip" style="margin-top: 10px; color: #909399; font-size: 12px;">
              💡 提示：AI助手基于麻将馆专属知识库回答，可准确解答业务问题
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import knowledgeRetriever from './knowledge-retriever.js';

export default {
  name: "AiCenter",
  data() {
    return {
      stores: [],
      scheduleForm: { storeId: null, date: "" },
      analyzeForm: { storeId: null },
      recommendForm: { playerId: null },
      scheduleList: [],
      analysisResult: null,
      recommendationList: [],
      chatHistory: [
        {
          type: 'ai',
          content: '您好！我是麻将馆AI助手，基于专属知识库为您提供服务。\n\n我可以准确解答以下业务问题：\n• 门店营业时间、地址、联系方式\n• 会员等级、积分规则、积分兑换\n• 预约规则、取消预约流程\n• 收费标准、优惠活动\n• 麻将玩法、付款方式等\n\n请问有什么可以帮助您的？'
        }
      ],
      chatInput: "",
      scheduleLoading: false,
      analyzeLoading: false,
      isAiTyping: false,
      apiError: "",
      currentRetrieval: ""
    };
  },
  created() {
    this.loadStores();
  },
  methods: {
    loadStores() {
      this.stores = [
        { storeId: 1, storeName: "总店" },
        { storeId: 2, storeName: "分店" }
      ];
      this.scheduleForm.storeId = 1;
      this.analyzeForm.storeId = 1;
    },
    generateSchedule() {
      if (!this.scheduleForm.storeId) {
        this.$message.warning("请选择门店");
        return;
      }
      if (!this.scheduleForm.date) {
        this.$message.warning("请选择日期");
        return;
      }
      this.scheduleLoading = true;
      setTimeout(() => {
        this.scheduleLoading = false;
        const isWeekend = this.isWeekend(this.scheduleForm.date);
        this.scheduleList = this.generateMockSchedule(isWeekend);
        this.$message.success("排班生成成功");
      }, 800);
    },
    isWeekend(dateStr) {
      const date = new Date(dateStr);
      const day = date.getDay();
      return day === 0 || day === 6;
    },
    generateMockSchedule(isWeekend) {
      return [
        { timeSlot: "09:00-12:00", employeeName: "张三", position: "前台接待", workload: isWeekend ? 7 : 5, aiSuggestion: "上午时段客流较少，建议安排基础人手" },
        { timeSlot: "12:00-15:00", employeeName: "李四", position: "服务员", workload: isWeekend ? 8 : 6, aiSuggestion: "午间用餐高峰，建议增加人手" },
        { timeSlot: "15:00-18:00", employeeName: "王五", position: "收银员", workload: isWeekend ? 6 : 4, aiSuggestion: "下午时段相对空闲" },
        { timeSlot: "18:00-21:00", employeeName: "赵六", position: "前台接待", workload: isWeekend ? 9 : 7, aiSuggestion: isWeekend ? "晚间客流高峰，建议增加2名员工" : "晚间客流较多，建议保持充足人手" },
        { timeSlot: "21:00-24:00", employeeName: "钱七", position: "值班经理", workload: isWeekend ? 7 : 5, aiSuggestion: "夜间值班，确保安全" }
      ];
    },
    runAnalysis() {
      if (!this.analyzeForm.storeId) {
        this.$message.warning("请选择门店");
        return;
      }
      this.analyzeLoading = true;
      setTimeout(() => {
        this.analyzeLoading = false;
        this.analysisResult = {
          completedOrders: 156,
          totalConsumes: 328,
          totalRevenue: 85600,
          activePlayers: 45,
          suggestions: [
            "周五晚间客流高峰，建议增加2名前台员工",
            "建议在周末增加促销活动，提升营业额",
            "会员消费占比65%，建议加强会员权益推广",
            "下午14:00-16:00时段客流偏低，可考虑推出下午茶优惠"
          ]
        };
      }, 800);
    },
    generateNewRecommendations() {
      if (!this.recommendForm.playerId) {
        this.$message.warning("请输入玩家ID");
        return;
      }
      this.recommendationList = [
        { recType: "优惠活动", recContent: "专属8折优惠券一张", recReason: "玩家近期消费频繁，忠诚度高" },
        { recType: "会员升级", recContent: "升级为金卡会员", recReason: "累计消费已达到金卡标准" },
        { recType: "活动邀请", recContent: "邀请参加周末麻将比赛", recReason: "玩家对竞技类活动感兴趣" }
      ];
      this.$message.success("推荐生成成功");
    },
    async sendMessage() {
      if (!this.chatInput || this.isAiTyping) return;

      const userInput = this.chatInput.trim();
      this.chatHistory.push({ type: 'user', content: userInput });
      this.chatInput = "";
      this.isAiTyping = true;
      this.apiError = "";
      this.currentRetrieval = "";

      try {
        const chatHistoryText = this.chatHistory.slice(0, -1).map(msg =>
          msg.type === 'user' ? `用户: ${msg.content}` : `AI: ${msg.content}`
        ).join('\n');
        
        const result = await this.callAiAgent(userInput, chatHistoryText);
        
        this.chatHistory.push({ type: 'ai', content: result.response });
        
        if (result.fromKnowledge && result.retrieval) {
          this.currentRetrieval = result.retrieval;
        }

      } catch (error) {
        console.error("API调用失败:", error);
        this.apiError = "AI服务暂时不可用，请稍后再试。错误: " + (error.message || "未知错误");
        this.chatHistory.push({
          type: 'ai',
          content: this.getFallbackResponse(userInput)
        });
      } finally {
        this.isAiTyping = false;
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      }
    },
    async callAiAgent(question, chatHistory) {
      try {
        const response = await fetch('http://localhost:8080/system/ai/agent/chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ 
            question: question,
            chatHistory: chatHistory || ''
          })
        });

        const result = await response.json();

        if (result.code === 200 && result.data) {
          return {
            response: result.data.response,
            intent: result.data.intent,
            fromDatabase: result.data.fromDatabase,
            fromKnowledge: result.data.fromKnowledge,
            success: result.data.success,
            retrieval: result.data.metadata?.knowledgeContext || ''
          };
        } else {
          throw new Error(result.msg || 'Agent处理失败');
        }
      } catch (error) {
        console.error("Agent调用失败:", error);
        throw error;
      }
    },
    async callDeepSeekAPIWithRAG(userInput) {
      const apiKey = "sk-d295badc99cf4ccdb18e0cbfa64a5721";
      const apiUrl = "https://api.deepseek.com/chat/completions";

      const retrievalResult = knowledgeRetriever.getFullPrompt(userInput);

      const systemPrompt = `${retrievalResult.basePrompt}
${retrievalResult.knowledgeInstruction}
${retrievalResult.context}`;

      const messages = [
        { role: "system", content: systemPrompt },
        ...this.chatHistory.slice(0, -1).map(msg => ({
          role: msg.type === 'user' ? 'user' : 'assistant',
          content: msg.content
        })),
        { role: "user", content: userInput }
      ];

      const requestBody = {
        model: "deepseek-chat",
        messages: messages,
        temperature: 0.3,
        max_tokens: 1500
      };

      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 20000);

      try {
        const response = await fetch(apiUrl, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + apiKey
          },
          body: JSON.stringify(requestBody),
          signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (!response.ok) {
          const errorText = await response.text();
          console.error("API错误响应:", response.status, errorText);
          throw new Error(`HTTP ${response.status}: ${errorText || response.statusText}`);
        }

        const data = await response.json();
        if (data.choices && data.choices.length > 0 && data.choices[0].message) {
          const retrieval = retrievalResult.searchResults.length > 0
            ? retrievalResult.searchResults.map(r => r.category).join(', ')
            : '';
          return {
            response: data.choices[0].message.content,
            retrieval: retrieval
          };
        } else {
          throw new Error("API返回格式错误");
        }
      } catch (error) {
        clearTimeout(timeoutId);
        if (error.name === 'AbortError') {
          throw new Error("请求超时，请稍后再试");
        }
        throw error;
      }
    },
    getFallbackResponse(userInput) {
      const lowerInput = userInput.toLowerCase();

      if (lowerInput.includes("营业时间") || lowerInput.includes("几点开门") || lowerInput.includes("几点关门")) {
        return "我们的门店营业时间为：周一至周五 9:00-24:00，周末及节假日24小时营业。建议提前预约以确保有包间哦！";
      }
      if (lowerInput.includes("地址") || lowerInput.includes("在哪里") || lowerInput.includes("位置")) {
        return "我们的门店位于市中心繁华地段，具体地址请咨询前台获取详细信息。您也可以通过地图导航搜索「麻将馆」找到我们。";
      }
      if (lowerInput.includes("会员") || lowerInput.includes("等级") || lowerInput.includes("银卡") || lowerInput.includes("金卡") || lowerInput.includes("钻卡")) {
        return "我们的会员等级分为四个等级：\n• 普通会员：享受基础服务\n• 银卡会员：消费满1000元升级，享9折优惠\n• 金卡会员：消费满5000元升级，享8折优惠\n• 钻卡会员：消费满10000元升级，享7折优惠\n会员生日当天可获赠免费包间体验！";
      }
      if (lowerInput.includes("积分") || lowerInput.includes("积分兑换") || lowerInput.includes("兑换礼品")) {
        return "积分规则：消费1元积1分\n积分兑换：\n• 100积分 = 10元消费抵扣\n• 500积分 = 免费茶水套餐\n• 1000积分 = 免费2小时包间\n• 2000积分 = 精美礼品一份\n如需兑换，请联系前台办理。";
      }
      if (lowerInput.includes("预约") || lowerInput.includes("订位") || lowerInput.includes("订包间")) {
        return "预约规则：\n• 可提前1-7天预约\n• 预约需提前30分钟到场\n• 如需取消预约，请提前2小时告知\n• 包间保留15分钟，超时将自动取消\n预约方式：电话预约或到店预约";
      }
      if (lowerInput.includes("优惠") || lowerInput.includes("活动") || lowerInput.includes("折扣") || lowerInput.includes("促销")) {
        return "当前优惠活动：\n• 新客优惠：首次到店享8折\n• 工作日特惠：周一至周四全天9折\n• 夜间特惠：22:00后包间费8折\n• 团购优惠：美团/大众点评搜索「麻将馆」享更多优惠\n更多活动请关注我们的官方公众号！";
      }
      if (lowerInput.includes("怎么收费") || lowerInput.includes("价格") || lowerInput.includes("多少钱") || lowerInput.includes("台费")) {
        return "收费标准：\n• 大厅散台：30-50元/小时\n• 普通包间：60-80元/小时\n• VIP包间：100-150元/小时\n• 茶水另计：10-30元/杯\n节假日可能有调整，以到店实际价格为准。";
      }
      if (lowerInput.includes("麻将规则") || lowerInput.includes("怎么玩") || lowerInput.includes("胡牌")) {
        return "我们提供多种麻将玩法：\n• 四川麻将：血战到底、血流成河\n• 广东麻将：推倒胡\n• 国标麻将：竞技规则\n• 本地麻将：特色玩法\n每桌配有详细规则说明，服务员也可现场讲解。";
      }

      return "感谢您的咨询！如有其他问题，欢迎随时联系我们的工作人员。客服热线：400-XXX-XXXX。";
    },
    scrollToBottom() {
      if (this.$refs.chatHistoryRef) {
        this.$refs.chatHistoryRef.scrollTop = this.$refs.chatHistoryRef.scrollHeight;
      }
    }
  }
};
</script>

<style scoped>
.ai-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  min-height: 400px;
}
.ai-card h3 {
  margin: 0 0 20px;
  font-size: 16px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}
.stat-item {
  display: flex;
  flex-direction: column;
  text-align: center;
}
.stat-item .label {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}
.stat-item .value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}
.suggestion-list {
  list-style: none;
  padding: 0;
  margin: 10px 0 0;
}
.suggestion-list li {
  padding: 8px 12px;
  background: #f0f9ff;
  border-left: 3px solid #409EFF;
  margin-bottom: 8px;
  border-radius: 0 4px 4px 0;
}
.chat-container {
  display: flex;
  flex-direction: column;
  height: 350px;
}
.chat-history {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #eee;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 4px;
}
.chat-message {
  margin: 8px 0;
  display: flex;
}
.chat-message.user {
  justify-content: flex-end;
}
.chat-message.ai {
  justify-content: flex-start;
}
.message-content {
  max-width: 80%;
  padding: 8px 12px;
  border-radius: 8px;
  line-height: 1.5;
  white-space: pre-wrap;
}
.chat-message.user .message-content {
  background: #409EFF;
  color: #fff;
}
.chat-message.ai .message-content {
  background: #f0f0f0;
  color: #333;
}
.chat-input-area {
  display: flex;
  gap: 10px;
}
.chat-input-area .el-input {
  flex: 1;
}
.api-error {
  padding: 8px;
  background: #fef0f0;
  border-radius: 4px;
}
.retrieval-info {
  display: block;
  font-size: 11px;
  margin-top: 4px;
  color: #67C23A;
}
</style>
