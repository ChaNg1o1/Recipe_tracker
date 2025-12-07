package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.service.DataExportService;

public class DataExportController extends BaseController {
  private final DataExportService dataExportService;

  public DataExportController() {
    this.dataExportService = new DataExportService();
  }

  public void exportData(User currentUser) {
    System.out.println("\n数据导出");

    System.out.println("请输入导出文件路劲：");
    String filePath = sessionManager.getScanner().nextLine().trim();

    if (filePath.isEmpty()) {
      filePath = "user_data_export.txt";
    }

    System.out.println("正在导出个人数据..");

    String healthPath =
        dataExportService.exportHealthDataReport(currentUser.getId());
    String recipePath = dataExportService.exportRecipeData(currentUser.getId());
    String pantryPath = dataExportService.exportPantryData(currentUser.getId());
    String checkInPath =
        dataExportService.exportCheckInRecords(currentUser.getId(), 30);

    boolean ok = (healthPath != null) || (recipePath != null) ||
                 (pantryPath != null) || (checkInPath != null);

    if (ok) {
      System.out.println("数据导出成功");
      if (healthPath != null)
        System.out.println("健康数据存放在：" + healthPath);
      if (recipePath != null)
        System.out.println("食谱数据存放在：" + recipePath);
      if (pantryPath != null)
        System.out.println("库存数据存放在：" + pantryPath);
      if (checkInPath != null)
        System.out.println("打卡数据存放在：" + checkInPath);

    } else {
      System.out.println("数据导出失败，请检查是否有写入权限");
    }
  }
}
