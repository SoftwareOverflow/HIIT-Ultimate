package com.softwareoverflow.hiit_trainer.ui.workout_creator.workout_set_creator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softwareoverflow.hiit_trainer.repository.IWorkoutRepository
import com.softwareoverflow.hiit_trainer.repository.dto.ExerciseTypeDTO
import com.softwareoverflow.hiit_trainer.repository.dto.WorkoutSetDTO

class WorkoutSetCreatorViewModel(workoutSetDTO: WorkoutSetDTO? = null, repo: IWorkoutRepository) :
    ViewModel() {

    val allExerciseTypes: LiveData<List<ExerciseTypeDTO>> = repo.getAllExerciseTypes()

    private var _exerciseType: MutableLiveData<ExerciseTypeDTO?> =
        MutableLiveData(workoutSetDTO?.exerciseTypeDTO)
    val exerciseType: LiveData<ExerciseTypeDTO?>
        get() = _exerciseType

    private var _workTime: MutableLiveData<Int> = MutableLiveData(15)
    val workTime: LiveData<Int>
        get() = _workTime

    private var _restTime: MutableLiveData<Int> = MutableLiveData(5)
    val restTime: LiveData<Int>
        get() = _restTime

    private var _numReps: MutableLiveData<Int> = MutableLiveData(3)
    val numReps: LiveData<Int>
        get() = _numReps

    private var _recoverTime: MutableLiveData<Int> = MutableLiveData(120)
    val recoverTime: LiveData<Int>
        get() = _recoverTime

    init {
        workoutSetDTO?.let {
            setValues(it.workTime, it.restTime, it.numReps, it.recoverTime)
        }
    }

    fun setExerciseType(exerciseType: ExerciseTypeDTO) {
        _exerciseType.value = exerciseType
    }

    fun setValues(workTime: Int, restTime: Int, numReps: Int, recoverTime: Int) {
        _workTime.value = workTime
        _restTime.value = restTime
        _numReps.value = numReps
        _recoverTime.value = recoverTime
    }
}