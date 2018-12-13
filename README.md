---
date: 2018-11-02 15:40
status: public
title: HtmlTextView
---

### 介绍
解析html标签进行布局的图文公式混排控件。基于htmlTextview和JLaTexMath这两个库上融合和修改。
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
tv.setHtml(HtmlUtils.parseHtmlData(content));
```
> HtmlTextView渲染的是题干数据，即后台接口返回数据的content字段,取得content字段后直接转换赋值即可**tv.setHtml(HtmlUtils.parseHtmlData(content))**;

#### 接口返回数据样例
![](_image/HtmlTextView/10-16-11.jpg)
#### 效果

![](_image/HtmlTextView/10-17-36.jpg)





### HtmlOptionButton(选择题选项按钮)
> 选择题选项返回的数据option字段和题干数据字段content是分开的，各个界面可能会有自定义样式及自己业务逻辑,所以选择题是一个单独的控件，下面是一个简单的通用选择题按钮，支持html标签和latex标签解析，参考可用。

#### xml
```java
<org.sufficientlysecure.htmltextview.internal.HtmlOptionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            app:content="这是一道选择题$k'(x)=\\lim_{\\Delta x\\to 0}\\frac{k(x)-k(x-\\Delta x)}{\\Delta x}$"
            app:head="B"
            app:state="YELLOW" />
```
#### 代码
```java
   /**
     * 
     * @param option
     * 这是一道选择题$k'(x)=\lim_{\Delta x\to 0}\frac{k(x)-k(x-\Delta x)}{\Delta x}$
     */
    private void setButton(String option) {
        HtmlOptionButton button = new HtmlOptionButton(this);
        //option支持html、latex标签的混合文本
        button.setContent(option);
        //选项A、B、C、D
        button.setHead("A");
        //按钮状态颜色
        button.setState(HtmlOptionButton.State.GREEN);
    }
```
#### 效果
![](_image/HtmlTextView/10-54-46.jpg)
#### 注意
由于TypedArray取值的原因，在xml里设置带有公式表达式的content的时候，转移符必须多一位。
![](_image/HtmlTextView/11-05-50.jpg)

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

### 原理
整个控件都是基于textview span的基础上进行拓展，对于不支持的html标签可以在**HtmlTagHandler**中拓展，html标签的解析是基于sax解析方式，详见**handleTag**方法。

![](_image/HtmlTextView/11-29-07.jpg)

``<table>``标签的二次拓展解析由于体量较小采用的是dom解析，然后绘制成图，详见TableConverter类。
![](_image/HtmlTextView/11-31-43.jpg)
``<formula>``标签是基于JLaTexMath库解析，详见HtmlFormulaImageGetter类。
![](_image/HtmlTextView/11-33-37.jpg)
由于android.text.html类属性传值不全的原因，自定义标如果需要获取属性(Attribute),只有通过反射获取，代码示例如下。
```java
/**
     * 利用反射获取html标签的属性值
     *
     * @param xmlReader
     * @param property
     * @return
     */
    private String getProperty(XMLReader xmlReader, String property) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            for (int i = 0; i < len; i++) {
                // 这边的property换成你自己的属性名就可以了
                if (property.equals(data[i * 5 + 1])) {
                    return data[i * 5 + 4];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

```
### future
- 使用mathview做htmltextview部分特殊排版题不兼容的降级处理。
- 使用jlatexmath-android替换JLaTexMath。
- 拓展``<a>``标签，支持路由跳转。
- 拓展``<img>``标签，支持点击预览及排版可居中对齐显示。

### 其他
- 公式的显示颜色是单独设置的详见AjLatexMath.setColor(int color)方法。
- 网络加载图片的``<img>``标签，加载的图片大小跟真实原图一样大，不受width和height限制，如果需要支持自定义大小需要拓展属性支持。
- 题干数据超过一屏幕的时候，使用Scrollview包裹HtmlTextView可以使滑动更流畅。


