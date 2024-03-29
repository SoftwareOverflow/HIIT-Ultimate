package com.softwareoverflow.hiit_trainer.ui.workout_creator.workout_set_creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.softwareoverflow.hiit_trainer.R
import com.softwareoverflow.hiit_trainer.databinding.FragmentWorkoutSetCreatorStep1Binding
import com.softwareoverflow.hiit_trainer.ui.MainActivity
import com.softwareoverflow.hiit_trainer.ui.utils.hideKeyboard
import com.softwareoverflow.hiit_trainer.ui.utils.safeNavigate
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.IEditableListEventListener
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.SpacedListDecoration
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.exercise_type.ExerciseTypePickerListAdapter
import com.softwareoverflow.hiit_trainer.ui.workout_creator.WorkoutCreatorViewModel
import kotlinx.android.synthetic.main.fragment_workout_set_creator_step_1.*

/**
 * Allows the user to select from the list of saved [com.softwareoverflow.hiit_trainer.repository.dto.ExerciseTypeDTO] objects.
 * Also supports edit / deletion of Exercise Type objects
 */
class WorkoutSetCreatorStep1Fragment : Fragment() {

    private val workoutViewModel: WorkoutCreatorViewModel by navGraphViewModels(R.id.nav_workout_creator)
    private val workoutSetViewModel: WorkoutSetCreatorViewModel by navGraphViewModels(R.id.nav_workout_set_creator)
    {
        WorkoutSetCreatorViewModelFactory(workoutViewModel.workoutSet, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentWorkoutSetCreatorStep1Binding>(
            inflater, R.layout.fragment_workout_set_creator_step_1, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = workoutSetViewModel

        if (!workoutViewModel.isNewWorkoutSet) {
            (requireActivity() as MainActivity).supportActionBar?.title =
                getString(R.string.nav_set_editor_step_1)
        }

        binding.exerciseTypePickerList.apply {
            adapter = ExerciseTypePickerListAdapter(object :
                IEditableListEventListener {
                override fun onItemSelected(id: Long?) {
                    workoutSetViewModel.selectedExerciseTypeId.value = id
                }

                override fun triggerItemDeletion(id: Long) {
                    workoutSetViewModel.selectedExerciseTypeId.value = null
                    workoutSetViewModel.deleteExerciseTypeById(
                        id,
                        workoutViewModel.workout.value!!.workoutSets
                    )
                }

                override fun triggerItemEdit(id: Long) {
                    workoutSetViewModel.selectedExerciseTypeId.value = id
                    createOrEditExerciseType()
                }
            })

            addItemDecoration(
                SpacedListDecoration(
                    context,
                    (this.layoutManager as GridLayoutManager).spanCount
                )
            )
        }

        workoutSetViewModel.unableToDeleteExerciseType.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrBlank()) {
                val snackbar = Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG)
                (snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)).maxLines =
                    3
                snackbar.show()

                workoutSetViewModel.unableToDeleteExerciseTypeWarningShown()
            }
        })

        workoutSetViewModel.selectedExerciseTypeId.observe(viewLifecycleOwner, Observer {
            it?.let {
                (exerciseTypePickerList.adapter as ExerciseTypePickerListAdapter).notifyItemSelected(
                    it
                )
            }
        })

        workoutSetViewModel.allExerciseTypes.observe(viewLifecycleOwner, Observer {
            it?.let {
                val adapter = (exerciseTypePickerList.adapter as ExerciseTypePickerListAdapter)
                adapter.submitDTOs(it) {
                    workoutSetViewModel.selectedExerciseTypeId.value?.let {
                        adapter.notifyItemSelected(it)
                    }
                }
            }
        })

        binding.createNewExerciseTypeFAB.setOnClickListener {
            workoutSetViewModel.selectedExerciseTypeId.value = null
            createOrEditExerciseType()
        }

        binding.workoutSetCreatorGoToStep2Button.setOnClickListener {
            workoutSetViewModel.selectedExerciseTypeId.value?.let {
                workoutSetViewModel.setChosenExerciseTypeId(it)
                findNavController().navigate(R.id.action_exerciseTypePickerFragment_to_workoutSetCreator)
                return@setOnClickListener
            }

            Snackbar.make(requireView(), R.string.select_exercise_type, Snackbar.LENGTH_SHORT)
                .show()
        }

        binding.sortButton.setOnClickListener {
            workoutSetViewModel.changeSortOrder()
        }

        binding.nameSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    workoutSetViewModel.setFilterText(it)
                }
                return true
            }
        })

        return binding.root
    }

    fun createOrEditExerciseType() {
        findNavController().safeNavigate(R.id.action_exerciseTypePickerFragment_to_exerciseTypeCreator)
    }

    override fun onStart() {
        super.onStart()

        hideKeyboard(requireActivity())
    }
}
