package com.chang1o.service;

import com.chang1o.model.UserHealthData;
import com.chang1o.model.DailyCheckIn;
import com.chang1o.model.Recipe;
import com.chang1o.model.RecipeIngredient;
import com.chang1o.model.PantryItem;
import com.chang1o.dao.DailyCheckInDao;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.time.LocalDate;

public class ApiService {

    private static final String API_URL;
    private static final String API_KEY;
    private static final String API_MODEL;

    static {
        Properties props = new Properties();
        String url = "";
        String key = "";
        String model = "";

        try (InputStream input = ApiService.class.getClassLoader()
                .getResourceAsStream("api.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            System.err.println("加载API配置文件时出错: " + e.getMessage());
        }

        String envKey = null;
        String provider = "";

        if ((envKey = System.getenv("KIMI_API_KEY")) != null && !envKey.isEmpty()) {
            provider = "kimi";
            url = props.getProperty("kimi.api.url", "https://api.moonshot.cn/v1/chat/completions");
            model = props.getProperty("kimi.api.model", "kimi-k2");
        } else if ((envKey = System.getenv("ZHIPU_API_KEY")) != null && !envKey.isEmpty()) {
            provider = "zhipu";
            url = props.getProperty("zhipu.api.url", "https://open.bigmodel.cn/api/paas/v4/chat/completions");
            model = props.getProperty("zhipu.api.model", "glm-4.6");
        } else if ((envKey = System.getenv("DEEPSEEK_API_KEY")) != null && !envKey.isEmpty()) {
            provider = "deepseek";
            url = props.getProperty("deepseek.api.url", "https://api.deepseek.com/chat/completions");
            model = props.getProperty("deepseek.api.model", "deepseek-chat");
        } else {
            provider = "kimi";
            url = props.getProperty("kimi.api.url", "https://api.moonshot.cn/v1/chat/completions");
            key = props.getProperty("kimi.api.key", "");
            model = props.getProperty("kimi.api.model", "kimi-k2");
        }

        if (envKey != null) {
            key = envKey;
        }

        API_URL = url;
        API_KEY = key;
        API_MODEL = model;

        if (API_KEY.isEmpty() || "YOUR_API_KEY_HERE".equals(API_KEY)) {
            System.err.println("警告: API 未配置。请设置配置文件或设置环境变量API KEY");
        }
    }

    private HealthDataService healthDataService;
    private RecipeService recipeService;
    private PantryService pantryService;

    public ApiService() {
        this.healthDataService = new HealthDataService();
        this.recipeService = new RecipeService();
        this.pantryService = new PantryService();
    }

    public String generatePersonalizedHealthAdvice(int userId) {
        try {
            UserHealthData healthData = healthDataService.getLatestHealthData(userId);
            List<DailyCheckIn> recentCheckIns = healthDataService.getRecentCheckIns(userId, 7);

            String prompt = buildHealthAdvicePrompt(healthData, recentCheckIns);

            String response = callAIAPI(prompt);

            return response != null ? response : "抱歉，暂时无法生成个性化建议，请稍后再试。";

        } catch (Exception e) {
            System.err.println("生成个性化健康建议时发生错误: " + e.getMessage());
            return "抱歉，生成建议时出现问题，请稍后再试。";
        }
    }

    public List<String> generateSmartRecipeRecommendations(int userId) {
        try {
            UserHealthData healthData = healthDataService.getLatestHealthData(userId);
            List<Recipe> userRecipes = recipeService.getRecipesByUser(userId);

            String prompt = buildRecipeRecommendationPrompt(healthData, userRecipes);

            String response = callAIAPI(prompt);

            if (response != null) {
                return parseRecipeRecommendations(response);
            }

        } catch (Exception e) {
            System.err.println("生成智能食谱推荐时发生错误: " + e.getMessage());
        }

        return getDefaultRecipeRecommendations();
    }

    public String generateSmartShoppingList(int userId, List<Integer> recipeIds) {
        try {
            List<PantryItem> pantryItems = pantryService.getPantryItemsByUser(userId);

            List<Recipe> selectedRecipes = new ArrayList<>();
            for (Integer recipeId : recipeIds) {
                Recipe recipe = recipeService.getRecipeById(recipeId);
                if (recipe != null) {
                    selectedRecipes.add(recipe);
                }
            }

            String prompt = buildShoppingListPrompt(selectedRecipes, pantryItems);

            String response = callAIAPI(prompt);

            return response != null ? response : "抱歉，暂时无法生成智能购物清单，请稍后再试。";

        } catch (Exception e) {
            System.err.println("生成智能购物清单时发生错误: " + e.getMessage());
            return "抱歉，生成购物清单时出现问题，请稍后再试。";
        }
    }

    public String generateNutritionAnalysisReport(int userId, int days) {
        try {
            List<DailyCheckIn> recentCheckIns = healthDataService.getRecentCheckIns(userId, days);
            UserHealthData healthData = healthDataService.getLatestHealthData(userId);

            if (recentCheckIns.isEmpty()) {
                return "暂无足够的打卡数据进行营养分析，请先进行每日打卡。";
            }

            String prompt = buildNutritionAnalysisPrompt(healthData, recentCheckIns, days);

            String response = callAIAPI(prompt);

            return response != null ? response : "抱歉，暂时无法生成营养分析报告，请稍后再试。";

        } catch (Exception e) {
            System.err.println("生成营养分析报告时发生错误: " + e.getMessage());
            return "抱歉，生成营养分析报告时出现问题，请稍后再试。";
        }
    }

    private String buildHealthAdvicePrompt(UserHealthData healthData, List<DailyCheckIn> recentCheckIns) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的营养师和健康顾问，请根据以下用户信息提供个性化的健康建议：\n\n");

        if (healthData != null) {
            prompt.append("用户基本信息：\n");
            prompt.append("- 身高：").append(healthData.getHeight()).append("cm\n");
            prompt.append("- 体重：").append(healthData.getWeight()).append("kg\n");
            prompt.append("- 年龄：").append(healthData.getAge()).append("岁\n");
            prompt.append("- 性别：").append("M".equals(healthData.getGender()) ? "男" : "女").append("\n");
            prompt.append("- BMI：").append(String.format("%.1f", healthData.calculateBMI())).append(" (")
                    .append(healthData.getBMICategory()).append(")\n");
            prompt.append("- 基础代谢率：").append(String.format("%.0f", healthData.calculateBMR())).append(" 卡路里/天\n");
            prompt.append("- 活动水平：").append(getActivityLevelDescription(healthData.getActivityLevel())).append("\n");

            if (healthData.getTargetWeight() > 0) {
                double diff = healthData.getWeightDifference();
                prompt.append("- 目标体重：").append(healthData.getTargetWeight()).append("kg\n");
                if (Math.abs(diff) >= 0.5) {
                    prompt.append("- 需要调整：").append(String.format("%.1f", Math.abs(diff))).append("kg\n");
                }
            }
            prompt.append("\n");
        }

        if (!recentCheckIns.isEmpty()) {
            prompt.append("最近7天打卡数据：\n");
            DailyCheckInDao.HealthStatistics stats = healthDataService.getHealthStatistics(healthData.getUserId(), 7);
            prompt.append("- 平均健康评分：").append(String.format("%.1f", stats.getAvgHealthScore())).append("/100\n");
            prompt.append("- 平均睡眠时长：").append(String.format("%.1f", stats.getAvgSleepHours())).append("小时\n");
            prompt.append("- 平均饮水量：").append(String.format("%.0f", stats.getAvgWaterIntake())).append("ml\n");
            prompt.append("- 平均运动时长：").append(String.format("%.0f", stats.getAvgExerciseMinutes())).append("分钟\n");
            prompt.append("- 连续打卡天数：").append(stats.getConsecutiveDays()).append("天\n");
            prompt.append("\n");
        }

        prompt.append("请提供：\n");
        prompt.append("1. 针对用户当前状况的具体健康建议\n");
        prompt.append("2. 饮食方面的建议\n");
        prompt.append("3. 运动方面的建议\n");
        prompt.append("4. 生活习惯改善建议\n");
        prompt.append("5. 需要特别注意的事项\n\n");
        prompt.append("请用中文回答，语气要专业但友好，建议要具体可操作。");

        return prompt.toString();
    }

