package qhaty.freemoney

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        s1.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.s_yi -> selcet1 = 0
                R.id.s_shi -> selcet1 = 1
                R.id.s_zhu -> selcet1 = 2
                R.id.s_xin -> selcet1 = 3
            }
        }
        s2.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.s_year -> selcet2 = 1
                R.id.s_month -> selcet2 = 12
                R.id.s_day -> selcet2 = 365
            }
        }
        s_import.setOnClickListener {
            if (s_outcome.text.toString().isBlank()) {
                toast("请输入支出!")
                return@setOnClickListener
            }
            val num = s_outcome.text.toString().toInt() * selcet2
            var str = ""
            when (selcet1) {
                0 -> {
                    yi = num
                    str = "衣"
                }
                1 -> {
                    shi = num
                    str = "食"
                }
                2 -> {
                    zhu = num
                    str = "住"
                }
                3 -> {
                    xin = num
                    str = "行"
                }
            }
            out_tv.addText("修改每年 $str 支出为 $num \n")
        }
        bt_other.setOnClickListener {
            val s = et_other.text.toString()
            if (s.isNotBlank()) {
                other = s.toInt()
                out_tv.addText("修改每年其他支出为 $other \n")
            }
        }
        bt_shouru.setOnClickListener {
            val s = et_shouru.text.toString()
            if (s.isNotBlank()) {
                shouru = s.toInt()
                out_tv.addText("修改每月收入为 $shouru \n")
            }
        }
        bt_licai.setOnClickListener {
            val s = et_licai.text.toString()
            if (s.isNotBlank()) {
                licai = s.toInt() / 100.0
                out_tv.addText("修改每年理财收益率为 $licai \n")
            }
        }
        bt_now.setOnClickListener {
            val s = et_now.text.toString()
            if (s.isNotBlank()) {
                now = s.toInt()
                out_tv.addText("修改当前资产为 $now \n")
            }
        }
        bt_increase.setOnClickListener {
            val s = et_increase.text.toString()
            if (s.isNotBlank()) {
                shouruIncrease = s.toInt() / 100.0
                out_tv.addText("修改收入增长率为 $shouruIncrease \n")
            }
        }
        bt_decrease.setOnClickListener {
            val s = et_decrease.text.toString()
            if (s.isNotBlank()) {
                moneyDecrease = s.toInt() / 100.0
                out_tv.addText("修改通货膨胀率为 $moneyDecrease \n")
            }
        }
        bt_ana.setOnClickListener {
            if (yi != -1 && shi != -1 && zhu != -1 && xin != -1) {
                sOut = yi + shi + zhu + xin + other
                target = sOut / licai
                out_tv.addText("\n每年支出$sOut\n")
            } else {
                toast("衣食住行录入不全")
                return@setOnClickListener
            }
            out_tv.addText("开始计算，货币通胀率${moneyDecrease}%\n")
            if (licai < 0.031) {
                out_tv.addText("理财收益无法跑赢通胀,不可能实现财务自由")
            }
            increase(sOut.toDouble(), shouru * 12.0, now.toDouble(), licai, out_tv)
            out_scroll.fullScroll(ScrollView.FOCUS_DOWN)
        }
        bt_expand.setOnClickListener {
            out_scroll.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    if (expand) 2f else 6f
                )
            bt_expand.text= if (expand) "展开" else "折叠"
            expand = !expand
        }
    }

    companion object {
        var selcet1 = 0
        var selcet2 = 1
        var sOut = -1
        var target = 0.0
        var yi = -1
        var shi = -1
        var zhu = -1
        var xin = -1
        var other = 0
        var shouru = 0
        var licai = 0.04
        var now = 0
        var expand = false
    }
}

var shouruIncrease = 0.1
var moneyDecrease = 0.03

fun Context.toast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

var yearCounter = 1
fun increase(sOut: Double, sIn: Double, now: Double, licai: Double, out: TextView) {
    val nextOut = sOut * (1 + moneyDecrease)
    val nextIn = sIn * (1 + shouruIncrease)
    val next = (nextIn - nextOut + now) * (1 + licai)
    val target = nextOut / (licai - moneyDecrease)

    out.addText(
        "${yearCounter}年后,你资产为${String.format("%.2f", next)}" +
                ",支出${String.format("%.2f", nextOut)}\n"
    )
    if (next < 0) {
        out.addText("你  没  钱  了\n")
        yearCounter = 0
        return
    }
    if (next > target) {
        out.addText("你实现了财务自由!\n")
        yearCounter = 0

        return
    } else {
        yearCounter++
        increase(nextOut, nextIn, next, licai, out)
    }
}

fun TextView.addText(s: String) {
    this.text = "${this.text}$s"
}