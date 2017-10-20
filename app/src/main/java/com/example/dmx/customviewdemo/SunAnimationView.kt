package com.example.dmx.customviewdemo

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import java.text.DecimalFormat


/**
 * Created by dmx on 17-10-19.
 */
open class SunAnimationView : View {
    private var mWidth: Int = 0 //屏幕宽度
    private var marginTop = 20//离顶部的高度
    private var mCircleColor: Int = 0  //圆弧颜色
    private var mFontColor: Int = 0  //字体颜色
    private var mRadius: Int = 0  //圆的半径

    private var mCurrentAngle: Float = 0.toFloat() //当前旋转的角度
    private var mTotalMinute: Float = 0.toFloat() //总时间(日落时间减去日出时间的总分钟数)
    private var mNeedMinute: Float = 0.toFloat() //当前时间减去日出时间后的总分钟数
    private var mPercentage: Float = 0.toFloat() //根据所给的时间算出来的百分占比
    private var positionX: Float = 0.toFloat()
    private var positionY: Float = 0.toFloat() //太阳图片的x、y坐标
    private var mFontSize: Float = 0.toFloat()  //字体颜色

    private var imageWidth: Float = 0F//
    private var imageHeight: Float = 0F

    private var mStartTime: String? = null //开始时间(日出时间)
    private var mEndTime: String? = null //结束时间（日落时间）
    private var mCurrentTime: String? = null //当前时间

    private var mPaint: Paint? = null //画笔
    private var mRectF: RectF? = null //半圆弧所在的矩形
    private var mSunIcon: Bitmap? = null //太阳图片
    private var wm: WindowManager? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typeArray = context.obtainStyledAttributes(attrs, R.styleable.SunAnimationView)
            mCircleColor = typeArray.getColor(R.styleable.SunAnimationView_sun_circle_color, Color.BLACK)
            mFontColor = typeArray.getColor(R.styleable.SunAnimationView_sun_font_color, Color.BLACK)
            mRadius = typeArray.getInteger(R.styleable.SunAnimationView_sun_circle_radius, 10)
            mRadius = DisplayUtil.dp2px(context, mRadius.toFloat())
            mFontSize = typeArray.getDimension(R.styleable.SunAnimationView_sun_font_size, 16f)
            mFontSize = DisplayUtil.dp2px(context, mFontSize).toFloat()
            typeArray.recycle()

            mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mSunIcon = BitmapFactory.decodeResource(context.resources, R.drawable.icon_sun)
            imageHeight = mSunIcon!!.height.toFloat()
            imageWidth = mSunIcon!!.width.toFloat()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        if (wm != null) {
            var point = Point()
            wm!!.defaultDisplay.getSize(point)
            mWidth = point.x
            positionX = (mWidth / 2 - mRadius - imageWidth / 2).toFloat()
            positionY = mRadius.toFloat() - imageHeight / 2
        }
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var newWidthMeasureSpec =widthMeasureSpec
        var newHeightMeasureSpec = heightMeasureSpec
        if (widthMode == MeasureSpec.EXACTLY) {
            if (mRadius * 2 > widthSize) {
                newWidthMeasureSpec = MeasureSpec.makeMeasureSpec((mRadius * 2 + imageWidth).toInt(), MeasureSpec.EXACTLY)
            }
        } else {
            newWidthMeasureSpec = MeasureSpec.makeMeasureSpec((mRadius * 2 + imageWidth).toInt(), MeasureSpec.EXACTLY)
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            if (mRadius * 2 > heightSize) {
                newHeightMeasureSpec = MeasureSpec.makeMeasureSpec((mRadius * 2 + imageHeight).toInt(), MeasureSpec.EXACTLY)
            }
        }
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, mWidth / 2 - mRadius, marginTop, mWidth / 2 + mRadius, (mRadius * 2 + marginTop))
    }

    override fun onDraw(canvas: Canvas) {
        //第一步：画半圆
        drawSemicircle(canvas)
        canvas.save()

        //第二步：绘制太阳的初始位置 以及 后面在动画中不断的更新太阳的X，Y坐标来改变太阳图片在视图中的显示
        drawSunPosition(canvas)

        //第三部：绘制图上的文字
        drawFont(canvas)

        super.onDraw(canvas)
    }

    private fun drawSemicircle(canvas: Canvas) {
        mRectF = RectF((mWidth / 2 - mRadius).toFloat(), marginTop.toFloat()+imageHeight/2, (mWidth / 2 + mRadius).toFloat(), (mRadius * 2 + marginTop).toFloat())
        mPaint?.style = Paint.Style.STROKE
        mPaint?.isDither = true
        mPaint?.color = mCircleColor
        canvas.drawArc(mRectF, 180F, 180F, true, mPaint)
    }

    private fun drawSunPosition(canvas: Canvas) {
        canvas.drawBitmap(mSunIcon, positionX, positionY, mPaint)
    }

    private fun drawFont(canvas: Canvas) {
        mPaint?.setColor(mFontColor)
        mPaint?.setTextSize(mFontSize)
        val startTime = if (TextUtils.isEmpty(mStartTime)) "" else mStartTime
        val endTime = if (TextUtils.isEmpty(mEndTime)) "" else mEndTime
        val sunrise = "日出时间:" + startTime
        val sunset = "日落时间:" + endTime
        canvas.drawText(sunrise, (mWidth / 2 - mRadius).toFloat(), (mRadius + imageHeight + marginTop).toFloat(), mPaint)
        canvas.drawText(sunset, (mWidth / 2 + mRadius - getTextWidth(mPaint, sunset)).toFloat(), (mRadius + imageHeight + marginTop).toFloat(), mPaint)
    }

    /**
     * 精确计算文字宽度
     *
     * @param paint 画笔
     * @param str   字符串文本
     * @return
     */
    fun getTextWidth(paint: Paint?, str: String?): Int {
        var iRet = 0
        if (str != null && str.length > 0) {
            val len = str.length
            val widths = FloatArray(len)
            paint?.getTextWidths(str, widths)
            for (j in 0 until len) {
                iRet += Math.ceil(widths[j].toDouble()).toInt()
            }
        }
        return iRet
    }

    fun setTimes(startTime: String, endTime: String, currentTime: String) {
        mStartTime = startTime
        mEndTime = endTime
        mCurrentTime = currentTime

        mTotalMinute = calculateTime(mStartTime!!, mEndTime!!)//计算总时间，单位：分钟
        mNeedMinute = calculateTime(mStartTime!!, mCurrentTime!!)//计算当前所给的时间 单位：分钟
        mPercentage = java.lang.Float.parseFloat(formatTime(mTotalMinute, mNeedMinute))//当前时间的总分钟数占日出日落总分钟数的百分比
        mCurrentAngle = 180 * mPercentage

        setAnimation(0F, mCurrentAngle, 5000)

    }

    private fun setAnimation(startAngle: Float, currentAngle: Float, duration: Int) {
        val sunAnimator = ValueAnimator.ofFloat(startAngle, currentAngle)
        sunAnimator.duration = duration.toLong()
        sunAnimator.setTarget(currentAngle)
        sunAnimator.addUpdateListener { animation ->
            //每次要绘制的圆弧角度
            mCurrentAngle = animation.animatedValue as Float
            invalidateView()
        }
        sunAnimator.start()
    }

    private fun invalidateView() {
        //绘制太阳的x坐标和y坐标
        positionX = mWidth / 2 - (mRadius * Math.cos(mCurrentAngle * Math.PI / 180).toFloat()) - imageWidth / 2
        positionY = mRadius - (mRadius * Math.sin(mCurrentAngle * Math.PI / 180).toFloat())

        invalidate()
    }

    /**
     * 根据日出和日落时间计算出一天总共的时间:单位为分钟
     *
     * @param startTime 日出时间
     * @param endTime   日落时间
     * @return
     */
    private fun calculateTime(startTime: String, endTime: String): Float {

        if (checkTime(startTime, endTime)) {
            val startTimes = startTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val endTimes = endTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val startHour = java.lang.Float.parseFloat(startTimes[0])
            val startMinute = java.lang.Float.parseFloat(startTimes[1])

            val endHour = java.lang.Float.parseFloat(endTimes[0])
            val endMinute = java.lang.Float.parseFloat(endTimes[1])

            return (endHour - startHour - 1f) * 60 + (60 - startMinute) + endMinute
        }
        return 0f
    }

    /**
     * 对所给的时间做一下简单的数据校验
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private fun checkTime(startTime: String, endTime: String): Boolean {
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)
                || !startTime.contains(":") || !endTime.contains(":")) {
            return false
        }

        val startTimes = startTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val endTimes = endTime.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val startHour = java.lang.Float.parseFloat(startTimes[0])
        val startMinute = java.lang.Float.parseFloat(startTimes[1])

        val endHour = java.lang.Float.parseFloat(endTimes[0])
        val endMinute = java.lang.Float.parseFloat(endTimes[1])
        if (mStartTime == null || mEndTime == null) {
            return false
        }
        //如果所给的时间(hour)小于日出时间（hour）或者大于日落时间（hour）
        if (startHour < java.lang.Float.parseFloat(mStartTime!!.split(":")[0]) || endHour > java.lang.Float.parseFloat(mEndTime!!.split(":")[0])) {
            return false
        }

        //如果所给时间与日出时间：hour相等，minute小于日出时间
        if (startHour == java.lang.Float.parseFloat(mStartTime!!.split(":")[0]) && startMinute < java.lang.Float.parseFloat(mStartTime!!.split(":")[1])) {
            return false
        }

        //如果所给时间与日落时间：hour相等，minute大于日落时间
        if (startHour == java.lang.Float.parseFloat(mEndTime!!.split(":")[0]) && endMinute > java.lang.Float.parseFloat(mEndTime!!.split(":")[1])) {
            return false
        }

        return !(startHour < 0 || endHour < 0
                || startHour > 23 || endHour > 23
                || startMinute < 0 || endMinute < 0
                || startMinute > 60 || endMinute > 60)
    }

    /**
     * 根据具体的时间、日出日落的时间差值 计算出所给时间的百分占比
     *
     * @param totalTime 日出日落的总时间差
     * @param needTime  当前时间与日出时间差
     * @return
     */
    private fun formatTime(totalTime: Float, needTime: Float): String {
        if (totalTime == 0f)
            return "0.00"
        val decimalFormat = DecimalFormat("0.00")//保留2位小数，构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(needTime / totalTime)//format 返回的是字符串
    }
}