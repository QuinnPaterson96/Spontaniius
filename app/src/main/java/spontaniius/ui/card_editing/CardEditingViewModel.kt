package spontaniius.ui.card_editing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.local.entities.UserEntity
import spontaniius.data.repository.CardRepository
import spontaniius.data.repository.UserRepository
import spontaniius.domain.models.Card
import spontaniius.domain.models.User
import javax.inject.Inject

@HiltViewModel
class CardEditingViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userDetails = MutableLiveData<User>()
    val userDetails = _userDetails

    private val _userCard = MutableLiveData<Card?>()
    val userCard: LiveData<Card?> = _userCard

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cardCreated = MutableLiveData<Boolean>()
    val cardCreated: LiveData<Boolean> = _cardCreated

    fun createCard(cardText: String?, backgroundId: Int) {
        _loading.value = true

        viewModelScope.launch {
            val result = cardRepository.createCard(cardText, backgroundId)
            result.onSuccess { card_id ->
                userRepository.updateUserCard(card_id)
                _cardCreated.value = true
            }.onFailure { error ->
                _error.value = error.message
            }
            _loading.value = false
        }
    }

    fun getUserDetails(){
        viewModelScope.launch {
            userDetails.postValue(userRepository.getUserDetails()?.toDomainModel())
        }
    }

    fun getUserCardDetails(){
        viewModelScope.launch{
            val userCardId = userRepository.getUserCardId()
            if (userCardId!=null){
                val result = cardRepository.getCardDetails(listOf(userCardId))
                if (result.size==1){
                    _userCard.postValue(result[0])
                }else{
                    Log.e("Card Editting", "Card id didn't result in card retrieval")
                    _userCard.postValue(null)
                }
            }else{
                _userCard.postValue(null)
            }
        }
    }
}
