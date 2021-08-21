package spontaniius.data.data_source.remote

import android.util.Log
import spontaniius.data.EventEntity
import spontaniius.data.data_source.DataSource
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class RemoteDataSource @Inject constructor() :
    DataSource {

    override suspend fun saveEvent(eventEntity: EventEntity) {
        val thread = Thread(Runnable {
            var urlConnection: HttpURLConnection? = null
            try {
                val url =
                    URL(" https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/createSpontaniiusEvent")
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.doOutput = true
                urlConnection.doInput = true
                urlConnection.connect()

                val outputStream = DataOutputStream(urlConnection.outputStream as OutputStream)
                val data = eventEntity.toJSON().toString()
                outputStream.writeBytes(data)
                outputStream.flush()
                outputStream.close()

                val responseCode = urlConnection.responseCode
                val responseMessage = urlConnection.responseMessage
//  TODO: Figure out why there is a server error: response code 500
                Log.i("STATUS", responseCode.toString())
                Log.i("MSG", responseMessage)


                val inputStream = DataInputStream(urlConnection.inputStream as InputStream)
                val inputStreamReader = InputStreamReader(inputStream)
                var inputData: Int = inputStreamReader.read()
                var returnMessage = ""
                while (inputData != -1) {
                    val currentChar = inputData.toChar()
                    returnMessage += currentChar
                    inputData = inputStreamReader.read()
                }
                Log.v("eventID: ", returnMessage)
                val message = returnMessage

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                urlConnection?.disconnect()
            }
        })
        thread.start()
    }
}