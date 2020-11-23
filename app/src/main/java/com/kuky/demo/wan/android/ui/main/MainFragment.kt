@file:Suppress("UNUSED_PARAMETER", "BlockingMethodInNonBlockingContext")

package com.kuky.demo.wan.android.ui.main

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.BaseFragmentPagerAdapter
import com.kuky.demo.wan.android.base.handleResult
import com.kuky.demo.wan.android.base.onChange
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.FragmentMainBinding
import com.kuky.demo.wan.android.databinding.UserProfileHeaderBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.home.HomeArticleFragment
import com.kuky.demo.wan.android.ui.hotproject.HotProjectFragment
import com.kuky.demo.wan.android.ui.system.KnowledgeSystemFragment
import com.kuky.demo.wan.android.ui.userarticles.UserArticleFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterFragment
import com.kuky.demo.wan.android.utils.GalleryTransformer
import com.kuky.demo.wan.android.utils.getAppVersionName
import com.kuky.demo.wan.android.utils.screenWidth
import com.youth.banner.listener.OnBannerListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 * @author kuky.
 * @description 主页面 fragment 持有者
 */
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val mAdapter by lazy {
        BaseFragmentPagerAdapter(
            childFragmentManager, arrayListOf(
                HomeArticleFragment(),
                HotProjectFragment(),
                KnowledgeSystemFragment(),
                UserArticleFragment(),
                WxChapterFragment()
            )
        )
    }

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by sharedViewModel<MainViewModel>()

    private val mAboutUsDialog by lifecycleScope.inject<AboutUsDialogFragment>()

    private val mWxDialog by lifecycleScope.inject<WxDialogFragment>()

    private val mHeaderBinding by lazy {
        DataBindingUtil.inflate<UserProfileHeaderBinding>(
            layoutInflater, R.layout.user_profile_header, mBinding?.userProfileDrawer, false
        )
    }

    private var mCoinsJob: Job? = null

    override fun actionsOnViewInflate() {
        mBinding?.run {
            adapter = mAdapter
            limit = mAdapter.count
            transformer = GalleryTransformer()
            mainPage.onChange(scrolled = { _, _, _ -> closeMenu() })

            mHeaderBinding.holder = this@MainFragment
            userProfileDrawer.addHeaderView(mHeaderBinding.root)
        }

        mViewModel.getBanners()

        if (mViewModel.hasLogin.value == true) getCoins()
    }

    override fun getLayoutId(): Int = R.layout.fragment_main

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            holder = this@MainFragment
            viewModel = mViewModel
            listener = OnBannerListener { position ->
                mViewModel.banners.value?.let {
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        it[position].url
                    )
                }
            }

            banner.let {
                it.layoutParams = it.layoutParams.apply {
                    width = screenWidth
                    height = (width * 0.45f).toInt()
                }
            }

            mViewModel.hasLogin.observe(this@MainFragment, {
                userProfileDrawer.menu.let { menus ->
                    menus.findItem(R.id.user_collections).isVisible = it
                    menus.findItem(R.id.login_out).isVisible = it
                    menus.findItem(R.id.todo_group).isVisible = it
                    menus.findItem(R.id.share).isVisible = it
                }

                mHeaderBinding.userCoins.isVisible = it
                mHeaderBinding.loginState = it
                val username = PreferencesHelper.fetchUserName(requireContext())
                mHeaderBinding.name = if (it) username else requireContext().getString(R.string.click_to_login)
                mHeaderBinding.avatarKey = username.run {
                    if (isNullOrBlank()) "A" else toCharArray()[0].toString().toUpperCase(Locale.getDefault())
                }

                if (it) getCoins()
            })

            handleUserProfile()
        }
    }

    private fun getCoins() {
        mCoinsJob?.cancel()
        mCoinsJob = launch {
            mViewModel.getCoinInfo().collectLatest {
                if (it == null) return@collectLatest

                mHeaderBinding.coinSpan = SpannableStringBuilder("${it.coinCount}").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.coin_color)),
                        0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )

                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
                        run { append("\t/\t\t"); length },
                        run { append("Lv${it.level}"); length },
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )

                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorAccent)),
                        run { append("\t\t/\t\t"); length },
                        run { append("R${it.rank}"); length },
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        closeMenu()
    }

    private fun handleUserProfile() {
        mBinding?.userProfileDrawer?.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.favourites -> toFavourite()

                R.id.share_list -> toShare()

                R.id.todo_list -> launchTodoList()

                R.id.about -> showAboutUs()

                R.id.go_star -> starForUs()

                R.id.helper -> requireContext()
                    .alert(
                        String.format(
                            resources.getString(R.string.operate_helper),
                            context?.getAppVersionName()
                        ), resources.getString(R.string.operate_title)
                    ) {
                        yesButton {
                            launch { PreferencesHelper.saveFirstInState(requireContext(), false) }
                        }
                    }.show()

                R.id.login_out -> requireContext()
                    .alert("是否退出登录") {
                        yesButton { loginOut() }
                        noButton { }
                    }.show()
            }
            true
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loginOut() {
        launch {
            mViewModel.loginOut().catch {
                context?.toast(R.string.no_network)
            }.onStart {
                mAppViewModel.showLoading()
            }.onCompletion {
                mAppViewModel.dismissLoading()
            }.collectLatest {
                it.handleResult({
                    context?.toast(R.string.login_out_failed)
                }, {
                    context?.toast(R.string.login_out_succeed)
                    mViewModel.hasLogin.postValue(false)
                    mViewModel.clearUserInfo()
                })
            }
        }
    }

    private fun toFavourite() {
        findNavController().navigate(R.id.action_mainFragment_to_collectionFragment)
        mBinding?.drawer?.closeDrawer(GravityCompat.START)
    }

    private fun toShare() {
        findNavController().navigate(R.id.action_mainFragment_to_userShareListFragment)
        mBinding?.drawer?.closeDrawer(GravityCompat.START)
    }

    private fun launchTodoList() {
        findNavController().navigate(R.id.action_mainFragment_to_todoListFragment)
        mBinding?.drawer?.closeDrawer(GravityCompat.START)
    }

    private fun showAboutUs() {
        mAboutUsDialog.apply {
            aboutUsHandler = { url ->
                WebsiteDetailFragment.viewDetail(
                    findNavController(),
                    R.id.action_mainFragment_to_websiteDetailFragment,
                    url
                )
                mBinding?.drawer?.closeDrawer(GravityCompat.START)
            }
        }.showAllowStateLoss(childFragmentManager, "about")
    }

    private fun starForUs() {
        WebsiteDetailFragment.viewDetail(
            findNavController(),
            R.id.action_mainFragment_to_websiteDetailFragment,
            "https://github.com/kukyxs/CoroutinesWanAndroid"
        )
        mBinding?.drawer?.closeDrawer(GravityCompat.START)
    }

    fun headerLogin(view: View) {
        if (mViewModel.hasLogin.value == false) {
            LoginDialogFragment().showAllowStateLoss(childFragmentManager, "login")
        }
    }

    fun userCoins(view: View) {
        closeMenu()
        findNavController().navigate(R.id.action_mainFragment_to_coinFragment)
        mBinding?.drawer?.closeDrawer(GravityCompat.START)
    }

    fun openSettings(view: View) {
        closeMenu()
        mBinding?.drawer?.openDrawer(GravityCompat.START)
    }

    fun showWxDialog(view: View) {
        mWxDialog.showAllowStateLoss(childFragmentManager, "wx_code")
    }

    fun searchArticles(view: View) {
        closeMenu()
        findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
    }

    fun closeMenu(animate: Boolean = true) {
        mBinding?.floatMenu?.close(animate)
    }
}