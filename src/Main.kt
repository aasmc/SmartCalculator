import java.math.BigInteger
import java.util.*
import kotlin.system.exitProcess

fun main() {
    val scanner = Scanner(System.`in`)
    val calculator = Calculator(InfixToPostfixConverter())
    println("You can add, subtract, multiply, delete numbers. You can use brackets. E.g. 9 + 6 - (5 + 4) * (3 - 1)")
    println("You can use the following commands:")
    println("/help")
    println("/exit")
    while (scanner.hasNext()) {
        val line = scanner.nextLine()
        if (line == "") continue
        else if (line.startsWith("/")) {
            when(line) {
                "/help" -> {
                    println("You can add, subtract, multiply, delete numbers. You can use brackets. E.g. 9 + 6 - (5 + 4) * (3 - 1)")
                }
                "/exit" -> {
                    println("Bye!")
                    exitProcess(1)
                }
                else -> {
                    println("Unknown command")
                }
            }
        }
        else {
            calculator.execute(line)
        }
    }

}
