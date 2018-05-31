package expression.parser

import expression.TripleExpression

open class ParsingException: Exception {
    constructor(message:String) : super(message)

    constructor(message:String, index:Int) : super("$message at position ${index + 1}")
}

interface Parser {
    fun parse(expression: String): TripleExpression
}