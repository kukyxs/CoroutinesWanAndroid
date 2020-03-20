package com.kuky.demo.wan.android.base

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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

fun FragmentActivity.requestPermissions(init: PermissionCallback.() -> Unit) {
    val callback = PermissionCallback()
    callback.init()
    onRuntimePermissionsRequest(callback)
}

private fun FragmentActivity.permissionGranted(permission: String) =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun FragmentActivity.onRuntimePermissionsRequest(callback: PermissionCallback) {
    val permissions = callback.permissions

    if (permissions.isEmpty()) return

    val requestCode = PermissionMap.put(callback)
    val needRequestPermissions = permissions.filterNot { permissionGranted(it) }

    if (needRequestPermissions.isEmpty()) {
        callback.granted()
    } else {
        val shouldShowRationalPermissions = mutableListOf<String>()
        val shouldNotShowRationalPermissions = mutableListOf<String>()

        permissions.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, it)) {
                shouldShowRationalPermissions.add(it)
            } else {
                shouldNotShowRationalPermissions.add(it)
            }
        }

        if (shouldShowRationalPermissions.isNotEmpty()) {
            callback.onShow(
                PermissionRequest(
                    getPermissionFragment(),
                    shouldShowRationalPermissions,
                    requestCode
                )
            )
        }

        if (shouldNotShowRationalPermissions.isNotEmpty()) {
            getPermissionFragment().requestPermissionsByFragment(shouldNotShowRationalPermissions.toTypedArray(), requestCode)
        }
    }
}

private fun FragmentActivity.getPermissionFragment(): KPermissionFragment =
    supportFragmentManager.findFragmentByTag(K_FRAGMENT_TAG) as? KPermissionFragment
        ?: KPermissionFragment().apply {
            supportFragmentManager.beginTransaction().add(this, K_FRAGMENT_TAG).commitNowAllowingStateLoss()
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
            if (neverAskedPermissions.isNotEmpty()) it.onNeverAsked(neverAskedPermissions)

            if (deniedPermissions.isNotEmpty()) it.onDenied(deniedPermissions)

            if (neverAskedPermissions.isEmpty() && deniedPermissions.isEmpty()) it.granted()
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
 * 权限申请回调
 */
class PermissionCallback {
    var permissions: Array<out String> = arrayOf()
    private var onAllPermissionsGranted: () -> Unit = {}
    private var onPermissionsDenied: (MutableList<String>) -> Unit = {}
    private var onPermissionsNeverAsked: (MutableList<String>) -> Unit = {}
    private var onShowRationale: (PermissionRequest) -> Unit = {}

    fun putPermissions(vararg ps: String) {
        permissions = ps
    }

    fun onAllPermissionsGranted(granted: () -> Unit) {
        onAllPermissionsGranted = granted
    }

    fun onPermissionsDenied(denied: (MutableList<String>) -> Unit) {
        onPermissionsDenied = denied
    }

    fun onPermissionsNeverAsked(neverAsked: (MutableList<String>) -> Unit) {
        onPermissionsNeverAsked = neverAsked
    }

    fun onShowRationale(showRationale: (PermissionRequest) -> Unit) {
        onShowRationale = showRationale
    }

    internal fun granted() = onAllPermissionsGranted.invoke()

    internal fun onDenied(permissions: MutableList<String>) = onPermissionsDenied.invoke(permissions)

    internal fun onNeverAsked(permissions: MutableList<String>) = onPermissionsNeverAsked.invoke(permissions)

    internal fun onShow(permissionRequest: PermissionRequest) = onShowRationale.invoke(permissionRequest)
}