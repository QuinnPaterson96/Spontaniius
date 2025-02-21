package spontaniius.ui.card_editing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.CardRepository
import javax.inject.Inject

@HiltViewModel
class CardEditingViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cardCreated = MutableLiveData<Boolean>()
    val cardCreated: LiveData<Boolean> = _cardCreated

    fun createCard(cardText: String?, backgroundId: Int) {
        _loading.value = true

        viewModelScope.launch {
            val result = cardRepository.createCard(card_text, backgroundId)
            result.onSuccess {
                _cardCreated.value = true
            }.onFailure { error ->
                _error.value = error.message
            }
            _loading.value = false
        }
    }
}
