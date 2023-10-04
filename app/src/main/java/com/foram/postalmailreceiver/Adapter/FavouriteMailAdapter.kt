package com.foram.postalmailreceiver.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foram.postalmailreceiver.Activity.MailDetailActivity
import com.foram.postalmailreceiver.Model.PostalMailModel
import com.foram.postalmailreceiver.R

class FavouriteMailAdapter(private val data: ArrayList<PostalMailModel>, val context: Context?) :
    RecyclerView.Adapter<FavouriteMailAdapter.ViewHolder>() {
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ivPostalMailImage: ImageView = itemView.findViewById(R.id.ivPostalMailImage)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val ivIsFav: ImageView = itemView.findViewById(R.id.ivIsFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_favourite_mail_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Using Glide to load image from URL into ImageView
        Glide.with(holder.itemView.context)
            .load(data[position].imageUri)
            .into(holder.ivPostalMailImage)

        holder.tvDateTime.text = data[position].dateTime
        if (data[position].favourite) {
            holder.ivIsFav.setImageResource(R.drawable.ic_favourite)
        } else {
            holder.ivIsFav.setImageResource(R.drawable.ic_unfavourite)
        }

        holder.ivIsFav.setOnClickListener {
            if (data[position].favourite) {
                holder.ivIsFav.setImageResource(R.drawable.ic_favourite)

                // ToDo save fav or unfav to firebase database
            } else {
                holder.ivIsFav.setImageResource(R.drawable.ic_unfavourite)

                // ToDo save fav or unfav to firebase database
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MailDetailActivity::class.java)
            intent.putExtra("postal_mail_model", data[position])
            context?.startActivity(Intent(intent))
        }
    }

    override fun getItemCount() = data.size
}