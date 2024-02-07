import kotlinx.coroutines.*

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    val job = CoroutineScope(Dispatchers.IO).launch {
        // Код корутины
        delay(1000)
        println("Coroutine completed")
    }

// Регистрация обратного вызова при завершении Job
    job.invokeOnCompletion { throwable ->
        if (throwable != null) {
            println("Job was cancelled: ${throwable.message}")
        } else {
            println("Job completed successfully")
        }
    }
}

suspend inline fun loadBigData() { delay(60_000) }

