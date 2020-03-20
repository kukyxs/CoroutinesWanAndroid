package com.kuky.demo.wan.android.ui.dialog

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.base.delayLaunch
import com.kuky.demo.wan.android.base.requestPermissions
import com.kuky.demo.wan.android.databinding.DialogWxBinding
import com.kuky.demo.wan.android.utils.ApplicationUtils
import com.kuky.demo.wan.android.utils.ImageSaveUtils
import org.jetbrains.anko.alert
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
            requireActivity().requestPermissions {
                putPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

                onAllPermissionsGranted {
                    saveQrCode(file)
                }

                onPermissionsNeverAsked {
                    toAppSettings()
                }

                onPermissionsDenied {
                    toAppSettings()
                }

                onShowRationale { request ->
                    requireContext().alert("必要权限，请务必同意o(╥﹏╥)o", "温馨提示") {
                        positiveButton("行，给你~") { request.retryRequestPermissions() }
                        negativeButton("不，我不玩了！") {}
                    }.show()
                }
            }
        } else {
            saveQrCode(file)
        }

        return true
    }

    private fun toAppSettings() {
        requireContext().alert("缺少必要权限，是否手动打开^_^", "温馨提示") {
            positiveButton("走起，小老弟~") {
                requireContext().startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }

            negativeButton("我不！") {}
        }.show()
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