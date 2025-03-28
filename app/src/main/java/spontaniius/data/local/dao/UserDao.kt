package spontaniius.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import spontaniius.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun clearUser()
}
