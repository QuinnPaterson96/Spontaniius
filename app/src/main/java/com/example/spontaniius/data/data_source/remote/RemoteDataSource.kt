package com.example.spontaniius.data.data_source.remote

import android.util.Log
import com.example.spontaniius.data.EventEntity
import com.example.spontaniius.data.data_source.DataSource
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class RemoteDataSource @Inject constructor() :
    DataSource {

    override suspend fun saveEvent(eventEntity: EventEntity) {
        var urlConnection: HttpURLConnection? = null
        try {
            val url =
                URL(" https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/createSpontaniiusEvent")
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.doOutput = true
            val outputStream = urlConnection.outputStream as DataOutputStream
            outputStream.writeBytes("PostData=" + eventEntity.toJSON().toString())
            outputStream.flush()
            outputStream.close()

            val inputStream = urlConnection.inputStream
            val inputStreamReader = InputStreamReader(inputStream)
            var inputData: Int = inputStreamReader.read()
            var returnMessage: String = ""
            while (inputData != -1) {
                val currentChar = inputData.toChar()
                returnMessage += currentChar
                inputData = inputStreamReader.read()
            }
            Log.v("eventID: ", returnMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }
    }
}