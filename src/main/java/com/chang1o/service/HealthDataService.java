package com.chang1o.service;

import com.chang1o.dao.UserHealthDataDao;
import com.chang1o.dao.DailyCheckInDao;
import com.chang1o.model.UserHealthData;
import com.chang1o.model.DailyCheckIn;

import java.time.LocalDate;
import java.util.List;

public class HealthDataService {

    private UserHealthDataDao healthDataDao;
    private DailyCheckInDao dailyCheckInDao;

    public HealthDataService() {
        this.healthDataDao = new UserHealthDataDao();
        this.dailyCheckInDao = new DailyCheckInDao();
    }

    public HealthDataResult saveHealthData(int userId, double weight, double height, int age,
                                         String gender, String activityLevel, double targetWeight) {
        ValidationResult validation = validateHealthDataInput(weight, height, age, gender, activityLevel, targetWeight);
        if (!validation.isValid()) {
            return new HealthDataResult(false, null, validation.getMessage());
        }

        UserHealthData existingData = healthDataDao.getLatestHealthDataByUserId(userId);

        UserHealthData healthData = new UserHealthData(userId, weight, height, age, gender, activityLevel, targetWeight);

        boolean success;
        if (existingData != null) {
            healthData.setId(existingData.getId());
            success = healthDataDao.updateHealthData(healthData);
        } else {
            success = healthDataDao.addHealthData(healthData);
        }

        if (success) {
            return new HealthDataResult(true, healthData, "健康数据保存成功！");
        } else {
            return new HealthDataResult(false, null, "健康数据保存失败，请稍后重试");
        }
    }

    public UserHealthData getLatestHealthData(int userId) {
        return healthDataDao.getLatestHealthDataByUserId(userId);
    }

    public List<UserHealthData> getAllHealthData(int userId) {
        return healthDataDao.getHealthDataByUserId(userId);
    }

    public String getHealthReport(int userId) {
        UserHealthData healthData = healthDataDao.getLatestHealthDataByUserId(userId);
        if (healthData == null) {
            return "暂无健康数据，请先完善您的健康信息。";
        }

        StringBuilder report = new StringBuilder();
        report.append("=== 个人健康报告 ===\n\n");

        report.append("📋 基本信息:\n");
        report.append("  身高: ").append(healthData.getHeight()).append("cm\n");
        report.append("  体重: ").append(healthData.getWeight()).append("kg\n");
        report.append("  年龄: ").append(healthData.getAge()).append("岁\n");
        report.append("  性别: ").append("M".equals(healthData.getGender()) ? "男" : "女").append("\n");
        report.append("  活动水平: ").append(getActivityLevelDescription(healthData.getActivityLevel())).append("\n\n");

        report.append("📊 健康指标:\n");
        report.append("  BMI指数: ").append(String.format("%.1f", healthData.calculateBMI())).append(" (").append(healthData.getBMICategory()).append(")\n");
        report.append("  基础代谢率: ").append(String.format("%.0f", healthData.calculateBMR())).append(" 卡路里/天\n");
        report.append("  每日总能量消耗: ").append(String.format("%.0f", healthData.calculateTDEE())).append(" 卡路里/天\n");
        report.append("  理想体重范围: ").append(healthData.getIdealWeightRange()).append("\n\n");

        if (healthData.getTargetWeight() > 0) {
            double diff = healthData.getWeightDifference();
            report.append("🎯 体重目标:\n");
            if (Math.abs(diff) < 0.5) {
                report.append("  ✅ 恭喜！您已达到目标体重\n");
            } else if (diff > 0) {
                report.append("  需要减重: ").append(String.format("%.1f", diff)).append("kg\n");
            } else {
                report.append("  需要增重: ").append(String.format("%.1f", Math.abs(diff))).append("kg\n");
            }
            report.append("\n");
        }

        report.append("💡 健康建议:\n");
        report.append(generateHealthAdvice(healthData)).append("\n");

        report.append("==================");
        return report.toString();
    }

