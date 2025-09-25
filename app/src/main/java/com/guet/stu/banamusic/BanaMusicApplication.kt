package com.guet.stu.banamusic

import android.app.Application

/**
 * BanaMusic 应用全局Application类
 * 
 * 功能描述：
 * - 管理应用的全局状态和变量
 * - 提供全局播放状态管理
 * - 作为单例模式，确保全局状态的一致性
 * 
 * 主要特性：
 * - 全局播放状态管理
 * - 线程安全的状态访问
 * - 便于所有Activity和Fragment访问全局状态
 * 
 * @author BanaMusic Team
 * @version 1.0
 * @since 2025
 */
class BanaMusicApplication : Application() {
    
    // ==================== 全局状态变量 ====================
    
    /**
     * 全局播放状态
     * true: 正在播放
     * false: 暂停/停止
     */
    @Volatile
    var isPlaying: Boolean = false
        private set
    
    // ==================== 单例模式 ====================
    
    companion object {
        @Volatile
        private var INSTANCE: BanaMusicApplication? = null
        
        /**
         * 获取Application实例
         * @return BanaMusicApplication实例
         */
        fun getInstance(): BanaMusicApplication {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: throw IllegalStateException("Application not initialized")
            }
        }
    }
    
    // ==================== 生命周期方法 ====================
    
    /**
     * Application创建时的回调
     * 初始化全局状态和配置
     */
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

    }
    
    /**
     * 设置播放状态（供PlayButtonManager调用）
     * @param playing true表示播放，false表示暂停/停止
     */
    fun setPlaying(playing: Boolean) {
        synchronized(this) {
            isPlaying = playing
        }
    }
}
