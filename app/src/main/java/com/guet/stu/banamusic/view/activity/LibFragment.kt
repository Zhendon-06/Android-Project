/**
 * 媒体库Fragment
 *
 * 用途：展示“音乐库”网格内容，并与主Activity的导航动画联动。
 * 结构：顶部ActionBar标题设置为“音乐”，下方为 2 列网格RecyclerView。
 */
package com.guet.stu.banamusic.view.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.guet.stu.banamusic.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guet.stu.banamusic.view.adapter.ContentAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LibFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lib, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 设置actionbar中的TextView显示为"音乐"
        val tvHome = view.findViewById<TextView>(R.id.tv_home)
        tvHome?.text = "音乐"

      //recyclerview的适配
        val contentRv = view.findViewById<RecyclerView>(R.id.rv_song_rv)
        contentRv?.layoutManager = GridLayoutManager(requireContext(), 2)
        val demo = (1..12).map {
            ContentAdapter.ContentItem(R.drawable.fm1, "歌曲$it", "歌手$it")
        }
        contentRv?.adapter = ContentAdapter(demo)

        // 绑定滚动以切换导航动画（绑定到真正滚动的 RecyclerView）
        contentRv?.let { (activity as? MainActivity)?.attachAutoHideOnScroll(it) }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LibFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}