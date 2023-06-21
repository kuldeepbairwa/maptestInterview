package com.kuldeep.maptestinterview.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.kuldeep.maptestinterview.room.MyRoomDB

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    open lateinit var mContext: Context
    protected var room: MyRoomDB? = null
    protected lateinit var binding: VB

    protected abstract fun getViewBinding(): VB
    open fun listeners() {}

    override fun onAttach(context: Context) {
        mContext = context

        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        initVariables()
        listeners()
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = getViewBinding()
        room = MyRoomDB.getDatabase(mContext)
        bindViewModels()
        updateUi()
        return binding.root
    }




    open fun showLoading(){ }

    open fun hideLoading() { }

    open fun updateUi() { }

    open fun initVariables() { }

    open fun attachObservers() { }

    open fun bindViewModels() {}


}