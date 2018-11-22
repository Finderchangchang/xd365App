package com.djj.xd365.adapter

import android.content.Context
import android.simple.toolbox.widget.CornerImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.djj.xd365.R
import com.djj.xd365.db.XDMenu

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class MainMenuAdapter(val context: Context,menu: XDMenu?) : BaseAdapter() {
    var menuList=mutableListOf<Map<String,Int>>()
    init {
        val p=menu
        if(p!=null){
            if(p.bqStudy){
                val map= mapOf<String,Int>("color" to R.color.metro1,"image" to R.drawable.bqstudy,"name" to R.string.bqstudy,"count" to 0)
                menuList.add(map)
            }
            if (p.zzStudy){
                val map= mapOf<String,Int>("color" to R.color.metro2,"image" to R.drawable.zzstudy,"name" to R.string.zzstudy,"count" to 0)
                menuList.add(map)
            }
            if (p.exam){
                val map= mapOf<String,Int>("color" to R.color.metro3,"image" to R.drawable.exam,"name" to R.string.exam,"count" to 0)
                menuList.add(map)
            }
            if (p.studyLog){
                val map= mapOf<String,Int>("color" to R.color.metro4,"image" to R.drawable.study_log,"name" to R.string.studylog,"count" to 0)
                menuList.add(map)
            }
            if (p.carPosition){
                val map= mapOf<String,Int>("color" to R.color.metro5,"image" to R.drawable.carposition,"name" to R.string.carPosition,"count" to 0)
                menuList.add(map)
            }
            if (p.violation){
                val map= mapOf<String,Int>("color" to R.color.metro6,"image" to R.drawable.violation,"name" to R.string.violation,"count" to 0)
                menuList.add(map)
            }
            if (p.roadInfo){
                val map= mapOf<String,Int>("color" to R.color.metro7,"image" to R.drawable.roadinfo,"name" to R.string.roadInfo,"count" to 0)
                menuList.add(map)
            }
            if(p.carCheck){
                val map= mapOf<String,Int>("color" to R.color.metro9,"image" to R.drawable.carcheck,"name" to R.string.carcheck,"count" to 0)
                menuList.add(map)
            }
            if (p.comMessage){
                val map= mapOf<String,Int>("color" to R.color.metro8,"image" to R.drawable.message,"name" to R.string.comMessage,"count" to 0)
                menuList.add(map)
            }

        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        var  view:View?=null
        if(convertView == null) {
            //使用自定义的list_items作为Layout
            view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
            holder=ViewHolder(view)
            //设置标记
            view.setTag(holder);
        }else{
            view=convertView
            holder=convertView.getTag() as ViewHolder
        }

        val user = menuList[position]
        if(user["name"]==R.string.comMessage){
            if(user["count"]!! <=0){
                holder.corner.visibility=View.GONE
            }else{
                holder.corner.visibility=View.VISIBLE
                holder.corner.setCornerText(user["count"].toString())
            }

        }else{
            holder.corner.visibility=View.GONE
        }
        holder.name.setText(user["name"] as Int)
        holder.image.setImageResource(user["image"] as Int)
        holder.background.setBackgroundResource(user["color"] as Int)
        if(view!=null){
            return view
        }else{
            val text= TextView(context)
            text.text="错误"
            return text
        }
    }

    /**
     * ViewHolder类
     */
    internal class ViewHolder(container: View) {

        var name: TextView
        var image: ImageView
        var background: RelativeLayout
var corner:CornerImageView
        init {
            this.name = container.findViewById(R.id.name) as TextView
            this.image = container.findViewById(R.id.image) as ImageView
            this.background=container.findViewById(R.id.background) as RelativeLayout
            this.corner=container.findViewById(R.id.corner) as CornerImageView
        }
    }

    override fun getItem(position: Int): Any {
        return menuList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return menuList.size
    }

    fun updateCornerCount(nameRes:Int,count:Int){
        for (index in menuList.indices){
            if (menuList[index]["name"]==nameRes){
                val map= mapOf<String,Int>("color" to menuList[index]["color"]!!,"image" to menuList[index]["image"]!!,"name" to menuList[index]["name"]!!,"count" to count)
                menuList.removeAt(index)
                menuList.add(index,map)
            }
        }
        notifyDataSetChanged()
    }
}