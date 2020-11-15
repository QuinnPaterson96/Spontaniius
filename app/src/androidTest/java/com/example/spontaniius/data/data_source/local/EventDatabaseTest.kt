package com.example.spontaniius.data.data_source.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.spontaniius.data.EventEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class EventDatabaseTest {

    private lateinit var db: EventDatabase
    private lateinit var eventDao: EventDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, EventDatabase::class.java).build()
        eventDao = db.eventDao()
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @After
    fun tearDown() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun testEventDao() {
        assertNotNull(db)
        assertNotNull(eventDao)
    }

    @Test
    fun testInsertionRetrieval() {
        //  Insertion
        val testData = EventEntity(
            "title",
            "description",
            "all genders",
            "uvic's address",
            "flower icon",
            "start time",
            "end time",
            100.0,
            200.0,
            1
        )
        runBlocking {
            eventDao.insertEvent(testData)
        }

//        Retrieval
        val result = eventDao.getEvents()
        val countDownLatch = CountDownLatch(1)
        var retrievedData: EventEntity? = null
        val observer = object : Observer<List<EventEntity>> {
            override fun onChanged(t: List<EventEntity>?) {
                if (t != null && t.isNotEmpty()) {
                    retrievedData = t[0]
                    countDownLatch.countDown()
                }
                result.removeObserver(this)
            }
        }

        result.observeForever(observer)
        countDownLatch.await()
        assertEquals(testData, retrievedData)
    }

//    TODO: test more DAO functions when more are created
}

