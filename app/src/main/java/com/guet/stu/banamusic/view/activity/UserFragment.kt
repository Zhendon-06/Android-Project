/**
 * 用户中心Fragment
 *
 * 用途：展示“我的”页面，包含滚动联动导航与新建歌单对话框。
 */
package com.guet.stu.banamusic.view.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.guet.stu.banamusic.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 设置actionbar中的TextView显示为"我的"
        val tvHome = view.findViewById<TextView>(R.id.tv_home)
        tvHome?.text = "我的"
        var putMusicicon : ImageView = view.findViewById<ImageView>(R.id.putmusic)
        // 绑定滚动以切换导航动画
        view.findViewById<android.widget.ScrollView>(R.id.user_scroll)?.let { scroll ->
            (activity as? MainActivity)?.attachAutoHideOnScroll(scroll)
        }
        putMusicicon.setOnClickListener{
            showCreatePlaylistDialog()
        }
    }

    /**
     * 显示新建歌单对话框
     */
    private fun showCreatePlaylistDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_playlist, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_playlist_name)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("新建歌单")
            .setView(dialogView)
            .create()

        // 设置按钮点击事件
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "名称不能为空"
            } else {
                // TODO: 保存歌单逻辑
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}