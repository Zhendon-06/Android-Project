package com.guet.stu.banamusic.view.manager

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.guet.stu.banamusic.R

/**
 * 导航动画管理器
 * 负责管理导航选择动画、滚动监听和导航事件绑定
 */
class NavigationAnimator(
    private val activity: android.app.Activity,
    private val fragmentManager: FragmentManager,
    private val playButtonManager: PlayButtonManager
) {
    
    private var navigationManager: NavigationManager? = null
    
    private var floatingNavigationContainer: View? = null
    private var selectedNavBackground: View? = null
    
    /**
     * 设置导航管理器引用
     */
    fun setNavigationManager(navigationManager: NavigationManager) {
        this.navigationManager = navigationManager
    }
    
    /**
     * 设置导航容器引用
     */
    fun setNavigationContainers(floatingContainer: View, selectedBackground: View) {
        this.floatingNavigationContainer = floatingContainer
        this.selectedNavBackground = selectedBackground
    }
    
    /**
     * 更新导航图标的选中状态
     */
    fun updateNavSelection(selectedId: Int) {
        val homeIcon = activity.findViewById<ImageView>(R.id.home_icon)
        val libIcon = activity.findViewById<ImageView>(R.id.music_icon)
        val profileIcon = activity.findViewById<ImageView>(R.id.profile_icon)
        
        // 全部判断一遍，只会保留一个当前的为true，其他的会变成false
        homeIcon.isSelected = (selectedId == R.id.home_layout)
        libIcon.isSelected = (selectedId == R.id.lib_layout)
        profileIcon.isSelected = (selectedId == R.id.profile_layout)
        
        // 移动选中背景到对应的导航项
        animateSelectedBackground(selectedId)
    }
    
    /**
     * 选中背景平移动画
     */
    private fun animateSelectedBackground(selectedId: Int) {
        selectedNavBackground?.post {
            val targetView = when (selectedId) {
                R.id.home_layout -> activity.findViewById<View>(R.id.home_layout)
                R.id.lib_layout -> activity.findViewById<View>(R.id.lib_layout)
                R.id.profile_layout -> activity.findViewById<View>(R.id.profile_layout)
                else -> activity.findViewById<View>(R.id.home_layout)
            }
            
            // 获取 targetView 的父级 LinearLayout
            val linearLayout = targetView.parent as View
            
            // 计算 targetView 相对于 MaterialCardView 的绝对左侧位置和宽度
            val targetViewAbsoluteLeft = linearLayout.left + targetView.left
            val targetViewWidth = targetView.width
            
            // 获取 selectedNavBackground 相对于 MaterialCardView 的绝对左侧位置和宽度
            val selectedNavBgAbsoluteLeft = selectedNavBackground!!.left
            val selectedNavBgWidth = selectedNavBackground!!.width
            
            // 计算 selectedNavBackground 居中覆盖 targetView 时所需的绝对左侧位置
            val desiredBgAbsoluteLeft = targetViewAbsoluteLeft + (targetViewWidth - selectedNavBgWidth) / 2f
            
            // 计算需要应用的 translationX (相对于其初始布局位置)
            val finalTranslationX = desiredBgAbsoluteLeft - selectedNavBgAbsoluteLeft
            
            // 执行平移动画
            selectedNavBackground!!.animate()
                .translationX(finalTranslationX.toFloat())
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }
    
    /**
     * 底部导航栏显示/隐藏动画
     */
    fun animateNav(visible: Boolean) {
        if (visible) {
            floatingNavigationContainer?.visibility = View.VISIBLE
            floatingNavigationContainer?.animate()
                ?.translationY(0f)?.alpha(1f)
                ?.setDuration(300)?.setInterpolator(DecelerateInterpolator())
                ?.start()
        } else {
            floatingNavigationContainer?.animate()
                ?.translationY(floatingNavigationContainer!!.height.toFloat())
                ?.alpha(0f)?.setDuration(300)
                ?.setInterpolator(DecelerateInterpolator())
                ?.withEndAction { floatingNavigationContainer?.visibility = View.GONE }
                ?.start()
        }
    }
    
    /**
     * 绑定导航事件
     */
    fun bindNavigationEvents() {
        val nav = floatingNavigationContainer ?: return
        
        // 导航按钮点击事件
        nav.findViewById<View>(R.id.home_layout)?.setOnClickListener {
            fragmentManager.switchToNavigation(R.id.home_layout)
            updateNavSelection(R.id.home_layout)
        }
        
        nav.findViewById<View>(R.id.lib_layout)?.setOnClickListener {
            fragmentManager.switchToNavigation(R.id.lib_layout)
            updateNavSelection(R.id.lib_layout)
        }
        
        nav.findViewById<View>(R.id.profile_layout)?.setOnClickListener {
            fragmentManager.switchToNavigation(R.id.profile_layout)
            updateNavSelection(R.id.profile_layout)
        }
        
        // 搜索按钮点击事件
        (nav.findViewById<View>(R.id.slide_search) ?: nav.findViewById<View>(R.id.search))?.setOnClickListener {
            activity.startActivity(android.content.Intent(activity, com.guet.stu.banamusic.view.activity.SearchPage::class.java))
        }
        
        // 音乐条点击事件
        nav.findViewById<View>(R.id.music_bar)?.setOnClickListener { 
            // 这里需要回调到Activity来显示音乐播放器
            if (activity is com.guet.stu.banamusic.view.activity.MainActivity) {
                activity.showMusicPlayer()
            }
        }
        
        // 播放按钮点击事件
        nav.findViewById<ImageView>(R.id.iv_play)?.setOnClickListener { 
            playButtonManager.togglePlayPause(activity.window.decorView)
        }
    }
    
    /**
     * 监听滚动，上滑->紧凑导航，下滑->完整导航
     */
    fun attachAutoHideOnScroll(scrollable: View) {
        when (scrollable) {
            is RecyclerView -> {
                scrollable.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy > 8) navigationManager?.switchToSlideNav(true) else if (dy < -8) navigationManager?.switchToSlideNav(false)
                    }
                })
            }
            is NestedScrollView -> {
                scrollable.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
                    val dy = scrollY - oldScrollY
                    if (dy > 8) navigationManager?.switchToSlideNav(true) else if (dy < -8) navigationManager?.switchToSlideNav(false)
                }
            }
            else -> {
                try {
                    val method = scrollable::class.java.getMethod(
                        "setOnScrollChangeListener",
                        View.OnScrollChangeListener::class.java
                    )
                    method.invoke(scrollable, View.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                        val dy = scrollY - oldScrollY
                        if (dy > 8) navigationManager?.switchToSlideNav(true) else if (dy < -8) navigationManager?.switchToSlideNav(false)
                    })
                } catch (_: Exception) { }
            }
        }
    }
    
    /**
     * 应用滑动导航图标
     */
    fun applySlideNavIcon(root: View) {
        val slideNavContainer = root.findViewById<View>(R.id.slide_nav) ?: return
        val icon = slideNavContainer.findViewById<ImageView>(R.id.search_icon) ?: return
        val iconRes = when (fragmentManager.getCurrentSelectedId()) {
            R.id.home_layout -> R.drawable.ic_home
            R.id.lib_layout -> R.drawable.ic_music
            R.id.profile_layout -> R.drawable.ic_profile
            else -> R.drawable.ic_home
        }
        icon.setImageResource(iconRes)
    }
}
