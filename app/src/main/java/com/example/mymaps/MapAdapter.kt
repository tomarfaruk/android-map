package com.example.mymaps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.UserMap

class MapAdapter(
    private val context: Context,
    private val userMapList: List<UserMap>,
    val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<MapAdapter.ViewHolder>() {

    companion object {
        private val TAG = "MapAdapter"
    }

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {

            val userMap = userMapList[position]
            val textView = itemView.findViewById<TextView>(R.id.tvUserMapName)
            textView.setOnClickListener {
                Log.i(TAG, "item no: $position clicked")
                onClickListener.onItemClick(position)
            }
            textView.text = userMap.title

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.user_map_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return userMapList.size
    }

}
