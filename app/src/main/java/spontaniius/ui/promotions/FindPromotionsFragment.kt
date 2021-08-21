package com.example.spontaniius.ui.promotions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import spontaniius.MainActivity
import spontaniius.PromotionDetailPage
import spontaniius.PromotionEntry
import spontaniius.R
import kotlinx.android.synthetic.main.activity_main.*

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

class PromotionDetailPage {//Class format taken from [1] https://grokonez.com/android/kotlin-listview-example-android.
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
    private var PromosForList = ArrayList<PromotionEntry>() //[1]
    private var promotionDetailList = ArrayList<PromotionDetailPage>()

   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           // param1 = it.getString(ARG_PARAM1)
           // param2 = it.getString(ARG_PARAM2)
        }
        PromosForList.add(PromotionEntry(1, "FirstYETI", "This is a text feild yooo!")) //[1]
        promotionDetailList.add(PromotionDetailPage(1,"FirstYETI","This is a detail pargarapgh. This code likely requires further rewrites but let's get the intial concept up and running"))
        PromosForList.add(PromotionEntry(2, "YETI^2", "Huh we still out here with text lads"))
        promotionDetailList.add(PromotionDetailPage(2,"YETI^2","YETI*YETI = YETI^2 QUICKMATHS"))
        PromosForList.add(
            PromotionEntry(
                3,
                "YETI YEET",
                "Listening to chillhop at 4:17am and coding. Quarintine life"
            )
        )
        promotionDetailList.add(PromotionDetailPage(3,"YETI YEET","The wild yeti can be heard screaming 'Yeet!' from the mountaintops regularly in this spectacular display of a mating call in the Hymilayas"))
       var promoAdapter = PromoDetailAdapter(requireActivity().application
           ,PromosForList);
        Promotion_List.adapter = promoAdapter
        Promotion_List.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            Toast.makeText(requireActivity().application, "Click on " + PromosForList[position].title, Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, PromotionTab::class.java)

           // val ID = "ID"
           // intent.putExtra(ID, PromosForList[position].id as? Int)
           // startActivity(intent);

        }


    } */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        PromosForList.add(PromotionEntry(1, "FirstYETI", "This is a text feild yooo!")) //[1]
        promotionDetailList.add(PromotionDetailPage(1,"FirstYETI","This is a detail pargarapgh. This code likely requires further rewrites but let's get the intial concept up and running"))
        PromosForList.add(PromotionEntry(2, "YETI^2", "Huh we still out here with text lads"))
        promotionDetailList.add(PromotionDetailPage(2,"YETI^2","YETI*YETI = YETI^2 QUICKMATHS"))
        PromosForList.add(
            PromotionEntry(
                3,
                "YETI YEET",
                "Listening to chillhop at 4:17am and coding. Quarintine life"
            )
        )
        promotionDetailList.add(PromotionDetailPage(3,"YETI YEET","The wild yeti can be heard screaming 'Yeet!' from the mountaintops regularly in this spectacular display of a mating call in the Hymilayas"))
        var inflatedview = inflater.inflate(R.layout.fragment_promotions, container, false)
     //   var Promotion_Listview = inflatedview.findViewById<ListView>(R.id.Promotion_List)
    //    var promoAdapter = PromoDetailAdapter(requireActivity().application,PromosForList);
    //    Promotion_Listview.adapter = promoAdapter
        //Promotion_List.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            //Toast.makeText(requireActivity().application, "Click on " + PromosForList[position].title, Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, PromotionTab::class.java)

            // val ID = "ID"
            // intent.putExtra(ID, PromosForList[position].id as? Int)
            // startActivity(intent);

        //}
        return inflatedview
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        PromosForList.add(PromotionEntry(1, "FirstYETI", "This is a text feild yooo!")) //[1]
        promotionDetailList.add(PromotionDetailPage(1,"FirstYETI","This is a detail pargarapgh. This code likely requires further rewrites but let's get the intial concept up and running"))
        PromosForList.add(PromotionEntry(2, "YETI^2", "Huh we still out here with text lads"))
        promotionDetailList.add(PromotionDetailPage(2,"YETI^2","YETI*YETI = YETI^2 QUICKMATHS"))
        PromosForList.add(
            PromotionEntry(
                3,
                "YETI YEET",
                "Listening to chillhop at 4:17am and coding. Quarintine life"
            )
        )
        promotionDetailList.add(PromotionDetailPage(3,"YETI YEET","The wild yeti can be heard screaming 'Yeet!' from the mountaintops regularly in this spectacular display of a mating call in the Hymilayas"))
        //var promoAdapter = PromoDetailAdapter(requireActivity().application,PromosForList);
        //Promotion_List.adapter = promoAdapter
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

    inner class PromoDetailAdapter : BaseAdapter { //[1]

        private var PromoList = ArrayList<PromotionEntry>()
        private var context: Context? = null

        constructor(context: Context, PromoList: ArrayList<PromotionEntry>){
            this.PromoList = PromoList
            this.context = context
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View?{

            val view: View?
            val viewhold: ViewHolder


            if(convertView == null){
                view = layoutInflater.inflate(R.layout.promotion_entry, parent, false)
                viewhold = ViewHolder(view)
                view.tag = viewhold
            }else{
                view = convertView
                viewhold = view.tag as ViewHolder

            }

            viewhold.tvTitle.text = PromosForList[position].title
            viewhold.tvContent.text = PromosForList[position].content

            return view
        }

        override fun getItem(position: Int): Any {
            return PromosForList[position]
        }

        override fun getItemId(position: Int): Long{
            return position.toLong()
        }

        override fun getCount(): Int {
            return PromosForList.size
        }
    }

    private class ViewHolder(view: View?){//[1]
    val tvTitle: TextView
        val tvContent: TextView

        init {
            this.tvTitle = view?.findViewById(R.id.tvTitle) as TextView
            this.tvContent = view.findViewById(R.id.tvContent) as TextView
        }
    }
}