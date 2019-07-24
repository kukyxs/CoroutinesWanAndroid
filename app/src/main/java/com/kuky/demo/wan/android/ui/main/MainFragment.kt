package com.kuky.demo.wan.android.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.BaseFragmentPagerAdapter
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.FragmentMainBinding
import com.kuky.demo.wan.android.databinding.UserProfileHeaderBinding
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.collection.CollectionFragment
import com.kuky.demo.wan.android.ui.dialog.AboutUsDialog
import com.kuky.demo.wan.android.ui.dialog.AboutUsHandler
import com.kuky.demo.wan.android.ui.dialog.LoginDialogFragment
import com.kuky.demo.wan.android.ui.home.HomeFragment
import com.kuky.demo.wan.android.ui.hotproject.HotProjectFragment
import com.kuky.demo.wan.android.ui.system.KnowledgeSystemFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterFragment
import com.kuky.demo.wan.android.utils.ApplicationUtils
import com.kuky.demo.wan.android.utils.GalleryTransformer
import com.kuky.demo.wan.android.utils.LogUtils
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description 主页面 fragment 持有者
 */
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val mAdapter: BaseFragmentPagerAdapter by lazy {
        BaseFragmentPagerAdapter(
            childFragmentManager, arrayListOf(
                HomeFragment(),
                HotProjectFragment(),
                KnowledgeSystemFragment(),
                WxChapterFragment()
            )
        )
    }

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders
            .of(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_main

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@MainFragment
        mBinding.viewModel = mViewModel
        mBinding.adapter = mAdapter
        mBinding.listener = OnBannerListener { position ->
            mViewModel.banners.value?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_mainFragment_to_websiteDetailFragment,
                    it[position].url
                )
            }
        }

        // Bind NavigationView Header Layout
        val headerBinding = DataBindingUtil.inflate<UserProfileHeaderBinding>(
            layoutInflater, R.layout.user_profile_header, view.user_profile_drawer, false
        )
        headerBinding.holder = this@MainFragment

        mViewModel.hasLogin.value = PreferencesHelper.hasLogin(requireContext())

        view.main_page.offscreenPageLimit = mAdapter.count
        view.main_page.setPageTransformer(true, GalleryTransformer())

        view.user_profile_drawer.addHeaderView(headerBinding.root)

        mViewModel.getBanners()

        mViewModel.hasLogin.observe(this, Observer<Boolean> {
            val menus = view.user_profile_drawer.menu

            menus.findItem(R.id.user_collections).isVisible = it
            menus.findItem(R.id.login_out).isVisible = it
            menus.findItem(R.id.todo_list).isVisible = it

            headerBinding.name =
                if (it) PreferencesHelper.fetchUserName(requireContext())
                else requireContext().getString(R.string.click_to_login)
        })

        handleUserProfile()
    }

    @SuppressLint("WrongConstant")
    private fun handleUserProfile() {
        // TODO("由于 NavigationView menu.xml 不支持 dataBinding 绑定，目前未想到更好办法进行处理")
        mBinding.root.user_profile_drawer.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.favourite_article -> {
                    CollectionFragment.viewCollections(
                        mNavController,
                        R.id.action_mainFragment_to_collectionFragment,
                        0
                    )
                    mBinding.root.drawer.closeDrawer(Gravity.START)
                }

                R.id.favourite_website -> {
                    CollectionFragment.viewCollections(
                        mNavController,
                        R.id.action_mainFragment_to_collectionFragment,
                        1
                    )
                    mBinding.root.drawer.closeDrawer(Gravity.START)
                }

                R.id.finish_todo -> {

                }

                R.id.todos -> {

                }

                R.id.about -> {
                    AboutUsDialog().setHandler(object : AboutUsHandler {
                        override fun spanClick(url: String) {
                            WebsiteDetailFragment.viewDetail(
                                mNavController,
                                R.id.action_mainFragment_to_websiteDetailFragment,
                                url
                            )
                            mBinding.root.drawer.closeDrawer(Gravity.START)
                        }
                    }).show(childFragmentManager, "about")
                }

                R.id.version -> {
                    requireContext()
                        .alert("当前版本为${ApplicationUtils.getAppVersionName(requireContext())}") {
                            yesButton { dialog -> dialog.dismiss() }
                        }.show()
                }

                R.id.go_star -> {
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        "https://github.com/kukyxs/CoroutinesWanAndroid"
                    )
                    mBinding.root.drawer.closeDrawer(Gravity.START)
                }

                R.id.login_out -> {
                    requireContext()
                        .alert("是否退出登录") {
                            yesButton { mViewModel.loginout() }
                            noButton { }
                        }.show()
                }
            }
            true
        }
    }

    /**
     * click to login in Navigation HeaderLayout
     */
    fun headerLogin(view: View) {
        mViewModel.hasLogin.observe(this, Observer<Boolean> {
            if (!it) {
                LoginDialogFragment().show(childFragmentManager, "login")
            }
        })
    }

    fun openSettings(view: View) {
        mBinding.root.float_menu.close(true)
        mBinding.root.drawer.openDrawer(GravityCompat.START)
    }

    fun searchArticles(view: View) {
        mBinding.root.float_menu.close(false)
        mNavController.navigate(R.id.action_mainFragment_to_searchFragment)
    }
}