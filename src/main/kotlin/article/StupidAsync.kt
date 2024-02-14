package article

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/*
    # Java world async API

    Родительский Поток
    -> блокирующая операция ИЛИ тяжёлая по нагрузке на IO или CPU задача. Далее по тексту просто Задача
    -> делегируем операцию другому потоку
        заставляем Другой поток делать грязную работу(например ждать в блокировке),
        а сами опционально можем взять Пульт управления(от Потока или Операции).
    -> Когда блокирующая операция ИЛИ задача завершена, "дергаем" *обработчик результат*

    - Где вызывается Async API? (варианты)
    - Что может делать Родительский Поток, после запуска async операции? (варианты)
    - Блокирующие операции, звучит как Абстракция-interface, как есть реализации? (варианты)
    - Кто, как, где и почему дергает *обработчик результат?



 */



fun main() {
    mainMethod3()
}


private fun funcWithBigIORead(): String {
    Thread.sleep(5_000)
    return "funcWithBigIORead # Result"
}

/** only main Thread way */
private fun mainMethod0() {
    val startNano = System.nanoTime()

    val resultA = funcWithBigIORead() + " Thread A"
    println("threadA # END")
    val resultB = funcWithBigIORead() + " Thread B"
    println("threadB # END")

    val resultMain = funcWithBigIORead() + " Main"
    val endNano = System.nanoTime()
    println("threadMain # END")

    println(resultA)
    println(resultB)
    println(resultMain)
    println(TimeUnit.NANOSECONDS.toMillis(endNano - startNano)) // 15014 приблизительно

    /* out:
            threadA # END
            threadB # END
            threadMain # END
            funcWithBigIORead # Result Thread A
            funcWithBigIORead # Result Thread B
            funcWithBigIORead # Result Main
            15015
     */
}
/*
    Особенности подхода
    - Исполнение занимает много времени.

    + бизнес логика пишется-читается последовательно т.к. это Один поток, всё как обычно, как привыкли.
 */


/** Thread way */
private fun mainMethod1() {
    val startNano = System.nanoTime()
    val operationResultContainerA = AtomicReference<String>()

    val threadA = Thread {
        operationResultContainerA.set(funcWithBigIORead() + " Thread A")
        println("threadA # END")
    }.also { it.start() }

    val operationResultContainerB = AtomicReference<String>()

    val threadB = Thread {
        operationResultContainerB.set(funcWithBigIORead() + " Thread B")
        println("threadB # END")
    }.also { it.start() }

    val resultMain = funcWithBigIORead() + " Main"
    val endNano = System.nanoTime()
    println("threadMain # END")

    threadA.join()
    threadB.join()
    println(operationResultContainerA.get())
    println(operationResultContainerB.get())
    println(resultMain)
    println(TimeUnit.NANOSECONDS.toMillis(endNano - startNano)) // приблизительно 5105 ms


    /* out:
            threadB # END
            threadMain # END
            threadA # END
            funcWithBigIORead # Result Thread A
            funcWithBigIORead # Result Thread B
            funcWithBigIORead # Result Main
            5105
     */
}
/*
    Особенности подхода
    - Ограниченное кол-во Thread(Потов ОС)
    - Синхронизация потоков(Mutex, synchronized-monitor, ReentrantLock и т.д.
        Требуется расширение мозга программиста, что бы понимать столь-специфичный стиль кода).
    - Блокировки (много простоя Потоков ОС)
    - Накладные расходы по времени и памяти на создание и запуск Thread
    - нужно самим придумывать способ читабельно обрабатывать случаи Отмены задачи
    - Неочевидно где, как, почему именно тут и так обрабатывать выкинутые exception.
    - Можно потерять exception и вы никогда не узнаете что ЧТО-ТО пошло не по плану.

    + бизнес логика пишется-читается последовательно, как с одним потоком.
    + задачи реально исполняются Параллельно (эффективнее используем ресурсы железа)
 */

/** Thread way + callback */
private fun mainMethod2() {
    val startNano = System.nanoTime()

    val threadA = Thread {
        val result = funcWithBigIORead() + " Thread A"
        println("threadA # END")
        println(result)
    }.also { it.start() }

    val threadB = Thread {
        val result = funcWithBigIORead() + " Thread B"
        println("threadB # END")
        println(result)
    }.also { it.start() }


    val resultMain = funcWithBigIORead() + " Main"
    println("threadMain # END")
    println(resultMain)

    threadA.join()
    threadB.join()
    val endNano = System.nanoTime()
    println(TimeUnit.NANOSECONDS.toMillis(endNano - startNano)) // приблизительно 5105 ms

    /* out:
            threadA # END
            funcWithBigIORead # Result Thread A
            threadB # END
            funcWithBigIORead # Result Thread B
            threadMain # END
            funcWithBigIORead # Result Main
            5294
     */
}
/*
    Особенности подхода
    - callback hell
    - сложно синхронизировать последовательное выполнение задач.

    + Хорошо подходит, где нет разницы в порядке выполнение.
 */

