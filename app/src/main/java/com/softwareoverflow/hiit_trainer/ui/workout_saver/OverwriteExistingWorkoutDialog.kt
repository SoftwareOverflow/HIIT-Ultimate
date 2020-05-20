package com.softwareoverflow.hiit_trainer.ui.workout_saver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.softwareoverflow.hiit_trainer.R
import com.softwareoverflow.hiit_trainer.databinding.DialogOverwriteExistingWorkoutBinding
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.ISelectableEditableListEventListener
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.SpacedListDecoration
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.workout.OverwriteWorkoutListAdapter
import kotlinx.android.synthetic.main.dialog_overwrite_existing_workout.*
import timber.log.Timber

class OverwriteExistingWorkoutDialog : SaveWorkoutDialog() {

    private val _overwriteExistingViewModel by navGraphViewModels<WorkoutSaverViewModel>(R.id.overwriteExistingWorkoutDialog) {
        WorkoutSaverViewModelFactory(requireContext(), workoutViewModel.workout.value!!, false)
    }
    private val overwriteExistingViewModel: OverwriteWorkoutViewModel
        get() = _overwriteExistingViewModel as OverwriteWorkoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val listAdapter = OverwriteWorkoutListAdapter(
            requireContext(),
            object : ISelectableEditableListEventListener {
                override fun onItemSelected(selected: Long?) {
                    overwriteExistingViewModel.setCurrentlySelectedId(
                        selected
                    )
                    Timber.d("Overwrite: onItemSelected in fragment $selected")
                }

                // There is no option for deleting or editing at this stage.
                override fun triggerItemDeletion(id: Long) {
                    throw NotImplementedError()
                }

                // There is no option for deleting or editing at this stage.
                override fun triggerItemEdit(id: Long) {
                    throw NotImplementedError()
                }
            })

        binding = DataBindingUtil.inflate<DialogOverwriteExistingWorkoutBinding>(
            inflater,
            R.layout.dialog_overwrite_existing_workout,
            container,
            false
        ).apply {
            lifecycleOwner = this@OverwriteExistingWorkoutDialog

            cancelButton.setOnClickListener {
                findNavController().navigateUp()
            }

            saveButton.setOnClickListener {

                overwriteExistingViewModel.newWorkoutName.value =
                    (recyclerView.adapter as OverwriteWorkoutListAdapter).updatedName;
                overwriteExistingViewModel.saveWorkout()
            }

            recyclerView.apply {
               adapter = listAdapter
                addItemDecoration(SpacedListDecoration(requireContext()))
            }
        }

        overwriteExistingViewModel.existingWorkouts.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    (recyclerView.adapter as OverwriteWorkoutListAdapter).submitList(it.toMutableList()) {
                        Timber.d("Overwrite: Submitted new list: $it")
                    }
                }
            })

        overwriteExistingViewModel.currentSelectedId.observe(
            viewLifecycleOwner,
            Observer {
                (recyclerView.adapter as OverwriteWorkoutListAdapter).notifyItemSelected(it)
            })

        return binding.root
    }

    override fun getWorkoutSaverViewModel() = overwriteExistingViewModel
}