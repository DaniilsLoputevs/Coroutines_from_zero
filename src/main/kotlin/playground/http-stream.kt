package playground

import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

fun requestTask01() {
    val responseStreamingOutput = ByteArrayOutputStream()

    CoroutineScope(Dispatchers.Default).launch {
        // do some work

        withContext(Dispatchers.IO) {
          // call blocking fun
            responseStreamingOutput.write(getBigData().toByteArray())
        }
        // do some async work
    }
}
/*
Моя промежуточная мини-версия

Многозадачность или канкаренсе - наличие/разбиения кода на деревья задач с ветками из под-задач, которые будут выполнятся вычислительными устройства, зависимо или независимо друг от друга.
* Присутствует проблема управление задачей(получить результат(возможно ожидания) отложить исполнение, отменить(обычно или и с Exception) )

Многопоточность - вычислительными устройствами для задач являются потоки исполнение программы. Термин актуален, когда потоков более 1.

Параллельность задач -
 */
suspend fun getBigData() : String {
    delay(500)
    return "AAA"
}