import kotlinx.coroutines.*
import java.net.Socket
import java.net.URL
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.util.concurrent.CompletableFuture
import kotlin.io.path.Path

fun main(args: Array<String>) {

    URL("").openConnection()
    Thread.yield()
//    Path().toFile().writeBytes()
//    Socket
//    AsynchronousSocketChannel()
    AsynchronousServerSocketChannel.open().accept()
        .let { CompletableFuture.completedFuture(it) }
        .apply { this.thenAccept {


        } }

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

