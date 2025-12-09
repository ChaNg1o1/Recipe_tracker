package com.chang1o.ui;

import com.chang1o.model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleUI {

    public static void showWelcomeMessage() {
        System.out.println("    ____            _            __                  __            ");
        System.out.println("   / __ \\___  _____(_)___  ___  / /__________ ______/ /_____  _____");
        System.out.println("  / /_/ / _ \\/ ___/ / __ \\/ _ \\/ __/ ___/ __ `/ ___/ //_/ _ \\/ ___/");
        System.out.println(" / _, _/  __/ /__/ / /_/ /  __/ /_/ /  / /_/ / /__/ ,< /  __/ /    ");
        System.out.println("/_/ |_|\\___/\\___/_/ .___/\\___/\\__/_/   \\__,_/\\___/_/|_|\\___/_/     ");
        System.out.println("                 /_/                                                ");
    }

    public static void showExitMessage() {
        System.out.println("已退出！");
    }

    public static void showAbout() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.println("│                 关于系统                         │");
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  RecipeTracker");
        System.out.println("  编译日期: 2025-12-09");
        System.out.println();
    }

    public static void showUserStatus(User currentUser) {
        System.out.println("  [当前用户]");
        System.out.println("    用户名: " + currentUser.getUsername());
        System.out.println("    登录时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println();
    }

    public static void showLoginStatusBanner(User currentUser) {
        if (currentUser != null) {
            System.out.println("[已登录] " + currentUser.getUsername());
        } else {
            System.out.println("[未登录]");
        }
    }

    public static void clearScreen() {
        System.out.println("\n\n\n\n\n\n\n\n");
    }

    public static void pause(java.util.Scanner scanner) {
        System.out.println("\n按回车键继续...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public static void showSeparator() {
        System.out.println("──────────────────────────────────────────────────");
    }

    public static void showTitleBox(String title) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────┐");
        System.out.printf("│%s│\n", centerText(title, 50));
        System.out.println("└──────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static String centerText(String text, int width) {
        int textLength = getDisplayWidth(text);
        int padding = (width - textLength) / 2;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < padding; i++) {
            sb.append(' ');
        }
        sb.append(text);

        int rightPadding = width - textLength - padding;
        for (int i = 0; i < rightPadding; i++) {
            sb.append(' ');
        }

        return sb.toString();
    }

    private static int getDisplayWidth(String text) {
        int width = 0;
        for (char c : text.toCharArray()) {
            if (c > 0x7F) { // 非ASCII字符（包括中文）
                width += 2;
            } else {
                width += 1;
            }
        }
        return width;
    }

    public static void showSuccess(String message) {
        System.out.println("[成功] " + message);
    }

    public static void showError(String message) {
        System.out.println("[错误] " + message);
    }

    public static void showWarning(String message) {
        System.out.println("[警告] " + message);
    }

    public static void showInfo(String message) {
        System.out.println("[提示] " + message);
    }
}
