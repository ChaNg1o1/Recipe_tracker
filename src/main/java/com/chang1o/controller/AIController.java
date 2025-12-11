package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.model.Recipe;
import com.chang1o.model.PantryItem;
import com.chang1o.service.ApiService;
import com.chang1o.service.RecipeService;
import com.chang1o.service.PantryService;
import com.chang1o.ui.MenuManager;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class AIController extends BaseController {

    private final ApiService apiService;
    private final RecipeService recipeService;
    private final PantryService pantryService;

    public AIController() {
        this.apiService = new ApiService();
        this.recipeService = new RecipeService();
        this.pantryService = new PantryService();
    }

    public void showMenu(User currentUser) {
        MenuManager.showAIMenu();

        System.out.print("请输入您的选择：");
        String choice = sessionManager.getScanner().nextLine().trim();

        switch (choice) {
            case "1":
                handlePersonalizedHealthAdvice(currentUser);
                break;
            case "2":
                handleSmartRecipeRecommendations(currentUser);
                break;
            case "3":
                handleSmartShoppingList(currentUser);
                break;
            case "4":
                handleNutritionAnalysis(currentUser);
                break;
            case "0":
                return;
            default:
                System.out.println("无效的选择，请输入0-4之间的数字！");
                break;
        }
    }

    private void handlePersonalizedHealthAdvice(User currentUser) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│              个性化健康建议                      │");
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("[处理中] 正在基于您的健康数据生成个性化建议...");

        try {
            String advice = apiService.generatePersonalizedHealthAdvice(currentUser.getId());
            System.out.println();
            System.out.println("[AI健康建议]");
            System.out.println("━".repeat(50));
            System.out.println(advice);
            System.out.println("━".repeat(50));
        } catch (Exception e) {
            System.out.println("[错误] 获取健康建议时出错：" + e.getMessage());
            System.out.println("[建议] 建议您先完善健康数据后再试");
        }
    }

    private void handleSmartRecipeRecommendations(User currentUser) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│               智能食谱推荐                       │");
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("[处理中] 正在分析您的健康状况和偏好...");

        try {
            List<String> recommendations = apiService.generateSmartRecipeRecommendations(currentUser.getId());
            System.out.println();
            System.out.println("[AI食谱推荐]");
            System.out.println("━".repeat(50));
            for (int i = 0; i < recommendations.size(); i++) {
                System.out.println((i + 1) + ". " + recommendations.get(i));
                System.out.println();
            }
            System.out.println("━".repeat(50));
        } catch (Exception e) {
            System.out.println("[错误] 获取食谱推荐时出错：" + e.getMessage());
            System.out.println("[建议] 建议您先添加一些食谱数据后再试");
        }
    }

    private void handleSmartShoppingList(User currentUser) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│               智能购物清单                       │");
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();

        System.out.println("请选择生成方式：");
        System.out.println("1. 基于当前库存生成");
        System.out.println("2. 基于选定食谱生成");
        System.out.print("请输入选择：");
        String choice = sessionManager.getScanner().nextLine().trim();

        try {
            List<Integer> recipeIds = new ArrayList<>();
            String shoppingList;

            if (choice.equals("1")) {
                System.out.println("正在基于您的库存情况生成智能购物清单...");
                shoppingList = apiService.generateSmartShoppingList(currentUser.getId(), recipeIds);
            } else if (choice.equals("2")) {
                List<Recipe> userRecipes = recipeService.getRecipesByUser(currentUser.getId());

                if (userRecipes.isEmpty()) {
                    System.out.println("[提示] 您还没有添加任何食谱");
                    return;
                }

                System.out.println("\n您的食谱列表：");
                System.out.println("─".repeat(50));
                for (int i = 0; i < userRecipes.size(); i++) {
                    Recipe recipe = userRecipes.get(i);
                    System.out.printf("%d. %s (%s)\n",
                        i + 1,
                        recipe.getName(),
                        recipe.getCategory() != null ? recipe.getCategory().getName() : "未分类");
                }
                System.out.println("─".repeat(50));

                System.out.print("\n请选择要做的食谱（输入编号，多个用逗号分隔，如 1,3,5）：");
                String recipeChoice = sessionManager.getScanner().nextLine().trim();

                String[] choices = recipeChoice.split(",");
                for (String c : choices) {
                    try {
                        int index = Integer.parseInt(c.trim()) - 1;
                        if (index >= 0 && index < userRecipes.size()) {
                            recipeIds.add(userRecipes.get(index).getId());
                        }
                    } catch (NumberFormatException e) {
                    }
                }

                if (recipeIds.isEmpty()) {
                    System.out.println("[错误] 未选择有效的食谱");
                    return;
                }

                System.out.println("\n正在基于您选择的 " + recipeIds.size() + " 个食谱生成智能购物清单...");
                shoppingList = apiService.generateSmartShoppingList(currentUser.getId(), recipeIds);
            } else {
                System.out.println("[错误] 无效的选择");
                return;
            }

            System.out.println();
            System.out.println("[AI智能购物建议]");
            System.out.println("━".repeat(50));
            System.out.println(shoppingList);
            System.out.println("━".repeat(50));

        } catch (Exception e) {
            System.out.println("[错误] 生成购物清单时出错：" + e.getMessage());
            System.out.println("[建议] 建议您先完善库存数据后再试");
        }
    }

    private void handleNutritionAnalysis(User currentUser) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│               营养分析报告                       │");
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();

        System.out.print("请输入分析天数（默认7天）：");
        String daysStr = sessionManager.getScanner().nextLine().trim();

        int days = 7;
        if (!daysStr.isEmpty()) {
            try {
                days = Integer.parseInt(daysStr);
            } catch (NumberFormatException e) {
                System.out.println("输入无效，使用默认7天");
            }
        }

        System.out.println("正在分析您最近" + days + "天的营养状况...");

        try {
            String report = apiService.generateNutritionAnalysisReport(currentUser.getId(), days);
            System.out.println();
            System.out.println("[AI营养分析报告]");
            System.out.println("━".repeat(50));
            System.out.println(report);
            System.out.println("━".repeat(50));
        } catch (Exception e) {
            System.out.println("[错误] 生成营养分析时出错：" + e.getMessage());
            System.out.println("[建议] 建议您先进行每日打卡后再试");
        }
    }
}
