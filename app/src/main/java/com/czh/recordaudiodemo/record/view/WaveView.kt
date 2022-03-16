package com.czh.recordaudiodemo.record.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.czh.recordaudiodemo.R
import com.czh.recordaudiodemo.record.util.dp2px
import kotlin.math.sin

/**
 * @Description: 正弦竖状波纹
 * @Author: CZH
 * @Create: 2022/3/15
 */
class WaveView : View {

    private lateinit var mPaint: Paint

    private var mLineCount = 0
    private var mLineWidth = 0
    private var mLineColor = 0
    private var mStartLevel = 0
    private var mLevelDivideCount = 0
    private var mPeakCount = 0

    private var mValue = 0
    private var mOffset = 0f
    private val dp1 = dp2px(context, 1f)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttr(attrs)
        initPaint()
    }

    /**
     * 初始化自定义的属性
     */
    private fun initAttr(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.WaveView)
        mLineCount = ta.getInteger(R.styleable.WaveView_lineCount, 100)
        mLineWidth = ta.getDimensionPixelSize(R.styleable.WaveView_lineWidth, dp2px(context, 2f))
        mLineColor = ta.getColor(R.styleable.WaveView_lineColor, Color.GRAY)
        mStartLevel = ta.getInteger(R.styleable.WaveView_startLevel, 0)
        mLevelDivideCount = ta.getInteger(R.styleable.WaveView_levelDivideCount, 50)
        mPeakCount = ta.getInteger(R.styleable.WaveView_peakCount, 8)
        mValue = mStartLevel
        ta.recycle()
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = mLineColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mLineWidth.toFloat()
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    /**
     * 实现波浪的效果我们需要用到一个正弦函数：a * sin(b * x + c) + d
     */
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        // 记录一下开始时间，用于后面计算绘制耗时
//        val startTime = System.currentTimeMillis()

        // 竖线的之间的距离
        // 将宽度分成(mLineCount - 1)份，可以得到竖线之间的距离。算上始末两个点也就是刚好mLineCount个分割点，在分割点上划竖线
        // 直接用width去算的话，始末两条线会只有一半，减去线的宽度再算，后面开始画的时候，平移半个线的宽度，就都能够完整展示
        val spaceWidth = (width - mLineWidth).toFloat() / (mLineCount - 1)

        // 振幅的单位高度
        // 竖线是上下对称，所以需要将高度折半，再讲其做mLevelDivideCount等分
        val spaceHeight = (height.toFloat() / 2 / mLevelDivideCount)

        // 振幅
        val amplitude = (mValue - mStartLevel) * spaceHeight

        // 横坐标压缩倍数
        val compressionMultiple = Math.PI.toFloat() / ((width.toFloat() - mLineWidth) / mPeakCount)

        // 更新正弦函数在水平方向的偏移量
        this.mOffset += dp1
        this.mOffset = this.mOffset % (width - mLineWidth)

        // 偏移量
        val offset = mOffset

        // 水平线高度
        val level = height.toFloat() / 2

        if (amplitude <= 0f) {
            // 如果振幅小于等于0，为了让它还能显示一个点，就画一个上下为1个像素的高度，也就是高度2个像素
            repeat(mLineCount) {
                val x = it * spaceWidth + mLineWidth / 2
                val y = 1 + level
                canvas?.drawLine(x, y, x, 2 * level - y, mPaint)
            }
        } else {
            // 根据正弦公式计算出y值，这里由于要平滑衔接，让每次的偏移量的增量都为固定的dp1，所以将y = a * sin(b * x + c) + d 改为y = a * sin(b * (x + c)) + d
            repeat(mLineCount) {
                val x = it * spaceWidth + mLineWidth / 2
                val y = amplitude * sin(compressionMultiple * (x + offset)) + level
                canvas?.drawLine(x, y, x, 2 * level - y, mPaint)
            }
        }

        // 计算绘制时间
//        val spentTime = System.currentTimeMillis() - startTime
//        if (spentTime < 16) {
//            Thread.sleep(16 - spentTime)
//            Log.d("WaveView", "spentTime: $spentTime")
//        }
    }

    fun updateValue(value: Int) {
        // 防止数值突变
        if (value > this.mValue) {
            this.mValue += 1
        } else {
            this.mValue -= 1
        }
        postInvalidate()
    }

    fun reset() {
        this.mValue = 0
        postInvalidate()
    }
}