package com.hackathon.pesttracker

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gallery_list_item.view.*

class PhotoGalleryAdapter(activity: Activity, val clickListener: (FirebasePhotoResponse) -> Unit, columnCount: Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSource = PhotoResponseList()

    private var imageSize: Int = 0

    init {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        imageSize = (displayMetrics.widthPixels / columnCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.gallery_list_item)
        return ThumbnailViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as ThumbnailViewHolder
        holder.bind(dataSource.list[position])
    }

    override fun getItemCount() = dataSource.list.size

    fun setData(data: PhotoResponseList) {
        dataSource = data
        notifyDataSetChanged()
    }

    inner class ThumbnailViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            val layoutParams = RelativeLayout.LayoutParams(imageSize, imageSize)
            view.gallery_image.layoutParams = layoutParams
        }

        fun bind(photo: FirebasePhotoResponse) = with(itemView) {
            gallery_image.loadImage(photo.thumbnail)
            setOnClickListener { clickListener(photo) }
        }
    }
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.loadImage(imageUrl: String) {
    Picasso.with(context).load(imageUrl).into(this)
}