    public CheckInResult saveDailyCheckIn(int userId, String mood, double sleepHours,
                                        int waterIntake, int exerciseMinutes, String notes) {
        ValidationResult validation = validateCheckInInput(mood, sleepHours, waterIntake, exerciseMinutes);
        if (!validation.isValid()) {
            return new CheckInResult(false, null, validation.getMessage());
        }

        LocalDate today = LocalDate.now();
        DailyCheckIn existingCheckIn = dailyCheckInDao.getCheckInByUserIdAndDate(userId, today);

        DailyCheckIn checkIn = new DailyCheckIn(userId, today, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        boolean success;
        if (existingCheckIn != null) {
            checkIn.setId(existingCheckIn.getId());
            success = dailyCheckInDao.updateCheckIn(checkIn);
        } else {
            success = dailyCheckInDao.addCheckIn(checkIn);
        }

        if (success) {
            int consecutiveDays = dailyCheckInDao.getConsecutiveCheckInDays(userId);
            String message = "打卡成功！连续打卡" + consecutiveDays + "天";
            return new CheckInResult(true, checkIn, message);
        } else {
            return new CheckInResult(false, null, "打卡失败，请稍后重试");
        }
    }

    public DailyCheckIn getTodayCheckIn(int userId) {
        return dailyCheckInDao.getCheckInByUserIdAndDate(userId, LocalDate.now());
    }

    public boolean hasCheckedInToday(int userId) {
        return dailyCheckInDao.hasCheckedInToday(userId);
    }

    public List<DailyCheckIn> getRecentCheckIns(int userId, int days) {
        return dailyCheckInDao.getRecentCheckIns(userId, days);
    }

    public DailyCheckInDao.HealthStatistics getHealthStatistics(int userId, int days) {
        return dailyCheckInDao.getHealthStatistics(userId, days);
    }

    public int getConsecutiveCheckInDays(int userId) {
        return dailyCheckInDao.getConsecutiveCheckInDays(userId);
    }

    private String generateHealthAdvice(UserHealthData healthData) {
        StringBuilder advice = new StringBuilder();
        double bmi = healthData.calculateBMI();

        if (bmi < 18.5) {
            advice.append("  • 您的BMI偏低，建议适当增加营养摄入\n");
            advice.append("  • 可以咨询营养师制定增重计划\n");
        } else if (bmi >= 28) {
            advice.append("  • 您的BMI偏高，建议控制饮食并增加运动\n");
            advice.append("  • 建议每天进行30分钟以上的有氧运动\n");
            advice.append("  • 减少高热量食物的摄入\n");
        } else {
            advice.append("  • 您的BMI正常，请继续保持健康的生活方式\n");
        }

        double tdee = healthData.calculateTDEE();
        advice.append("  • 建议每日摄入").append(String.format("%.0f", tdee)).append("卡路里以维持当前体重\n");

        advice.append("  • 保持规律的作息时间\n");
        advice.append("  • 每天保证充足的睡眠（7-8小时）\n");
        advice.append("  • 适量运动，每周至少150分钟中等强度运动\n");
        advice.append("  • 多喝水，每天至少1500-2000毫升\n");
        advice.append("  • 定期监测体重和健康指标\n");

        return advice.toString();
    }

    private String getActivityLevelDescription(String activityLevel) {
        switch (activityLevel) {
            case "sedentary": return "久坐不动（办公室工作）";
            case "light": return "轻度活动（每周运动1-3次）";
            case "moderate": return "中度活动（每周运动3-5次）";
            case "active": return "高度活动（每周运动6-7次）";
            case "very_active": return "极高活动（每天高强度运动）";
            default: return "未知";
        }
    }

    private ValidationResult validateHealthDataInput(double weight, double height, int age,
                                                   String gender, String activityLevel, double targetWeight) {
        if (weight <= 0 || weight > 300) {
            return new ValidationResult(false, "体重必须在0-300kg之间");
        }

        if (height <= 0 || height > 250) {
            return new ValidationResult(false, "身高必须在0-250cm之间");
        }

        if (age <= 0 || age > 150) {
            return new ValidationResult(false, "年龄必须在0-150岁之间");
        }

        if (!"M".equals(gender) && !"F".equals(gender)) {
            return new ValidationResult(false, "性别必须是M（男）或F（女）");
        }

        String[] validLevels = {"sedentary", "light", "moderate", "active", "very_active"};
        boolean validLevel = false;
        for (String level : validLevels) {
            if (level.equals(activityLevel)) {
                validLevel = true;
                break;
            }
        }
        if (!validLevel) {
            return new ValidationResult(false, "活动水平必须是有效的值");
        }

        if (targetWeight < 0 || targetWeight > 300) {
            return new ValidationResult(false, "目标体重必须在0-300kg之间");
        }

        return new ValidationResult(true, "验证通过");
    }

    private ValidationResult validateCheckInInput(String mood, double sleepHours, int waterIntake, int exerciseMinutes) {
        String[] validMoods = {"great", "good", "normal", "bad", "terrible"};
        boolean validMood = false;

        for (String validMoodValue : validMoods) {
            if (validMoodValue.equals(mood)) {
                validMood = true;
                break;
            }
        }

        if (!validMood) {
            return new ValidationResult(false, "心情必须是有效的值");
        }

        if (sleepHours < 0 || sleepHours > 24) {
            return new ValidationResult(false, "睡眠时长必须在0-24小时之间");
        }

        if (waterIntake < 0 || waterIntake > 10000) {
            return new ValidationResult(false, "饮水量必须在0-10000毫升之间");
        }

        if (exerciseMinutes < 0 || exerciseMinutes > 1440) {
            return new ValidationResult(false, "运动时长必须在0-1440分钟之间");
        }

        return new ValidationResult(true, "验证通过");
    }

    public static class HealthDataResult {
        private boolean success;
        private UserHealthData healthData;
        private String message;

        public HealthDataResult(boolean success, UserHealthData healthData, String message) {
            this.success = success;
            this.healthData = healthData;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public UserHealthData getHealthData() {
            return healthData;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class CheckInResult {
        private boolean success;
        private DailyCheckIn checkIn;
        private String message;

        public CheckInResult(boolean success, DailyCheckIn checkIn, String message) {
            this.success = success;
            this.checkIn = checkIn;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public DailyCheckIn getCheckIn() {
            return checkIn;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class ValidationResult {
        private boolean valid;
        private String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

}