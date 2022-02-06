package com.example.mymaps

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap
import com.example.mymaps.utils.CREATE_MAP_TITLE
import com.example.mymaps.utils.EXTRA_USER_MAP
import com.example.mymaps.utils.FILE_NAME
import com.example.mymaps.utils.USER_MAP
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*

class MainActivity : AppCompatActivity() {
    private lateinit var mapAdapter: MapAdapter
    private lateinit var rvMaps: RecyclerView
    private lateinit var userMaps: MutableList<UserMap>
    private lateinit var fabCreateMap: FloatingActionButton

    companion object {
        private val TAG = "MainActivity"
        private val CREATE_MAP_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        rvMaps = findViewById(R.id.rvMaps)
        fabCreateMap = findViewById(R.id.fabCreateMap)
        val mapList = deserializerData(context = this).toMutableList()
        userMaps = (mapList+ generateSampleData()) as MutableList<UserMap>

        mapAdapter = MapAdapter(this, userMaps, object : MapAdapter.OnClickListener {
            override fun onItemClick(position: Int) {
                Log.i(TAG, "click item #$position")
                val i = Intent(this@MainActivity, DisplayMapsActivity::class.java)
                i.putExtra(USER_MAP, userMaps[position])
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_out_right)
            }
        })

        fabCreateMap.setOnClickListener {
            Log.i(TAG, "click fabCreateMap")
            val mapTitleView = LayoutInflater.from(this).inflate(R.layout.map_title, null)
            val dialog = AlertDialog.Builder(this)
                .setTitle("Map title")
                .setView(mapTitleView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", null)
                .show()
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val title = mapTitleView.findViewById<EditText>(R.id.etMapTitle).text.toString()
                if (title.isEmpty()) {
                    Toast.makeText(this, "Filed can't emtpy", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val i = Intent(this@MainActivity, CreateMapsActivity::class.java)
                i.putExtra(CREATE_MAP_TITLE, title)
                startActivityForResult(i, CREATE_MAP_CODE)

                dialog.dismiss()

            }

        }

        rvMaps.layoutManager = LinearLayoutManager(this)
        rvMaps.adapter = mapAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "onActivityResult $data")

        if (requestCode == CREATE_MAP_CODE && resultCode == Activity.RESULT_OK) {
            val userMap = data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
            Log.i(TAG, "onActivityResult $userMap")
            userMaps.add(userMap)
            mapAdapter.notifyItemInserted(userMaps.size - 1)
            serializerData(this, userMaps)
        }

    }

    fun getDataFile(context: Context): File {
        Log.i(TAG, "getDataFile call $FILE_NAME")
        return File(context.filesDir, FILE_NAME)
    }

    fun serializerData(context: Context, listUserMap: List<UserMap>) {
        Log.i(TAG, "serializerData call $listUserMap")
        ObjectOutputStream(FileOutputStream(getDataFile(context))).use { it.writeObject(listUserMap) }
    }

    fun deserializerData(context: Context): List<UserMap> {
        Log.i(TAG, "deserizerData call")
        val dataFile = getDataFile(context)
        if (!dataFile.exists()) {
            return emptyList()
        }
        ObjectInputStream(FileInputStream(dataFile)).use { return it.readObject() as List<UserMap> }
    }

    private fun generateSampleData(): List<UserMap> {
        return listOf(
            UserMap(
                "Memories from University",
                listOf(
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
                    Place(
                        "Gates CS building",
                        "Many long nights in this basement",
                        37.430,
                        -122.173
                    ),
                    Place("Pinkberry", "First date with my wife", 37.444, -122.170)
                )
            ),
            UserMap(
                "January vacation planning!",
                listOf(
                    Place("Tokyo", "Overnight layover", 35.67, 139.65),
                    Place("Ranchi", "Family visit + wedding!", 23.34, 85.31),
                    Place("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
                )
            ),
            UserMap(
                "Singapore travel itinerary",
                listOf(
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
                    Place(
                        "Jurong Bird Park",
                        "Family-friendly park with many varieties of birds",
                        1.319,
                        103.706
                    ),
                    Place("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
                    Place(
                        "Botanic Gardens",
                        "One of the world's greatest tropical gardens",
                        1.3138,
                        103.8159
                    )
                )
            ),
            UserMap(
                "My favorite places in the Midwest",
                listOf(
                    Place(
                        "Chicago",
                        "Urban center of the midwest, the \"Windy City\"",
                        41.878,
                        -87.630
                    ),
                    Place("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
                    Place(
                        "Mackinaw City",
                        "The entrance into the Upper Peninsula",
                        45.777,
                        -84.727
                    ),
                    Place("Michigan State University", "Home to the Spartans", 42.701, -84.482),
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
                )
            ),
            UserMap(
                "Restaurants to try",
                listOf(
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
                    Place("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
                    Place("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
                    Place(
                        "Citizen Eatery",
                        "Bright cafe in Austin with a pink rabbit",
                        30.322,
                        -97.739
                    ),
                    Place(
                        "Kati Thai",
                        "Authentic Portland Thai food, served with love",
                        45.505,
                        -122.635
                    )
                )
            )
        )
    }
}