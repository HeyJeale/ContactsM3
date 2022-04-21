package edu.zjut.contactsMaterial3Experimental.ui.views

import android.widget.PopupWindow
import android.widget.TextView
import android.util.TypedValue
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.os.VibratorManager
import android.os.VibrationEffect
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import edu.zjut.contactsMaterial3Experimental.R
import java.util.ArrayList

class LetterIndexView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val letterList: MutableList<String> = ArrayList()
    private var paint: Paint? = null
    private var itemHeight // 字母每一项的高度
            = 0f
    private var fontSize = 0
    private var currentPosition = -1
    private var circleRadius = 0f
    private var circleRadiusExtra // 默认的选中时圆形的边距，如果不设置，圆形无法完成包含字母（无需自定义）
            = 0f
    private var circlePadding // 选中时圆形的边距，可自定义
            = 0
    private var circleColor = 0
    private var drawCircleActionUp = false
    private var itemPadding = 0
    private var textColor = 0
    private var textSelectedColor = 0
    private var stateChangeListener: OnStateChangeListener? = null
    private var eventAction = 0
    private var pop: PopupWindow? = null
    private var popTextView: TextView? = null
    private var showLetterPop = true
    private var activityRootView: View? = null
    private fun init(attrs: AttributeSet?) {
        loadDefaultSetting()
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.LetterIndexView)
            val textSize = array.getInteger(R.styleable.LetterIndexView_text_size, 0)
            if (textSize != 0) fontSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                textSize.toFloat(),
                resources.displayMetrics
            ).toInt()
            val textColor = array.getColor(R.styleable.LetterIndexView_text_color, 0)
            if (textColor != 0) this.textColor = textColor
            val textSelectedColor =
                array.getColor(R.styleable.LetterIndexView_text_selected_color, 0)
            if (textSelectedColor != 0) this.textSelectedColor = textSelectedColor
            val circlePadding = array.getDimension(R.styleable.LetterIndexView_circle_padding, 0f)
                .toInt()
            if (circlePadding != 0) this.circlePadding = circlePadding
            val circleColor = array.getColor(R.styleable.LetterIndexView_circle_color, 0)
            if (circleColor != 0) this.circleColor = circleColor
            val itemSpace = array.getDimension(R.styleable.LetterIndexView_item_space, 0f)
                .toInt()
            if (itemSpace != 0) itemPadding = itemSpace / 2
            drawCircleActionUp =
                array.getBoolean(R.styleable.LetterIndexView_draw_circle_action_up, true)
            showLetterPop = array.getBoolean(R.styleable.LetterIndexView_show_pop, true)
            array.recycle()
        }
        paint = Paint()
        paint!!.color = Color.YELLOW
        paint!!.textSize = fontSize.toFloat()
        paint!!.textAlign = Paint.Align.CENTER
        paint!!.isAntiAlias = true
        loadDefaultLetters()
        calculateCircleRadius(true)
        initPopupWindow()
        if (context is Activity) activityRootView = getActivityRootView(context as Activity)
        invalidate()
    }

    /**
     * 默认数据
     */
    private fun loadDefaultSetting() {
        fontSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, resources.displayMetrics)
                .toInt()
        circleRadiusExtra = dp2px(2f).toFloat()
        circlePadding = dp2px(2f)
        itemPadding = dp2px(5f)
        textColor = Color.BLACK
        textSelectedColor = Color.WHITE
        circleColor = Color.RED
    }

    private fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
            .toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        if (heightMode == MeasureSpec.EXACTLY) { // 固定高度
            itemHeight = height / letterList.size
        } else { // 自适应高度
            itemHeight = (paint!!.measureText(letterList[0]).toInt() + itemPadding * 2).toFloat()
            height = itemHeight * letterList.size
        }
        height += (paddingTop + paddingBottom).toFloat()
        val width = (paint!!.measureText(letterList[0]) + paddingLeft + paddingRight).toInt()
        setMeasuredDimension(width, height.toInt())
    }

    private fun calculateCircleRadius(force: Boolean) {
        if (force || circleRadius == 0f) circleRadius =
            paint!!.measureText(letterList[0]) / 2 + circleRadiusExtra + circlePadding
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in letterList.indices) {
            val itemCenterY = i * itemHeight + itemHeight / 2 + paddingTop
            val metrics = paint!!.fontMetrics
            val baseLineY =
                (itemCenterY + (metrics.bottom - metrics.top) / 2 - metrics.bottom).toInt()
            val showCircle = eventAction != MotionEvent.ACTION_UP || drawCircleActionUp
            if (currentPosition == i && showCircle) {
                paint!!.color = circleColor
                canvas.drawCircle(width / 2f, itemCenterY, circleRadius, paint!!)
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(
                    VibrationEffect.createPredefined(
                        VibrationEffect.EFFECT_TICK
                    )
                )
            }
            paint!!.color = if (i == currentPosition && showCircle) textSelectedColor else textColor
            canvas.drawText(letterList[i], width / 2f, baseLineY.toFloat(), paint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventAction = event.action
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (showLetterPop && activityRootView != null) {
                if (pop == null) {
                    initPopupWindow()
                }
                pop!!.showAtLocation(activityRootView, Gravity.CENTER, 0, 0)
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> if (pop != null && pop!!.isShowing) pop!!.dismiss()
        }
        val position = getCurrentPosition(event.y)
        if (currentPosition == position && this.eventAction == eventAction) return true
        currentPosition = position
        this.eventAction = eventAction
        popTextView!!.text = letterList[currentPosition]
        invalidate()
        if (stateChangeListener != null) stateChangeListener!!.onStateChange(
            eventAction,
            currentPosition,
            letterList[currentPosition],
            (itemHeight * currentPosition + itemHeight / 2).toInt() + paddingTop
        )
        return true
    }

    private fun getCurrentPosition(y: Float): Int {
        var y = y
        if (y >= height) return letterList.size - 1
        if (y <= 0) return 0
        y -= paddingTop.toFloat()
        val pos = (y / itemHeight).toInt()
        return Math.min(pos, letterList.size - 1)
    }

    /**
     * 加载默认的26个字母
     */
    private fun loadDefaultLetters() {
        for (i in 0..25) letterList.add(Character.toUpperCase((65 + i).toChar()).toString())
    }

    private fun initPopupWindow() {
        popTextView = createPopTextView()
        pop = PopupWindow(popTextView, dp2px(90f), dp2px(90f))
        pop!!.setBackgroundDrawable(shapeDrawable)
    }

    private fun getActivityRootView(activity: Activity): View? {
        var rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        if (rootView == null) rootView =
            (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        return rootView
    }

    private fun createPopTextView(): TextView {
        val textView = TextView(context)
        val params: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        textView.layoutParams = params
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.WHITE)
        textView.textSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25f, resources.displayMetrics)
        return textView
    }

    /**
     * 创建默认的Shape背景
     */
    private val shapeDrawable: Drawable
        private get() {
            val radius = 20
            val innerRadii = floatArrayOf(
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat()
            )
            val roundRectShape = RoundRectShape(innerRadii, RectF(), innerRadii)
            val drawable = ShapeDrawable(roundRectShape)
            drawable.paint.color = Color.argb(78, 128, 125, 120)
            drawable.paint.style = Paint.Style.FILL
            return drawable
        }

    interface OnStateChangeListener {
        fun onStateChange(eventAction: Int, position: Int, letter: String?, itemCenterY: Int)
    }

    fun setOnStateChangeListener(listener: OnStateChangeListener?): LetterIndexView {
        stateChangeListener = listener
        return this
    }

    /**
     * 当前字母总数
     */
    val letterCount: Int
        get() = letterList.size

    /**
     * 可在字母先后添加额外的字母
     */
    fun addLetter(position: Int, vararg letter: String): LetterIndexView {
        if (letter != null && letter.size > 0) {
            for (l in letter) letterList.add(position, l)
            invalidate()
        }
        return this
    }

    /**
     * 设置字体大小
     *
     * @param size 单位 SP
     */
    fun setFontSize(size: Int): LetterIndexView {
        fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            size.toFloat(),
            resources.displayMetrics
        ).toInt()
        invalidate()
        return this
    }

    /**
     * 设置字体颜色
     */
    fun setTextColor(@ColorInt color: Int): LetterIndexView {
        textColor = color
        invalidate()
        return this
    }

    /**
     * 设置字体选中时的颜色
     */
    fun setTextSelectedColor(@ColorInt color: Int): LetterIndexView {
        textSelectedColor = color
        invalidate()
        return this
    }

    /**
     * 设置选中时的圆形背景颜色，如果不需要圆形背景，可设置完全透明颜色即可
     */
    fun setCircleColor(@ColorInt color: Int): LetterIndexView {
        circleColor = color
        invalidate()
        return this
    }

    /**
     * 设置选中时的圆形的内边距
     */
    fun setCirclePadding(padding: Int): LetterIndexView {
        circlePadding = dp2px(padding.toFloat())
        invalidate()
        return this
    }

    /**
     * 设置两个字母Item之间的距离
     *
     * @param space 单位 DP
     * @return
     */
    fun setItemSpace(space: Int): LetterIndexView {
        itemPadding = dp2px(space.toFloat()) / 2
        invalidate()
        return this
    }

    /**
     * 设置 手势离开屏幕后选中字母是否显示圆形背景，默认 false
     *
     * @param draw
     * @return
     */
    fun setDrawCircleActionUp(draw: Boolean): LetterIndexView {
        drawCircleActionUp = draw
        invalidate()
        return this
    }

    /**
     * 设置 滑动时，是否显示默认的 提示 泡泡窗口，默认 true，不支持自定义，如需自定义 可通过setOnStateChangeListener监听状态做相应的处理
     */
    fun setShowLetterPop(showLetterPop: Boolean): LetterIndexView {
        this.showLetterPop = showLetterPop
        invalidate()
        return this
    }

    init {
        init(attrs)
    }
}