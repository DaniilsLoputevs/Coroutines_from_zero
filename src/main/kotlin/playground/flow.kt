package playground

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

fun main() {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Handle exception: ${throwable.message}")
    }
//
//
    val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)


    val flow = flow<Int> {
        emit(2)
        var multiply = 2
        emit(2 * multiply++)
        emit(2 * multiply++)
//        while (true) {
//            emit(2 * multiply++)
//        }
    }

    runBlocking {
        flow.collect { println(it) }
        flow.collect { println(it) }
//        flow.collect { println(it) }
//        flow.collect { println(it) }
//        flow.collect { println(it) }
//        flow.collect { println(it) }
    }


}