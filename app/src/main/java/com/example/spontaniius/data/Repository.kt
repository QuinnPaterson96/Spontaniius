package com.example.spontaniius.data

interface Repository {

    suspend fun saveEvent(eventEntity: EventEntity)
}