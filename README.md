<div align="right">
  <details>
    <summary>🌐 Language</summary>
    <a href="README_EN.md">English</a><br>
    <a href="README.md">简体中文</a>
  </details>
</div>

<div align="center">
<br>
<img src="assets/screenshots/logo_transparent.png" alt="RecipeTracker Logo" width="300">

## 基于Java 带有LLM API交互的食谱管理系统

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue?style=flat-square&logo=apachemaven)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-00758F?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-WTFPL-green?style=flat-square)](LICENSE)

[界面展示](#界面展示) •
[快速开始](#云端开始) •
[本地部署](#本地部署) 

<a href="https://managing-tallie-copytek-1cbed079.koyeb.app/">
  <img src="https://img.shields.io/badge/无需配置-点我体验-blue?style=for-the-badge" alt="在线演示">
</a>

</div>

## 🗺️ 界面展示

<details open>
<summary><b>点击展开/收起 界面截图</b></summary>
<br>

| 登录/注册和主界面 | 食谱管理 |
|:---:|:---:|
| <img src="assets/screenshots/main.gif" width="400"> | <img src="assets/screenshots/recipe.gif" width="400"> |

| 食材库 | 健康数据追踪 |
|:---:|:---:|
| <img src="assets/screenshots/pantry.gif" width="400"> | <img src="assets/screenshots/health.gif" width="400"> |

</details>

## ☁️ 云端开始

<div align="center">
  <img src="assets/screenshots/workflow.svg" alt="云端部署流程" width="100%">

[查看配套Docker云容器项目](https://github.com/ChaNg1o1/recipetracker-deploy)
</div>


[云端快速体验](https://managing-tallie-copytek-1cbed079.koyeb.app/)或者通过已配置的云数据库跳过本地环境配置直接体验。

## 💬 Chat with the Repo

和仓库聊聊该项目的结构和功能特征？


[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://app.devin.ai/wiki/)
[![zread](https://img.shields.io/badge/Ask_Zread-_.svg?style=flat&color=00b0aa&labelColor=000000&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZpZXdCb3g9IjAgMCAxNiAxNiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTQuOTYxNTYgMS42MDAxSDIuMjQxNTZDMS44ODgxIDEuNjAwMSAxLjYwMTU2IDEuODg2NjQgMS42MDE1NiAyLjI0MDFWNC45NjAxQzEuNjAxNTYgNS4zMTM1NiAxLjg4ODEgNS42MDAxIDIuMjQxNTYgNS42MDAxSDQuOTYxNTZDNS4zMTUwMiA1LjYwMDEgNS42MDE1NiA1LjMxMzU2IDUuNjAxNTYgNC45NjAxVjIuMjQwMUM1LjYwMTU2IDEuODg2NjQgNS4zMTUwMiAxLjYwMDEgNC45NjE1NiAxLjYwMDFaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00Ljk2MTU2IDEwLjM5OTlIMi4yNDE1NkMxLjg4ODEgMTAuMzk5OSAxLjYwMTU2IDEwLjY4NjQgMS42MDE1NiAxMS4wMzk5VjEzLjc1OTlDMS42MDE1NiAxNC4xMTM0IDEuODg4MSAxNC4zOTk5IDIuMjQxNTYgMTQuMzk5OUg0Ljk2MTU2QzUuMzE1MDIgMTQuMzk5OSA1LjYwMTU2IDE0LjExMzQgNS42MDE1NiAxMy43NTk5VjExLjAzOTlDNS42MDE1NiAxMC42ODY0IDUuMzE1MDIgMTAuMzk5OSA0Ljk2MTU2IDEwLjM5OTlaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik0xMy43NTg0IDEuNjAwMUgxMS4wMzg0QzEwLjY4NSAxLjYwMDEgMTAuMzk4NCAxLjg4NjY0IDEwLjM5ODQgMi4yNDAxVjQuOTYwMUMxMC4zOTg0IDUuMzEzNTYgMTAuNjg1IDUuNjAwMSAxMS4wMzg0IDUuNjAwMUgxMy43NTg0QzE0LjExMTkgNS42MDAxIDE0LjM5ODQgNS4zMTM1NiAxNC4zOTg0IDQuOTYwMVYyLjI0MDFDMTQuMzk4NCAxLjg4NjY0IDE0LjExMTkgMS42MDAxIDEzLjc1ODQgMS42MDAxWiIgZmlsbD0iI2ZmZiIvPgo8cGF0aCBkPSJNNCAxMkwxMiA0TDQgMTJaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00IDEyTDEyIDQiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSIxLjUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIvPgo8L3N2Zz4K&logoColor=ffffff)](https://zread.ai/)


## 🛠️ 本地部署

#### 1. 克隆项目

```bash
git clone https://gitee.com/chang1o/recipe-tracker
cd recipe-tracker
```

#### 2. 配置数据库

<details open>
<summary><b>方式一：本地 MySQL 数据库</b></summary>

```bash
# 1. 确保 MySQL 服务已启动

# 2. 创建数据库表结构
mysql -u root -p < src/main/resources/schema.sql

# 3. 导入初始数据
mysql -u root -p < src/main/resources/init_data.sql

# 4. 修改配置文件 src/main/resources/database.properties
```

```properties
db.username=root
db.password=your_password
```

</details>

<details>
<summary><b>方式二：云数据库</b></summary>

编辑 `src/main/java/com/chang1o/util/DBUtil.java`：

```diff
- props.load(DBUtil.class.getClassLoader().getResourceAsStream("database.properties"));
+ props.load(DBUtil.class.getClassLoader().getResourceAsStream("clouddatabase.properties"));
```

>TiDB 云数据库位于 AWS 日本区域，存在网络延迟

</details>

#### 3. 配置[ Kimi API Key](https://platform.moonshot.cn/)

为启用 AI 功能，需要配置 Kimi API Key：

<details open>
<summary><b>方式一：配置文件</b></summary>

1. 复制示例配置文件：
```bash
cp src/main/resources/api.properties.example src/main/resources/api.properties
```

2. 编辑 `src/main/resources/api.properties` 填入您的 API Key：
```properties
kimi.api.key=sk-*
```
</details>

<details>
<summary><b>方式二：环境变量</b></summary>

您也可以直接设置环境变量 `KIMI_API_KEY`，它将覆盖配置文件中的设置：

```bash
export KIMI_API_KEY=sk-*
```

</details>

#### 4. 构建与运行

```bash
# 编译项目
mvn clean compile
```

| 运行方式 | 命令 | 适用场景 |
|:--------:|------|:--------:|
| Unix | `chmod +x run.sh && ./run.sh` | 开发调试 |
| Windows | `run.bat` | 开发调试 |
| Maven | `mvn exec:java -Dexec.mainClass="com.chang1o.recipe.Main"` | 开发调试 |
| JAR | `mvn clean package && java -jar target/RecipeTracker-1.0-SNAPSHOT.jar` | 生产部署 |
---
<div align="center">
  <img src="assets/screenshots/sticker.png" alt="Made with care by ChaNg1o and his AI Friends" width="400">
</div>
