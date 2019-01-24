package com.hewking.rxjava2demo

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        refreshlayout.setOnRefreshListener {

        }

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.itemAnimator = DefaultItemAnimator()
        recyclerview.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.bottom = dp2px(1f)
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                val layoutManager = parent.layoutManager!!
                for (i in 0 until layoutManager.childCount) {
                    val child = layoutManager.getChildAt(i)!!
                    val drawable = ColorDrawable(getColor(R.color.lineColor))
                    drawable.setBounds(child.left,child.bottom,child.right,child.bottom + dp2px(1f))
                    drawable.draw(c)
                }
            }
        })
        recyclerview.adapter = mAdapter
    }

    val mAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> by lazy {
        object : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(){

            val datas = createItems()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
                val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.item_text,parent,false)
                val vh = object : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
                }
                return vh
            }

            override fun getItemCount(): Int {
                return datas.size
            }

            override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
                val itemView = holder?.itemView
                itemView?.v<TextView>(R.id.tv_text)?.text = datas[position].info
                itemView?.setOnClickListener {
                    if (datas[position].type == 1) {
                        val intent = Intent(this@MainActivity, DemoFragmentActivity::class.java)
                        intent.putExtra(DemoFragmentActivity.FRAGMENT, datas[position].clazz.name)
                        this@MainActivity.startActivity(intent)
                    } else {
                        val intent = Intent(this@MainActivity,datas[position].clazz)
                        intent.resolveActivity(this@MainActivity.packageManager)
                        if (intent == null) {
//                            T("${datas[position].clazz.simpleName } 不存在")
                            return@setOnClickListener
                        }
                        this@MainActivity.startActivity(intent)
                    }
                }
            }
        }
    }

    fun createItems() : MutableList<Item> {
        val list = mutableListOf<Item>()
        list.add(Item(1,"just operation",JustOpFragment::class.java))
        return list
    }

    data class Item(val id : Int,val info : String ,val clazz: Class<*>,val type : Int = 1)

    fun Activity.dp2px(dp : Float) : Int {
        return (resources.displayMetrics.density.times(dp) + 0.5f).toInt()
    }

    fun Activity.px2dp(px : Int) : Int{
        return (px.div(resources.displayMetrics.density) + 0.5f).toInt()
    }


}
