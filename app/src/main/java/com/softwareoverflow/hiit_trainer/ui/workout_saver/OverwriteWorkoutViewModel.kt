package com.softwareoverflow.hiit_trainer.ui.workout_saver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.softwareoverflow.hiit_trainer.repository.IWorkoutRepository
import com.softwareoverflow.hiit_trainer.repository.dto.WorkoutDTO
import com.softwareoverflow.hiit_trainer.ui.upgrade.BillingViewModel
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.workout.WorkoutOverwriteDomainObject

class OverwriteWorkoutViewModel(
    billingViewModel: BillingViewModel,
    workoutRepo: IWorkoutRepository,
    val dto: WorkoutDTO
) : WorkoutSaverViewModel(billingViewModel, workoutRepo, dto) {

    // TODO FUTURE VERSION Dialog shows with nothing inside when no workouts saved in the database. Find a way to prevent the user accessing the Overwrite option when no workouts exist

    private val _savedWorkouts = workoutRepo.getAllWorkouts()
    private val _existingWorkouts = MediatorLiveData<List<WorkoutOverwriteDomainObject>>();
    val existingWorkouts: LiveData<List<WorkoutOverwriteDomainObject>>
        get() = _existingWorkouts

    private val _currentSelectedId = MutableLiveData(dto.id)
    val currentSelectedId: LiveData<Long?>
        get() = _currentSelectedId

    init {
        _existingWorkouts.addSource(_savedWorkouts) { workouts ->
            workouts?.let {
                _existingWorkouts.value = getWorkoutNamesToDisplay(it)
            }
        }

        _existingWorkouts.addSource(_currentSelectedId) {
            _savedWorkouts.value?.let {
                _existingWorkouts.value = getWorkoutNamesToDisplay(it)
            }
        }
    }

    private fun getWorkoutNamesToDisplay(dtoList: List<WorkoutDTO>): List<WorkoutOverwriteDomainObject> {
        return dtoList.map { WorkoutOverwriteDomainObject(it, it.id == _currentSelectedId.value) }
    }

    fun setCurrentlySelectedId(selected: Long?) {
        _currentSelectedId.value = selected
    }

    fun saveWorkout() {
        val idToSave = currentSelectedId.value
        dto.id = idToSave

        super.saveWorkout(isOverwriting = true)
    }
}