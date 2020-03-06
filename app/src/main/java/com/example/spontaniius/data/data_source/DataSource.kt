package com.example.spontaniius.data.data_source

import com.example.spontaniius.data.EventEntity

interface DataSource {

    suspend fun saveEvent(eventEntity: EventEntity)

}