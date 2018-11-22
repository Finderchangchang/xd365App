package com.djj.xd365.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AbsListView
import com.djj.xd365.R
import com.jpay.JPay
import kotlinx.android.synthetic.main.dialog_pay.*
import java.util.*

/**
 * 选择支付方式Dialog
 * Created by Hh on 2017/3/6.
 */
/**
 * 如果ifRecharge 传入true 则是充值,就隐藏掉我的钱包, 否则则显示
 *
 * @param context
 * @param themeResId
 * @param onClickListener
 */
class PayWayDialog(context: Context, themeResId: Int, private val onPositive: OnPayWayDialogPositive) : Dialog(context, themeResId), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pay)
        val dialogWindow = window
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        val lp = dialogWindow.attributes
        lp.width = AbsListView.LayoutParams.MATCH_PARENT
        lp.y = 0//设置Dialog距离底部的距离
        dialogWindow.attributes = lp
        dialog_confirm_pay.setOnClickListener {
            val mode = getChecked()
            onPositive.onPositive(mode)
            dismiss()
        }
        recharge_dialog_close.setOnClickListener(this)
        dialog_zhifubao.setOnClickListener(this)
        dialog_wechat.setOnClickListener(this)

    }

    interface OnPayWayDialogPositive {
        fun onPositive(mode: JPay.PayMode)
    }

    /**
     * 设置充值金额
     *
     * @param num
     */
    fun setRechargeNum(num: Double) {
        recharge_num.text = "￥ " + String.format(Locale.CHINA, "%.2f", num)
    }

    override
    fun onClick(view: View) {
        when (view.id) {
            R.id.recharge_dialog_close -> dismiss()
            R.id.dialog_zhifubao -> {
                recharge_zhifubao_cb.visibility = View.VISIBLE
                recharge_wechat_cb.visibility = View.GONE
                mode = JPay.PayMode.ALIPAY
            }
            R.id.dialog_wechat -> {
                recharge_zhifubao_cb.visibility = View.GONE
                recharge_wechat_cb.visibility = View.VISIBLE
                mode = JPay.PayMode.WXPAY
            }
        }
    }

    private var mode = JPay.PayMode.WXPAY
    private fun getChecked(): JPay.PayMode {
        return mode
    }

}

