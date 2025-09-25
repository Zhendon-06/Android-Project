package com.guet.stu.banamusic.view.manager

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import android.view.animation.PathInterpolator
import com.guet.stu.banamusic.R

/**
 * 导航栏布局切换管理器
 * 负责管理完整导航和紧凑导航之间的切换动画
 */
class NavigationManager(
    private val container: ViewGroup,
    private val layoutInflater: android.view.LayoutInflater
) {
    
    private var isSlideNav = false
    private var isAnimatingSwap = false
    
    // 公共属性，用于外部访问当前导航状态
    val isSlideNavigation: Boolean get() = isSlideNav
    
    // 回调接口
    interface NavigationCallback {
        fun onNavigationSwapped(newView: View)
        fun onApplySlideNavIcon(root: View)
        fun onSyncPlayButtonIcons()
        fun getPlayButtonManager(): PlayButtonManager
    }
    
    private var callback: NavigationCallback? = null
    
    fun setCallback(callback: NavigationCallback) {
        this.callback = callback
    }
    
    /**
     * 切换到紧凑导航或完整导航
     * @param slide true为紧凑导航，false为完整导航
     */
    fun switchToSlideNav(slide: Boolean) {
        if (slide == isSlideNav || isAnimatingSwap) return
        isSlideNav = slide
        isAnimatingSwap = true

        val oldView = container.getChildAt(0)
        val newLayout = if (slide) R.layout.slide_navigation else R.layout.navigation
        val newView = layoutInflater.inflate(newLayout, container, false)
        newView.visibility = View.INVISIBLE
        newView.alpha = 0f
        container.addView(newView)

        fun View.findRectInContainer(containerView: View): android.graphics.Rect {
            val vLoc = IntArray(2)
            val cLoc = IntArray(2)
            getLocationOnScreen(vLoc)
            containerView.getLocationOnScreen(cLoc)
            val left = vLoc[0] - cLoc[0]
            val top = vLoc[1] - cLoc[1]
            return android.graphics.Rect(left, top, left + width, top + height)
        }

        // 等待新视图测量完成，计算三元素的目标位置
        newView.post {
            val curMusic = oldView?.findViewById<View>(R.id.music_bar)
            val curNav = (oldView?.findViewById<View>(R.id.nav_blur)?.parent as? View)
            val curSearch = oldView?.findViewById<View>(R.id.search)

            val tgtMusic = newView.findViewById<View>(R.id.music_bar)
            val tgtNav = newView.findViewById<View>(R.id.slide_nav) ?: newView.findViewById(R.id.nav_all_container)
            val tgtSearch = newView.findViewById<View>(R.id.slide_search) ?: newView.findViewById(R.id.search)

            // 根据当前选中的导航，设置 slide_nav 中图标
            callback?.onApplySlideNavIcon(newView)
            
            // 在动画开始前就设置正确的播放按钮状态，避免闪现
            val tgtPlayButton = tgtMusic?.findViewById<android.widget.ImageView>(R.id.iv_play)
            val playButtonManager = callback?.getPlayButtonManager()
            val isPlaying = playButtonManager?.isPlaying() ?: false
            tgtPlayButton?.setImageResource(if (isPlaying) R.drawable.stop else R.drawable.play)
            
            // 同步所有播放按钮状态，确保一致性
            callback?.onSyncPlayButtonIcons()

            // 让新布局先可见，但将需要淡入的目标控件先置为透明，避免整块突然出现
            newView.visibility = View.VISIBLE
            newView.alpha = 1f
            tgtNav?.apply { alpha = 0f; scaleX = 0.92f; scaleY = 0.92f }
            tgtSearch?.apply { alpha = 0f; scaleX = 0.92f; scaleY = 0.92f }
            if (slide) {
                // 为避免闪现，目标 musicbar 同步做渐显，和旧 musicbar 的位移缩放重叠进行
                tgtMusic?.alpha = 0f
                tgtMusic?.animate()?.alpha(1f)?.setDuration(320)?.setInterpolator(FastOutSlowInInterpolator())?.start()
            }

            fun animToTarget(view: View?, target: View?, end: ()->Unit) {
                if (view == null || target == null) { end(); return }
                val startRect = view.findRectInContainer(container)
                val endRect = target.findRectInContainer(container)
                val transX = (endRect.centerX() - startRect.centerX()).toFloat()
                val transY = (endRect.centerY() - startRect.centerY()).toFloat().coerceAtLeast(0f)
                val scaleX = if (startRect.width() != 0) endRect.width().toFloat() / startRect.width() else 1f
                val scaleY = if (startRect.height() != 0) endRect.height().toFloat() / startRect.height() else 1f
                view.pivotX = (view.width / 2f)
                view.pivotY = (view.height / 2f)
                // 对 music_bar 采用"从上方平移+左右收缩"的苹果风惯性曲线
                if (view.id == R.id.music_bar) {
                    val startTX = view.translationX
                    val startTY = view.translationY
                    val startSX = view.scaleX.takeIf { it != 0f } ?: 1f
                    val startSY = view.scaleY.takeIf { it != 0f } ?: 1f
                    // iOS-like 弹性贝塞尔曲线（接近 UIKit spring 的感觉）
                    val iosInterpolator = PathInterpolator(0.18f, 0.9f, 0.2f, 1f)
                    // 开启硬件层以避免绘制抖动
                    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    ValueAnimator.ofFloat(0f, 1f).apply {
                        duration = 320
                        interpolator = iosInterpolator
                        addUpdateListener { a ->
                            val f = a.animatedValue as Float
                            view.translationX = startTX + f * transX
                            view.translationY = startTY + f * transY
                            if (slide) {
                                // slide 方向：只做左右收缩（scaleX），高度保持
                                view.scaleX = startSX + f * (scaleX - startSX)
                                view.scaleY = startSY
                            } else {
                                // 还原方向：等比还原
                                view.scaleX = startSX + f * (scaleX - startSX)
                                view.scaleY = startSY + f * (scaleY - startSY)
                            }
                        }
                        addListener(object : android.animation.AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: android.animation.Animator) {
                                // 关闭硬件层
                                view.setLayerType(View.LAYER_TYPE_NONE, null)
                                end()
                            }
                        })
                    }.start()
                } else {
                    view.animate().translationX(transX).translationY(transY).scaleX(scaleX).scaleY(scaleY)
                        .setDuration(260).setInterpolator(FastOutSlowInInterpolator()).withEndAction { end() }.start()
                }
            }

            var finished = 0
            fun oneDone() {
                finished++
                if (finished >= 3) {
                    container.removeView(oldView)
                    // 最后确保事件与状态
                    newView.animate().alpha(1f).setDuration(120).withEndAction {
                        // slide 方向：动画完成后让目标 musicbar 出现
                        if (slide) {
                            val tgtMusic2 = newView.findViewById<View>(R.id.music_bar)
                            tgtMusic2?.animate()?.alpha(1f)?.setDuration(120)?.start()
                        }
                        callback?.onNavigationSwapped(newView)
                        isAnimatingSwap = false
                    }.start()
                }
            }

            animToTarget(curMusic, tgtMusic, ::oneDone)
            animToTarget(curNav, tgtNav, ::oneDone)
            animToTarget(curSearch, tgtSearch, ::oneDone)

            // 同步淡入新控件，实现交叉淡入效果 + 轻微放大
            tgtNav?.animate()?.alpha(1f)?.scaleX(1f)?.scaleY(1f)?.setDuration(260)?.setInterpolator(FastOutSlowInInterpolator())?.start()
            tgtSearch?.animate()?.alpha(1f)?.scaleX(1f)?.scaleY(1f)?.setDuration(260)?.setInterpolator(FastOutSlowInInterpolator())?.start()
        }
    }
}
