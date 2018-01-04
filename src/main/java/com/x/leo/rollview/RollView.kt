package com.x.leo.rollview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @作者:XLEO
 * @创建日期: 2017/8/11 13:51
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class RollView(context: Context?, attributes: AttributeSet) : ViewPager(context, attributes) {
    var circleColor: Int = 0
    var circleRadius = 0.0f
    var circleFillRadius = 0.0f
    var circleStroke = 0.0f
    private var mCircleBottomPadding: Int = 0
    private var mCircleMargin = 10
    private val STARTROLL: Int = 0x0011
    private val STOPROLL: Int = 0x0012
    private val mhandler: Handler by lazy {
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                if (msg != null) {
                    when (msg.what) {
                        STARTROLL -> {
                            postDelayed({
                                rollToNext()
                            }, 5000)
                        }
                        else -> {
                            throw IllegalArgumentException("illegal message")
                        }
                    }
                }
            }
        }
    }

    private fun stopRoll() {
        mhandler.removeCallbacksAndMessages(null)
    }

    private fun rollToNext() {
        if (adapter != null && adapter.count >= 2) {
            setCurrentItem((currentItem + 1) % adapter.count, true)
            mhandler.sendEmptyMessage(STARTROLL)
        }
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        stopRoll()
        super.setAdapter(adapter)
        mhandler.sendEmptyMessage(STARTROLL)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mhandler.removeCallbacksAndMessages(null)
    }

    private var downIntercepter: Boolean = false
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return super.onInterceptTouchEvent(ev)
    }

    private var downItem = 0
    private var downTime: Long = 0
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    stopRoll()
                    downIntercepter = true
                    downTime = SystemClock.uptimeMillis()
                    downItem = currentItem
                }
                MotionEvent.ACTION_UP -> {
                    mhandler.sendEmptyMessage(STARTROLL)
                    downIntercepter = false
                    if (currentItem == downItem && SystemClock.uptimeMillis() - downTime <=100) {
                        downTime = 0
                        doClick()
                    }
                }
                else -> {
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    /**
     * 只保存3个子view（前一个，当前，下一个）
     */
    private fun doClick() {
        when (currentItem) {
            0 -> {
                getChildAt(0).performClick()
            }
            adapter.count - 1->{
                getChildAt(1).performClick()
            }
            else -> {
                getChildAt(1).performClick()
            }
        }
    }

    val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    init {
        val attrs = context!!.obtainStyledAttributes(attributes, R.styleable.RollView)
        circleColor = attrs.getColor(R.styleable.RollView_circleColor, Color.WHITE)
        circleFillRadius = attrs.getDimension(R.styleable.RollView_circleFilledRadius, 50f)
        circleRadius = attrs.getDimension(R.styleable.RollView_circleRadius, 50f)
        circleStroke = attrs.getDimension(R.styleable.RollView_circleStroke, 10f)
        mCircleBottomPadding = attrs.getDimension(R.styleable.RollView_circleToBottom, 0f).toInt()
        mCircleMargin = attrs.getDimension(R.styleable.RollView_circleMargin, 10f).toInt()
        paint.color = circleColor
        paint.strokeWidth = circleStroke

    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        if (adapter != null && adapter.count >= 2) {
            drawCircles(canvas)
        }
    }

    private fun drawCircles(canvas: Canvas?) {
        if (canvas == null || adapter == null || adapter.count <= 0) {
            return
        }
        var i = 0
        while (i < adapter.count) {
            val center: PointF = calcCenter(i, adapter.count)
            if (currentItem != i) {
                paint.style = Paint.Style.FILL
                canvas!!.drawCircle(center.x + scrollX, center.y, circleFillRadius, paint)
            } else {
                paint.style = Paint.Style.STROKE
                canvas!!.drawCircle(center.x + scrollX, center.y, circleRadius, paint)
            }
            i++
        }
    }


    val lineStartY: Float by lazy<Float> {
        height - mCircleBottomPadding - if (circleFillRadius > circleRadius) {
            circleFillRadius
        } else {
            circleRadius
        }
    }
    val lineStartX by lazy<Float> {
        if (adapter == null || adapter.count <= 0) {
            (width / 2).toFloat()
        } else {
            width / 2 - (2 * (circleFillRadius * (adapter.count - 1) + circleRadius) + (adapter.count - 1) * mCircleMargin) / 2
        }
    }

    private fun calcCenter(i: Int, count: Int): PointF {
        var circlex = 0f
        var circley = lineStartY
        if (i == 0) {
            if (i == currentItem) {
                circlex = lineStartX + circleRadius
            } else {
                circlex = lineStartX + circleFillRadius
            }
        } else {
            if (i > currentItem) {
                circlex = lineStartX + circleRadius * 2 + circleFillRadius * (2 * i - 1) + mCircleMargin * i
            } else if (i == currentItem) {
                circlex = lineStartX + circleRadius + 2 * i * circleFillRadius + mCircleMargin * i
            } else {
                circlex = lineStartX + (2 * i + 1) * circleFillRadius + mCircleMargin * i
            }
        }
        return PointF(circlex, circley)
    }
}

class RollViewAdapter<T>(val ctx: Context, val datas: ArrayList<T>) : PagerAdapter() {
    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val imageView = ImageView(ctx)
        val layoutParams = ViewPager.LayoutParams()
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        imageView.layoutParams = layoutParams
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        if (datas[position] is Int) {
            imageView.setImageResource(
                    datas[position] as Int
            )
        } else if (datas[position] is String) {
            Glide.with(ctx).load(datas[position] as String).into(imageView)
        } else {
            throw IllegalArgumentException("not supported argument type")
        }
        imageView.onClick {
            if (listener != null) {
                listener!!.onItemClick(imageView, position)
            }
        }
        container!!.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container!!.removeView(`object`!! as View)
    }

    override fun getCount(): Int {
        return datas.size
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(l: OnItemClickListener) {
        listener = l
    }
}

interface OnItemClickListener {
    fun onItemClick(view: View, position: Int)
}