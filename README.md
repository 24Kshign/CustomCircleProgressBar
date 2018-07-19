# CustomCircleProgressBar

![真机上没有这么卡顿的效果](http://upload-images.jianshu.io/upload_images/490111-f74f09fddab84c6b.gif?imageMogr2/auto-orient/strip)

简单自定义了一个比较通用的圆形进度条，像上图所示的可以定义圆的半径，进度颜色，宽度，中间字体等信息。下面我就一步一步来为大家讲解：
>#### 1、首先我们先要找出有哪些属性需要自定义的，进度条颜色、进度颜色、整个进度条的半径、进度的宽度、进度条内文字颜色及大小、最大进度、当前进度，后来我加了一个方向的属性，方向表示进度从哪里开始（默认有四个方向，上左下右），确定好之后我们就在attrs中定义出来：
	
	 <declare-styleable name="CustomCircleProgressBar">
        <attr name="outside_color" format="color" />
        <attr name="outside_radius" format="dimension" />
        <attr name="inside_color" format="color" />
        <attr name="progress_text_color" format="color" />
        <attr name="progress_text_size" format="dimension" />
        <attr name="progress_width" format="dimension" />
        <attr name="max_progress" format="integer" />
        <attr name="progress" format="float" />
        <attr name="direction">
            <enum name="left" value="0" />
            <enum name="top" value="1" />
            <enum name="right" value="2" />
            <enum name="bottom" value="3" />
        </attr>
    </declare-styleable>
>#### 2、然后在自定义**View**的构造方法中获取一下这些值：
	
	public CustomCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCircleProgressBar, defStyleAttr, 0);
        outsideColor = a.getColor(R.styleable.CustomCircleProgressBar_outside_color, ContextCompat.getColor(getContext(), R.color.colorPrimary));
        outsideRadius = a.getDimension(R.styleable.CustomCircleProgressBar_outside_radius, DimenUtil.dp2px(getContext(), 60.0f));
        insideColor = a.getColor(R.styleable.CustomCircleProgressBar_inside_color, ContextCompat.getColor(getContext(), R.color.inside_color));
        progressTextColor = a.getColor(R.styleable.CustomCircleProgressBar_progress_text_color, ContextCompat.getColor(getContext(), R.color.colorPrimary));
        progressTextSize = a.getDimension(R.styleable.CustomCircleProgressBar_progress_text_size, DimenUtil.dp2px(getContext(), 14.0f));
        progressWidth = a.getDimension(R.styleable.CustomCircleProgressBar_progress_width, DimenUtil.dp2px(getContext(), 10.0f));
        progress = a.getFloat(R.styleable.CustomCircleProgressBar_progress, 50.0f);
        maxProgress = a.getInt(R.styleable.CustomCircleProgressBar_max_progress, 100);
        direction = a.getInt(R.styleable.CustomCircleProgressBar_direction, 3);

        a.recycle();

        paint = new Paint();
    }
>#### 3、接下来我们要重写onMeasure方法，让其可以自适应你的设置：
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = (int) ((2 * outsideRadius) + progressWidth);
        }
        size = MeasureSpec.getSize(heightMeasureSpec);
        mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = (int) ((2 * outsideRadius) + progressWidth);
        }
        setMeasuredDimension(width, height);
    }
>#### 4、这两块就不多说了，相信大多数看官应该都知道，接下来我们来分析要画些什么？怎么画？首先肯定是画最底层的那个圆环了，给画笔设置空心属性，然后设置线的宽度，就可以画一个圆环了：
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int circlePoint = getWidth() / 2;
        //第一步:画背景(即内层圆)
        paint.setColor(insideColor); //设置圆的颜色
        paint.setStyle(STROKE); //设置空心
        paint.setStrokeWidth(progressWidth); //设置圆的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(circlePoint, circlePoint, outsideRadius, paint); //画出圆

    }
