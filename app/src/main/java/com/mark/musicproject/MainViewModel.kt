package com.mark.musicproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mark.musicproject.db.models.TrackItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.FieldPosition

class MainViewModel:ViewModel() {

    sealed class State{
        class TrackSelected(val track:TrackItem,val position: Int):State()
    }

    private val repo = TrackRepo()

    private val _state = MutableLiveData<State>()
    val state:LiveData<State> = _state

    private val _tracks = MutableLiveData<ArrayList<TrackItem>>(arrayListOf())
    val tracks: LiveData<ArrayList<TrackItem>> = _tracks

    fun getTracks(){
        viewModelScope.launch {
            repo.getTracks().collect {
                _tracks.value?.clear()
                _tracks.value?.addAll(it)
                _tracks.value =_tracks.value

            }
        }
    }

    fun addTracks(items:List<TrackItem>){
        viewModelScope.launch {
            repo.addTrack((items))
        }
    }

    fun selectTrack(track:TrackItem, position: Int){
        _state.value = State.TrackSelected(track,position)

    }
}