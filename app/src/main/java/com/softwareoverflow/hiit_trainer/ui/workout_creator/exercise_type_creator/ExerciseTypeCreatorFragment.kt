package com.softwareoverflow.hiit_trainer.ui.workout_creator.exercise_type_creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.softwareoverflow.hiit_trainer.R
import com.softwareoverflow.hiit_trainer.databinding.FragmentExerciseTypeCreatorBinding
import com.softwareoverflow.hiit_trainer.ui.workout_creator.workout_set_creator.WorkoutSetCreatorViewModel
import kotlinx.android.synthetic.main.fragment_exercise_type_creator.*

class ExerciseTypeCreatorFragment : Fragment() {

    private lateinit var viewModel: ExerciseTypeViewModel

    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentExerciseTypeCreatorBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_exercise_type_creator,
            container,
            false
        )

        // This does not take the corresponding factory, as the view model *SHOULD* always be created by this point
        val workoutSetViewModel: WorkoutSetCreatorViewModel by navGraphViewModels(R.id.nav_workout_set_creator)
        val viewModelFactory =
            ExerciseTypeViewModelFactory(
                requireActivity(),
                workoutSetViewModel.selectedExerciseTypeId.value,
                workoutSetViewModel
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(ExerciseTypeViewModel::class.java)
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        val etNameMaxLength = requireActivity().resources.getInteger(R.integer.et_name_length_max)
        binding.createOrUpdateExerciseTypeButton.setOnClickListener {
            if (etExerciseTypeName.text.toString().isBlank()) {
                Snackbar.make(
                    etExerciseTypeName,
                    R.string.error_name_required,
                    Snackbar.LENGTH_SHORT
                ).show()
            } else if (etExerciseTypeName.text.toString().length > etNameMaxLength) {
                if(snackbar?.isShown != true)
                    snackbar?.show()
            } else {
                viewModel.createOrUpdateExerciseType(
                    requireActivity(),
                    etExerciseTypeName.text.toString(),
                    etViewPagerPicker.getIconId(),
                    etViewPagerPicker.getColorId()
                )
            }
        }

        viewModel.newExerciseTypeSaved.observe(viewLifecycleOwner, Observer {
            it?.let{
                workoutSetViewModel.selectedExerciseTypeId.value = it
                workoutSetViewModel.setChosenExerciseTypeId(it)

                viewModel.savedExerciseTypeHandled()
                findNavController().navigate(R.id.action_exerciseTypeCreator_to_workoutSetCreator)
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        requireActivity().window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        if (snackbar == null) {
            snackbar = Snackbar.make(
                etExerciseTypeName,
                R.string.name_too_long_warning,
                Snackbar.LENGTH_SHORT
            ).setAction(R.string.dismiss) {
                snackbar?.dismiss()
            }
        }
    }
}
