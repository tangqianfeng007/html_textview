---
date: 2018-10-26 09:57
status: public
title: latex公式与图文混排控件调研
---

date: 2018-10-26 10:00
update:2018-11-01 11:00
status: public

## 背景
由于历史原因，现有教师端和学生端app在涉及作业、题目、公式的界面都采用了嵌入webview+js bridge的方式实现，经过多次版本迭代之后，在性能、用户体验、协作开发上遇到瓶颈，需要新的native组件替换改善。
##目的及作用
- 替换现有webview整屏实现方式，采用navtive实现。
- 大幅度提高界面渲染速度及运行性能，提高用户体验。
- 减少js bridge使用，隔离终端开发和H5开发耦合度，减少人力投入及多端协作。
- 易于终端开发独立维护。
## 基本需求
- 支持latex公式渲染
- 支持图文混排
- 支持html解析
- html标签可拓展
- native渲染
## 控件
| 控件名 | 描述 | 实现方式 | 缺陷 |star |
| ------------ | -------------- | --- | ---|---|
| JLaTexMath | latex公式渲染  |  natvive  | 部分复杂公式不能渲染 |91|
| MathView | latex公式渲染 | webview  | web渲染，性能有所降低 |823|
| FlexibleRichTextView | 图文混排+公式渲染  | native  | 不支持html排版解析、图文换行有缺陷 |279|
| HtmlSpanner | html标签解析、css解析 |  native | 不支持latex公式渲染、实现相对粗糙，性能较低 |548|
| htmlTextview | html标签解析 | native  | 不支持latex公式渲染、不支持css解析 |1676|

## 备选方案
- 基于**Mathview**局部渲染二次开发
- 基于**FlexibleRichTextView**二次开发
- 基于**HtmlSpanner**+**JLaTexMath**二次开发
- 基于**htmlTextview**+**JLaTexMath**二次开发