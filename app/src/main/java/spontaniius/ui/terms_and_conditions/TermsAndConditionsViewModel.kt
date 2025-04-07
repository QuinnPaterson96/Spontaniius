package spontaniius.ui.terms_and_conditions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

// ViewModel
@HiltViewModel
class TermsAndConditionsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _acceptanceState = MutableLiveData<Boolean>()
    val acceptanceState: LiveData<Boolean> = _acceptanceState

    fun acceptTermsAndConditions() {
        viewModelScope.launch {
            val response = userRepository.acceptTermsAndConditions()
            response.onSuccess {
                _acceptanceState.postValue(true)
            }
            response.onFailure { error ->
                error.message?.let { Log.e("terms and conditions", error.toString()) }
            }
        }
    }
}
