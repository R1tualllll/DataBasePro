# DataBasePro

企业员工管理系统，基于 Spring Boot、JDBC 和 openGauss 实现。系统提供部门、岗位、员工、考勤、薪资和统计分析等模块，并带有一个原生 HTML/CSS/JavaScript 单页管理界面。

## 技术栈

- Java 21
- Spring Boot 3.2.0
- Spring JDBC
- openGauss JDBC Driver
- Lombok
- 原生 HTML/CSS/JavaScript 前端

## 功能模块

- 部门管理：部门新增、修改、删除、查询、CSV 导出。
- 岗位管理：岗位新增、修改、删除、查询、CSV 导出。
- 员工管理：员工 CRUD、服务端筛选分页、员工详情、转正、离职登记、CSV 导出。
- 考勤管理：考勤 CRUD、按员工/月度/状态/日期范围筛选分页、CSV 导出。
- 薪资管理：薪资 CRUD、服务端筛选分页、自动按岗位基础工资计算实发工资、批量生成月度薪资、CSV 导出。
- 统计分析：部门人数统计、岗位人数统计、月度考勤汇总、首页仪表盘。
- 数据保护：部门、岗位、员工存在关联数据时禁止删除并返回明确提示。

## 项目结构

```text
src/main/java/com/ritual/ems
  common       通用返回结构、异常处理
  controller   REST API 控制器
  dto          请求和响应 DTO
  mapper       JDBC RowMapper
  model        数据模型
  repository   JDBC 数据访问
  service      业务逻辑

src/main/resources
  application.yml          本地运行配置
  application-example.yml  配置示例
  schema.sql               表结构脚本
  data.sql                 示例数据
  static/index.html        前端单页界面

http
  HTTP Client 调试示例
```

## 数据库准备

项目使用 openGauss，默认 schema 为 `joe`。可以使用：

- `src/main/resources/schema.sql` 创建表结构
- `src/main/resources/data.sql` 初始化示例数据

数据库连接配置位于：

```text
src/main/resources/application.yml
```

建议根据 `application-example.yml` 修改自己的数据库地址、用户名和密码。

## 启动方式

在项目根目录执行：

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn package -DskipTests
java -jar target/employee-management-system-1.0-SNAPSHOT.jar
```

启动后访问：

```text
http://localhost:8080/
```

## 主要接口

### 部门

- `GET /api/departments`
- `POST /api/departments`
- `PUT /api/departments/{deptId}`
- `DELETE /api/departments/{deptId}`
- `GET /api/export/departments.csv`

### 岗位

- `GET /api/positions`
- `POST /api/positions`
- `PUT /api/positions/{positionId}`
- `DELETE /api/positions/{positionId}`
- `GET /api/export/positions.csv`

### 员工

- `GET /api/employees`
- `GET /api/employees/search`
- `GET /api/employees/{empId}/detail`
- `POST /api/employees`
- `PUT /api/employees/{empId}`
- `PUT /api/employees/{empId}/regularize`
- `PUT /api/employees/{empId}/resign`
- `DELETE /api/employees/{empId}`
- `GET /api/export/employees.csv`

### 考勤

- `GET /api/attendance`
- `GET /api/attendance/search`
- `POST /api/attendance`
- `PUT /api/attendance/{attendanceId}`
- `DELETE /api/attendance/{attendanceId}`
- `GET /api/export/attendance.csv`

### 薪资

- `GET /api/salaries`
- `GET /api/salaries/search`
- `POST /api/salaries`
- `POST /api/salaries/generate?month=yyyy-MM`
- `PUT /api/salaries/{salaryId}`
- `DELETE /api/salaries/{salaryId}`
- `GET /api/export/salaries.csv?month=yyyy-MM`

### 统计

- `GET /api/statistics/employees-by-department`
- `GET /api/statistics/employees-by-position`
- `GET /api/statistics/monthly-attendance?month=yyyy-MM`
- `GET /api/statistics/dashboard?month=yyyy-MM`
- `GET /api/export/monthly-attendance.csv?month=yyyy-MM`

## 调试

`http/` 目录下提供了 IntelliJ IDEA HTTP Client 示例，可直接调用主要接口。

## 说明

- 本项目不依赖 ORM，数据访问统一使用 `JdbcTemplate`。
- CSV 响应使用 UTF-8 BOM，方便 Excel 打开中文内容。
- 删除部门、岗位、员工时会先检查关联数据，避免直接暴露数据库约束错误。
