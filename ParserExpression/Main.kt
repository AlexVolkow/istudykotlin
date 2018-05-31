import expression.parser.ExpressionParser
import expression.parser.Tokenizer

fun main(args: Array<String>) {
    val parser = ExpressionParser()
    val expr = parser.parse("(-492307943) * (y)")
    println(expr)
    print(expr.evaluate(721337873, 1978272111, -2127396603))
}

