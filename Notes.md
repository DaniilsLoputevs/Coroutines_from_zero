### Интересный факт. 
Отмена Coroutine кидает exception со сбором полного StackTrace, что немного тяжеловато.

Такой вывод можно сделать посмотрев на документацию метода
```kotlin
kotlinx.coroutines.Job.invokeOnCompletion(handler: CompletionHandler): DisposableHandle
    // CancellationException использует обычные Exception конструкторы, что собирают StackTrace
```



### Материалы
* [(документация) Kotlin Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)
* [(видео) Андрей Бреслав — Асинхронно, но понятно. Сопрограммы в Kotlin](https://youtu.be/ffIVVWHpups?si=17zNiuB7jISG3g6w)
* [(видео) Антон Полухин — Анатомия асинхронных движков](https://www.youtube.com/watch?v=bSJp3lRjU7k)
* []()
* []()
* []()
