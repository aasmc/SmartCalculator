import java.lang.StringBuilder
import java.util.concurrent.ConcurrentLinkedDeque

class InfixToPostfixConverter : Converter {

    override fun convert(expression: String): String {
        val stack = ConcurrentLinkedDeque<String>()
        val correctInfix = removeExtraOperatorsFromInfix(expression)
        if ("Invalid expression" == correctInfix) {
            return correctInfix
        }
        val infixArray = correctInfix.split("\\s+".toRegex())
        val postfix = StringBuilder()

        for (i in infixArray.indices) {
            val c = infixArray[i]
            if (c == " " || c == ",") {
                continue
            } else if (isOperator(c)) {
                while (stack.isNotEmpty() && stack.peek() != "(" && getPrecedence(stack.peek()) >= getPrecedence(c)) {
                    postfix.append(stack.pop()).append(" ")
                }
                stack.push(c)
            } else if (isOperand(c)) {
                postfix.append(c).append(" ")
            } else if (c == "(") {
                stack.push(c)
            } else if (c == ")") {
                while (stack.isNotEmpty() && stack.peek() != "(") {
                    postfix.append(stack.pop()).append(" ")
                }
                stack.pop()
            }
        }

        while (stack.isNotEmpty()) {
            postfix.append(stack.pop()).append(" ")
        }
        return postfix.toString()
    }


    private fun removeExtraOperatorsFromInfix(expression: String): String {
        val withSpaces = createInfixWithSpaces(expression)
        val split = withSpaces.split("\\s+".toRegex()).toMutableList()
        var countLeftBraces = 0
        var countRightBraces = 0
        for (i in split.indices) {
            val symbol = split[i]
            if (symbol.matches("\\++".toRegex())) {
                split[i] = " + "
            } else if (symbol.matches("-+".toRegex())) {
                val minuses = symbol.split("")
                if (minuses.size % 2 == 0) {
                    split[i] = " + "
                } else {
                    split[i] = " - "
                }
            } else if (symbol.matches("\\*{2,}".toRegex())) {
                return "Invalid expression"
            } else if (symbol.matches("/{2,}".toRegex())) {
                return "Invalid expression"
            } else if (symbol == "(") {
                countLeftBraces++
            } else if (symbol == ")") {
                countRightBraces++
            }
        }
        val sb = StringBuilder()
        for (symbol in split) {
            sb.append(" $symbol ")
        }
        if (countLeftBraces != countRightBraces) {
            return "Invalid expression"
        }
        return sb.toString()
    }

    private fun isOperator(c: String): Boolean {
        return c == "+" || c == "-" || c == "*" || c == "/" || c == "^"
    }

    private fun getPrecedence(ch: String): Int {
        return when (ch) {
            "+", "-" -> 1
            "*", "/" -> 2
            "^" -> 3
            else -> -1
        }
    }

    private fun isOperand(ch: String): Boolean {
        return (ch.matches("[a-zA-Z]+".toRegex())) || (ch.matches("\\d+".toRegex()))
    }

    /**
     * Converts infix expression to the one containing spaces before ) and after ( so that it could be used in calculation of double or triple digits
     */
    private fun createInfixWithSpaces(expression: String): String {
        var result = ""
        for (i in expression.indices) {
            when (val ch = expression[i]) {
                '(' -> {
                    result += "$ch "
                }
                ')' -> {
                    result += " $ch"
                }
                else -> {
                    result += ch
                }
            }
        }
        var correctExpression = ""
        val split = result.split("\\s+".toRegex())
        for (symbol in split) {
            when {
                isOperand(symbol) -> {
                    correctExpression += "$symbol "
                }
                symbol == "(" -> {
                    correctExpression += "$symbol "
                }
                symbol == ")" -> {
                    correctExpression += " $symbol"
                }
                symbol.matches("(\\*+|-+|/+|\\++)".toRegex()) -> {
                    correctExpression += " $symbol "
                }
            }
        }
        return correctExpression
    }
}