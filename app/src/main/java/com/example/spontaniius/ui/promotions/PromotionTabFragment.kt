package com.example.spontaniius.ui.promotions

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.spontaniius.R

class PromotionTabFragment : Fragment() {

    companion object {
        fun newInstance() = PromotionTabFragment()
    }

    private lateinit var viewModel: PromotionTabViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find_event, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PromotionTabViewModel::class.java)
        // TODO: Use the ViewModel
    }

}