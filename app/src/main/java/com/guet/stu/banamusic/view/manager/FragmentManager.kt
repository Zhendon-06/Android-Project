package com.guet.stu.banamusic.view.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.guet.stu.banamusic.R
import com.guet.stu.banamusic.view.activity.MainFragment
import com.guet.stu.banamusic.view.activity.LibFragment
import com.guet.stu.banamusic.view.activity.UserFragment

/**
 * Fragment管理器
 * 负责管理Fragment的切换和状态
 */
class FragmentManager(private val fragmentManager: FragmentManager) {
    
    private var lastPage: Fragment = MainFragment()
    private var currentSelectedId: Int = R.id.home_layout
    
    /**
     * 切换Fragment（不带动画）
     */
    fun switchTo(fragment: Fragment) {
        lastPage = fragment
        fragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .commit()
    }
    
    /**
     * 切换Fragment（带动画）
     */
    fun switchWithAnim(fragment: Fragment, enter: Int, exit: Int) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(enter, exit)
            .replace(R.id.content_container, fragment)
            .commit()
    }
    
    /**
     * 获取上一个页面
     */
    fun getLastPage(): Fragment = lastPage
    
    /**
     * 获取当前选中的导航ID
     */
    fun getCurrentSelectedId(): Int = currentSelectedId
    
    /**
     * 根据导航ID获取对应的Fragment
     */
    fun getFragmentById(id: Int): Fragment {
        return when (id) {
            R.id.home_layout -> MainFragment()
            R.id.lib_layout -> LibFragment()
            R.id.profile_layout -> UserFragment()
            else -> MainFragment()
        }
    }
    
    /**
     * 切换到指定导航ID对应的Fragment
     */
    fun switchToNavigation(id: Int) {
        currentSelectedId = id
        switchTo(getFragmentById(id))
    }
}

