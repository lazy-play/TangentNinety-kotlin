package com.pudding.tangentninety.di.component

import android.app.Activity

import com.pudding.tangentninety.di.module.FragmentModule
import com.pudding.tangentninety.di.scope.FragmentScope
import com.pudding.tangentninety.view.root.main.MainFragment
import com.pudding.tangentninety.view.root.main.collection.CollectionFragment
import com.pudding.tangentninety.view.root.main.history.HistoryFragment
import com.pudding.tangentninety.view.root.main.home.HomeFragment
import com.pudding.tangentninety.view.root.main.section.SectionFragment
import com.pudding.tangentninety.view.root.main.section.detail.SectionDetailFragment
import com.pudding.tangentninety.view.root.main.setting.SettingFragment
import com.pudding.tangentninety.view.root.main.storydetail.StoryDetailFragment
import com.pudding.tangentninety.view.root.main.storydetail.comment.StoryCommentFragment

import dagger.Component

/**
 * Created by Error on 2017/6/22 0022.
 */

@FragmentScope
@Component(dependencies = [(AppComponent::class)], modules = [(FragmentModule::class)])
interface FragmentComponent {

    val activity: Activity?

    fun inject(mainFragment: MainFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(sectionFragment: SectionFragment)
    fun inject(collectionFragment: CollectionFragment)
    fun inject(historyFragment: HistoryFragment)
    fun inject(settingFragment: SettingFragment)

    fun inject(detailFragment: SectionDetailFragment)
    fun inject(storyDetailFragment: StoryDetailFragment)
    fun inject(storyCommentFragment: StoryCommentFragment)
}

