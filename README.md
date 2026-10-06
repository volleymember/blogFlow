# BlogFlow

BlogFlow 是一个前后端分离的个人博客系统，包含公开博客、内容管理后台和 Spring Boot 后端服务。

## 项目结构

```text
blogFlow/
├── blog-server/  # Spring Boot 后端
├── blog-admin/   # Vue 3 管理后台
├── blog-web/     # Vue 3 博客前台
└── sql/          # 数据库结构与迁移脚本
```

## 技术栈

- 后端：Java 17、Spring Boot 3、Spring Security、MyBatis、MySQL、Redis、JWT
- 管理后台：Vue 3、Vite、Element Plus、Pinia、WangEditor、ECharts
- 博客前台：Vue 3、Vite、Tailwind CSS、Pinia、Marked、DOMPurify
- 对象存储：七牛云（可选）

## 主要功能

- 文章、分类和标签管理
- Markdown/富文本编辑与渲染
- 草稿、发布、置顶和可见性控制
- JWT 登录认证与角色权限
- 前台明暗主题与响应式页面
- 图片上传与对象存储
- 访问、点赞和后台统计

## 本地运行

### 1. 初始化数据库

创建 MySQL 数据库后执行：

```bash
mysql -u root -p your_database < sql/schema.sql
```

如需升级已有数据库，再按日期顺序执行 `sql/migration_*.sql`。

### 2. 配置并启动后端

```bash
cp blog-server/src/main/resources/application.example.yml \
  blog-server/src/main/resources/application.yml
```

编辑本地 `application.yml`，或通过环境变量提供数据库、Redis、JWT 和对象存储配置。实际配置文件已被 `.gitignore` 排除，请勿提交密钥。

```bash
cd blog-server
mvn spring-boot:run
```

### 3. 启动管理后台

```bash
cd blog-admin
npm install
npm run dev
```

### 4. 启动博客前台

```bash
cd blog-web
npm install
npm run dev
```

## 安全说明

- 不要将数据库密码、JWT 密钥或对象存储密钥提交到仓库。
- 生产环境请使用高强度随机 JWT 密钥，并通过环境变量或密钥管理服务注入。
- 首次部署后请立即设置独立的管理员凭据。
