package com.softwareoverflow.hiit_trainer.ui.view.picker

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getResourceIdOrThrow
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.softwareoverflow.hiit_trainer.R
import com.softwareoverflow.hiit_trainer.ui.view.picker.ExerciseTypePagerAdapter.Companion
import kotlinx.android.synthetic.main.x_view_pager_picker.view.*

class XViewPagerPicker : ConstraintLayout {

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        inflate(context, R.layout.x_view_pager_picker, this)

        setupColorPager()
        setupIconPager()
    }

    private fun setupColorPager() {
        val ids: MutableList<Int> = resources.getIntArray(R.array.et_colors).toMutableList()
        /* Duplicate all the IDs so when the currentItem is set to halfway it smooth scrolls through
            all the options */
        ids.addAll(ids)
        val colorAdapter = ExerciseTypePagerAdapter(Companion.ExerciseTypeAdapter.COLOR, ids)
        setupPager(colorViewPager, colorAdapter)
    }

    private fun setupIconPager() {
        val ids: MutableList<Int> = ArrayList()
        val icons = resources.obtainTypedArray(R.array.et_icons)
        try {
            for (i in 0 until icons.length())
                ids.add(icons.getResourceIdOrThrow(i))
        } finally {
            icons.recycle()
        }
        /* Duplicate all the IDs so when the currentItem is set to halfway it smooth scrolls through
            all the options and presents options either side */
        ids.addAll(ids)
        ids.addAll(ids) // TODO remove this line once more icons have been generated

        val iconAdapter = ExerciseTypePagerAdapter(Companion.ExerciseTypeAdapter.ICON, ids)
        setupPager(iconViewPager, iconAdapter)
    }

    private fun <T : RecyclerView.ViewHolder> setupPager(
        pager: ViewPager2,
        adapter: RecyclerView.Adapter<T>
    ) {
        with(pager) {
            this.adapter = adapter

            offscreenPageLimit = 5
            clipToPadding = false
            clipChildren = false

            setPageTransformer(MultipleVisiblePagesTransformer(context))
            registerOnPageChangeCallback(InfiniteScrollPageChangeListener(pager))

            currentItem = adapter.itemCount / 2
        }
    }
    /*
    TODO investigate use cases architecture further as a potential alternative to the DTO model

    TODO investigate if DiffUtil and ListAdapter can be used for the ViewPager2 instead of manually removing and adding the items each swipe

    TODO create some fake data for the database.
    TODO create recyclerview and listAdapter for showing all of the exercise types
    TODO use DiffUtil for it
     */
}