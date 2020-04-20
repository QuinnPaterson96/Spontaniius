package com.example.spontaniius

import android.content.Intent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*


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


class MainActivity : AppCompatActivity() { //So I have random notations pointing to a tutorial, we'll see what happens with that later

//To do: get list view working

    //Default Repo: will have a bunch of infomation. Get data from there after debug builds (Wat dis mean??)

    private var PromosForList = ArrayList<PromotionEntry>() //[1]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PromosForList.add(PromotionEntry(1, "FirstYETI", "This is a text feild yooo!")) //[1]
        PromosForList.add(PromotionEntry(2, "YETI^2", "Huh we still out here with text lads"))
        PromosForList.add(
            PromotionEntry(
                3,
                "YETI YEET",
                "Listening to chillhop at 4:17am and coding. Quarintine life"
            )
        )

        var promoAdapter = PromosAdapter(this, PromosForList)
        Promotion_List.adapter = promoAdapter
        Promotion_List.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            Toast.makeText(this, "Click on " + PromosForList[position].title, Toast.LENGTH_SHORT).show()
        }


        /*val button1: Button = findViewById(R.id.UwU)//UwU UwU UwU UwU UwU
        button1.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, PromotionTab::class.java)
            startActivity(intent);
        }*/
        //val myStringArray = arrayOf("Element 1", "Element 2", "Element 3")
        //val adapter = ArrayAdapter<String>(
          // this,
           // android.R.layout.simple_list_item_1,
           // myStringArray
       // )  //This helps set up a layout? https://developer.android.com/guide/topics/ui/declaring-layout.html#FillingTheLayout
        //val listView: ListView = findViewById(R.id.list_view)
        //listView.adapter = adapter

    }
    inner class PromosAdapter : BaseAdapter { //[1]

        private var PromoList = ArrayList<PromotionEntry>()
        private var context: Context? = null

        constructor(context:Context,PromoList: ArrayList<PromotionEntry>){
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
            this.tvContent = view?.findViewById(R.id.tvContent) as TextView
        }
    }


}

