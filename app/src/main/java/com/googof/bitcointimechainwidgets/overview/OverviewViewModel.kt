/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googof.bitcointimechainwidgets.overview


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.googof.bitcointimechainwidgets.BuildConfig
import com.googof.bitcointimechainwidgets.network.BitnodesApi
import com.googof.bitcointimechainwidgets.network.MempoolApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _blockHeight = MutableLiveData<String>()
    private val _fastestFee = MutableLiveData<String>()
    private val _halfHourFee = MutableLiveData<String>()
    private val _hourFee = MutableLiveData<String>()
    private val _economyFee = MutableLiveData<String>()
    private val _minimumFee = MutableLiveData<String>()
    private val _recommendedFees = MutableLiveData<String>()
    private val _totalNodes = MutableLiveData<String>()
    private val _snapshotTime = MutableLiveData<String>()

    val version = "Version : " + BuildConfig.VERSION_NAME

    // The external immutable LiveData for the request status
    val blockHeight: LiveData<String> = _blockHeight
    val recommendedFees: LiveData<String> = _recommendedFees
    val fastestFee: LiveData<String> = _fastestFee
    val halfHourFee: LiveData<String> = _halfHourFee
    val hourFee: LiveData<String> = _hourFee
    val economyFee: LiveData<String> = _economyFee
    val minimumFee: LiveData<String> = _minimumFee
    val totalNodes: LiveData<String> = _totalNodes
    val snapshotTime: LiveData<String> = _snapshotTime

    init {
        fetchDatafromApi()
    }

    private fun convertEpochtoDateTimeString(epoch: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val netDate = Date(epoch.toLong() * 1000)
        return sdf.format(netDate)
    }

    private fun fetchDatafromApi() {
        viewModelScope.launch {
            val blockHeight = MempoolApi.retrofitService.getBlockTipHeight()
            _blockHeight.value = blockHeight

            val totalNodes = BitnodesApi.retrofitService.getTotalNodes()
            _totalNodes.value = totalNodes.results[0].total_nodes.toString()

            val snapshotTime = convertEpochtoDateTimeString(totalNodes.results[0].timestamp)
            _snapshotTime.value = snapshotTime
        }

//        viewModelScope.launch {
//            val recommendedFees = MempoolApi.retrofitService.getRecommendedFee()
//            _fastestFee.value = recommendedFees.fastestFee
//            _halfHourFee.value = recommendedFees.halfHourFee
//            _hourFee.value =  recommendedFees.hourFee
//            _economyFee.value =  recommendedFees.economyFee
//            _minimumFee.value = recommendedFees.minimumFee
//        }
    }

}
