/**
 * qu75_build_a_respons.kt
 * 
 * This project aims to build a responsive data pipeline controller using Kotlin.
 * The controller will manage data flow between multiple sources and destinations,
 * ensuring efficient and timely processing of data.
 * 
 * Features:
 *  - Multiple data source integration (API, database, files)
 *  - Real-time data processing and transformation
 *  - Scalable architecture for high-volume data processing
 *  - Error handling and fault tolerance
 *  - Responsive dashboard for monitoring and control
 */

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

// Data source interfaces
interface DataSource {
    suspend fun fetchData(): List<Data>
}

interface DataDestination {
    suspend fun sendData(data: List<Data>)
}

// Data class for processing
data class Data(val id: Int, val value: String)

// Responsive data pipeline controller
class ResponsiveDataPipelineController {
    private val dataSource: DataSource
    private val dataDestination: DataDestination
    private val dataProcessingQueue: MutableList<Data> = mutableListOf()
    private val dataProcessedCount: AtomicInteger = AtomicInteger(0)

    constructor(dataSource: DataSource, dataDestination: DataDestination) {
        this.dataSource = dataSource
        this.dataDestination = dataDestination
    }

    // Start data processing pipeline
    suspend fun startPipeline() {
        launchProcessingLoop()
    }

    // Private function to launch data processing loop
    private suspend fun launchProcessingLoop() {
        while (true) {
            val data = dataSource.fetchData()
            dataProcessingQueue.addAll(data)
            processAndSendData()
        }
    }

    // Private function to process and send data
    private suspend fun processAndSendData() {
        val dataToSend = dataProcessingQueue.takeIf { it.size > 10 }?.let {
            val sendData = it.take(10)
            dataProcessedCount.addAndGet(sendData.size)
            sendData
        }
        if (dataToSend != null) {
            dataDestination.sendData(dataToSend)
            dataProcessingQueue.removeAll(dataToSend)
        }
    }
}

// Example usage
fun main() {
    val dataSource = object : DataSource {
        override suspend fun fetchData(): List<Data> {
            // Simulate fetching data from API
            delay(1000)
            return listOf(Data(1, "data1"), Data(2, "data2"))
        }
    }

    val dataDestination = object : DataDestination {
        override suspend fun sendData(data: List<Data>) {
            // Simulate sending data to database
            delay(500)
            println("Sent data to database: $data")
        }
    }

    val controller = ResponsiveDataPipelineController(dataSource, dataDestination)
    runBlocking {
        controller.startPipeline()
    }
}