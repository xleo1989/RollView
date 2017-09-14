package com.x.leo.rollview

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.DragEvent
import android.view.MotionEvent

/**
 * @作者:XLEO
 * @创建日期: 2017/8/14 9:54
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class UnDragableViewPager(context: Context, attributes: AttributeSet): ViewPager(context,attributes){

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onDragEvent(event: DragEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}


class FragmentAdapter(val fragment:OnFragmentNeeded,val size:Int,fm:FragmentManager): FragmentStatePagerAdapter(fm){
    override fun getItem(position: Int): Fragment {
        val onFragmentNeeded = fragment.onFragmentNeeded(position)
        return onFragmentNeeded
    }


    override fun getCount(): Int {
        return size
    }

}

interface OnFragmentNeeded{
    fun onFragmentNeeded(position:Int):Fragment
}