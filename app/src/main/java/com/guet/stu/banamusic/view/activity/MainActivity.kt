/**
 * 主Activity - 应用的核心控制器
 * 
 * 功能描述：
 * - 作为应用的入口点和主控制器
 * - 管理底部导航栏的显示和切换
 * - 协调各个管理类的工作
 * - 处理Fragment的切换和状态管理
 * - 提供音乐播放器的显示/隐藏控制
 * - 实现滚动时导航栏的自动隐藏/显示
 * 
 * 架构设计：
 * - 采用管理器模式，将不同功能分离到专门的管理类
 * - NavigationManager：导航栏管理
 * - FragmentManager：Fragment切换管理
 * - PlayButtonManager：播放按钮管理
 * - NavigationAnimator：导航动画管理
 * - MusicPlayerManager：音乐播放器管理
 * 
 * 主要特性：
 * - 支持两种导航模式：完整导航和紧凑导航
 * - 响应式设计，适配不同屏幕尺寸
 * - 流畅的动画效果和用户体验
 * - 模块化设计，便于维护和扩展
 * 
 * @author BanaMusic Team
 * @version 1.0
 * @since 2025
 */
package com.guet.stu.banamusic.view.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.guet.stu.banamusic.R
import android.view.ViewGroup
import com.guet.stu.banamusic.view.manager.NavigationManager
import com.guet.stu.banamusic.view.manager.PlayButtonManager
import com.guet.stu.banamusic.view.manager.FragmentManager
import com.guet.stu.banamusic.view.manager.NavigationAnimator
import com.guet.stu.banamusic.view.manager.MusicPlayerManager

/**
 * 主Activity类
 * 
 * 继承自AppCompatActivity，提供现代化的Activity功能
 * 作为应用的核心控制器，协调各个管理类的工作
 */
class MainActivity : AppCompatActivity() {

    // ==================== 视图组件 ====================
    
    /** 底部导航容器 - 浮动导航栏的容器视图 */
    private lateinit var floatingNavigationContainer: View
    
    /** 底部容器 - 用于切换完整导航和紧凑导航两种布局 */
    private lateinit var bottomNavContainer: View
    
    /** 选中背景视图 - 显示当前选中导航项的背景效果 */
    private lateinit var selectedNavBackground: View
    
    // ==================== 管理类实例 ====================
    
    /** 导航管理器 - 负责导航栏的显示、切换和状态管理 */
    private lateinit var navigationManager: NavigationManager
    
    /** 播放按钮管理器 - 负责播放按钮的状态管理和图标同步 */
    private lateinit var playButtonManager: PlayButtonManager
    
    /** Fragment管理器 - 负责Fragment的切换和生命周期管理 */
    private lateinit var fragmentManager: FragmentManager
    
    /** 导航动画管理器 - 负责导航栏的动画效果和交互 */
    private lateinit var navigationAnimator: NavigationAnimator
    
    /** 音乐播放器管理器 - 负责音乐播放器的显示、隐藏和状态管理 */
    private lateinit var musicPlayerManager: MusicPlayerManager


    /**
     * Activity创建时的回调方法
     * 
     * 执行流程：
     * 1. 启用边缘到边缘显示（沉浸式状态栏）
     * 2. 设置主布局文件
     * 3. 处理系统栏的边距适配
     * 4. 初始化所有视图组件
     * 5. 初始化所有管理类实例
     * 6. 设置默认状态和初始页面
     * 
     * @param savedInstanceState 保存的实例状态，用于Activity重建时恢复数据
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 启用边缘到边缘显示，实现沉浸式状态栏效果
        enableEdgeToEdge()
        
        // 设置Activity的主布局文件
        setContentView(R.layout.activity_main)

        // 设置窗口边距监听器，处理系统栏（状态栏、导航栏）的适配
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            // 获取系统栏的边距信息
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 为根视图设置内边距，避免内容被系统栏遮挡
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom)
            insets
        }

        // 初始化所有视图组件
        initializeViews()
        
        // 初始化所有管理类实例
        initializeManagers()

        // 设置默认状态和初始页面
        initializeDefaultState()
    }

    /**
     * 初始化所有视图组件
     * 
     * 功能：
     * - 获取底部导航容器的引用
     * - 加载完整导航布局到容器中
     * - 获取浮动导航容器和选中背景视图的引用
     * 
     * 布局结构：
     * - bottomNavContainer：底部导航的主容器
     * - floatingNavigationContainer：浮动导航栏容器
     * - selectedNavBackground：选中状态的背景视图
     */
    private fun initializeViews() {
        // 获取底部导航容器
        bottomNavContainer = findViewById(R.id.bottom_nav_container)
        
        // 初始加载完整导航布局到容器中
        layoutInflater.inflate(R.layout.navigation, bottomNavContainer as ViewGroup, true)
        
        // 获取浮动导航容器的引用
        floatingNavigationContainer = (bottomNavContainer as ViewGroup)
            .findViewById(R.id.floating_navigation_container)
        
        // 获取选中背景视图的引用
        selectedNavBackground = (bottomNavContainer as ViewGroup)
            .findViewById(R.id.selected_nav_background)
    }

