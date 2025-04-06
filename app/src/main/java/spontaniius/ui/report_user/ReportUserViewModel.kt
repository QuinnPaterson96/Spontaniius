package spontaniius.ui.report_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.remote.models.ReportResponse
import spontaniius.data.repository.ReportUserRepository
import javax.inject.Inject

@HiltViewModel
class ReportUserViewModel @Inject constructor(
    private val repository: ReportUserRepository
) : ViewModel() {

    private val _reportResult = MutableLiveData<ReportResponse>()
    val reportResult: LiveData<ReportResponse> get() = _reportResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun submitReport(cardId: Int? = null, reportText: String) {
        viewModelScope.launch {
            val result = repository.reportUser(cardId, reportText = reportText)
            result.onSuccess { reportResponse ->
                _reportResult.postValue(reportResponse) // ✅ Now returns full response
            }.onFailure { error ->
                _errorMessage.postValue(error.localizedMessage ?: "Failed to submit report")
            }
        }
    }
}
