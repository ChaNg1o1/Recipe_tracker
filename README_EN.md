<div align="right">
  <details>
    <summary>🌐 Language</summary>
    <a href="README_EN.md">English</a><br>
    <a href="README.md">简体中文</a>
  </details>
</div>

<div align="center">

<img src="assets/screenshots/logo_transparent.png" alt="RecipeTracker Logo" width="300">

## Java-based Recipe Management System with LLM API Interaction

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=openjdk)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue?style=flat-square&logo=apachemaven)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-00758F?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-WTFPL-green?style=flat-square)](LICENSE)

[Screenshots](#%EF%B8%8F-screenshots) •
[Quick Start](#%EF%B8%8F-cloud-start) •
[Local Deployment](#-%EF%B8%8F-local-deployment) 

<a href="https://managing-tallie-copytek-1cbed079.koyeb.app/">
  <img src="https://img.shields.io/badge/No_Config-Try_Live-blue?style=for-the-badge" alt="Live Demo">
</a>

</div>

## 🗺️ Screenshots

<details open>
<summary><b>Click to expand/collapse screenshots</b></summary>
<br>

| Login/Register & Main Interface | Recipe Management |
|:---:|:---:|
| <img src="assets/screenshots/main.gif" width="400"> | <img src="assets/screenshots/recipe.gif" width="400"> |

| Pantry | Health Data Tracking |
|:---:|:---:|
| <img src="assets/screenshots/pantry.gif" width="400"> | <img src="assets/screenshots/health.gif" width="400"> |

</details>

## ☁️ Cloud Start

<div align="center">
  <img src="assets/screenshots/workflow.svg" alt="Cloud Deployment Workflow" width="100%">

[View Companion Docker Cloud Container Project](https://github.com/ChaNg1o1/recipetracker-deploy)
</div>


[Cloud Quick Experience](https://managing-tallie-copytek-1cbed079.koyeb.app/) or skip local environment configuration and experience directly via configured cloud database.

## 💬 Chat with the Repo

Chat with the repository about the project structure and features?

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://app.devin.ai/wiki/)
[![zread](https://img.shields.io/badge/Ask_Zread-_.svg?style=flat&color=00b0aa&labelColor=000000&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZpZXdCb3g9IjAgMCAxNiAxNiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTQuOTYxNTYgMS42MDAxSDIuMjQxNTZDMS44ODgxIDEuNjAwMSAxLjYwMTU2IDEuODg2NjQgMS42MDE1NiAyLjI0MDFWNC45NjAxQzEuNjAxNTYgNS4zMTM1NiAxLjg4ODEgNS42MDAxIDIuMjQxNTYgNS42MDAxSDQuOTYxNTZDNS4zMTUwMiA1LjYwMDEgNS42MDE1NiA1LjMxMzU2IDUuNjAxNTYgNC45NjAxVjIuMjQwMUM1LjYwMTU2IDEuODg2NjQgNS4zMTUwMiAxLjYwMDEgNC45NjE1NiAxLjYwMDFaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00Ljk2MTU2IDEwLjM5OTlIMi4yNDE1NkMxLjg4ODEgMTAuMzk5OSAxLjYwMTU2IDEwLjY4NjQgMS42MDE1NiAxMS4wMzk5VjEzLjc1OTlDMS42MDE1NiAxNC4xMTM0IDEuODg4MSAxNC4zOTk5IDIuMjQxNTYgMTQuMzk5OUg0Ljk2MTU2QzUuMzE1MDIgMTQuMzk5OSA1LjYwMTU2IDE0LjExMzQgNS42MDE1NiAxMy43NTk5VjExLjAzOTlDNS42MDE1NiAxMC42ODY0IDUuMzE1MDIgMTAuMzk5OSA0Ljk2MTU2IDEwLjM5OTlaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik0xMy43NTg0IDEuNjAwMUgxMS4wMzg0QzEwLjY4NSAxLjYwMDEgMTAuMzk4NCAxLjg4NjY0IDEwLjM5ODQgMi4yNDAxVjQuOTYwMUMxMC4zOTg0IDUuMzEzNTYgMTAuNjg1IDUuNjAwMSAxMS4wMzg0IDUuNjAwMUgxMy43NTg0QzE0LjExMTkgNS42MDAxIDE0LjM5ODQgNS4zMTM1NiAxNC4zOTk0IDQuOTYwMVYyLjI0MDFDMTQuMzk4NCAxLjg4NjY0IDE0LjExMTkgMS42MDAxIDEzLjc1ODQgMS42MDAxWiIgZmlsbD0iI2ZmZiIvPgo8cGF0aCBkPSJNNCAxMkwxMiA0TDQgMTJaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00IDEyTDEyIDQiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSIxLjUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIvPgo8L3N2Zz4K&logoColor=ffffff)](https://zread.ai/)


## 🛠️ Local Deployment

#### 1. Clone Project

```bash
git clone https://gitee.com/chang1o/recipe-tracker
cd recipe-tracker
```

#### 2. Configure Database

<details open>
<summary><b>Option 1: Local MySQL Database</b></summary>

```bash
# 1. Ensure MySQL service is started

# 2. Create database schema
mysql -u root -p < src/main/resources/schema.sql

# 3. Import initial data
mysql -u root -p < src/main/resources/init_data.sql

# 4. Modify configuration file src/main/resources/database.properties
```

```properties
db.username=root
db.password=your_password
```

</details>

<details>
<summary><b>Option 2: Cloud Database</b></summary>

Edit `src/main/java/com/chang1o/util/DBUtil.java`:

```diff
- props.load(DBUtil.class.getClassLoader().getResourceAsStream("database.properties"));
+ props.load(DBUtil.class.getClassLoader().getResourceAsStream("clouddatabase.properties"));
```

>TiDB Cloud Database is located in AWS Japan region, network latency may exist

</details>

#### 3. Configure [Kimi API Key](https://platform.moonshot.cn/)

To enable AI features, you need to configure Kimi API Key:

<details open>
<summary><b>Option 1: Configuration File</b></summary>

1. Copy example configuration file:
```bash
cp src/main/resources/api.properties.example src/main/resources/api.properties
```

2. Edit `src/main/resources/api.properties` and fill in your API Key:
```properties
kimi.api.key=sk-*
```
</details>

<details>
<summary><b>Option 2: Environment Variable</b></summary>

You can also directly set the environment variable `KIMI_API_KEY`, which will override the settings in the configuration file:

```bash
export KIMI_API_KEY=sk-*
```

</details>

#### 4. Build and Run

```bash
# Compile Project
mvn clean compile
```

| Run Method | Command | Applicable Scenario |
|:--------:|------|:--------:|
| Unix | `chmod +x run.sh && ./run.sh` | Dev & Debug |
| Windows | `run.bat` | Dev & Debug |
| Maven | `mvn exec:java -Dexec.mainClass="com.chang1o.recipe.Main"` | Dev & Debug |
| JAR | `mvn clean package && java -jar target/RecipeTracker-1.0-SNAPSHOT.jar` | Production Deployment |
---
<div align="center">
  <img src="assets/screenshots/sticker.png" alt="Made with care by ChaNg1o and his AI Friends" width="400">
</div>
