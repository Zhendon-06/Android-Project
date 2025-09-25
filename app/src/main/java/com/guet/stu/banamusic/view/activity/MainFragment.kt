/**
 * 主页面Fragment - 应用的首页
 * 
 * 功能描述：
 * - 显示应用的主页面内容
 * - 包含推荐内容、历史记录、每日推荐等模块
 * - 支持滚动时自动隐藏/显示导航栏
 * - 提供多个RecyclerView展示不同类型的内容
 * 
 * 页面结构：
 * 1. 推荐内容栏：水平滚动的推荐专辑/歌单
 * 2. 历史记录栏：用户最近播放的歌曲
 * 3. 每日推荐栏：网格布局的推荐歌曲
 * 
 * 交互功能：
 * - 点击推荐内容跳转到专辑详情页
 * - 滚动页面时触发导航栏自动隐藏
 * - 支持多种布局管理器（线性、网格）
 * 
 * @author BanaMusic Team
 * @version 1.0
 * @since 2024
 */
package com.guet.stu.banamusic.view.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guet.stu.banamusic.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guet.stu.banamusic.view.adapter.ActionBarAdapter
import com.guet.stu.banamusic.view.adapter.HistoryAdapter
import com.guet.stu.banamusic.view.adapter.DayListAdapter

// Fragment初始化参数常量
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 主页面Fragment类
 * 
 * 继承自Fragment，提供主页面功能
 * 使用工厂方法创建实例，支持参数传递
 */
class MainFragment : Fragment() {
    // ==================== Fragment参数 ====================
    
    /** Fragment参数1 - 预留参数，用于扩展功能 */
    private var param1: String? = null
    
    /** Fragment参数2 - 预留参数，用于扩展功能 */
    private var param2: String? = null

    /**
     * Fragment创建时的回调方法
     * 
     * 功能：从Bundle中恢复保存的参数
     * 
     * @param savedInstanceState 保存的实例状态
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 从参数Bundle中恢复保存的数据
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    /**
     * 创建Fragment视图时的回调方法
     * 
     * 功能：加载Fragment的布局文件
     * 
     * @param inflater 布局填充器
     * @param container 父容器
     * @param savedInstanceState 保存的实例状态
     * @return 创建的视图
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载Fragment的布局文件
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    /**
     * 视图创建完成后的回调方法
     * 
     * 功能：
     * - 设置滚动监听，实现导航栏自动隐藏
     * - 初始化三个RecyclerView及其适配器
     * - 配置不同的布局管理器
     * - 设置点击事件处理
     * 
     * @param view 创建的视图
     * @param savedInstanceState 保存的实例状态
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 绑定整体ScrollView以触发导航栏自动隐藏/显示
        view.findViewById<android.widget.ScrollView>(R.id.home_scroll)?.let { scroll ->
            // 将滚动事件传递给MainActivity处理
            (activity as? MainActivity)?.attachAutoHideOnScroll(scroll)
        }

        // 获取三个RecyclerView的引用
        val actionBarRv = view.findViewById<RecyclerView>(R.id.action_bar_recyclerview)
        val historyRv = view.findViewById<RecyclerView>(R.id.history_list)
        val dayRv = view.findViewById<RecyclerView>(R.id.rv_day)

        // ==================== 推荐内容栏 ====================
        // 设置水平线性布局管理器
        actionBarRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // 创建推荐内容适配器
        actionBarRv.adapter = ActionBarAdapter(
            listOf(
                ActionBarAdapter.ActionBarItem(R.drawable.fm1, "推荐1"),
                ActionBarAdapter.ActionBarItem(R.drawable.fm1, "推荐2"),
                ActionBarAdapter.ActionBarItem(R.drawable.fm1, "推荐3"),
                ActionBarAdapter.ActionBarItem(R.drawable.fm1, "推荐4")
            )
        ) {
            // 点击推荐内容时跳转到专辑详情页
            startActivity(android.content.Intent(requireContext(), AlbumListActivity::class.java))
        }

        // ==================== 历史记录栏 ====================
        // 设置水平线性布局管理器
        historyRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // 创建历史记录适配器
        historyRv.adapter = HistoryAdapter(
            listOf(
                HistoryAdapter.HistoryItem(R.drawable.fm2, "歌曲A"),
                HistoryAdapter.HistoryItem(R.drawable.fm2, "歌曲B"),
                HistoryAdapter.HistoryItem(R.drawable.fm2, "歌曲C"),
                HistoryAdapter.HistoryItem(R.drawable.fm2, "歌曲D")
            )
        )

        // ==================== 每日推荐栏 ====================
        // 设置水平网格布局管理器，3行布局
        dayRv.layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)
        // 生成30个演示数据项
        val dayItems = (1..30).map {
            DayListAdapter.DayItem(R.drawable.music, "歌曲$it", "歌手$it")
        }
        // 创建每日推荐适配器
        dayRv.adapter = DayListAdapter(dayItems)
    }

    // ==================== 工厂方法 ====================
    
    companion object {
        /**
         * 创建MainFragment实例的工厂方法
         * 
         * 功能：使用提供的参数创建新的Fragment实例
         * 用途：替代直接构造函数，提供更好的参数管理
         * 
         * @param param1 参数1 - 预留参数，用于扩展功能
         * @param param2 参数2 - 预留参数，用于扩展功能
         * @return 新创建的MainFragment实例
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                // 将参数保存到Bundle中
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}