package com.djj.xd365.activity

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.djj.xd365.R

/**
 * Created by liufeng on 2018-07-03.
 * 用于 xd365
 */
class PhotoSelFragment :DialogFragment() {

    var listener:OnPhotoTaskSel?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view=inflater?.inflate(R.layout.fragment_photo_sel, container)
        if (view!=null){
            view.findViewById<Button>(R.id.camera_take).setOnClickListener{
                listener?.onCamera()
                dismiss()
            }
            view.findViewById<Button>(R.id.file_take).setOnClickListener{
                listener?.onSelPhoto()
                dismiss()
            }
            view.findViewById<Button>(R.id.cancel).setOnClickListener{dismiss()}
            return view
        }else{
            val text=TextView(activity)
            text.text="异常错误"
            return text
        }


    }

    interface OnPhotoTaskSel{
        fun onCamera()
        fun onSelPhoto()
    }
}