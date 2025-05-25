# Cursor Workflow Rules

This project has been updated to use the auto rule generator from [cursor-auto-rules-agile-workflow](https://github.com/bmadcode/cursor-auto-rules-agile-workflow).

> **Note**: This script can be safely re-run at any time to update the template rules to their latest versions. It will not impact or overwrite any custom rules you've created.

## Core Features

- Automated rule generation
- Standardized documentation formats
- AI behavior control and optimization
- Flexible workflow integration options

## Workflow Integration Options

1. Automatic Rule Application (Recommended)

The core workflow rules are automatically installed in `.cursor/rules/`:

- `901-prd.mdc` - Product Requirements Document standards
- `902-arch.mdc` - Architecture documentation standards
- `903-story.mdc` - User story standards
- `801-workflow-agile.mdc` - Complete Agile workflow (optional)

These rules are automatically applied when working with corresponding file types.

### 2. Notepad-Based Workflow

For a more flexible approach, use the templates in `xnotes/`:

1. Enable Notepads in Cursor options
2. Create a new notepad (e.g., "agile")
3. Copy contents from `xnotes/workflow-agile.md`
4. Use \`@notepad-name\` in conversations

> 💡 **Tip:** The Notepad approach is ideal for:
>
> - Initial project setup
> - Story implementation
> - Focused development sessions
> - Reducing context overhead

## Getting Started

1. Review the templates in \`xnotes/\`
2. Choose your preferred workflow approach
3. Start using the AI with confidence!

For demos and tutorials, visit: [BMad Code Videos](https://youtube.com/bmadcode)




# 光标工作流规则

此项目已更新为使用来自[cursor-auto-rules-agile-workflow](https://github.com/bmadcode/cursor-auto-rules-agile-workflow)的自动规则生成器。

> **注意**：此脚本可以安全地在任何时候重新运行，以将模板规则更新为其最新版本。它不会影响或覆盖您自定义创建的任何规则。

## 核心功能

- 自动化规则生成
- 标准化的文档格式
- AI行为控制与优化
- 灵活的工作流集成选项

## 工作流集成选项

1. **自动规则应用（推荐）**

核心工作流规则会自动安装到 `.cursor/rules/`中：

- `901-prd.mdc` - 产品需求文档标准
- `902-arch.mdc` - 架构文档标准
- `903-story.mdc` - 用户故事标准
- `801-workflow-agile.mdc` - 完整的敏捷工作流（可选）

这些规则会在处理相应文件类型时自动应用。

### 2. 基于记事本的工作流

为了更灵活的方法，使用 `xnotes/`中的模板：

1. 在Cursor选项中启用记事本
2. 创建一个新的记事本（例如，“agile”）
3. 复制内容来自 `xnotes/workflow-agile.md`
4. 在对话中使用 `\@notepad-name`

> 💡 **提示**：记事本方法非常适合：
>
> - 项目初始设置

- 故事实现
- 集中式开发时段
- 减少上下文开销

## 入门指南

1. 查看 `xnotes/`中的模板
2. 选择您偏好的工作流方法
3. 开始自信地使用AI！

有关演示和教程，请访问：[BMad Code 视频](https://youtube.com/bmadcode)
