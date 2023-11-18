import java.util.*

enum class ForkStatus {
    FREE, // вилка свободна
    TAKEN // вилка занята
}

data class Philosopher(
    val id: Int,//идентификатор философа
    val leftFork: Fork,//левая вилка
    val rightFork: Fork//правая вилка
) : Runnable {
    override fun run() {
        while (true) {//бесконечный цикл, который будет повторяться, пока не будет прерван выполнением потока.
            if (leftFork.take(id) && rightFork.take(id)) {
                println("Филосов $id ест")
                leftFork.put(id)//возвращение вилок в исходное состояние
                rightFork.put(id)
                break
            } else {
                leftFork.put(id)
                rightFork.put(id)
                println("Филосов $id думает")

            }
        }
    }
}

class Fork {//описывает вилку, которая может быть свободной или занятой.

    private var status = ForkStatus.FREE
    private var takenBy: Int? = null

    @Synchronized
    fun take(id: Int): Boolean {
        if (status == ForkStatus.FREE) {
            status = ForkStatus.TAKEN
            takenBy = id
            return true
        }
        return false
    }

    @Synchronized
    fun put(id: Int) {
        if (status == ForkStatus.TAKEN && takenBy == id) {
            status = ForkStatus.FREE
            takenBy = null
        }
    }
}

fun main() {
    print ("Введите кол-во философов: ")
    val philosophersCount = readlnOrNull()?.toIntOrNull()
    if (philosophersCount == null || philosophersCount <= 0) {
        println("Не правильный ввод")
        return
    }

    val forks = List(philosophersCount) { Fork() }
    val philosophers = List(philosophersCount) { id ->
        val leftFork = forks[id]
        val rightFork = forks[(id + 1) % philosophersCount]
        Philosopher(id, leftFork, rightFork)
    }

    philosophers.shuffled().forEach { philosopher ->
        // перемешиваем элементы коллекции и для каждого перемешанного элемента выполняем определенное действие
        // для каждого философа в массиве запускается новый поток
        Thread(philosopher).start()
    }
}