    private String buildRecipeRecommendationPrompt(UserHealthData healthData, List<Recipe> userRecipes) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的营养师，请根据以下用户信息推荐适合的食谱：\n\n");

        if (healthData != null) {
            prompt.append("用户健康信息：\n");
            prompt.append("- BMI：").append(String.format("%.1f", healthData.calculateBMI())).append(" (")
                    .append(healthData.getBMICategory()).append(")\n");
            prompt.append("- 每日能量需求：").append(String.format("%.0f", healthData.calculateTDEE())).append(" 卡路里\n");
            prompt.append("- 性别：").append("M".equals(healthData.getGender()) ? "男" : "女").append("\n");
            prompt.append("- 年龄：").append(healthData.getAge()).append("岁\n");
            prompt.append("\n");
        }

        prompt.append("用户已有食谱（共").append(userRecipes.size()).append("个）：\n");
        if (userRecipes.isEmpty()) {
            prompt.append("- 暂无食谱\n");
        } else {
            for (Recipe recipe : userRecipes) {
                prompt.append("- ").append(recipe.getName());
                if (recipe.getCategory() != null) {
                    prompt.append(" (").append(recipe.getCategory().getName()).append(")");
                }
                prompt.append("\n");
            }
        }
        prompt.append("\n");

        prompt.append("请推荐：\n");
        prompt.append("1. 5个适合用户当前健康状况的食谱\n");
        prompt.append("2. 每个食谱要说明推荐理由\n");
        prompt.append("3. 考虑营养均衡和卡路里控制\n");
        prompt.append("4. 食谱要简单易做\n");
        prompt.append("5. 避免推荐与用户已有食谱重复或过于相似的菜品\n\n");
        prompt.append("请用中文回答，格式要清晰易读。");

