package spontaniius.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import spontaniius.data.local.entities.CardEntity

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Query("SELECT * FROM cards WHERE cardId IN (:cardIds)")
    suspend fun getCardsByIds(cardIds: List<Int>): List<CardEntity>

    @Query("DELETE FROM cards")
    suspend fun clearAllCards()
}
