import com.spontaniius.R

data class ActivityCategory(
    val key: String,
    val label: String,
    val iconRes: Int
)

val activityCategories = mapOf(
    "bike" to ActivityCategory("bike", "Bike", R.drawable.activity_bike),
    "boardgames" to ActivityCategory("boardgames", "Board Games", R.drawable.activity_boardgames),
    "coffee" to ActivityCategory("coffee", "Coffee", R.drawable.activity_coffee),
    "drinks" to ActivityCategory("drinks", "Drinks", R.drawable.activity_drinks),
    "eating" to ActivityCategory("eating", "Eating", R.drawable.activity_eating),
    "other" to ActivityCategory("other", "Other", R.drawable.activity_other),
    "sports" to ActivityCategory("sports", "Sports", R.drawable.activity_sports),
    "videogame" to ActivityCategory("videogame", "Video Game", R.drawable.activity_videogame),
    "walk" to ActivityCategory("walk", "Walk", R.drawable.activity_walk)
)
