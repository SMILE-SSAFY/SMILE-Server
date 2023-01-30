package com.ssafy.smile.presentation.view.home

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ssafy.smile.R
import com.ssafy.smile.common.util.setOnSingleClickListener
import com.ssafy.smile.databinding.FragmentAddressBinding
import com.ssafy.smile.domain.model.AddressDomainDto
import com.ssafy.smile.domain.model.Types
import com.ssafy.smile.presentation.adapter.AddressRVAdapter
import com.ssafy.smile.presentation.base.BaseBottomSheetDialogFragment
import com.ssafy.smile.presentation.viewmodel.home.AddressGraphViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressFragment : BaseBottomSheetDialogFragment<FragmentAddressBinding>(FragmentAddressBinding::inflate) {

    private val viewModel : AddressGraphViewModel by viewModels()
    private lateinit var rvAdapter : AddressRVAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                isDraggable = false
            }
            setupRatio(bottomSheet, 90)
        }
        return dialog
    }

    override fun initView() {
        initRVAdapter()
        binding.apply {
            lAddressSearch.btnAddressSearch.setOnSingleClickListener {
                moveToAddressSearchFragment()
            }
            btnAddressMapSearch.setOnClickListener {
                moveToAddressMapFragment()
            }
        }
    }

    override fun setEvent() {
        viewModel.apply {
            getAddressListResponseLiveData.observe(viewLifecycleOwner){
                if (it.isNullOrEmpty()){
                    setEmptyView(true)
                    setRVView(false)
                }else{
                    setEmptyView(false)
                    setRVView(true, it as ArrayList<AddressDomainDto>)
                }
            }
            selectedAddressResponseLiveData.observe(viewLifecycleOwner){
                showToast(requireContext(), getString(R.string.msg_address_success), Types.ToastType.SUCCESS)
                moveToPopUpSelf()
            }
        }
    }

    private fun initRVAdapter(){
        binding.apply {
            rvAdapter = AddressRVAdapter().apply {
                setItemClickListener(object : AddressRVAdapter.ItemClickListener{
                    override fun onClickItem(view: View, position: Int, addressDomainDto: AddressDomainDto) {
                        lifecycleScope.launch(Dispatchers.IO){ viewModel.selectAddress(addressDomainDto.apply { isSelected = true }) }
                    }
                    override fun onClickRemove(view: View, position: Int, addressDomainDto: AddressDomainDto) {
                        lifecycleScope.launch(Dispatchers.IO){ viewModel.deleteAddress(addressDomainDto) }
                    }
                })
            }
            binding.rvAddress.adapter = rvAdapter
        }
    }

    private fun setEmptyView(isSet : Boolean){
        if (isSet){
            binding.layoutAddressEmptyView.layoutEmptyView.visibility = View.VISIBLE
            binding.layoutAddressEmptyView.tvEmptyView.text = getString(R.string.tv_address_empty)
        }else binding.layoutAddressEmptyView.layoutEmptyView.visibility = View.GONE
    }

    private fun setRVView(isSet : Boolean, itemList : ArrayList<AddressDomainDto> = arrayListOf()){
        if (isSet) {
            binding.layoutRvAddress.visibility = View.VISIBLE
            rvAdapter.setListData(itemList)
        } else binding.layoutRvAddress.visibility = View.GONE
    }

    private fun moveToPopUpSelf() { findNavController().navigate(R.id.action_addressFragment_pop) }
    private fun moveToAddressSearchFragment() = findNavController().navigate(R.id.action_addressFragment_to_addressSearchFragment)
    private fun moveToAddressMapFragment() = findNavController().navigate(R.id.action_addressFragment_to_addressMapFragment)

}