/** Thread way + ThreadOperationController(Пульт управления, он же Result<T>) */
private fun mainMethod3() {
    class ThreadResult<T> {

        val value = AtomicReference<T>()
        val exception = AtomicReference<Throwable>()
        lateinit var thread: Thread

        public fun completeWithValue(value: T) = this.value.set(value)
        public fun completeWithException(value: Throwable) = this.exception.set(value)
        public fun getWithBlock(): T {
            thread.join()
            return value.get()
        }
    }

    val startNano = System.nanoTime()

    val threadResultA = ThreadResult<String>()
    threadResultA.thread = Thread {
        try {
            val result = funcWithBigIORead() + " Thread A" // место где может взлететь exception
            threadResultA.completeWithValue(result)
            println("threadA # END")
        } catch (e: Exception) {
            threadResultA.completeWithException(e)
        }
    }.also { it.start() }

    val threadResultB = ThreadResult<String>()
    threadResultB.thread = Thread {
        try {
            val result = funcWithBigIORead() + " Thread B" // место где может взлететь exception
            threadResultB.completeWithValue(result)
            println("threadB # END")
        } catch (e: Exception) {
            threadResultB.completeWithException(e)
        }
    }.also { it.start() }


    val resultMain = funcWithBigIORead() + " Main"
    println("threadMain # END")

    println(resultMain)
    val resultA = threadResultA.getWithBlock()
    if (threadResultA.exception.get() != null) println("threadA # END # Exception:" + threadResultA.exception.get())
    else println(resultA)

    val resultB = threadResultB.getWithBlock()
    if (threadResultB.exception.get() != null) println("threadB # END # Exception:" + threadResultB.exception.get())
    else println(resultB)

    val endNano = System.nanoTime()
    println(TimeUnit.NANOSECONDS.toMillis(endNano - startNano)) // приблизительно 5114 ms

    /* out:
            threadB # END
            threadA # END
            threadMain # END
            funcWithBigIORead # Result Main
            funcWithBigIORead # Result Thread A
            funcWithBigIORead # Result Thread A
            5114
     */
}
/*
    Особенности подхода
    - Ограниченное кол-во Thread(Потов ОС)
    - Накладные расходы по памяти и времени на создание и запуск Thread
    - нужно самим придумывать способ читабельно обрабатывать случаи Отмены задачи
    - Можно потерять exception и вы никогда не узнаете что ЧТО-ТО пошло не по плану,
        однако вероятность меньше, т.к. вы знаете что используя пульт управления можно проверить,
        было ли брошено Исключение.

    + сложный код по Синхронизации Потоков ОС убран в библиотеки и бизнес программисту
        не нужно расширять свой мозг, что бы понимать что тут происходит.
    + убрали callback hell
    + нормально синхронизировать последовательное выполнение задач.
    + Хорошо подходит, где есть и нет разницы в порядке выполнение.
 */


// TODO : везде добавить случи с Cancel & Exception
// TODO : везде добавить конкретики, что именно значит "расширять мозг программиста и специфичный стиль"
//          heppens-before, race condition, atomic operations, synchronize primitive, deadlock, lost exception

fun mainRequestDispatcherEndellesLoop() {
    // check socket -> submit requestProcess() it RequestProcessThreadPool
}
suspend fun requestProcess() { // CoroutineThreadPool
    val id = 1
    val email = "bla-bla"
    var data : String = ""
//    data = httpRequestMyStupidWithBlock() // suspend call
//    data = withContext(Dispatchers.IO){
//        httpRequestMyStupidWithBlock()
//    } // suspend call

//    data = httpRequestSmartEngine()

//    println(data)
}

/**
 * Block API
 */
suspend fun httpRequestMyStupidWithBlock() : String {
    // curl http ....
    return "aaa"
}
//suspend fun httpRequestSmartEngine()  : Result<String> {
//    return withContext(Dispatchers.IO){ // OurSmartDispatcherReactive
//        httpRequestMyStupidWithBlock()
//    }
//}

