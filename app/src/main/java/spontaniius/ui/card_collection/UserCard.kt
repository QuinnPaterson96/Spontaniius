package spontaniius.ui.card_collection

class UserCard(cardOwnerName: String?, cardBackground: Int, cardId: String?, greeting: String?) {
    private var cardOwnerName: String? = cardOwnerName
    private var cardid: String? = cardId
    private var background =  cardBackground
    private var greeting: String? = greeting


    fun getCardUserID(): String? {
        return cardid
    }

    fun getBackground(): Int {
        return background
    }
    fun getGreeting(): String? {
        return greeting
    }
    fun getCardOwnerName(): String? {
        return cardOwnerName
    }

}
