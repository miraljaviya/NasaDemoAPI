package spartons.com.koinmvvm.activities.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import spartons.com.koinmvvm.R
import spartons.com.koinmvvm.activities.NearEarthObject
import spartons.com.koinmvvm.backend.ServiceUtil
import spartons.com.koinmvvm.util.Constants
import spartons.com.koinmvvm.util.Event

/**
 * Ahsen Saeed}
 * ahsansaeed067@gmail.com}
 * 10/24/19}
 */

class MovieViewModel constructor(private val serviceUtil: ServiceUtil) : ViewModel() {

    private val _astState = MutableLiveData<AsteriodsDataState>()
    val astState: LiveData<AsteriodsDataState> get() = _astState


    fun retrieveNearEarthObjects(startDate: String, endDate: String) {
        viewModelScope.launch {
            runCatching {
                emitUiAsteriodsState(showProgress = true)
                serviceUtil.retrieveNearEarthObjects(apiKey = Constants.API_KEY, startDate = startDate, endDate = endDate)
            }.onSuccess {
                emitUiAsteriodsState(movies = Event(it.asteroidsByDate))
            }.onFailure {
                it.printStackTrace()
                emitUiAsteriodsState(error = Event(R.string.internet_failure_error))
            }
        }
    }

    private fun emitUiAsteriodsState(
        showProgress: Boolean = false,
        movies: Event<Map<String, List<NearEarthObject>>>? = null,
        error: Event<Int>? = null
    ) {
        val dataState = AsteriodsDataState(showProgress, movies, error)
        _astState.value = dataState
    }
}

data class AsteriodsDataState(
    val showProgress: Boolean,
    val asteriods: Event<Map<String, List<NearEarthObject>>>?,
    val error: Event<Int>?
)