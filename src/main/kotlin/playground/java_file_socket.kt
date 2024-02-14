package playground

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.Option
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future


fun main() {
    val o =
    ConnectionFactories.get(ConnectionFactoryOptions.builder()
        .option(ConnectionFactoryOptions.DRIVER, "a-driver")
        .option(ConnectionFactoryOptions.PROTOCOL, "pipes")
        .option(ConnectionFactoryOptions.HOST, "localhost")
        .option(ConnectionFactoryOptions.PORT, 3306)
        .option(ConnectionFactoryOptions.DATABASE, "my_database")
        .option(Option.valueOf("locale"), "en_US")
        .build())
//    val pub = o.create().subscribe()



//    val o = File("")
//    o.outputStream()
    try {
        // Открываем файл асинхронным способом
        val fileChannel = AsynchronousFileChannel.open(
            Paths.get("example.txt"), StandardOpenOption.READ
        )

        // Создаем буфер для чтения данных
        val buffer: ByteBuffer = ByteBuffer.allocate(1024)

        // Инициируем операцию чтения файла
//        val readResult: Future<Int> = fileChannel.read(buffer, 0)
        val readResult: Future<Int> = fileChannel.read(buffer, 0, )

        // Дожидаемся завершения операции чтения
        while (!readResult.isDone()) {
            // Выполняем другие задачи, пока операция чтения не завершится
            println("Waiting for read operation to complete...")
        }

        // Получаем количество прочитанных байтов
        val bytesRead: Int = readResult.get()

        // Выводим прочитанные данные
        buffer.flip()
        val data = ByteArray(bytesRead)
        buffer.get(data)
        println("Read data: " + String(data))

        // Закрываем канал файла
        fileChannel.close()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ExecutionException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
}