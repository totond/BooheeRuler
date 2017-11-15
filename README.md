# BooheeRuler
　　这是仿写薄荷健康里面体重选择尺的控件，因为最新的0.1.0版本经历重大更新（重构，把尺子分成多种形式），所以重新写一个README来介绍（旧的README在[这里](https://github.com/totond/BooheeRuler/blob/master/README_0.0.7.md)）：

![](https://i.imgur.com/80KtaQJ.gif)

## 介绍
　　由于不少伙伴们发邮件或者issue让我做纵向的尺子，其实我也很想做，但是最近项目有点忙，最后就只能在不太忙的阶段偷偷把它做出来了，欢迎大家使用，多多指教小弟哈。
　　这次更新主要是**把InnerRuler里面的一些公共的逻辑提取出来**，做成抽象类，然后再由两个HorizontalRuler和VerticalRuler继承它，再实现一些公共逻辑的处理，最后实现四个类型的子类：LeftHeadRuler、TopHeadRuler、RightHeadRuler、BottomHeadRuler。**这样能大大减少重复的代码，缺点就是逻辑分开了，想通过代码看实现一个尺子的逻辑就要跳来跳去有点不方便。**（这里重构的思想和我之前[YMenuView2.0的重构思路](http://blog.csdn.net/totond/article/details/78059196)有点相似）具体的重构思路我后面可能会写一篇文章介绍（又挖坑，目前还有很多坑没填，项目有点坑爹/(ㄒoㄒ)/~~）。

 > 要是想参考下一个完整的尺子的实现逻辑，可以看上一个版本0.0.7的代码，直接在Gradle里面：`    compile 'com.yanzhikai:BooheeRuler:0.0.7'`就可以了。
　　

## 使用

### Gradle

```
    compile 'com.yanzhikai:BooheeRuler:0.1.0'
```

### 使用方法
　　Demo里面有4个尺子，这里就写一个，因为都是差不多的。BooheeRuler分成两个部分，一个是上面的显示多少kg的数字Layout，还有一个就是下面的尺子。首先在xml文件里调用（下面属性的作用可以看后面的表格）：

```
        <!--数字和单位-->
        <yanzhikai.ruler.KgNumberLayout
            android:id="@+id/knl_bottom_head"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:kgTextSize="20sp"
            app:scaleTextSize="50sp" />                          
                                                           
    <!--尺子-->
    <yanzhikai.ruler.BooheeRuler
        android:id="@+id/br_top_head"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:layout_alignParentBottom="true"
        app:bigScaleLength="40dp"
        app:bigScaleWidth="2.5dp"
        app:count="10"
        app:currentScale="666"
        app:cursorDrawable="@drawable/cursor_shape"
        app:cursorHeight="45dp"
        app:cursorWidth="4dp"
        app:maxScale="2000"
        app:minScale="464"
        app:numberTextSize="22sp"
        app:paddingStartAndEnd="10dp"
        app:rulerStyle="TOP_HEAD"
        app:scaleInterval="11.5dp"
        app:smallScaleLength="20dp"
        app:smallScaleWidth="1.5dp"
        app:textMarginHead="80dp" />                                                       
                                                          
```

　　然后在java代码里面使用：

```
public class MainActivity extends AppCompatActivity {
    private BooheeRuler br_top_head;
    private KgNumberLayout knl_top_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        br_top_head = (BooheeRuler) findViewById(R.id.br_top_head);
        knl_top_head = (KgNumberLayout) findViewById(R.id.knl_top_head);
        knl_top_head.bindRuler(br_top_head);

    }
}
```

### 属性

#### BooheeRuler的属性

|**属性名称**|**意义**|**类型**|**默认值**|
|--|--|:--:|:--:|
|minScale      | 尺子的最小刻度值     | integer| 464（在尺子上显示就是46.4）|
|maxScale      | 尺子的最大刻度值     | integer| 2000（在尺子上显示就是200.0）|
|smallScaleLength | 尺子小刻度（0.1）的刻度线长度     | dimension| 30px|
|smallScaleWidth | 尺子小刻度（0.1）的刻度线宽度/粗细     | dimension| 3px|
|bigScaleLength | 尺子大刻度（1.0）的刻度线长度     | dimension| 60px|
|bigScaleWidth | 尺子大刻度（1.0）的刻度线宽度/粗细   | dimension| 5px|
|cursorHeight | 尺子中间的选定光标的刻度图高度    | dimension| 70px|
|cursorWidth | 尺子中间的选定光标的刻度图宽度   | dimension| 8px|
|~~textMarginTop~~ textMarginHead| 尺子数字文字距离边界距离    | dimension| 120px|
|numberTextSize | 尺子数字文字大小   | dimension| 28px|
|scaleInterval | 尺子每条刻度线之间的距离    | dimension| 18px|
|numberTextColor | 尺子数字文字颜色  | color| #2B2E2B|
|scaleColor | 尺子刻度线的颜色  | color| #e2e5e2|
|currentScale | 尺子初始选定刻度  | float| (maxScale + minScale)/2|
|cursorDrawable | 尺子中间选定光标的Drawable(会把drawable伸缩到设定的宽高上) | dimension| @drawable/cursor_shape|
|count | 一个大刻度格子里面的小刻度格子数| integer|10|
|paddingStartAndEnd| 控制尺子两端的padding| dimension|0|
|rulerBackGround（new）| 尺子的背景|reference或者color|#f6f9f6|
|rulerStyle|尺子的形态（下面有具体介绍）|enum|TOP_HEAD|

　　接下来是选择尺子形态的属性rulerStyle：

|rulerStyle选项|意义|
|--|--|
|TOP_HEAD|头部向上的尺子|
|BOTTOM_HEAD|头部向下的尺子|
|LEFT_HEAD |头部向左的尺子|
|RIGHT_HEAD|头部向右的尺子|

> 如效果图中，对应位置的尺子的rulerStyle为：上——BOTTOM_HEAD，下——TOP_HEAD，左——RIGHT_HEAD，右——LEFT_HEAD。


#### KgNumberLayout属性

|**属性名称**|**意义**|**类型**|**默认值**|
|--|--|:--:|:--:|
|scaleTextSize      | 数字字体大小     | dimension| 80|
|kgTextSize      | 单位字体大小     | dimension| 40|
|scaleTextColor      | 数字字体颜色| color| #4bbb74|
|kgTextColor      | 单位字体颜色 | color| #4bbb74|
|kgUnitText      | 单位文字内容     | string| kg|


### 接口

　　除了使用KgNumberLayout作为显示BooheeRuler的当前刻度之外，还可以通过实现RulerCallback回调接口来获取当前选定刻度：

```
public interface RulerCallback {
    //选取刻度变化的时候回调
    void onScaleChanging(float scale);
}

```
　　实现了这个接口之后，再调用`BooheeRuler.setCallback(RulerCallback rulerCallback)`方法传入即可。


## 更新

 - 2017/10/23 **version 0.0.5**: 
     - 修改了画刻度的方法，改为只画当前屏幕显示的刻度
     - 增加了count属性，用于设置一个大刻度格子里面的小刻度格子数，默认是10
 - 2017/10/30 **version 0.0.6**: 
     - 加入VelocityTracker的回收处理（之前只是clear并没有recycle），提高性能。
     - 加入属性`paddingStartAndEnd`，用于控制尺子两端的padding。
     - 让刻度的绘制前后多半大格，这样可以提前显示出下面的数字，让过渡不会变得那么突兀。
     - 取消了一些不必要的log输出。

> 非常感谢[JulianAndroid](https://github.com/JulianAndroid)为我指出来VelocityTracker这个错误。

 - 2017/10/30 **version 0.0.7**:
     - 之前VelocityTracker重复使用了addMovement，现在取消掉了。

 - 2017/11/15 **version 0.1.0**:
     - 重构代码，将尺子分为4个形态。
     - 对细节有一些小改动：如背景设置换成以InnerRuler为主体，优化Padding等。

## 开源协议
　　BooheeRuler遵循MIT协议。

## 关于作者
 > id：炎之铠

 > 炎之铠的邮箱：yanzhikai_yjk@qq.com

 > CSDN：http://blog.csdn.net/totond