![](http://upload-images.jianshu.io/upload_images/490111-f1650614c8e2523a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>#### 5、然后我们接着画外面的进度，外面进度就是一段弧，根据我们获取的进度和总进度来画这段弧，画弧需要用到`canvas.drawArc()`这个方法，这个方法有两个重载方法：
`drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter,Paint paint)`
`drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle,boolean useCenter,Paint paint)`
其实，可以看作是一个方法，因为`RectF`这个东西呢就是由`left top right bottom`构成的，那`RectF`这玩意儿到底什么东西呢，我也不知道，那就去看源码呗：

	RectF holds four float coordinates for a rectangle.
	The rectangle isrepresented by the coordinates of its 4 edges (left, top, right bottom).
	These fields can be accessed directly. 
	Use width() and height() to retrieve the rectangle's width and height. 
	Note: most methods do not check to see that the coordinates are sorted correctly (i.e. left <= right and top <= bottom).
>简单点说，这东西可以构造一个矩形，如何构造呢？我也不知道，哈哈哈！我们不妨来看看它的构造方法：

	@param left   The X coordinate of the left side of the rectangle
	@param top    The Y coordinate of the top of the rectangle
	@param right  The X coordinate of the right side of the rectangle
	@param bottom The Y coordinate of the bottom of the rectangle
>这里解释一下，矩形有四个点，这四个值就把矩形四个点的坐标给确定了，`left`表示矩形左边两个点的`X`轴坐标，`right`表示矩形右边两个点的`X`轴坐标，`top`表示矩形上边两个点的`Y`轴坐标，`bottom`表示矩形下边两个点的`Y`轴坐标，详细参照下图：

![RectF的左上右下](http://upload-images.jianshu.io/upload_images/490111-e65494642466b8e4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
	
	写法一：
	RectF oval = new RectF(circlePoint - outsideRadius, circlePoint - outsideRadius, circlePoint + outsideRadius, circlePoint + outsideRadius);  //用于定义的圆弧的形状和大小的界限
	写法二：
	RectF oval = new RectF();
	oval.left=circlePoint - outsideRadius;
    oval.top=circlePoint - outsideRadius;
    oval.right=circlePoint + outsideRadius;
    oval.bottom=circlePoint + outsideRadius;
>然后`drawArc()`方法中的第二（五）个参数`startAngle`表示我们画弧度开始的角度，这里的值为**0-360**，**0**表示三点钟方向，**90**表示六点钟方向，以此类推；后面那个参数`sweepAngle`表示要画多少弧度，这里的取值也是从**0-360**，我们通常使用当前进度占总进度的百分之多少，再乘以弧度**360**就是我们所要画的弧度了；再后面那个参数`useCenter`表示是否连接圆心一起画，下面来看看代码：

	//第二步:画进度(圆弧)不连接圆心
    paint.setColor(outsideColor);  //设置进度的颜色
    RectF oval = new RectF(circlePoint - outsideRadius, circlePoint - outsideRadius, circlePoint + outsideRadius, circlePoint + outsideRadius);  //用于定义的圆弧的形状和大小的界限
    canvas.drawArc(oval, CustomCircleProgressBar.DirectionEnum.getDegree(direction), 360 * (progress / maxProgress), false, paint);  //根据进度画圆弧

![不连接圆心](http://upload-images.jianshu.io/upload_images/490111-7e4b5289589e4a3f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
    
    //第二步:画进度(圆弧)连接圆心
    paint.setColor(outsideColor);  //设置进度的颜色
    RectF oval = new RectF(circlePoint - outsideRadius, circlePoint - outsideRadius, circlePoint + outsideRadius, circlePoint + outsideRadius);  //用于定义的圆弧的形状和大小的界限
    canvas.drawArc(oval, CustomCircleProgressBar.DirectionEnum.getDegree(direction), 360 * (progress / maxProgress), true, paint);  //根据进度画圆弧
![连接圆心](http://upload-images.jianshu.io/upload_images/490111-fbb8b084df3a37c5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>#### 6、接下来就是画圆环内的百分比文字了，可能有的人就说，画文字嘛，那不是很简单，直接`drawText`方法画不就好了，要什么值就传什么呗！大兄弟别急撒，下面给大家看看直接画文字的效果：
	
	paint.setColor(progressTextColor);
    paint.setTextSize(progressTextSize);
    paint.setStrokeWidth(0);
    progressText = (int) ((progress / maxProgress) * 100) + "%";
    canvas.drawText(progressText, circlePoint , circlePoint, paint);
![](http://upload-images.jianshu.io/upload_images/490111-39922ed83c633a5c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>**WTF**，发生了什么？？？所以说大兄弟，憋着急，这里`drawText`方法是从文字的左上角开始画的，所以我们需要剪去文字一半的宽高：
	
	rect = new Rect();
    paint.getTextBounds(progressText, 0, progressText.length(), rect);
    canvas.drawText(progressText, circlePoint- rect.width() / 2 , circlePoint- rect.height() / 2, paint);
![](http://upload-images.jianshu.io/upload_images/490111-976c527b3d0bf1ea.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>再次**WTF**，可能又有人说，LZ你骗人，它还是没有居中，这尼玛心中顿时有一万只草泥马在奔腾，别急，还没讲完，给你看看源码你就知道了：

	/**
     * Draw the text, with origin at (x,y), using the specified paint. The
     * origin is interpreted based on the Align setting in the paint.
     *
     * @param text  The text to be drawn
     * @param x     The x-coordinate of the origin of the text being drawn
     * @param y     The y-coordinate of the baseline of the text being drawn
     * @param paint The paint used for the text (e.g. color, size, style)
     */
    public void drawText(@NonNull String text, float x, float y, @NonNull Paint paint) {
        native_drawText(mNativeCanvasWrapper, text, 0, text.length(), x, y, paint.mBidiFlags,
                paint.getNativeInstance(), paint.mNativeTypeface);
    }
>不知道各位有没有看到第三个参数`y`的解释，它不是纵轴的坐标，而是基准线`y`坐标，至于这个基准线，LZ不打算在这里展开讲，因为这个也有很多内容，给大家推荐一篇讲的非常详细的博客：
[自定义控件之绘图篇（ 五）：drawText()详解](http://blog.csdn.net/harvic880925/article/details/50423762)
接下来来看看咱是怎么写的：

	//第三步:画圆环内百分比文字
    rect = new Rect();
    paint.setColor(progressTextColor);
    paint.setTextSize(progressTextSize);
    paint.setStrokeWidth(0);
    progressText = getProgressText();
    paint.getTextBounds(progressText, 0, progressText.length(), rect);
    Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
    int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;  //获得文字的基准线
    canvas.drawText(progressText, getMeasuredWidth() / 2 - rect.width() / 2, baseline, paint);
>再来看看最终的效果：

![](http://upload-images.jianshu.io/upload_images/490111-bb07e687be2b896a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

好了，现在进度和文字都画出来了，个人觉得就这样直接展示在用户眼前显得有点生硬，有没有什么办法让它的进度从零开始跑动画到我们要设置的进度值呢，答案是肯定的咯，这里我们可以用属性动画来实现，前面几篇博客我们有讲到属性动画的知识，如果你还没有看过的话，请移步：

#### [Android自定义view之属性动画熟悉](http://www.jianshu.com/p/50d974db7bc5)

#### [Android自定义view之属性动画初见](http://www.jianshu.com/p/0e10a6ed80dc)

这里我们使用的是`ValueAnimator`，通过监听动画改变进度的值来设置圆环的进度：

	private void startAnim(float startProgress) {
        animator = ObjectAnimator.ofFloat(0, startProgress);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                CustomCircleProgressBar.this.progress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.setStartDelay(500);   //设置延迟开始
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());   //动画匀速
        animator.start();
    }
到此就完成了自定义的原型进度条了。


![公众号：Android先生](http://upload-images.jianshu.io/upload_images/490111-8c1cdb3bd9dfd604.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
