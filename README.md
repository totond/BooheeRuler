# BooheeRuler
　　这是仿写薄荷健康里面体重选择尺的控件，是为了参加[HenCoder「仿写酷界面」活动](https://juejin.im/post/59e019c1f265da4319554878),效果如下：

![](https://i.imgur.com/PVoVWlI.gif)


然后这是原效果：

![](https://i.imgur.com/WvKyJEG.gif)

## 介绍
　　通过观察原作的效果图和下载了薄荷健康APP观察原控件（不过最新版本已经改了一下UI，要改成最新版那种UI也可以通过在xml设置下颜色和光标的图案就可以实现了），我仿写的这个BooheeRuler主要实现了以下技术难点（对我来说）：
 - 滑动界面
 - 触摸滑动后的惯性滚动
 - 让光标保持在中间，不随尺子滚动
 - 计算光标选中的刻度
 - 让触摸滑动、惯性滚动之后，回滚到最近的整点刻度，如最后滑动到66.5和66.6中间靠右，则回滚到66.6

## 使用

### Gradle

```
    compile 'com.yanzhikai:BooheeRuler:0.0.7'
```

### 使用方法
　　BooheeRuler分成两个部分，一个是上面的显示多少kg的数字Layout，还有一个就是下面的尺子。首先在xml文件里调用（下面属性的作用可以看后面的表格）：

```
    <!--数字和单位-->                                           
    <yanzhikai.ruler.KgNumberLayout                        
        android:layout_width="match_parent"                
        android:layout_height="wrap_content"               
        android:layout_above="@+id/br"                     
        app:scaleTextSize="50sp"                           
        app:kgTextSize="20sp"                              
        android:layout_marginBottom="20dp"                 
        android:id="@+id/knl"/>                            
                                                           
    <!--尺子-->                                              
    <yanzhikai.ruler.BooheeRuler                           
        android:layout_width="match_parent"                
        android:layout_height="100dp"                      
        android:id="@+id/br"                               
        android:background="@color/colorDirtyWithe"        
        app:minScale="464"                                 
        app:maxScale="2000"                                
        app:numberTextSize="22sp"                          
        app:textMarginTop="80dp"                           
        app:cursorHeight="45dp"                            
        app:smallScaleLength="20dp"                        
        app:smallScaleWidth="1.5dp"                        
        app:bigScaleLength="40dp"                          
        app:bigScaleWidth="2.5dp"                          
        app:scaleInterval="11.5dp"                         
        app:currentScale="666"                             
        android:layout_centerInParent="true"               
        />                                                                                           
                                                          
```

　　然后在java代码里面使用：

```
public class MainActivity extends AppCompatActivity {
    private BooheeRuler mBooheeRuler;
    private KgNumberLayout mKgNumberLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBooheeRuler = (BooheeRuler) findViewById(R.id.br);
        mKgNumberLayout = (KgNumberLayout) findViewById(R.id.knl);
        mKgNumberLayout.bindRuler(mBooheeRuler);

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
|cursorHeight | 尺子中间的选定光标的刻度线长度     | dimension| 70px|
|cursorWidth | 尺子中间的选定光标的刻度线宽度/粗细   | dimension| 8px|
|textMarginTop | 尺子数字文字距离上边界距离    | dimension| 120px|
|numberTextSize | 尺子数字文字大小   | dimension| 28px|
|scaleInterval | 尺子每条刻度线之间的距离    | dimension| 18px|
|numberTextColor | 尺子数字文字颜色  | color| #2B2E2B|
|scaleColor | 尺子刻度线的颜色  | color| #e2e5e2|
|currentScale | 尺子初始选定刻度  | float| (maxScale + minScale)/2|
|cursorDrawable | 尺子中间选定光标的Drawable(会把drawable伸缩到设定的宽高上) | dimension| @drawable/cursor_shape|
|count | 一个大刻度格子里面的小刻度格子数| integer|10|
|paddingStartAndEnd（新版本新增） | 控制尺子两端的padding（设置这个会使尺子的实际宽度改变）| dimension|0|

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

## 开源协议
　　BooheeRuler遵循MIT协议。

## 关于作者
 > id：炎之铠

 > 炎之铠的邮箱：yanzhikai_yjk@qq.com

 > CSDN：http://blog.csdn.net/totond

