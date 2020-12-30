package com.example.loginmaterialg1.exam1

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginmaterialg1.Model._Product
import com.example.loginmaterialg1.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.card_custom.view.*
import java.lang.Exception


class productoList_adapter(
    private val exampleList: MutableList<_Product>,
    private val listener: OnItemClickListener
) :RecyclerView.Adapter<productoList_adapter.ExampleViewHolder>() {
    private var mStorageRef: StorageReference? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.card_custom,
                parent, false
            )
            return ExampleViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
            val currentItem = exampleList[position]
            holder.textView1.text = currentItem.description
            holder.textView2.text = currentItem.category
            getBitmapFromURL(currentItem.photo,holder.imageView)
        }
        override fun getItemCount() = exampleList.size
        fun getBitmapFromURL(src: String?, imageView: ImageView) {
            val ONE_MEGABYTE: Long = 1024 * 1024
            mStorageRef = FirebaseStorage.getInstance().reference;
            val islandRef = mStorageRef?.storage?.getReferenceFromUrl(src!!)
            try {
                islandRef!!.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                        ByteArray->
                    val bmp = BitmapFactory.decodeByteArray(ByteArray, 0, ByteArray.size)
                    imageView.setImageBitmap(bmp)
                }.addOnFailureListener {
                        Exception->
                    Exception.message?.let { Log.d("myTag", it) }
                }
            }catch (ex: Exception){
                ex.message?.let { Log.d("myTag", it) }
            }
        }
        inner class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener{
            val imageView: ImageView = itemView.image_view
            val textView1: TextView = itemView.text_view_1
            val textView2: TextView = itemView.text_view_2
            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }
            override fun onClick(v: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position)
                }
            }

            override fun onLongClick(v: View?): Boolean {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemLongClick(position)
                }
                return true
            }

        }
        interface OnItemClickListener{
            fun onItemClick(position: Int)
            fun onItemLongClick(position: Int)
        }
}