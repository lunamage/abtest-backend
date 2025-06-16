# 🐛 Cursor 调试指南

## 🎯 现在可以在Cursor中直接调试了！

### 📋 配置已完成：
- ✅ 恢复了 `pom.xml` (Maven项目识别)
- ✅ 配置了 `.vscode/settings.json` (Maven支持)
- ✅ 更新了 `.vscode/launch.json` (调试配置)
- ✅ 优化了 `.vscode/tasks.json` (编译任务)

## 🚀 使用方法

### 方法1：使用运行和调试面板
1. 点击左侧栏的运行图标（三角形播放按钮）
2. 在下拉菜单中选择：
   - `Debug SimpleDemo` - 调试模式（推荐）
   - `Run SimpleDemo` - 运行模式
3. 点击绿色播放按钮

### 方法2：在代码中直接运行
1. 打开 `SimpleDemo.java` 文件
2. 在 `main` 方法上方应该会出现 `Run | Debug` 按钮
3. 点击 `Debug` 进入调试模式

### 方法3：右键菜单
1. 在 `SimpleDemo.java` 文件中右键
2. 选择 `Debug Java` 或 `Run Java`

## 🔧 调试功能

### 设置断点
- 在代码行号左侧点击，设置红色断点
- 推荐在以下位置设置断点测试：
  - 第12行：`System.out.println("=== 简化版AB测试SQL生成演示 ===\n");`
  - 第149行：`if (i < metricFields.size() - 1) {`
  - 第25行：`demonstrateBasicSqlGeneration();`

### 调试控制
- **F5**: 继续执行
- **F10**: 单步跳过
- **F11**: 单步进入
- **Shift+F11**: 单步跳出
- **Shift+F5**: 停止调试

### 查看变量
- 在调试时，左侧面板会显示：
  - **Variables**: 当前作用域的变量
  - **Watch**: 监视表达式
  - **Call Stack**: 调用堆栈

## 🔍 如果还是不能调试

### 重新加载项目
1. 按 `Cmd+Shift+P` (Mac) 或 `Ctrl+Shift+P` (Windows)
2. 输入：`Java: Reload Projects`
3. 选择并执行

### 检查Java扩展
确保安装了以下扩展：
- Extension Pack for Java
- Language Support for Java(TM) by Red Hat
- Debugger for Java

### 手动编译测试
如果自动编译有问题，可以手动编译：
```bash
cd abtest-backend
mkdir -p target/classes
javac -d target/classes -sourcepath src/main/java src/main/java/com/smzdm/abtest/SimpleDemo.java
```

## 📊 调试演示内容

程序包含两个主要演示：

1. **基本SQL生成** (`demonstrateBasicSqlGeneration()`)
   - 生成AB测试基础分析SQL
   - 包含分组逻辑和对比分析

2. **参数化查询** (`demonstrateParameterizedQuery()`)
   - 动态生成多指标分析SQL
   - 展示循环构建SQL的逻辑

## 💡 调试技巧

1. **在SQL生成前设置断点**：查看参数值
2. **在循环中设置断点**：观察SQL构建过程
3. **使用Watch表达式**：监控 `sql.toString()` 的变化
4. **查看变量面板**：检查 `params` 和 `metricFields` 的内容

---

**现在你可以在Cursor中愉快地调试Java代码了！** 🎉 