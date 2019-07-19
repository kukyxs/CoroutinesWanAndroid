package com.kuky.demo.wan.android.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.BaseFragmentPagerAdapter
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.FragmentMainBinding
import com.kuky.demo.wan.android.ui.collection.CollectionFragment
import com.kuky.demo.wan.android.ui.dialog.LoginDialogFragment
import com.kuky.demo.wan.android.ui.home.HomeFragment
import com.kuky.demo.wan.android.ui.hotproject.HotProjectFragment
import com.kuky.demo.wan.android.ui.system.KnowledgeSystemFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterFragment
import com.kuky.demo.wan.android.utils.ApplicationUtils
import com.kuky.demo.wan.android.utils.GalleryTransformer
import com.kuky.demo.wan.android.utils.ScreenUtils
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.user_profile_header.view.*
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

    @SuppressLint("WrongConstant")
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

        mViewModel.hasLogin.value = PreferencesHelper.hasLogin(requireContext())

        view.main_page.offscreenPageLimit = mAdapter.count
        view.main_page.setPageTransformer(true, GalleryTransformer())

        mViewModel.getBanners()

        mViewModel.hasLogin.observe(this, Observer<Boolean> {
            val header = view.user_profile_drawer.getHeaderView(0)

            view.user_profile_drawer.menu.findItem(R.id.user_collections).isVisible = it
            view.user_profile_drawer.menu.findItem(R.id.login_out).isVisible = it

            header.user_name.text =
                if (it) PreferencesHelper.fetchUserName(requireContext())
                else requireContext().getString(R.string.click_to_login)

            header.user_name.setOnClickListener(
                if (it) null
                else View.OnClickListener {
                    LoginDialogFragment().show(childFragmentManager, "login")
                }
            )
        })

        Glide.with(requireContext())
            .load(R.drawable.ava_kuky)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(ScreenUtils.dip2px(requireContext(), 80f))))
            .into(view.user_profile_drawer.getHeaderView(0).avatar)

        view.user_profile_drawer.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.favourite_article -> {
                    CollectionFragment.viewCollections(
                        mNavController,
                        R.id.action_mainFragment_to_collectionFragment,
                        0
                    )
                    view.drawer.closeDrawer(Gravity.START)
                }

                R.id.favourite_website -> {
                    CollectionFragment.viewCollections(
                        mNavController,
                        R.id.action_mainFragment_to_collectionFragment,
                        1
                    )
                    view.drawer.closeDrawer(Gravity.START)
                }

                R.id.about -> {

                }

                R.id.version -> {
                    requireContext()
                        .alert("当前版本为${ApplicationUtils.getAppVersionName(requireContext())}") {
                            yesButton { dialog -> dialog.dismiss() }
                        }.show()
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

    fun openSettings(view: View) {
        mBinding.root.float_menu.close(true)
        mBinding.root.drawer.openDrawer(GravityCompat.START)
    }

    fun searchArticles(view: View) {
        mBinding.root.float_menu.close(false)
        mNavController.navigate(R.id.action_mainFragment_to_searchFragment)
    }
}