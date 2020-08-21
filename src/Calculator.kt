import java.math.BigInteger
import java.util.concurrent.ConcurrentLinkedDeque

class Calculator(private val converter: Converter) {

    private val variablesToValues = mutableMapOf<String, BigInteger>()

    fun execute(command: String) {
        when {
            // command matches a single digit with + or - sign
            command.matches("[-+]?\\d+".toRegex()) -> {
                println(if (command.startsWith("+")) command.substring(1) else command)
            }
            command.matches("[a-zA-Z]+".toRegex()) -> {
                printVariableValue(command)
            }
            command.contains("=") -> {
                val result = readAssignment(command)
                if (result != "Success") {
                    println(result)
                }
            }
            else -> {
                val result = calculateInfixExpression(command)
                println(result)
            }
        }
    }

    private fun calculateInfixExpression(expression: String): String {

        val postfix = converter.convert(expression)
        if ("Invalid expression" == postfix) {
            return postfix
        }
        val postfixSymbols = postfix.split("\\s+".toRegex())

        val stack = ConcurrentLinkedDeque<String>()
        for (i in postfixSymbols.indices) {
            val symbol = postfixSymbols[i]
            when {
                symbol.matches("(\\d+)".toRegex()) -> {
                    stack.push(symbol)
                }
                symbol.matches("[a-zA-Z]".toRegex()) -> {
                    stack.push(variablesToValues[symbol].toString())
                }
                symbol.matches("([+*\\-/^])".toRegex()) -> {
                    val first = stack.pop().toBigInteger()
                    val second = stack.pop().toBigInteger()
                    var calculationResult = BigInteger.ZERO
                    when(symbol) {
                        "+" -> {
                            calculationResult = second + first
                        }
                        "-" -> {
                            calculationResult = second - first
                        }
                        "*" -> {
                            calculationResult = second * first
                        }
                        "/" -> {
                            calculationResult = second / first
                        }
                        "^" -> {
                            calculationResult = second.pow(first.toInt())
                        }
                    }
                    stack.push(calculationResult.toString())
                }
            }
        }
        return stack.pop()
    }

    private fun printVariableValue(variable: String) {
        println(if (variablesToValues.containsKey(variable)) variablesToValues[variable] else "Unknown variable")
    }

    private fun readAssignment(line: String): String {
        val split = line.split("=")
        val variable = split[0].trim()
        val value = split[1].trim()
        return if (split.size > 2) {
            "Invalid assignment"
        } else if (!variable.matches("[a-zA-Z]+".toRegex())) {
            "Invalid identifier"
        } else if (!value.matches("-?\\d+".toRegex()) && value.matches("[a-zA-Z]+".toRegex())
            && variablesToValues.containsKey(value)) {
            variablesToValues[value]?.let { assignValueToVariable(variable, it) }
            "Success"
        } else if (variable.matches("[a-zA-Z]+".toRegex()) && value.matches("-?\\d+".toRegex())) {
            assignValueToVariable(variable, value.toBigInteger())
            "Success"
        } else {
            "Invalid assignment"
        }
    }

    private fun assignValueToVariable(variable: String, value: BigInteger) {
        variablesToValues[variable] = value
    }
}