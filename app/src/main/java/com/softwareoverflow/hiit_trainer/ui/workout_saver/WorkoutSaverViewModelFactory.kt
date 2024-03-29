package com.softwareoverflow.hiit_trainer.ui.workout_saver

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softwareoverflow.hiit_trainer.repository.WorkoutRepositoryFactory
import com.softwareoverflow.hiit_trainer.repository.dto.WorkoutDTO
import com.softwareoverflow.hiit_trainer.ui.upgrade.BillingViewModel

class WorkoutSaverViewModelFactory(private val application: Application, private val context: Context, private val workout: WorkoutDTO, private val saveAsNew: Boolean) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WorkoutSaverViewModel::class.java)) {
            val billingViewModel = BillingViewModel(application)
            val workoutRepo = WorkoutRepositoryFactory.getInstance(context)

            return if(saveAsNew) {
                workout.id = null
                WorkoutSaverViewModel(billingViewModel, workoutRepo, workout) as T
            } else {
                OverwriteWorkoutViewModel(billingViewModel, workoutRepo, workout) as T
            }
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}