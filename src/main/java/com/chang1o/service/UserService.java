package com.chang1o.service;

import com.chang1o.dao.UserDao;
import com.chang1o.model.User;

import java.util.List;

public class UserService {

    private UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public RegistrationResult register(String username, String password, String confirmPassword) {
        ValidationResult validation = validateRegistrationInput(username, password, confirmPassword);
        if (!validation.isValid()) {
            return new RegistrationResult(false, validation.getMessage());
        }

        if (userDao.isUsernameExists(username)) {
            return new RegistrationResult(false, "用户名已存在，请选择其他用户名");
        }

        User newUser = new User(username, password);
        boolean success = userDao.addUser(newUser);

        if (success) {
            return new RegistrationResult(true, "注册成功！欢迎加入食谱管理系统");
        } else {
            return new RegistrationResult(false, "注册失败，请稍后重试");
        }
    }

    public LoginResult login(String username, String password) {
        ValidationResult validation = validateLoginInput(username, password);
        if (!validation.isValid()) {
            return new LoginResult(false, null, validation.getMessage());
        }

        if (!userDao.isUsernameExists(username)) {
            return new LoginResult(false, null, "用户不存在，请检查用户名或先注册");
        }

        User user = userDao.authenticate(username, password);
        if (user == null) {
            return new LoginResult(false, null, "密码错误，请重试");
        }

        return new LoginResult(true, user, "登录成功！");
    }

    public PasswordChangeResult changePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            return new PasswordChangeResult(false, "用户不存在");
        }

        if (!user.getPassword().equals(oldPassword)) {
            return new PasswordChangeResult(false, "原密码错误");
        }

        ValidationResult validation = validatePasswordChange(newPassword, confirmPassword);
        if (!validation.isValid()) {
            return new PasswordChangeResult(false, validation.getMessage());
        }

        user.setPassword(newPassword);
        boolean success = userDao.updateUser(user);

        if (success) {
            return new PasswordChangeResult(true, "密码修改成功！");
        } else {
            return new PasswordChangeResult(false, "密码修改失败，请稍后重试");
        }
    }

    public User getUserInfo(int userId) {
        return userDao.getUserById(userId);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public boolean deleteUser(int userId) {
        return userDao.deleteUser(userId);
    }

    private ValidationResult validateRegistrationInput(String username, String password, String confirmPassword) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "用户名不能为空");
        }

        if (username.length() < 3 || username.length() > 20) {
            return new ValidationResult(false, "用户名长度必须在3-20个字符之间");
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return new ValidationResult(false, "用户名只能包含字母、数字和下划线");
        }

        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, "密码不能为空");
        }

        if (password.length() < 6) {
            return new ValidationResult(false, "密码长度至少为6个字符");
        }

        if (!password.equals(confirmPassword)) {
            return new ValidationResult(false, "两次输入的密码不一致");
        }

        return new ValidationResult(true, "验证通过");
    }

    private ValidationResult validateLoginInput(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "用户名不能为空");
        }

        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, "密码不能为空");
        }

        return new ValidationResult(true, "验证通过");
    }

    private ValidationResult validatePasswordChange(String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return new ValidationResult(false, "新密码不能为空");
        }

        if (newPassword.length() < 6) {
            return new ValidationResult(false, "新密码长度至少为6个字符");
        }

        if (!newPassword.equals(confirmPassword)) {
            return new ValidationResult(false, "两次输入的新密码不一致");
        }

        return new ValidationResult(true, "验证通过");
    }

    public UserStatistics getUserStatistics() {
        int totalUsers = userDao.countUsers();
        List<User> allUsers = userDao.getAllUsers();

        return new UserStatistics(totalUsers, allUsers);
    }

    public static class RegistrationResult {
        private boolean success;
        private String message;

        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class LoginResult {
        private boolean success;
        private User user;
        private String message;

        public LoginResult(boolean success, User user, String message) {
            this.success = success;
            this.user = user;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public User getUser() {
            return user;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class PasswordChangeResult {
        private boolean success;
        private String message;

        public PasswordChangeResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
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

    public static class UserStatistics {
        private int totalUsers;
        private List<User> allUsers;

        public UserStatistics(int totalUsers, List<User> allUsers) {
            this.totalUsers = totalUsers;
            this.allUsers = allUsers;
        }

        public int getTotalUsers() {
            return totalUsers;
        }

        public List<User> getAllUsers() {
            return allUsers;
        }
    }

}