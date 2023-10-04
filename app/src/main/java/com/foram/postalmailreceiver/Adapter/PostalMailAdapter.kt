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
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.Model.UserType
import com.foram.postalmailreceiver.R

class PostalMailAdapter(
    private val data: ArrayList<PostalMailModel>,
    val context: Context?,
    val userModel: UserModel
) : RecyclerView.Adapter<PostalMailAdapter.ViewHolder> () {


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ivPostalMailImage: ImageView = itemView.findViewById(R.id.ivPostalMailImage)
        val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvSendBySendToTitle: TextView = itemView.findViewById(R.id.tvSendBySendToTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_postal_mail_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Using Glide to load image from URL into ImageView
        Glide.with(holder.itemView.context)
            .load(data[position].imageUri)
            .into(holder.ivPostalMailImage)

        holder.tvDateTime.text = data[position].dateTime

        if (userModel.userType == UserType.MailReceiver) {

            holder.tvSendBySendToTitle.text = "Sended by: "
            holder.tvSenderName.text = data[position].senderName

            holder.itemView.setOnClickListener {
                val intent = Intent(context, MailDetailActivity::class.java)
                intent.putExtra("postal_mail_model", data[position])
                context?.startActivity(Intent(intent))
            }
        }
        else{
            holder.tvSendBySendToTitle.text = "Sended to: "
            holder.tvSenderName.text = data[position].receiverName
        }
    }

    override fun getItemCount() = data.size
}