    /**
     * 初始化所有管理类实例
     * 
     * 功能：
     * - 创建并配置所有管理类实例
     * - 建立管理类之间的依赖关系
     * - 设置导航管理器的回调接口
     * 
     * 管理类职责：
     * - FragmentManager：管理Fragment的切换和生命周期
     * - PlayButtonManager：管理播放按钮的状态和图标
     * - NavigationAnimator：处理导航栏的动画效果
     * - MusicPlayerManager：管理音乐播放器的显示和隐藏
     * - NavigationManager：管理导航栏的布局切换
     */
    private fun initializeManagers() {
        // 初始化Fragment管理器，传入FragmentManager实例
        fragmentManager = FragmentManager(supportFragmentManager)
        
        // 初始化播放按钮管理器
        playButtonManager = PlayButtonManager()
        // 设置浮动导航容器，用于播放按钮的定位
        playButtonManager.setFloatingNavigationContainer(floatingNavigationContainer)
        
        // 初始化导航动画管理器，传入必要的依赖
        navigationAnimator = NavigationAnimator(this, fragmentManager, playButtonManager)
        // 设置导航容器，用于动画效果
        navigationAnimator.setNavigationContainers(floatingNavigationContainer, selectedNavBackground)
        
        // 初始化音乐播放器管理器
        musicPlayerManager = MusicPlayerManager(this, fragmentManager, navigationAnimator)
        
        // 初始化导航管理器
        navigationManager = NavigationManager(bottomNavContainer as ViewGroup, layoutInflater)
        // 设置导航管理器到动画管理器中
        navigationAnimator.setNavigationManager(navigationManager)
        
        // 设置导航管理器的回调接口，处理导航切换事件
        navigationManager.setCallback(object : NavigationManager.NavigationCallback {
            /**
             * 导航布局切换时的回调
             * 当从完整导航切换到紧凑导航（或反之）时调用
             */
            override fun onNavigationSwapped(newView: View) {
                // 更新浮动导航容器的引用
                floatingNavigationContainer = newView.findViewById(R.id.floating_navigation_container) ?: newView
                // 更新选中背景视图的引用
                selectedNavBackground = newView.findViewById(R.id.selected_nav_background) ?: selectedNavBackground
                
                // 更新播放按钮管理器的容器引用
                playButtonManager.setFloatingNavigationContainer(floatingNavigationContainer)
                // 更新动画管理器的容器引用
                navigationAnimator.setNavigationContainers(floatingNavigationContainer, selectedNavBackground)
                // 重新绑定导航事件
                navigationAnimator.bindNavigationEvents()
                
                // 如果不是滑动导航模式，更新导航选中状态
                if (!navigationManager.isSlideNavigation) {
                    navigationAnimator.updateNavSelection(fragmentManager.getCurrentSelectedId())
                }
            }
            
            /**
             * 应用滑动导航图标时的回调
             * 用于设置滑动导航的特殊图标
             */
            override fun onApplySlideNavIcon(root: View) {
                navigationAnimator.applySlideNavIcon(root)
            }
            
            /**
             * 同步播放按钮图标时的回调
             * 确保所有播放按钮的图标状态一致
             */
            override fun onSyncPlayButtonIcons() {
                playButtonManager.syncAllPlayButtonIcons(window.decorView)
            }
            
            /**
             * 获取播放按钮管理器的回调
             * 供其他组件使用播放按钮管理器
             */
            override fun getPlayButtonManager(): PlayButtonManager {
                return playButtonManager
            }
        })
    }
    
    /**
     * 初始化默认状态
     * 
     * 功能：
     * - 设置默认显示的页面（首页）
     * - 更新导航栏的选中状态
     * - 绑定导航事件监听器
     * - 初始化播放按钮的状态
     */
    private fun initializeDefaultState() {
        // 默认加载首页Fragment
        fragmentManager.switchToNavigation(R.id.home_layout)
        
        // 更新导航栏的选中状态，高亮首页
        navigationAnimator.updateNavSelection(R.id.home_layout)
        
        // 绑定导航事件监听器，处理导航点击
        navigationAnimator.bindNavigationEvents()
        
        // 初始化播放按钮状态，设置默认图标
        playButtonManager.initializePlayButton(window.decorView)
    }

    // ==================== 公共接口方法 ====================
    
    /**
     * 显示音乐播放器
     * 
     * 功能：显示底部音乐播放器界面
     * 用途：供外部组件调用，如点击歌曲时显示播放器
     */
    fun showMusicPlayer() {
        musicPlayerManager.showMusicPlayer()
    }

    /**
     * 隐藏音乐播放器
     * 
     * 功能：隐藏底部音乐播放器界面
     * 用途：供外部组件调用，如停止播放时隐藏播放器
     */
    fun hideMusicPlayer() {
        musicPlayerManager.hideMusicPlayer()
    }

    /**
     * 为可滚动视图附加自动隐藏导航栏功能
     * 
     * 功能：
     * - 监听滚动事件
     * - 上滑时切换到紧凑导航（隐藏导航栏）
     * - 下滑时切换到完整导航（显示导航栏）
     * 
     * @param scrollable 可滚动的视图，如RecyclerView、ScrollView等
     */
    fun attachAutoHideOnScroll(scrollable: View) {
        navigationAnimator.attachAutoHideOnScroll(scrollable)
    }
}
