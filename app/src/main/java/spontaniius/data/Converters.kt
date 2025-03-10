package spontaniius.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(value: List<Int>): String {
        return value.joinToString(",") // Convert List<Int> to a comma-separated string
    }

    @TypeConverter
    fun toList(value: String): List<Int> {
        return if (value.isEmpty()) emptyList() else value.split(",").map { it.toInt() }
    }
}