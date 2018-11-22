package com.djj.xd365.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.djj.xd365.R
import com.djj.xd365.db.StuFile
import com.djj.xd365.db.Stucourselog
import org.devio.takephoto.model.TImage
import java.io.File

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class CheckImageAdapter(val context: Context, data: MutableList<TImage>?,val listener:onTImageItemClickListener) : RecyclerView.Adapter<TImageViewHolder>() {

    val mData: MutableList<TImage> = data ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TImageViewHolder {
        return TImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_check_image, parent, false),listener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: TImageViewHolder?, position: Int) {
        holder?.initView(mData[position])
    }

    fun onRefresh(data: List<TImage>) {
        mData.clear()
        onLoadMore(data)
    }

    fun onLoadMore(data: List<TImage>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun delPhoto(data: TImage?){
        if (data!=null){
            mData.remove(data)
            notifyDataSetChanged()
        }
    }

}

class TImageViewHolder(view: View,listener:onTImageItemClickListener) : RecyclerView.ViewHolder(view) {
    var pic:ImageView
    var del:ImageView
    var data: TImage? = null
    init {
        this.pic = view.findViewById(R.id.pic) as ImageView
        this.del = view.findViewById(R.id.del) as ImageView
        view.setOnClickListener {
            listener.onClick(data)
        }
        del.setOnClickListener{
            listener.onDelClick(data)
        }
    }

    fun initView(stufile: TImage) {
        data = stufile
        pic.setImageURI(null)
        pic.setImageURI(Uri.fromFile(File(stufile.compressPath)))
    }
}

interface onTImageItemClickListener{
    fun onClick(data:TImage?)
    fun onDelClick(data:TImage?)
}
