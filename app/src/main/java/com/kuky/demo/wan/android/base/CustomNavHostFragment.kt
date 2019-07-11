package com.kuky.demo.wan.android.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import java.util.*

/**
 * @author kuky.
 * @description 解决 navigation 返回上个 fragment 导致 fragment 重新调用 onCreated 的问题
 */
class CustomNavHostFragment : NavHostFragment() {
    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return CustomNavigator(
            requireContext(),
            childFragmentManager,
            id
        )
    }

    //参考相关链接
    // https://stackoverflow.com/questions/50485988/is-there-a-way-to-keep-fragment-alive-when-using-bottomnavigationview-with-new-n/51684125
    // https://www.jianshu.com/p/ebd2f6d7a349
    @Navigator.Name("fragment")
    open class CustomNavigator(var mContext: Context, var mFragmentManager: FragmentManager, var mContainerId: Int) :
        FragmentNavigator(mContext, mFragmentManager, mContainerId) {

        override fun navigate(
            destination: Destination, args: Bundle?,
            navOptions: NavOptions?, navigatorExtras: Navigator.Extras?
        ): NavDestination? {
            try {
                // 反射获取mBackStack mIsPendingBackStackOperation
                val mBackStackField = FragmentNavigator::class.java.getDeclaredField("mBackStack")
                mBackStackField.isAccessible = true

                @Suppress("UNCHECKED_CAST")
                val mBackStack: ArrayDeque<Int> = mBackStackField.get(this) as ArrayDeque<Int>

                val mIsPendingBackStackOperationField =
                    FragmentNavigator::class.java.getDeclaredField("mIsPendingBackStackOperation")
                mIsPendingBackStackOperationField.isAccessible = true
                var mIsPendingBackStackOperation: Boolean = mIsPendingBackStackOperationField.get(this) as Boolean

                if (mFragmentManager.isStateSaved) {
                    //Log.i("TAG", "Ignoring navigate() call: FragmentManager has already" + " saved its state")
                    return null
                }
                var className = destination.className
                if (className[0] == '.') {
                    className = mContext.packageName + className
                }

                val ft = mFragmentManager.beginTransaction()

                var enterAnim = navOptions?.enterAnim ?: -1
                var exitAnim = navOptions?.exitAnim ?: -1
                var popEnterAnim = navOptions?.popEnterAnim ?: -1
                var popExitAnim = navOptions?.popExitAnim ?: -1
                if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
                    enterAnim = if (enterAnim != -1) enterAnim else 0
                    exitAnim = if (exitAnim != -1) exitAnim else 0
                    popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
                    popExitAnim = if (popExitAnim != -1) popExitAnim else 0
                    ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
                }

                val tag = destination.id.toString()
                //ft.replace(mContainerId, frag)

                val currentFragment = mFragmentManager.primaryNavigationFragment
                if (currentFragment != null) {
                    ft.hide(currentFragment)
                }

                var frag = mFragmentManager.findFragmentByTag(tag)
                if (frag == null) {
                    frag = instantiateFragment(
                        mContext, mFragmentManager,
                        className, args
                    )
                    frag.arguments = args
                    ft.add(mContainerId, frag, tag)
                } else {
                    ft.show(frag)
                }

                ft.setPrimaryNavigationFragment(frag)

                @IdRes val destId = destination.id
                val initialNavigation = mBackStack.isEmpty()

                val isSingleTopReplacement = (navOptions != null && !initialNavigation
                        && navOptions.shouldLaunchSingleTop()
                        && mBackStack.peekLast().toInt() == destId)

                val isAdded = when {
                    initialNavigation -> true
                    isSingleTopReplacement -> {
                        // Single Top means we only want one instance on the back stack
                        if (mBackStack.size > 1) {
                            // If the Fragment to be replaced is on the FragmentManager's
                            // back stack, a simple replace() isn't enough so we
                            // remove it from the back stack and put our replacement
                            // on the back stack in its place
                            mFragmentManager.popBackStack(
                                generateMyBackStackName(mBackStack.size, mBackStack.peekLast()),
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                            ft.addToBackStack(generateMyBackStackName(mBackStack.size, destId))
                            mIsPendingBackStackOperation = true
                            mIsPendingBackStackOperationField.set(this, true)
                        }
                        false
                    }
                    else -> {
                        ft.addToBackStack(generateMyBackStackName(mBackStack.size + 1, destId))
                        mIsPendingBackStackOperation = true
                        mIsPendingBackStackOperationField.set(this, true)
                        true
                    }
                }

                if (navigatorExtras is Extras) {
                    val extras = navigatorExtras as Extras?
                    for ((key, value) in extras!!.sharedElements) {
                        ft.addSharedElement(key, value)
                    }
                }
                ft.setReorderingAllowed(true)
                ft.commit()
                // The commit succeeded, update our view of the world
                return if (isAdded) {
                    mBackStack.add(destId)
                    destination
                } else {
                    null
                }
            } catch (e: Throwable) {
                return super.navigate(destination, args, navOptions, navigatorExtras)
            }
        }

        private fun generateMyBackStackName(backStackIndex: Int, destId: Int): String {
            return "$backStackIndex-$destId"
        }
    }
}