# 💼 Swati Cool Services — Company Bill ERP

<p align="center">
  <a href="https://github.com/vishalg0419/blog_app/stargazers"><img src="https://img.shields.io/github/stars/vishalg0419/blog_app?style=social" alt="Stars"></a>
  <a href="https://github.com/vishalg0419/blog_app"><img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg" alt="PRs welcome"></a>
  <img src="https://img.shields.io/badge/Java-blue.svg" alt="PHP">
   <img src="https://img.shields.io/badge/Mysql-blue.svg" alt="Mysql">
     <img src="https://img.shields.io/badge/Swing-blue.svg" alt="Bootstrap">
</p>

> A robust **desktop billing software** built with **Java Swing + MySQL**, designed for small-to-medium businesses to manage invoices, estimates, and export them as professional **PDF** or **Excel** files. Built from scratch with modern UI, clean modular code, and offline-first capability.

---

---

## ✨ Features

- 🖥️ Modern **Java Swing UI** with **FlatLaf** styling
- 📦 **Modular architecture**: reusable `PDFGenerator`, `ExcelGenerator`, `DBConnection`, and UI modules
- 🧾 Generate clean, Excel/Tally-style **PDF invoices**
- 📊 Export professional-looking **Excel files** using Apache POI
- 🔄 Full CRUD operations: **Add / Edit / Delete** bills and items
- ⏱️ Real-time clock on dashboard
- ✅ Emoji action buttons + intuitive 3-dot menus
- 📦 SQL-backed persistence with **MySQL + JDBC**
- 🔧 Works offline – no cloud dependency
- 🎯 Auto recalculation of **SR. No.**, totals, and bill types (BILL/ESTIMATE)

---

## 🖼 Screenshots

| Dashboard 
|----------
| ![Dashboard](Swati_Cool_Service/src/images/Screenshot%202025-06-28%20161620.png) 

> 📁 *Place your actual PNG screenshots in `docs/screenshots/` folder.*

---

## 🛠️ Tech Stack

| Component      | Technology             |
|----------------|------------------------|
| UI             | Java Swing + FlatLaf   |
| Language       | Java 8                 |
| Database       | MySQL 8                |
| PDF Export     | Apache PDFBox 2.0.34   |
| Excel Export   | Apache POI 5.x         |
| Build Tool     | Maven or Gradle        |
| IDE            | Eclipse / IntelliJ     |

---

## 🧱 Architecture Overview

```text
+-------------------+           +------------------+
|   DashboardUI     |<--------->|     bills        |
| + Real-time Clock |           |                  |
| + Bill Table      |           +------------------+
| + ︙ Menu Buttons |
+-------------------+
          ▲
          │ JTable model
          ▼
+-------------------+     JDBC     +--------------------+
|     BillUI        |<------------>|     bill_items     |
| + Item Form       |              +--------------------+
+-------------------+

  ↓            ↓
PDFGenerator  ExcelGenerator
```

## 🚀 Getting Started

### 1. Clone & install

```bash
git clone https://github.com/vishalg0419/company_bill_erp.git


```
=======
# Swati_Cool_Service
