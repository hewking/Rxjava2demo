package com.hewking.rxjava2demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

/**
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2018/5/3
 * 修改人员：hewking
 * 修改时间：2018/5/3
 * 修改备注：
 * Version: 1.0.0
 */

class DemoFragmentActivity : AppCompatActivity() {

    companion object {
        val FRAGMENT = "fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_navi)

        val fragmentName = intent.getStringExtra(FRAGMENT)
        val fragment = Class.forName(fragmentName).newInstance() as androidx.fragment.app.Fragment
        supportFragmentManager.beginTransaction()
                .add(R.id.container,fragment,fragmentName)
                .commit()

    }

}