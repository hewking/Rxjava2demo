package com.hewking.rxjava2demo

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.*

class JustOpFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(-1,-1)
            addView(TextView(context).apply {
                layoutParams = FrameLayout.LayoutParams(-2,-2).apply {
                    gravity = Gravity.CENTER
                }
                setPadding(dp2px(10f),dp2px(10f),dp2px(10f),dp2px(10f))
                id = R.id.text_id
                text = "heheda"
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sb = StringBuffer()

        Observable.just("just Operation")
                .subscribe(object : Consumer<String>{
                    override fun accept(t: String) {
                        sb.append(t)
                        view.findViewById<TextView>(R.id.text_id).text = sb.toString()
                    }
                })

        val list = ArrayList<String>()

    }

    fun Fragment.dp2px(dp : Float) : Int {
        return (resources.displayMetrics.density.times(dp) + 0.5f).toInt()
    }

    fun Fragment.px2dp(px : Int) : Int{
        return (px.div(resources.displayMetrics.density) + 0.5f).toInt()
    }
}