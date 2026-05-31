import knowledgeBase from './knowledge-base.json';

class KnowledgeBaseRetriever {
  constructor() {
    this.knowledgeBase = knowledgeBase.knowledge_base;
    this.metadata = knowledgeBase.metadata;
  }

  preprocessText(text) {
    return text.toLowerCase().replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, ' ');
  }

  tokenize(text) {
    const tokens = [];
    const keywords = ['VIP', 'WiFi', 'VIP包间', '普通会员', '银卡会员', '金卡会员', '钻卡会员'];

    for (const keyword of keywords) {
      if (text.includes(keyword)) {
        tokens.push(keyword);
      }
    }

    const chinesePattern = /[\u4e00-\u9fa5]+/g;
    let match;
    while ((match = chinesePattern.exec(text)) !== null) {
      tokens.push(match[0]);
    }

    return tokens;
  }

  calculateSimilarity(queryTokens, candidateTokens) {
    if (candidateTokens.length === 0) return 0;

    let matchCount = 0;
    for (const qt of queryTokens) {
      for (const ct of candidateTokens) {
        if (ct.includes(qt) || qt.includes(ct)) {
          matchCount++;
          break;
        }
      }
    }

    return matchCount / queryTokens.length;
  }

  search(query, topK = 3) {
    if (!query || query.trim() === '') {
      return [];
    }

    const processedQuery = this.preprocessText(query);
    const queryTokens = this.tokenize(processedQuery);

    if (queryTokens.length === 0) {
      return [];
    }

    const results = [];

    for (const item of this.knowledgeBase) {
      const keywords = item.keywords || [];

      let maxSimilarity = 0;

      for (const keyword of keywords) {
        const processedKeyword = this.preprocessText(keyword);
        const keywordTokens = this.tokenize(processedKeyword);
        const similarity = this.calculateSimilarity(queryTokens, keywordTokens);

        if (keywordTokens.some(kt => queryTokens.includes(kt))) {
          maxSimilarity = Math.max(maxSimilarity, similarity + 0.3);
        } else {
          maxSimilarity = Math.max(maxSimilarity, similarity);
        }
      }

      const questionTokens = this.tokenize(this.preprocessText(item.question));
      const questionSimilarity = this.calculateSimilarity(queryTokens, questionTokens);
      maxSimilarity = Math.max(maxSimilarity, questionSimilarity * 0.8);

      if (item.answer.includes(processedQuery.split(' ').find(word => word.length > 2))) {
        maxSimilarity += 0.2;
      }

      if (maxSimilarity > 0) {
        results.push({
          ...item,
          score: maxSimilarity
        });
      }
    }

    results.sort((a, b) => b.score - a.score);

    return results.slice(0, topK);
  }

  formatContext(searchResults) {
    if (!searchResults || searchResults.length === 0) {
      return '';
    }

    let context = '\n\n【相关业务知识】\n';
    context += '━━━━━━━━━━━━━━━━━━━━\n';

    for (let i = 0; i < searchResults.length; i++) {
      const result = searchResults[i];
      context += `\n📌 ${result.category}：\n`;
      context += `${result.answer}\n`;

      if (i < searchResults.length - 1) {
        context += '\n────────────────────\n';
      }
    }

    context += '━━━━━━━━━━━━━━━━━━━━\n';
    context += '\n⚠️ 请根据以上业务知识，准确回答用户问题。如果业务知识中没有相关内容，请基于通用麻将馆服务经验回答，但需提醒用户如有疑问可联系客服：400-XXX-XXXX。';

    return context;
  }

  getFullPrompt(query) {
    const searchResults = this.search(query, 3);
    const context = this.formatContext(searchResults);

    const basePrompt = `你是一个专业的麻将馆智能助手。你的职责是为顾客提供准确、友好的服务信息。`;

    let knowledgeInstruction = '';

    if (searchResults.length > 0) {
      knowledgeInstruction = `\n\n【当前任务】\n用户询问的问题与「${searchResults[0].category}」相关。\n请根据下方的业务知识库内容，准确回答用户的问题。\n\n回答要求：\n1. 严格按照业务知识库中的信息回答，不要编造或添加不存在的细节\n2. 如果用户询问的具体信息在知识库中没有提及，请明确告知用户，并建议联系客服\n3. 回答要专业、友好、简洁明了\n4. 如果有具体的数字、规则、流程，请清晰列出\n5. 涉及价格、时间等敏感信息时，可提醒用户以到店实际为准`;
    } else {
      knowledgeInstruction = `\n\n【当前任务】\n用户的问题没有直接匹配的业务知识。\n请基于你对麻将馆服务的了解，友好地回答用户的问题。\n\n回答要求：\n1. 如果不确定某些具体信息（如价格、时间等），请明确告知用户并建议联系客服确认\n2. 不要编造具体的数字、规则或承诺\n3. 建议用户如有疑问可联系客服：400-XXX-XXXX\n4. 保持专业、友好的服务态度`;
    }

    return {
      basePrompt,
      knowledgeInstruction,
      context,
      searchResults
    };
  }

  getAllCategories() {
    const categories = new Set();
    for (const item of this.knowledgeBase) {
      categories.add(item.category);
    }
    return Array.from(categories);
  }

  getKnowledgeByCategory(category) {
    return this.knowledgeBase.filter(item => item.category === category);
  }

  getVersion() {
    return this.metadata;
  }
}

export default new KnowledgeBaseRetriever();
