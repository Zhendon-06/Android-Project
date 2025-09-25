package com.guet.stu.banamusic.view.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guet.stu.banamusic.R
import com.guet.stu.banamusic.view.adapter.LyricAdapter
import com.guet.stu.banamusic.view.widget.GradientMaskView

/**
 * 音乐播放器Fragment
 * 
 * 功能特性：
 * 1. 动态切换唱片和歌词视图
 * 2. 点击唱片区域显示歌词，点击歌词区域切换回唱片
 * 3. 整个页面支持下滑关闭（仅在显示唱片时）
 * 4. 智能触摸事件处理，区分点击和滑动操作
 * 
 * 实现原理：
 * - 使用动态布局切换：通过LayoutInflater动态加载不同的布局文件
 * - 统一触摸事件处理：一个监听器处理所有触摸事件，避免冲突
 * - 精确区域检测：通过坐标计算判断点击是否在指定区域内
 */
class MusicPlayerFragment : Fragment() {

    // ==================== 触摸事件相关变量 ====================
    private var initialY = 0f   // 初始触摸Y坐标
    private var initialX = 0f   // 初始触摸X坐标
    private var isDragging = false  // 是否正在拖拽
    private var hasMoved = false   // 是否已经移动过（用于区分点击和滑动）
    
    // ==================== 视图状态变量 ====================
    private var isShowingLyrics = false  // 当前是否显示歌词视图
    
    // ==================== 视图引用 ====================
    private lateinit var containerDiscLyrics: ViewGroup  // 唱片/歌词容器
    private lateinit var musicPlayerSheet: View          // 音乐播放器主容器
    private var currentView: View? = null                // 当前显示的视图
    private var gradientMaskView: GradientMaskView? = null  // 渐变蒙版视图

    companion object {
        private const val DRAG_THRESHOLD_DP = 25    // 拖拽触发阈值（dp）
        private const val ANIM_DURATION = 200L      // 动画时长（ms）
    }

    /**
     * 创建Fragment视图
     * 
     * 初始化流程：
     * 1. 加载主布局文件
     * 2. 初始化视图引用
     * 3. 设置返回按钮
     * 4. 默认显示唱片视图
     * 5. 设置统一的触摸事件处理器
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_music_player, container, false)

        // 初始化视图引用
        initViews(view)

        // 设置返回按钮点击事件
        view.findViewById<View>(R.id.btn_back)?.setOnClickListener { hideSelf() }

        // 初始化默认显示唱片视图
        showDiscView()

        // 为整个页面设置统一的触摸事件处理器
        musicPlayerSheet = view.findViewById<View>(R.id.music_player_sheet)
        setupUnifiedTouchHandler(view)

        return view
    }

    /**
     * 初始化视图引用
     * 获取container_disc_lyrics容器的引用，用于动态切换内容
     */
    private fun initViews(view: View) {
        containerDiscLyrics = view.findViewById(R.id.container_disc_lyrics)
    }

    /**
     * 显示唱片视图
     * 
     * 实现步骤：
     * 1. 清除容器中的所有子视图
     * 2. 动态加载layout_disc.xml布局
     * 3. 将新布局添加到容器中
     * 4. 移除渐变蒙版（如果存在）
     * 5. 更新状态变量
     */
    private fun showDiscView() {
        // 清除当前视图
        containerDiscLyrics.removeAllViews()
        
        // 动态加载唱片布局文件
        val discView = LayoutInflater.from(context).inflate(R.layout.layout_disc, containerDiscLyrics, false)
        containerDiscLyrics.addView(discView)
        currentView = discView
        
        // 移除渐变蒙版
        removeGradientMask()
        
        // 更新状态：当前显示唱片
        isShowingLyrics = false
    }

    /**
     * 显示歌词视图
     * 
     * 实现步骤：
     * 1. 清除容器中的所有子视图
     * 2. 动态加载layout_lyrics.xml布局
     * 3. 将新布局添加到容器中
     * 4. 设置RecyclerView和适配器
     * 5. 添加渐变蒙版效果
     * 6. 更新状态变量
     */
    private fun showLyricsView() {
        // 清除当前视图
        containerDiscLyrics.removeAllViews()
        
        // 动态加载歌词布局文件
        val lyricsView = LayoutInflater.from(context).inflate(R.layout.layout_lyrics, containerDiscLyrics, false)
        containerDiscLyrics.addView(lyricsView)
        currentView = lyricsView
        
        // 设置RecyclerView和适配器
        setupLyricsRecyclerView(lyricsView.findViewById(R.id.rv_lyrics))
        
        // 添加渐变蒙版效果
        addGradientMask()
        
        // 更新状态：当前显示歌词
        isShowingLyrics = true
    }


