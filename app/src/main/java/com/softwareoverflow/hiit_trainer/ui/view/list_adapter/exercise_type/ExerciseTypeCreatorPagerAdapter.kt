package com.softwareoverflow.hiit_trainer.ui.view.list_adapter.exercise_type

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softwareoverflow.hiit_trainer.R
import com.softwareoverflow.hiit_trainer.ui.utils.takeN
import com.softwareoverflow.hiit_trainer.ui.view.CircularIconImageView

class ExerciseTypeCreatorPagerAdapter(
    val adapterType: ExerciseTypeAdapter,
    private var ids: MutableList<Int>
) : RecyclerView.Adapter<ExerciseTypeCreatorPagerAdapter.ExerciseTypeViewHolder>() {

    override fun getItemCount(): Int = ids.size

    override fun onBindViewHolder(holder: ExerciseTypeViewHolder, position: Int) {
        val item = ids[position]
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return ids[position].toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseTypeViewHolder {
        return createViewHolder(parent)
    }

    private fun createViewHolder(parent: ViewGroup): ExerciseTypeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_exercise_type_creator, parent, false)

        return ExerciseTypeViewHolder(view)
    }

    /**
     * This item will move the requested id to the middle of the list, maintaining the list order
     * with respect to the infinite page scroll
     *
     * @param id the id in the adapter data to move to the center of the list
     */
    fun moveItemToCenter(id: Int) {
        val position = ids.indexOf(id)

        if (position == -1)
            return

        val itemCount = ids.count()
        val numToMove = position - itemCount / 2
        val movedItems = ids.takeN(numToMove)

        if (numToMove < 0) {
            ids.addAll(0, movedItems)
            ids = ids.takeN(itemCount).toMutableList()
        } else if (numToMove > 0) {
            ids.addAll(movedItems)
            ids = ids.takeN(itemCount * -1).toMutableList()
        }
    }

    /**
     * Loops the data set as if the user is moving forward. Takes the first item and move to the
     * last item
     */
    fun loopForward() {
        val removed = ids.removeAt(0)
        ids.add(removed)

        notifyItemMoved(0, itemCount - 1)
    }

    /**
     * Loops the data set as if the user is moving backward. Takes the last item and move to
     * front
     */
    fun loopBackward() {
        val removed = ids.removeAt(itemCount - 1)
        ids.add(0, removed)

        notifyItemMoved(itemCount - 1, 0)
    }

    companion object {
        enum class ExerciseTypeAdapter { ICON, COLOR }
    }

    inner class ExerciseTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: CircularIconImageView =
            itemView.findViewById(R.id.list_color_image)

        fun bind(resourceId: Int) {
            when (adapterType) {
                ExerciseTypeAdapter.ICON -> {
                    iconImageView.setColor(android.R.color.transparent)
                    iconImageView.setBackground(resourceId)
                }
                ExerciseTypeAdapter.COLOR -> iconImageView.setColor(resourceId)
            }
        }
    }
}