package com.ssafy.smile.presentation.view.portfolio

import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.smile.R
import com.ssafy.smile.common.util.Constants
import com.ssafy.smile.common.util.NetworkUtils
import com.ssafy.smile.databinding.FragmentPortfolioBinding
import com.ssafy.smile.domain.model.PortfolioDomainDto
import com.ssafy.smile.domain.model.Types
import com.ssafy.smile.presentation.adapter.PortfolioViewPagerAdapter
import com.ssafy.smile.presentation.base.BaseFragment
import com.ssafy.smile.presentation.view.user.SignUp2FragmentArgs
import com.ssafy.smile.presentation.viewmodel.portfolio.PortfolioViewModel

private const val TAG = "PortfolioFragment_스마일"
class PortfolioFragment() : BaseFragment<FragmentPortfolioBinding>(FragmentPortfolioBinding::bind, R.layout.fragment_portfolio) {

    private val portfolioViewModel: PortfolioViewModel by navGraphViewModels(R.id.portfolioGraph)
    private val args: PortfolioFragmentArgs by navArgs()
    var photographerId = -1L

    override fun initView() {
        initToolbar()
        setPhotographerId()
        initViewPager()
        portfolioViewModel.getPortfolio(photographerId)
        setObserver()
    }

    private fun setPhotographerId() {
        photographerId = args.photographerId
    }

    private fun initToolbar(){
        val toolbar : Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("프로필", true) { moveToPopUpSelf() }
    }

    private fun moveToPopUpSelf() = findNavController().navigate(R.id.action_portfolioFragment_pop)

    private fun setObserver() {
        portfolioResponseObserver()
        photographerHeartResponseObserver()
    }

    override fun setEvent() {
        setRefreshLayoutEvent()
        binding.apply {
            ctvLike.setOnClickListener {
                portfolioViewModel.photographerHeart(photographerId)
            }
            btnReservation.setOnClickListener {
                val action = PortfolioFragmentDirections.actionPortfolioFragmentToReservationFragment(photographerId)
                findNavController().navigate(action)
            }
            btnWritePost.setOnClickListener {
                findNavController().navigate(R.id.action_portfolioFragment_to_writePostFragment)
            }
        }
    }

    private fun setRefreshLayoutEvent() {
        binding.apply {
            refreshLayout.setOnRefreshListener {
                portfolioViewModel.getPortfolio(photographerId)
                refreshLayout.isRefreshing = false
            }
        }
    }

    private fun initViewPager() {
        val viewPagerAdapter = PortfolioViewPagerAdapter(requireActivity())
        val tabTitle = listOf("게시물", "작가 리뷰")

        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()
    }

    private fun portfolioResponseObserver() {
        portfolioViewModel.getPortfolioResponse.observe(viewLifecycleOwner) {
            when(it) {
                is NetworkUtils.NetworkResponse.Success -> {
                    dismissLoadingDialog()
                    setPhotographerInfo(it.data.toPortfolioDomainDto())
                }
                is NetworkUtils.NetworkResponse.Failure -> {
                    dismissLoadingDialog()
                    Log.d(TAG, "portfolioResponseObserver: ${it.errorCode}")
                    showToast(requireContext(), "작가 포트폴리오 조회 요청에 실패했습니다. 다시 시도해주세요.", Types.ToastType.WARNING)
                }
                is NetworkUtils.NetworkResponse.Loading -> {
                    showLoadingDialog(requireContext())
                }
            }
        }
    }

    private fun photographerHeartResponseObserver() {
        binding.apply {
            portfolioViewModel.photographerHeartResponse.observe(viewLifecycleOwner) {
                when(it) {
                    is NetworkUtils.NetworkResponse.Loading -> { }
                    is NetworkUtils.NetworkResponse.Success -> {
                        ctvLike.toggle()
                        portfolioViewModel.getPortfolio(photographerId)
                    }
                    is NetworkUtils.NetworkResponse.Failure -> {
                        showToast(requireContext(), "작가 좋아요 요청에 실패했습니다. 다시 시도해주세요.", Types.ToastType.WARNING)
                    }
                }
            }
        }
    }

    private fun setPhotographerInfo(portfolioDomainDto: PortfolioDomainDto) {
        binding.apply {
            setButtons(portfolioDomainDto.isMe)
            Glide.with(requireContext())
                .load(Constants.IMAGE_BASE_URL+portfolioDomainDto.profileImg)
                .into(ivProfile)
            tvCategory.text = portfolioDomainDto.category
            tvName.text = portfolioDomainDto.photographerName
            tvPlace.text = portfolioDomainDto.place
            tvIntroduction.text = portfolioDomainDto.introduction
            ctvLike.isChecked = portfolioDomainDto.isHeart
            tvLike.text = portfolioDomainDto.hearts.toString()
        }
    }

    private fun setButtons(isMe: Boolean) {
        binding.apply {
            if (isMe) {
                btnWritePost.visibility = View.VISIBLE
            } else {
                btnReservation.visibility = View.VISIBLE
            }
        }
    }

}