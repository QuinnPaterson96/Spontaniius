package com.example.spontaniius.ui.find_event

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.spontaniius.R
import com.example.spontaniius.dependency_injection.VolleySingleton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class FindEventFragment : Fragment() {
    private var eventList: ArrayList<EventTile> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var viewAdapter: RecyclerView.Adapter<*> = EventFindAdapter(eventList)
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var swipeContainer:SwipeRefreshLayout

    companion object {
        fun newInstance() = FindEventFragment()
    }

    private lateinit var viewModel: FindEventViewModel
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val exampleList: ArrayList<EventTile> = ArrayList()
        exampleList.add(EventTile(R.drawable.ic_cofee_24, "Line 1", "Line 2"))
        exampleList.add(EventTile(R.drawable.ic_cofee_24, "Line 3", "Line 4"))
        exampleList.add(EventTile(R.drawable.ic_cofee_24, "Line 5", "Line 6"))

        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_find_event, container, false)
        swipeContainer = view.findViewById(R.id.swipeContainer);
        recyclerView = view.findViewById(R.id.recyclerview);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        recyclerView.setLayoutManager(LinearLayoutManager(view.context));
        recyclerView.setAdapter(viewAdapter);
        getEvents()


        // Setup refresh listener which triggers new data loading
        swipeContainer!!.setOnRefreshListener() {
            getEvents()

        }
        // Configure the refreshing colors
        swipeContainer!!.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FindEventViewModel::class.java)
        // TODO: Use the ViewModel
    }


    fun getEvents(){
        val queue = this!!.context?.let { VolleySingleton.getInstance(it).requestQueue }

        // ...

        // Add a request (in this example, called stringRequest) to your RequestQueue.

             var streetAddress = "49.627795, -123.199644"
        if (this!!.getContext()?.let { ContextCompat.checkSelfPermission(it,android.Manifest.permission.ACCESS_COARSE_LOCATION ) } != PackageManager.PERMISSION_GRANTED )

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                streetAddress=location.toString()
            }


        val url =
            "https://2xhan6hu38.execute-api.us-west-2.amazonaws.com/default/GetEventsInArea?streetAddress=$streetAddress"
            val getEventsRequest: StringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    eventList.clear()
                    val newList: ArrayList<EventTile> = ArrayList()
                val eventJSONArray= JSONArray(response.toString())
                    for (i in 0 until eventJSONArray.length()) {
                        val event:JSONObject = eventJSONArray.getJSONObject(i)
                        newList.add(EventTile(R.drawable.ic_cofee_24, event.get("eventtitle").toString(),event.get("eventtext").toString()))

                        /*
                        * "cardids": null,
                          "eventid": 1,
                          "eventtitle": "Lamb party",
                          "eventtext": "Test from Lambda",
                          "genderrestrict": "any",
                          "streetaddress": {
                            "x": 48.4335854,
                            "y": -123.3371036
                          },
                          "icon": "01.jpg",
                          "maxradius": 100,
                          "eventstarts": "11:39:27.331481",
                          "eventends": "11:39:27.331481"*/
                    }

                    eventList.addAll(newList)
                    viewAdapter.notifyDataSetChanged()
                    swipeContainer.isRefreshing = false

            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )
        queue?.add(getEventsRequest)
        }
    }
