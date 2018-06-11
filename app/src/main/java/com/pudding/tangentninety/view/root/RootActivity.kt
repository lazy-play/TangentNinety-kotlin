package com.pudding.tangentninety.view.root

import android.os.Bundle

import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.SimpleActivity
import com.pudding.tangentninety.view.root.main.MainFragment
import com.pudding.tangentninety.weight.InsetRootView

import butterknife.BindView
import com.pudding.tangentninety.module.bean.DailyNewListBean
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

/**
 * This created by Error on 2018/04/02,11:17.
 */
class RootActivity : SimpleActivity() {
    override val layout: Int=R.layout.activity_root_view
    var bean: DailyNewListBean? = null
    @BindView(R.id.rootview)
    lateinit var rootView: InsetRootView

    val bottomPadding: Int
        get() = rootView.bottomPadding

    val topPadding: Int
        get() = rootView.topPadding

    override fun initEventAndData(savedInstanceState: Bundle?) {
        bean=intent.getParcelableExtra("DailyNewListBean")
        if(savedInstanceState==null)
        loadRootFragment(R.id.rootview, MainFragment())
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }

    public override fun setDayNightTheme() {
        super.setDayNightTheme()
    }

}
