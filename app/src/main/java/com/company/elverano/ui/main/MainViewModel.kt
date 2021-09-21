package com.company.elverano.ui.main


import androidx.lifecycle.*
import com.company.elverano.data.OpenWeather
import com.company.elverano.data.OpenWeatherRepository
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject  constructor(
    private val repository: OpenWeatherRepository,
    private val state: SavedStateHandle
    ): ViewModel() {

     var currentWeather = MutableLiveData<OpenWeather>()
    private val currentQuery  = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()

    init {
        searchWeather(DEFAULT_QUERY)
    }

    fun searchWeather(query: String) = viewModelScope.launch {
        currentQuery.value =query
        val response = repository.getWeatherResponse(query).body()

       if(response!=null){


              if(response.count>0){
                  val firstResponse = response.list[0]
                  currentWeather.value = firstResponse
                  resultChannel.send(ResultEvent.Success(currentWeather.value))
              }else{
                  resultChannel.send(ResultEvent.Error("No Item's found"))
              }

       }


    }



    companion object{
        private const val CURRENT_QUERY="current_query"
         const val DEFAULT_QUERY="Warsaw"
    }

    sealed class ResultEvent{
        data class Success(var data: OpenWeather?): ResultEvent()
        data class Error(var message: String): ResultEvent()
    }
}