    /**
     * 设置歌词RecyclerView
     * 
     * 功能：
     * 1. 创建模拟歌词数据
     * 2. 创建LyricAdapter适配器
     * 3. 设置点击回调：点击任意歌词项切换回唱片
     * 4. 配置RecyclerView的布局管理器和适配器
     */
    private fun setupLyricsRecyclerView(rvLyrics: RecyclerView) {
        // 模拟歌词数据
        val lyrics = listOf(
            "执",
            "演唱：阿YueYue",
            "",
            "执念如影随形",
            "挥之不去",
            "心中有你",
            "便有了执念",
            "",
            "执念如影随形",
            "挥之不去",
            "心中有你",
            "便有了执念",
            "",
            "执念如影随形",
            "挥之不去",
            "心中有你",
            "便有了执念"
        )

        // 创建适配器，设置点击回调
        val adapter = LyricAdapter(lyrics) {
            // 点击歌词项时切换回唱片
            toggleView()
        }

        // 设置RecyclerView的布局管理器和适配器
        rvLyrics.layoutManager = LinearLayoutManager(context)
        rvLyrics.adapter = adapter
    }

    /**
     * 切换视图显示
     * 
     * 根据当前状态决定显示唱片还是歌词
     * - 当前显示歌词 → 切换到唱片
     * - 当前显示唱片 → 切换到歌词
     */
    private fun toggleView() {
        if (isShowingLyrics) {
            showDiscView()
        } else {
            showLyricsView()
        }
    }

    /**
     * 设置统一的触摸事件处理器
     * 
     * 核心功能：
     * 1. 智能区分点击、滑动和拖拽操作
     * 2. 处理视图切换和滑动关闭
     * 3. 精确的区域检测
     * 
     * 事件判断逻辑：
     * - 移动距离 < 10dp：点击操作
     * - 移动距离 > 25dp 且向下：拖拽关闭操作
     * - 其他：滑动操作（无特殊处理）
     * 
     * 区域检测：
     * - 通过getLocationOnScreen()获取container_disc_lyrics的屏幕坐标
     * - 判断触摸点是否在该区域内
     * - 只有在指定区域内的点击才触发视图切换
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupUnifiedTouchHandler(rootView: View) {
        val dragThresholdPx = dpToPx(DRAG_THRESHOLD_DP)  // 拖拽阈值：25dp
        val clickThresholdPx = dpToPx(10)               // 点击阈值：10dp
        
        rootView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 记录初始触摸位置
                    initialY = event.rawY
                    initialX = event.rawX
                    isDragging = false
                    hasMoved = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dy = event.rawY - initialY
                    val dx = event.rawX - initialX
                    val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                    
                    // 如果移动距离超过点击阈值，标记为已移动
                    if (distance > clickThresholdPx) {
                        hasMoved = true
                    }
                    
                    // 如果移动距离超过拖拽阈值且向下，开始拖拽
                    // 注意：只在显示唱片时允许拖拽关闭
                    if (distance > dragThresholdPx && dy > 0 && !isShowingLyrics) {
                        isDragging = true
                    }
                    
                    // 如果是向下拖拽，跟随手指移动
                    if (isDragging && dy > 0) {
                        musicPlayerSheet.translationY = dy
                    }
                }

                MotionEvent.ACTION_UP -> {
                    val dy = event.rawY - initialY
                    
                    if (isDragging) {
                        // 处理拖拽关闭
                        if (dy > musicPlayerSheet.height / 4) {
                            // 下拉超过 1/4 高度 → 关闭Fragment
                            animateSheet(musicPlayerSheet, musicPlayerSheet.height.toFloat()) { hideSelf() }
                        } else {
                            // 回弹到原位置
                            animateSheet(musicPlayerSheet, 0f)
                        }
                    } else if (!hasMoved) {
                        // 处理点击事件 - 检查是否点击在container_disc_lyrics区域
                        val location = IntArray(2)
                        containerDiscLyrics.getLocationOnScreen(location)
                        val x = event.rawX
                        val y = event.rawY
                        
                        // 判断点击是否在container_disc_lyrics区域内
                        if (x >= location[0] && x <= location[0] + containerDiscLyrics.width &&
                            y >= location[1] && y <= location[1] + containerDiscLyrics.height) {
                            // 点击在指定区域内，切换视图
                            toggleView()
                        }
                    }
                    
                    // 重置状态变量
                    isDragging = false
                    hasMoved = false
                }
            }
            true // 返回true，表示事件已被处理
        }
    }

    /**
     * 执行视图动画
     * 
     * @param view 要执行动画的视图
     * @param targetY 目标Y坐标
     * @param endAction 动画结束后的回调函数
     */
    private fun animateSheet(view: View, targetY: Float, endAction: (() -> Unit)? = null) {
        view.animate()
            .translationY(targetY)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(ANIM_DURATION)
            .withEndAction { endAction?.invoke() }
            .start()
    }

