package com.hsdroid.animepics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsdroid.animepics.data.repository.DataRepository
import com.hsdroid.animepics.models.Images
import com.hsdroid.animepics.utils.APIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewmodel @Inject constructor(private val repository: DataRepository) : ViewModel() {

    private val response: MutableStateFlow<APIState<Images>> =
        MutableStateFlow(APIState.EMPTY)

    val _response: StateFlow<APIState<Images>> get() = response

    init {
        callData("happy")
    }

    fun callData(tags: String) = viewModelScope.launch {
        repository.callData(tags)
            .onStart { response.value = APIState.LOADING }
            .catch { response.value = APIState.FAILURE(it)}
            .collect { response.value = APIState.SUCCESS(it) }
    }

}