package spontaniius.ui.card_collection

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.CardCollectionRepository
import spontaniius.data.repository.CardRepository
import spontaniius.data.remote.models.CardCollectionResponse
import spontaniius.domain.models.Card
import javax.inject.Inject

@HiltViewModel
class CardCollectionViewModel @Inject constructor(
    private val collectionRepository: CardCollectionRepository,
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _cardCollections = MutableLiveData<List<CardCollectionResponse>>()
    val cardCollections: LiveData<List<CardCollectionResponse>> = _cardCollections

    private val _userCards = MutableLiveData<List<Card>>()
    val userCards: LiveData<List<Card>> = _userCards

    fun loadUserCardCollections() {
        viewModelScope.launch {
            val collections = collectionRepository.getUserCardCollections()
            collections.onSuccess { cardCollections ->
                val uniqueCardIds = cardCollections.flatMap { it.cardIds!! }.distinct()
                if (uniqueCardIds.isNotEmpty()) {
                    val cards = cardRepository.getCardDetails(uniqueCardIds)
                    _userCards.postValue(cards)
                }
            }
        }
    }
}