    /**
     * dp转px单位转换
     * 
     * @param dp dp值
     * @return 对应的px值
     */
    private fun dpToPx(dp: Int): Float {
        val density = resources.displayMetrics.density
        return dp * density
    }
    
    /**
     * 添加渐变蒙版效果
     * 
     * 功能：
     * 1. 获取container_disc_lyrics的背景颜色
     * 2. 创建GradientMaskView并设置参数
     * 3. 将蒙版添加到容器的最上层
     * 4. 确保蒙版不阻挡触摸事件
     */
    private fun addGradientMask() {
        // 移除已存在的蒙版
        removeGradientMask()
        
        // 获取背景颜色
        val backgroundColor = getContainerBackgroundColor()
        
        // 创建渐变蒙版View
        gradientMaskView = GradientMaskView.create(
            context = requireContext(),
            backgroundColor = backgroundColor,
            gradientHeightDp = 150f,  // 渐变高度150dp，大幅增加渐变范围，使过渡更平滑
            gradientAlpha = 0.8f      // 渐变透明度80%，稍微提高让过渡更自然
        )
        
        // 设置蒙版不阻挡触摸事件
        gradientMaskView?.isClickable = false
        gradientMaskView?.isFocusable = false
        
        // 添加到容器的最上层
        containerDiscLyrics.addView(gradientMaskView)
    }
    
    /**
     * 移除渐变蒙版
     */
    private fun removeGradientMask() {
        gradientMaskView?.let { maskView ->
            containerDiscLyrics.removeView(maskView)
            gradientMaskView = null
        }
    }
    
    /**
     * 获取container_disc_lyrics的背景颜色
     * 
     * 实现步骤：
     * 1. 尝试从背景drawable中获取颜色
     * 2. 如果无法获取，尝试从父容器获取背景颜色
     * 3. 如果仍然无法获取，使用当前主题的背景颜色
     * 4. 返回适合渐变的背景颜色
     * 
     * @return 背景颜色值
     */
    private fun getContainerBackgroundColor(): Int {
        return try {
            // 方法1: 尝试获取container_disc_lyrics的背景颜色
            val background = containerDiscLyrics.background
            if (background is android.graphics.drawable.ColorDrawable) {
                return background.color
            }
            
            // 方法2: 尝试从父容器获取背景颜色
            val parentBackground = musicPlayerSheet.background
            if (parentBackground is android.graphics.drawable.ColorDrawable) {
                return parentBackground.color
            }
            
            // 方法3: 尝试从根布局获取背景颜色
            val rootView = view
            if (rootView != null) {
                val rootBackground = rootView.background
                if (rootBackground is android.graphics.drawable.ColorDrawable) {
                    return rootBackground.color
                }
            }
            
            // 方法4: 使用当前主题的背景颜色
            val typedArray = requireContext().theme.obtainStyledAttributes(intArrayOf(android.R.attr.colorBackground))
            val backgroundColor = typedArray.getColor(0, Color.WHITE)
            typedArray.recycle()
            backgroundColor
            
        } catch (e: Exception) {
            // 异常情况下使用白色作为默认颜色
            Color.WHITE
        }
    }

    /**
     * 通知Activity隐藏音乐播放器
     * 通过回调到MainActivity来关闭当前Fragment
     */
    private fun hideSelf() {
        (activity as? MainActivity)?.hideMusicPlayer()
    }
}