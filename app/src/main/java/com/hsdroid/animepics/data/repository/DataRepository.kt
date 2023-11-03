package com.hsdroid.animepics.data.repository

import com.hsdroid.animepics.data.APIInterface
import com.hsdroid.animepics.models.Images
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DataRepository @Inject constructor(private val apiInterface: APIInterface) {

    fun callData(tags : String) : Flow<Images> = flow {
        emit(apiInterface.getData(tags))
    }.flowOn(Dispatchers.IO)
}