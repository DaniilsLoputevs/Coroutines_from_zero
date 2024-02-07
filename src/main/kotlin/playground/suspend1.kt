package playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() {

    CoroutineScope(Dispatchers.IO). launch {
        val userId = loadUserId() // suspend function


        println("User ID is downloaded")


        val user = loadUserById(userId) // suspend function


        println("User is downloaded: $user")
    }
}

private suspend fun loadUserId() = 15
private suspend fun loadUserById(id : Int) = "User{id=$id,name='alex'}"