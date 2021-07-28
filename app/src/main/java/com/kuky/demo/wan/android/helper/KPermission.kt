package com.kuky.demo.wan.android.base

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author kuky.
 * @description 权限申请
 */
private const val K_FRAGMENT_TAG = "base.k.permission.fragment.tag"
private const val K_REQUEST_CODE = 0xFF

@RequiresApi(Build.VERSION_CODES.M)
fun FragmentActivity.requestPermissions(init: PermissionCallback.() -> Unit) {
    val callback = PermissionCallback(activity = this).apply(init)
    onRuntimePermissionsRequest(callback)
}

@RequiresApi(Build.VERSION_CODES.M)
fun Fragment.requestPermissions(init: PermissionCallback.() -> Unit) {
    val callback = PermissionCallback(activity = requireActivity(), fragment = this).apply(init)
    onRuntimePermissionsRequest(callback)
}

// 权限是否已授权
private fun FragmentActivity.permissionGranted(permission: String) =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

// 请求权限
@RequiresApi(Build.VERSION_CODES.M)
private fun onRuntimePermissionsRequest(callback: PermissionCallback) {
    val permissions = callback.permissions

    if (permissions.isEmpty()) return

    val requestCode = PermissionMap.put(callback)
    val needRequestPermissions = permissions.filterNot { callback.activity.permissionGranted(it) }

    if (needRequestPermissions.isEmpty()) {
        callback.onAllPermissionsGranted()
    } else {
        val shouldShowRationalPermissions = mutableListOf<String>() // 权限被拒绝后，可弹出信息告诉用户需要权限的理由
        val shouldNotShowRationalPermissions = mutableListOf<String>() // 首次申请的权限

        permissions.forEach {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(callback.activity, it)

            if (showRationale) {
                shouldShowRationalPermissions.add(it)
            } else {
                shouldNotShowRationalPermissions.add(it)
            }
        }

        if (shouldShowRationalPermissions.isNotEmpty()) {
            callback.onShowRationale(
                PermissionRequest(
                    getPermissionFragment(callback),
                    shouldShowRationalPermissions,
                    requestCode
                )
            )
        }

        if (shouldNotShowRationalPermissions.isNotEmpty()) {
            getPermissionFragment(callback)
                .requestPermissionsByFragment(
                    shouldNotShowRationalPermissions.toTypedArray(),
                    requestCode
                )
        }
    }
}

private fun getPermissionFragment(callback: PermissionCallback): KPermissionFragment {
    val fragmentManager = callback.fragment?.childFragmentManager ?: callback.activity.supportFragmentManager
    return fragmentManager.findFragmentByTag(K_FRAGMENT_TAG) as? KPermissionFragment
        ?: KPermissionFragment().apply {
            fragmentManager.beginTransaction()
                .add(this, K_FRAGMENT_TAG).commitNowAllowingStateLoss()
        }
}

/**
 * 权限申请 Fragment
 */
class KPermissionFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun requestPermissionsByFragment(permissions: Array<out String>, requestCode: Int) =
        requestPermissions(permissions, requestCode)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val neverAskedPermissions = mutableListOf<String>()
        val deniedPermissions = mutableListOf<String>()
        val grantedPermissions = mutableListOf<String>()

        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(permission)) deniedPermissions.add(permission)
                else neverAskedPermissions.add(permission)
            } else if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permission)
            }
        }

        PermissionMap.fetch(requestCode)?.let {
            if (neverAskedPermissions.isNotEmpty()) it.onPermissionsNeverAsked(neverAskedPermissions)

            if (deniedPermissions.isNotEmpty()) it.onPermissionsDenied(deniedPermissions)

            if (neverAskedPermissions.isEmpty() && deniedPermissions.isEmpty()) it.onAllPermissionsGranted()
        }
    }
}

/**
 * 根据 PermissionCallback 生成 code
 */
private object PermissionMap {
    private val atomicInteger = AtomicInteger(K_REQUEST_CODE)
    private val map = mutableMapOf<Int, PermissionCallback>()

    fun put(callback: PermissionCallback) =
        atomicInteger.getAndIncrement().apply {
            map[this] = callback
        }

    fun fetch(requestCode: Int): PermissionCallback? =
        map[requestCode].apply {
            map.remove(requestCode)
        }
}

/**
 * 首次申请拒绝，再次申请回调
 */
data class PermissionRequest(
    private val kPermissionFragment: KPermissionFragment,
    private val permissions: MutableList<String>,
    private val requestCode: Int
) {
    fun retryRequestPermissions() = kPermissionFragment.requestPermissions(permissions.toTypedArray(), requestCode)
}

/**
 * 权限申请回调, 请勿手动设置 activity, fragment 参数
 */
data class PermissionCallback(
    var permissions: MutableList<String> = mutableListOf(),
    var onAllPermissionsGranted: () -> Unit = {},
    var onPermissionsDenied: (MutableList<String>) -> Unit = {},
    var onPermissionsNeverAsked: (MutableList<String>) -> Unit = {},
    var onShowRationale: (PermissionRequest) -> Unit = { it.retryRequestPermissions() },
    internal var activity: FragmentActivity, internal var fragment: Fragment? = null
) {
    fun putPermissions(vararg ps: String) {
        permissions = ps.toMutableList()
    }
}