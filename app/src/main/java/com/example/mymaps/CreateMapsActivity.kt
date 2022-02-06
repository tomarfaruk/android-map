package com.example.mymaps

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mymaps.databinding.ActivityCreateMapsBinding
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap
import com.example.mymaps.utils.CREATE_MAP_TITLE
import com.example.mymaps.utils.EXTRA_USER_MAP
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar

class CreateMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapsBinding

    private var markers: MutableList<Marker> = mutableListOf()

    companion object {
        private val TAG = "CreateMapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = intent.getStringExtra(CREATE_MAP_TITLE) ?: ""

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mi_save_map) {
            if (markers.isEmpty()) {
                Toast.makeText(this, "There must be at least one marker", Toast.LENGTH_LONG).show()
                return true
            }
            val places = markers.map { marker ->
                Place(
                    marker.title,
                    marker.snippet,
                    marker.position.latitude,
                    marker.position.longitude
                )
            }
            val userMap = UserMap(intent.getStringExtra(CREATE_MAP_TITLE) ?: "", places)
            val i = Intent()
            i.putExtra(EXTRA_USER_MAP, userMap)
            setResult(Activity.RESULT_OK, i)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener { marker ->
            Log.i(TAG, "$marker")
            markers.remove(marker)
            marker.remove()
        }
        mMap.setOnMapLongClickListener { latlog ->
            Log.i(TAG, "setOnMapClickListener")

            val placeFormView = LayoutInflater.from(this).inflate(R.layout.place_form, null)
            val dialog = AlertDialog.Builder(this).setTitle("Create Marker")
                .setView(placeFormView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("ok", null)
                .show()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
                val name = placeFormView.findViewById<EditText>(R.id.etName).text.toString()
                if (title.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this, "Title and name must non empty", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val marker =
                    mMap.addMarker(MarkerOptions().position(latlog).title(title).snippet(name))
                markers.add(marker)
                dialog.dismiss()
            }

        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(24.50, 88.880)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}