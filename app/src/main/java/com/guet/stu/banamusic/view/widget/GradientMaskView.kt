package com.guet.stu.banamusic.view.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils

/**
 * 渐变蒙版View
 * 
 * 功能：
 * 1. 在RecyclerView的上下边缘创建渐变蒙版效果
 * 2. 根据背景颜色动态生成自然的渐变效果
 * 3. 支持自定义渐变高度和透明度
 * 
 * 实现原理：
 * - 使用LinearGradient创建线性渐变
 * - 根据背景颜色计算渐变起始和结束颜色
 * - 通过Shader绘制渐变效果
 */
class GradientMaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 渐变相关属性
    private var gradientHeight = 80f  // 渐变高度（dp）
    private var gradientAlpha = 0.8f  // 渐变透明度
    private var backgroundColor = Color.TRANSPARENT  // 背景颜色
    
    // 绘制相关
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var topGradient: LinearGradient? = null
    private var bottomGradient: LinearGradient? = null
    
    // 渐变区域
    private val topRect = RectF()
    private val bottomRect = RectF()

    init {
        // 设置View为透明背景，不阻挡触摸事件
        setBackgroundColor(Color.TRANSPARENT)
    }

    /**
     * 设置背景颜色并重新计算渐变
     * 
     * @param color 背景颜色
     */
    fun setMaskBackgroundColor(color: Int) {
        this.backgroundColor = color
        calculateGradients()
        invalidate()
    }

    /**
     * 设置渐变高度
     * 
     * @param heightDp 渐变高度（dp）
     */
    fun setGradientHeight(heightDp: Float) {
        this.gradientHeight = heightDp
        calculateGradients()
        invalidate()
    }

    /**
     * 设置渐变透明度
     * 
     * @param alpha 透明度（0.0-1.0）
     */
    fun setGradientAlpha(alpha: Float) {
        this.gradientAlpha = alpha.coerceIn(0f, 1f)
        calculateGradients()
        invalidate()
    }

    /**
     * 计算渐变颜色和Shader
     * 
     * 根据背景颜色生成自然的渐变效果：
     * 1. 提取背景颜色的RGB值
     * 2. 计算渐变起始颜色（背景颜色 + 透明度）
     * 3. 计算渐变结束颜色（完全透明）
     * 
     * 渐变方向：
     * - 顶部：从边缘（深色）向中心（透明）
     * - 底部：从边缘（完全不透明）向中心（透明）
     */
    private fun calculateGradients() {
        val gradientHeightPx = dpToPx(gradientHeight)
        
        // 计算渐变颜色
        val topStartColor = ColorUtils.setAlphaComponent(backgroundColor, (255 * gradientAlpha).toInt())  // 顶部边缘深色
        val bottomStartColor = ColorUtils.setAlphaComponent(backgroundColor, 255)  // 底部边缘完全不透明
        val endColor = ColorUtils.setAlphaComponent(backgroundColor, 0)  // 中心透明
        
        // 创建顶部渐变（从上到下：边缘深色 → 中心透明）
        // 使用多个颜色停止点，让过渡更平滑
        topGradient = LinearGradient(
            0f, 0f,
            0f, gradientHeightPx,
            intArrayOf(topStartColor, topStartColor, endColor),
            floatArrayOf(0f, 0.3f, 1f),  // 前30%保持深色，后70%逐渐透明
            Shader.TileMode.CLAMP
        )
        
        // 创建底部渐变（从下到上：边缘完全不透明 → 中心透明）
        // 使用多个颜色停止点，让过渡更平滑
        bottomGradient = LinearGradient(
            0f, height - gradientHeightPx,
            0f, height.toFloat(),
            intArrayOf(endColor, bottomStartColor, bottomStartColor),
            floatArrayOf(0f, 0.7f, 1f),  // 前70%逐渐透明，后30%保持深色
            Shader.TileMode.CLAMP
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // 更新渐变区域
        val gradientHeightPx = dpToPx(gradientHeight)
        topRect.set(0f, 0f, w.toFloat(), gradientHeightPx)
        bottomRect.set(0f, h - gradientHeightPx, w.toFloat(), h.toFloat())
        
        // 重新计算渐变
        calculateGradients()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (width == 0 || height == 0) return
        
        // 绘制顶部渐变
        topGradient?.let { gradient ->
            paint.shader = gradient
            canvas.drawRect(topRect, paint)
        }
        
        // 绘制底部渐变
        bottomGradient?.let { gradient ->
            paint.shader = gradient
            canvas.drawRect(bottomRect, paint)
        }
        
        // 清除shader
        paint.shader = null
    }

    /**
     * dp转px单位转换
     */
    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * 静态方法：创建渐变蒙版View
     * 
     * @param context 上下文
     * @param backgroundColor 背景颜色
     * @param gradientHeightDp 渐变高度（dp）
     * @param gradientAlpha 渐变透明度
     * @return 配置好的GradientMaskView
     */
    companion object {
        fun create(
            context: Context,
            backgroundColor: Int,
            gradientHeightDp: Float = 80f,
            gradientAlpha: Float = 0.8f
        ): GradientMaskView {
            return GradientMaskView(context).apply {
                setMaskBackgroundColor(backgroundColor)
                setGradientHeight(gradientHeightDp)
                setGradientAlpha(gradientAlpha)
            }
        }
    }
}
