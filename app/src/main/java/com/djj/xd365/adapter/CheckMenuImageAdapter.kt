package com.djj.xd365.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.djj.xd365.R
import com.djj.xd365.db.CarCheckMenu
import org.devio.takephoto.model.TImage
import org.w3c.dom.Text
import java.io.File

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class CheckMenuImageAdapter(val context: Context, data: MutableList<CarCheckMenu>?, val listener: onCarCheckMenuItemClickListener) : RecyclerView.Adapter<CarCheckMenuViewHolder>() {

    val mData: MutableList<CarCheckMenu> = data ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CarCheckMenuViewHolder {
        return CarCheckMenuViewHolder(LayoutInflater.from(context).inflate(R.layout.item_check_image, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: CarCheckMenuViewHolder?, position: Int) {
        holder?.initView(mData[position])
    }

//    fun onRefresh(data: List<CarCheckMenu>) {
//        mData.clear()
//        onLoadMore(data)
//    }

    fun onLoadMore(image:TImage,data: CarCheckMenu) {
        data.url=image.compressPath
        notifyDataSetChanged()
    }

    fun delPhoto(data: CarCheckMenu?) {
        if (data != null) {
            data.url=""
            notifyDataSetChanged()
        }
    }

}

class CarCheckMenuViewHolder(view: View, listener: onCarCheckMenuItemClickListener) : RecyclerView.ViewHolder(view) {
    var pic: ImageView
    var del: ImageView
    var name_panel: LinearLayout
    var image_panel: RelativeLayout
    var name:TextView
    var data: CarCheckMenu? = null

    init {
        this.pic = view.findViewById(R.id.pic) as ImageView
        this.del = view.findViewById(R.id.del) as ImageView
        this.name_panel = view.findViewById(R.id.name_panel) as LinearLayout
        this.image_panel = view.findViewById(R.id.image_panel) as RelativeLayout
        this.name = view.findViewById(R.id.name) as TextView
        view.setOnClickListener {
            listener.onClick(data)
        }
        del.setOnClickListener {
            listener.onDelClick(data)
        }
    }

    fun initView(stufile: CarCheckMenu) {
        data = stufile
        name.text=stufile.name
        if (stufile.url.isNullOrEmpty()){
            image_panel.visibility=View.GONE
            name_panel.visibility=View.VISIBLE
        }else{
            image_panel.visibility=View.VISIBLE
            name_panel.visibility=View.GONE
            pic.setImageURI(null)
            pic.setImageURI(Uri.fromFile(File(stufile.url)))
        }
    }
}

interface onCarCheckMenuItemClickListener {
    fun onClick(data: CarCheckMenu?)
    fun onDelClick(data: CarCheckMenu?)
}
