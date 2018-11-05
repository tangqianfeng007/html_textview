---
date: 2018-11-02 15:40
status: public
title: HtmlTextView
---

date: 2018-11-02 10:00
status: protected
## 介绍
基于解析html标签进行布局的图文公式混排控件。
### 使用实例
```java
<org.sufficientlysecure.htmltextview.HtmlTextView
            android:id="@+id/tv"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="#123456"
            android:textIsSelectable="false"
            android:textSize="16dp" />
            
HtmlTextView tv = findViewById(R.id.tv);
tv.setHtml(HtmlUtils.parseHtmlData(html));
```
### 支持的html标签
* ``<p>``
* ``<div>`` 
* ``<br>``
* ``<b>``
* ``<i>``
* ``<strong>`` ([bug on some Android versions: generates italic](https://code.google.com/p/android/issues/detail?id=3473))
* ``<em>`` ([bug on some Android versions: generates bold](https://code.google.com/p/android/issues/detail?id=3473))
* ``<u>``
* ``<tt>``
* ``<dfn>``
* ``<sub>``
* ``<sup>``
* ``<blockquote>``
* ``<cite>``
* ``<big>``
* ``<small>``
* ``<font size="..." color="..." face="...">``
* ``<h1>``, ``<h2>``, ``<h3>``, ``<h4>``, ``<h5>``, ``<h6>``
* ``<a href="...">``
* ``<img src="...">``
* ``<ul>``
* ``<ol>``
* ``<li>``
* ``<del>``
* ``<code>``
* ``<center>``
* ``<strike>``
* ``<table>``
* ``<tr>``
* ``<th>``
* ``<td>``
* ``<formula>``

### HtmlOptionButton
```java
<org.sufficientlysecure.htmltextview.internal.HtmlOptionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            app:content="这是一道选择题的选项B"
            app:head="B"
            app:state="YELLOW" />
```

### HtmlUtils类
 - String parseHtmlData(String data)  转换公式分割符$为html标签符``<formula>``

### tip
>数据超过一屏幕的时候，使用Scrollview包裹HtmlTextView可以使滑动更流畅。