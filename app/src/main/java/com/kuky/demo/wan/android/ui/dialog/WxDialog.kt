package com.kuky.demo.wan.android.ui.dialog

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseActivity
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.base.PermissionListener
import com.kuky.demo.wan.android.base.delayLaunch
import com.kuky.demo.wan.android.databinding.DialogWxBinding
import com.kuky.demo.wan.android.utils.ApplicationUtils
import com.kuky.demo.wan.android.utils.ImageSaveUtils
import org.jetbrains.anko.toast
import java.io.File

/**
 * @author kuky.
 * @description
 */
class WxDialog : BaseDialogFragment<DialogWxBinding>() {

    override fun getLayoutId(): Int = R.layout.dialog_wx

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@WxDialog
    }

    fun saveImg(view: View): Boolean {
        val file = ImageSaveUtils.getNewFile(requireContext(), "wx_taonce")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (requireActivity() as BaseActivity<*>).onRuntimePermissionsAsk(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), object : PermissionListener {
                    override fun onGranted() {
                        saveQrCode(file)
                    }

                    override fun onDenied(deniedPermissions: List<String>) {
                        requireContext().toast("缺少必要权限，请前往权限管理中心打开权限")
                    }
                }
            )
        } else {
            saveQrCode(file)
        }

        return true
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun saveQrCode(file: File?) {
        file?.let {
            if (it.parentFile?.exists() == false) {
                it.parentFile?.mkdirs()
            }

            if (it.exists()) it.delete()

            val result =
                if (it.createNewFile()) ImageSaveUtils.cropView(mBinding.wxCode, it)
                else false

            if (result) {
                requireContext().toast("保存图片成功，即将打开微信")
                delayLaunch(1000) {
                    ApplicationUtils.starApp(requireContext(), "com.tencent.mm") { requireContext().toast("未安装微信") }
                    dialog?.dismiss()
                }
            } else {
                requireContext().toast("保存图片出错啦~")
            }
        }
    }

    fun close(view: View) {
        dialog?.dismiss()
    }
}