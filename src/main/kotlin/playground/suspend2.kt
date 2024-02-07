package playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() {

    CoroutineScope(Dispatchers.IO). launch {
        val unit = loadUnit() // suspend function
        println("log #1 : $unit")

        val user = loadUserById(14) // suspend function
        println("log #2 : $user")

        val `class` = loadInlineClass<Pair<Int, String>>() // suspend function
        println("log #3 : $`class`")
    }
}

private suspend fun loadUnit() = Unit
private suspend fun loadUserById(id : Int) = "User{id=$id,name='alex'}"
private inline suspend fun <reified T> loadInlineClass() : String {
    loadUnit()
    loadUserById(11)
    return "${T::class.java.simpleName}.{id=11,name='alex'}"
}