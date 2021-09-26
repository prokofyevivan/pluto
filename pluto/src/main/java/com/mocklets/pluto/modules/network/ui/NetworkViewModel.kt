package com.mocklets.pluto.modules.network.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mocklets.pluto.R
import com.mocklets.pluto.core.extensions.color
import com.mocklets.pluto.core.ui.spannable.createSpan
import com.mocklets.pluto.modules.network.ApiCallData
import com.mocklets.pluto.modules.network.NetworkCallsRepo
import com.mocklets.pluto.modules.network.ResponseData
import com.mocklets.pluto.modules.network.beautifyHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class NetworkViewModel(application: Application) : AndroidViewModel(application) {

    val apiCalls: LiveData<List<ApiCallData>>
        get() = NetworkCallsRepo.apiCalls

    private val _currentApiCall = MutableLiveData<ApiCallData>()
    private val _contentSearch = MutableLiveData<String>()

    val detailContentLiveData: LiveData<DetailContentData>
        get() = _detailContentLiveData
    private val _detailContentLiveData = MediatorLiveData<DetailContentData>()

    init {
        _detailContentLiveData.addSource(_currentApiCall) {
            combineData(_currentApiCall, _contentSearch)
        }
        _detailContentLiveData.addSource(_contentSearch) {
            combineData(_currentApiCall, _contentSearch)
        }
    }

    private fun combineData(apiCallLD: LiveData<ApiCallData>, searchLD: LiveData<String>) {
        apiCallLD.value?.let {
            viewModelScope.launch(Dispatchers.Default) {
                var formattedResponse: FormatterResponse? = null
                it.response?.let { response -> formattedResponse = formatResponse(response, searchLD.value) }
                _detailContentLiveData.postValue(DetailContentData(it, searchLD.value, formattedResponse))
            }
        }
    }

    fun fetchCurrent(id: String) {
        _currentApiCall.postValue(NetworkCallsRepo.get(id))
    }

    fun searchContent(it: String) {
        _contentSearch.postValue(it.trim())
    }

    private fun formatResponse(data: ResponseData, search: String?): FormatterResponse {
        val context = getApplication<Application>().applicationContext
        var header: CharSequence? = null
        var body: CharSequence? = null
        context.beautifyHeaders(data.headers)?.let { header = context.createSpan { append(highlight(it, search)) } }

        data.body?.let {
            if (it.isValid) {
                body = context.createSpan {
                    if (it.isBinary) {
                        append(fontColor(italic("${it.body}"), context.color(R.color.pluto___text_dark_60)))
                    } else {
                        append(highlight("${it.body}", search))
                    }
                }
            }
        }
        return FormatterResponse(data, header, body)
    }
}

internal data class DetailContentData(
    val api: ApiCallData,
    val search: String?,
    val formatterResponse: FormatterResponse?
)

internal data class FormatterResponse(
    val data: ResponseData,
    val header: CharSequence?,
    val body: CharSequence?
)
