@file:Suppress("UNUSED_PARAMETER")

package com.kuky.demo.wan.android.ui.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.base.delayLaunch
import com.kuky.demo.wan.android.base.requestPermissions
import com.kuky.demo.wan.android.databinding.DialogWxBinding
import com.kuky.demo.wan.android.utils.ImageSaveUtils
import com.kuky.demo.wan.android.utils.starApp
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.io.File

/**
 * @author kuky.
 * @description
 */
class WxDialogFragment : BaseDialogFragment<DialogWxBinding>() {

    override fun layoutId(): Int = R.layout.dialog_wx

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this
    }

    fun saveImg(view: View): Boolean {
        val file = ImageSaveUtils.getNewFile(requireContext(), "wx_taonce")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestNecessaryPermissions(file)
        } else {
            saveQrCode(file)
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestNecessaryPermissions(file: File?) {
        requestPermissions {
            putPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            onAllPermissionsGranted = { saveQrCode(file) }

            onPermissionsNeverAsked = { toAppSettings() }

            onPermissionsDenied = { toAppSettings() }

            onShowRationale = { request ->
                context?.alert("必要权限，请务必同意o(╥﹏╥)o", "温馨提示") {
                    positiveButton("行，给你~") { request.retryRequestPermissions() }
                    negativeButton("不，我不玩了！") {}
                }?.show()
            }
        }
    }

    private fun toAppSettings() {
        context?.alert("缺少必要权限, 是否手动打开^_^", "温馨提示") {
            positiveButton("走起, 小老弟~") {
                context?.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }

            negativeButton("我不!") {}
        }?.show()
    }

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
                delayLaunch(1000) {
                    context?.starApp("com.tencent.mm") { context?.toast("未安装微信") }
                    dialog?.dismiss()
                }
                context?.toast("保存图片成功，即将打开微信")
            } else {
                context?.toast("保存图片出错啦~")
            }
        }
    }

    fun close(view: View) {
        dialog?.dismiss()
    }
}