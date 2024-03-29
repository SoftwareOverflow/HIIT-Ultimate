package com.softwareoverflow.hiit_trainer.ui.workout_loader

import android.content.Context
import androidx.lifecycle.*
import com.softwareoverflow.hiit_trainer.repository.IWorkoutRepository
import com.softwareoverflow.hiit_trainer.repository.billing.BillingRepository
import com.softwareoverflow.hiit_trainer.ui.utils.SortOrder
import com.softwareoverflow.hiit_trainer.ui.view.list_adapter.workout.WorkoutLoaderDomainObject
import kotlinx.coroutines.launch

class WorkoutLoaderViewModel(
    private val billingRepo: BillingRepository,
    private val context: Context,
    private val workoutRepo: IWorkoutRepository
) :
    ViewModel() {

    val sortOrder = MutableLiveData(SortOrder.ASC)
    private val _searchFilter = MutableLiveData("")

    private val _workouts = workoutRepo.getAllWorkouts()
    private val _workoutsSorted = MediatorLiveData<List<WorkoutLoaderDomainObject>>()
    val workouts: LiveData<List<WorkoutLoaderDomainObject>>
        get() = _workoutsSorted

    private var initialized = false

    private val _workoutsSortedChangeObserver = Observer<Any> {
        if (initialized)
            _workoutsSorted.value = getWorkoutsToDisplay()
    }

    init {
        _workoutsSorted.addSource(sortOrder, _workoutsSortedChangeObserver)
        _workoutsSorted.addSource(_searchFilter, _workoutsSortedChangeObserver)

        viewModelScope.launch {
            _workoutsSorted.addSource(_workouts) {
                initialized = true
                _workoutsSorted.value = getWorkoutsToDisplay()
            }
        }
    }

    fun changeSortOrder() {
        sortOrder.value =
            if (sortOrder.value == SortOrder.ASC)
                SortOrder.DESC
            else SortOrder.ASC
    }

    fun setFilterText(filterText: String) {
        _searchFilter.value = filterText
    }

    private fun getWorkoutsToDisplay(): List<WorkoutLoaderDomainObject> {
        var workouts = _workouts.value ?: arrayListOf()

        // Filter
        val filter = _searchFilter.value
        if (!filter.isNullOrBlank())
            workouts = workouts.filter { it.name.contains(filter, ignoreCase = true) }

        // Sort
        workouts = workouts.sortedBy { it.name }
        if (sortOrder.value == SortOrder.ASC)
            workouts = workouts.reversed()

        val domainObjects = workouts.map { WorkoutLoaderDomainObject(it) }.toMutableList()

        if ((billingRepo.proUpgradeLiveData.value?.entitled != true) && _searchFilter.value.isNullOrEmpty()) {
            while (domainObjects.size < billingRepo.getMaxWorkoutSlots())
                domainObjects.add(WorkoutLoaderDomainObject.getPlaceholderUnlocked(context))

            domainObjects.add(WorkoutLoaderDomainObject.getPlaceholderLocked(context))
        }

        return domainObjects
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch {
            workoutRepo.deleteWorkoutById(id)
        }
    }
}

