package com.example.spontaniius.ui.create_event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.spontaniius.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsFragment : Fragment() {

    private lateinit var confirmButton: FloatingActionButton
    private lateinit var latLng: LatLng
    private var parent: ViewGroup? = null
    private var marker: Marker? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        googleMap.setOnMapLongClickListener { latLng ->
            marker?.remove()
            marker =
                googleMap.addMarker(MarkerOptions().position(latLng).title("Location of event"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            this.latLng = latLng
        }
    }

    private fun createConfirmButton() {
        val c = context
        confirmButton = FloatingActionButton(c)
        confirmButton.maxWidth = 10
        confirmButton.maxHeight = 10
        confirmButton.setImageResource(R.drawable.ic_baseline_check_24)
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        params.setMargins(2, 2, 2, 2)
        confirmButton.layoutParams = params
        parent?.addView(confirmButton)
        if (c != null && c is MapsInteractionListener) {
            confirmButton.setOnClickListener {
                if (this::latLng.isInitialized) {
                    c.onLocationSelected(latLng)
                }
            }
        } else {
            throw RuntimeException("Base context must implement the MapsInteractionListener interface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        this.parent = container
        createConfirmButton()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    override fun onDestroy() {
        parent?.removeView(confirmButton)
        super.onDestroy()
    }

    interface MapsInteractionListener {
        fun onLocationSelected(latLng: LatLng)
    }


}