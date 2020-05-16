package com.softwareoverflow.hiit_trainer.ui.workout_loader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.softwareoverflow.hiit_trainer.R
import com.softwareoverflow.hiit_trainer.databinding.FragmentWorkoutLoaderBinding
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.ISelectableEditableListEventListener
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.SpacedListDecoration
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.workout.SavedWorkoutListAdapter
import kotlinx.android.synthetic.main.fragment_workout_loader.*

class LoadSavedWorkoutFragment : Fragment() {

    val viewModel: WorkoutLoaderViewModel by navGraphViewModels(R.id.loadSavedWorkoutFragment) {
        WorkoutLoaderViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentWorkoutLoaderBinding>(
            inflater,
            R.layout.fragment_workout_loader,
            container,
            false
        )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.listSavedWorkouts.adapter = SavedWorkoutListAdapter(
            requireContext()
        ).apply {
            setEventListener(object :
                ISelectableEditableListEventListener {
                override fun onItemSelected(selected: Long?) {
                    val action =
                        LoadSavedWorkoutFragmentDirections.actionLoadSavedWorkoutFragmentToWorkoutFragment(
                            selected!!
                        )
                    findNavController().navigate(action)
                }

                override fun triggerItemDeletion(id: Long) {
                    // TODO probably add are you sure dialog
                    viewModel.deleteWorkout(id)
                }

                override fun triggerItemEdit(id: Long) {
                    val action =
                        LoadSavedWorkoutFragmentDirections.actionLoadSavedWorkoutFragmentToNavWorkoutCreator(
                            id
                        )
                    findNavController().navigate(action)
                }
            })
        }

        binding.listSavedWorkouts.addItemDecoration(
            SpacedListDecoration(
                requireContext()
            )
        )

        viewModel.workouts.observe(viewLifecycleOwner, Observer {
            // TODO fix having to cast this - it's not very nice!
            it?.let {
                (listSavedWorkouts.adapter as SavedWorkoutListAdapter).submitList(it.toMutableList())
            }
        })

        // TODO sort out click listener on the sort order, and add search filtering
        binding.sortButton.setOnClickListener {
            viewModel.changeSortOrder();
        }

        binding.nameSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?) =  true

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    viewModel.setFilterText(newText)
                }

                return true
            }
        })

        return binding.root
    }

    // TODO add some form of animation to the views when first displayed
}