package com.example.spontaniius.ui.promotions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spontaniius.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FindPromotionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class PromotionEntry {//Class format taken from [1] https://grokonez.com/android/kotlin-listview-example-android.
var id: Int? = null
    var title: String? = null
    var content: String? = null

    constructor(id: Int, title: String, content: String){
        this.id = id
        this.title = title
        this.content = content
    }
}



class FindPromotionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           // param1 = it.getString(ARG_PARAM1)
           // param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_promotions, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FindPromotionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FindPromotionsFragment().apply {
                arguments = Bundle().apply {
                 //   putString(ARG_PARAM1, param1)
                  //  putString(ARG_PARAM2, param2)
                }
            }
    }
}