package com.example.spontaniius.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class EventEntityTest {

    @Test
    fun toJSON() {
        val now = Calendar.getInstance()
        val entity = EventEntity(
            "testTitle",
            "testDesc",
            "ANY",
            "0001",
            "null icon",
            "1970-01-01 00:00:00",
            "1970-01-01 00:00:00",
            1
        )
        val year = now.get((Calendar.YEAR))
        val month = now.get((Calendar.MONTH)) + 1
        val day = now.get((Calendar.DAY_OF_MONTH))
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        val expected =
            "\"eventTitle\":\"testTitle\",\"eventText\":\"testDesc\",\"genderRestrict\":\"ANY\",\"icon\":\"null icon\",\"streetAddress\":\"(48.4335854,-123.33710359999999)\",\"maxRadius\":1,\"eventStarts\":\"$year-$month-$day $hour:$minute:00\",\"eventEnds\":\"$year-$month-$day $hour:$minute:00"
        val result = entity.toJSON().toString()
        assertEquals(
            expected, result
        )
    }
}