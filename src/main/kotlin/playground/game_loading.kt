package playground

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object ResourceHolder {
    private val resources: ConcurrentHashMap<String, Any> = ConcurrentHashMap()
    private val maxResourceLoaded = AtomicInteger(0)

    fun putLoaded(string: String, resource: Any) = resources.put(string, resource)
    fun getLoadedResourcesCount() = resources.size
    fun addMaxResourceLoaded(int: Int) = maxResourceLoaded.addAndGet(int)
    fun getMaxResourceLoaded() = maxResourceLoaded
}

/**
 * Получается сабмитим 6 задач при каждом вызове...
 * suspend режет на части
 */
suspend fun loadResource(filename : String) : Any {
    ResourceHolder.addMaxResourceLoaded(5)
    delay(500)
    ResourceHolder.putLoaded(filename, filename)
    delay(500)
    ResourceHolder.putLoaded(filename + "1", filename + "1")
    delay(500)
    ResourceHolder.putLoaded(filename + "2", filename + "2")
    delay(500)
    ResourceHolder.putLoaded(filename + "3", filename + "3")
    delay(500)
    ResourceHolder.putLoaded(filename + "4", filename + "4")
    return filename
}

suspend fun main() {
    val handler = ResourceHolder // variable for debugger only
    CoroutineScope(Dispatchers.Default).launch { // this : CoroutineScope

        /* запускает задачи последовательно, однако юзает другой ThreadPool, 1 ветка дерева */
//        println("submit 1")
//        withContext(Dispatchers.IO) { loadResource("aaa.png") }
//        println("submit 2")
//        withContext(Dispatchers.IO) { loadResource("bbb.png") }
//        println("submit 3")
//        withContext(Dispatchers.IO) { loadResource("ccc.png") }

        /* запускает задачи последовательно, 1 ветка дерева */
//        println("submit 1")
//        loadResource("aaa.png")
//        println("submit 2")
//         loadResource("bbb.png")
//        println("submit 3")
//       loadResource("ccc.png")


        /* запускает задачи параллельно, 3 ветки дерева */
        println("submit 1")
        this.launch { loadResource("aaa.png") }
        println("submit 2")
        this.launch { loadResource("bbb.png") }
        println("submit 3")
        this.launch { loadResource("ccc.png") }

    }
//        .join()

    /* coroutine стартуют не молниеносно и получали кейс: 0 != 0 => false */
//    while (ResourceHolder.getLoadedResourcesCount() != ResourceHolder.getMaxResourceLoaded().toInt()) {
    while (ResourceHolder.getLoadedResourcesCount() != 15) {
        // simulate render UI each frame
        print("\rloading... (loaded parts: ${ResourceHolder.getLoadedResourcesCount()})")
    }
    println("Finish")
    println("loading... (loaded parts: ${ResourceHolder.getLoadedResourcesCount()})")

}
/*
out:
loading... (loaded parts: 0)submit 1
loading... (loaded parts: 0)submit 2
loading... (loaded parts: 0)submit 3
loading... (loaded parts: 14)Finish    // эта строка меняется по ходу дела.
loading... (loaded parts: 15)

Если бы загрузка была бы Блокирующей, мы не смогли бы писать в консоль/рендерить UI,
мы бы заблокали Main Thread намертво!
 */