        return prompt.toString();
    }

    private String buildShoppingListPrompt(List<Recipe> selectedRecipes, List<PantryItem> pantryItems) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位智能购物助手。\n\n");

        if (selectedRecipes.isEmpty()) {
            prompt.append("【任务】根据用户的当前库存情况，生成日常补货建议\n\n");

            prompt.append("用户当前库存：\n");
            if (pantryItems.isEmpty()) {
                prompt.append("- 库存为空\n");
            } else {
                for (PantryItem item : pantryItems) {
                    prompt.append("- ").append(item.getIngredient().getName()).append(" (").append(item.getQuantity())
                            .append(")");
                    if (item.getExpiryDate() != null) {
                        prompt.append(" 过期日期：").append(item.getExpiryDate());
                    }
                    prompt.append("\n");
                }
            }
            prompt.append("\n");

            prompt.append("请提供：\n");
            prompt.append("1. 分析当前库存状况（充足/缺少/即将过期的食材）\n");
            prompt.append("2. 推荐需要补充的常用食材和调料\n");
            prompt.append("3. 建议购买的新鲜食材（肉类、蔬菜、水果等）\n");
            prompt.append("4. 为每个食材估算合适的购买数量\n");
            prompt.append("5. 按食材类别分类整理（肉类、蔬菜、调料、主食等）\n");
            prompt.append("6. 标注优先级（必需/建议/可选）\n");
            prompt.append("7. 估算大概的总预算\n\n");
            prompt.append("请用中文回答，格式要清晰实用。");

        } else {
            prompt.append("【任务】根据用户选择的食谱，生成精确的购物清单\n\n");

            prompt.append("用户选择的食谱及所需食材：\n");
            for (Recipe recipe : selectedRecipes) {
                prompt.append("【").append(recipe.getName()).append("】\n");

                if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                    prompt.append("  所需食材：\n");
                    for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
                        if (recipeIngredient.getIngredient() != null) {
                            prompt.append("    - ").append(recipeIngredient.getIngredient().getName())
                                    .append(" (").append(recipeIngredient.getQuantity()).append(")\n");
                        }
                    }
                } else {
                    prompt.append("  备注：此食谱暂无详细食材清单，请从以下制作步骤中推测：\n");
                    if (recipe.getInstructions() != null) {
                        prompt.append("  制作步骤：").append(
                                recipe.getInstructions().substring(0, Math.min(150, recipe.getInstructions().length())))
                                .append("...\n");
                    }
                }
                prompt.append("\n");
            }

            prompt.append("用户当前库存：\n");
            if (pantryItems.isEmpty()) {
                prompt.append("- 库存为空\n");
            } else {
                for (PantryItem item : pantryItems) {
                    prompt.append("- ").append(item.getIngredient().getName()).append(" (").append(item.getQuantity())
                            .append(")");
                    if (item.getExpiryDate() != null) {
                        prompt.append(" 过期日期：").append(item.getExpiryDate());
                    }
                    prompt.append("\n");
                }
            }
            prompt.append("\n");

            prompt.append("请提供：\n");
            prompt.append("1. 根据食谱所需的完整食材清单\n");
            prompt.append("2. 去除用户已有库存的物品（考虑库存数量是否足够）\n");
            prompt.append("3. 标注需要优先购买的物品（考虑保质期）\n");
            prompt.append("4. 为每个食材估算合适的购买数量\n");
            prompt.append("5. 添加必要的基础调料和食材\n");
            prompt.append("6. 按食材类别分类整理（肉类、蔬菜、调料等）\n");
            prompt.append("7. 估算大概的总预算\n\n");
            prompt.append("请用中文回答，格式要清晰实用。");
        }

        return prompt.toString();
    }

    private String buildNutritionAnalysisPrompt(UserHealthData healthData, List<DailyCheckIn> checkIns, int days) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的营养师，请分析用户的营养状况：\n\n");

        if (healthData != null) {
            prompt.append("用户基本信息：\n");
            prompt.append("- BMI：").append(String.format("%.1f", healthData.calculateBMI())).append(" (")
                    .append(healthData.getBMICategory()).append(")\n");
            prompt.append("- 每日能量需求：").append(String.format("%.0f", healthData.calculateTDEE())).append(" 卡路里\n");
            prompt.append("\n");
        }

        DailyCheckInDao.HealthStatistics stats = healthDataService.getHealthStatistics(healthData.getUserId(), days);

        prompt.append("最近").append(days).append("天健康数据：\n");
        prompt.append("- 平均健康评分：").append(String.format("%.1f", stats.getAvgHealthScore())).append("/100\n");
        prompt.append("- 平均睡眠：").append(String.format("%.1f", stats.getAvgSleepHours())).append("小时\n");
        prompt.append("- 平均饮水：").append(String.format("%.0f", stats.getAvgWaterIntake())).append("ml\n");
        prompt.append("- 平均运动：").append(String.format("%.0f", stats.getAvgExerciseMinutes())).append("分钟\n");
        prompt.append("- 平均心情：").append(String.format("%.1f", stats.getAvgMoodScore())).append("/5\n");
        prompt.append("\n");

        prompt.append("请提供：\n");
        prompt.append("1. 整体营养状况评估\n");
        prompt.append("2. 可能存在的营养问题\n");
        prompt.append("3. 具体的营养改善建议\n");
        prompt.append("4. 推荐的营养素摄入重点\n");
        prompt.append("5. 饮食结构调整建议\n\n");
        prompt.append("请用中文回答，要专业但易懂。");

        return prompt.toString();
    }

    private String callAIAPI(String prompt) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String requestBody = buildRequestBody(prompt);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return parseApiResponse(response.toString());
                }
            } else {
                System.err.println("API调用失败，响应码：" + responseCode);
                return null;
            }

        } catch (Exception e) {
            System.err.println("调用API时发生错误: " + e.getMessage());
            return null;
        }
    }

    private String buildRequestBody(String prompt) {
        return "{\n" +
                "  \"model\": \"" + API_MODEL + "\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + escapeJson(prompt) + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"temperature\": 0.7,\n" +
                "  \"max_tokens\": 8000\n" +
                "}";
    }

    private String parseApiResponse(String response) {
        try {
            if (response.contains("\"content\":\"")) {
                int startIndex = response.indexOf("\"content\":\"") + 11;

                StringBuilder content = new StringBuilder();
                boolean inEscape = false;

                for (int i = startIndex; i < response.length(); i++) {
                    char c = response.charAt(i);

                    if (inEscape) {
                        switch (c) {
                            case 'n':
                                content.append('\n');
                                break;
                            case 'r':
                                content.append('\r');
                                break;
                            case 't':
                                content.append('\t');
                                break;
                            case '\\':
                                content.append('\\');
                                break;
                            case '"':
                                content.append('"');
                                break;
                            default:
                                content.append(c);
                                break;
                        }
                        inEscape = false;
                    } else if (c == '\\') {
                        inEscape = true;
                    } else if (c == '"') {
                        return content.toString();
                    } else {
                        content.append(c);
                    }
                }

                return content.toString();
            }
            return response;
        } catch (Exception e) {
            System.err.println("解析API响应时发生错误: " + e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    private String escapeJson(String input) {
        if (input == null)
            return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private List<String> parseRecipeRecommendations(String response) {
        List<String> recommendations = new ArrayList<>();
        String[] lines = response.split("\n");

        boolean inRecipeList = false;
        for (String line : lines) {
            line = line.trim();

            if (line.matches("^\\d+[.)].*") && line.length() > 3) {
                recommendations.add(line);
                inRecipeList = true;
            } else if (inRecipeList && !line.isEmpty() && !line.startsWith("请") && !line.startsWith("推荐理由")) {
                if (line.length() > 5) {
                    recommendations.add("  " + line);
                }
            }
        }

        if (recommendations.isEmpty()) {
            return getDefaultRecipeRecommendations();
        }

        return recommendations;
    }

    private List<String> getDefaultRecipeRecommendations() {
        List<String> defaults = new ArrayList<>();
        defaults.add("1. 清蒸鲈鱼 - 低脂高蛋白，适合控制体重");
        defaults.add("2. 番茄鸡蛋面 - 营养均衡，制作简单");
        defaults.add("3. 蒜蓉西兰花 - 富含维生素，健康蔬菜");
        defaults.add("4. 红枣银耳汤 - 美容养颜，滋补养生");
        defaults.add("5. 香菇鸡肉粥 - 易消化，营养丰富");
        return defaults;
    }

    private String getActivityLevelDescription(String activityLevel) {
        switch (activityLevel) {
            case "sedentary":
                return "久坐不动";
            case "light":
                return "轻度活动";
            case "moderate":
                return "中度活动";
            case "active":
                return "高度活动";
            case "very_active":
                return "极高活动";
            default:
                return "未知";
        }
    }

}
