package expression.parser

import expression.*
import kotlin.text.*


class ExpressionParser: Parser {
    private var tokens: Tokenizer? = null
    private var lastLeftBracket = -1

    override fun parse(expression: String): TripleExpression {
        tokens = Tokenizer(expression)
        lastLeftBracket = -1
        return expression(false)
    }

    private fun expression(isLeftBracketPresent: Boolean): TripleExpression {
        val acc = add()

        if (tokens!!.hasMoreElements()) {
            val token = tokens!!.nextElement()

            when (token.type) {
                TokenType.END -> if (isLeftBracketPresent) {
                    throw ParsingException("no pairing closing parenthesis for opening parenthesis", lastLeftBracket)
                }
                TokenType.LEFT_BR -> throw ParsingException("unexpected ( found", token.idx)

                TokenType.RIGHT_BR -> if (!isLeftBracketPresent) {
                    throw ParsingException("no pairing opening parenthesis for closing parenthesis", token.idx)
                }
                else -> throw ParsingException("incorrect expression", token.idx)
            }
        }

        return acc
    }

    private fun add(): TripleExpression {
        var acc = term()

        while (tokens!!.hasMoreElements()) {
            val operation = tokens!!.nextElement()

            acc = when (operation.type) {
                TokenType.PLUS -> Add(acc, term())

                TokenType.MINUS -> Subtract(acc, term())

                else -> {
                    tokens!!.prevElement()
                    return acc
                }
            }
        }

        return acc
    }

    private fun term(): TripleExpression {
        var acc = primary()

        while (tokens!!.hasMoreElements()) {
            val operation = tokens!!.nextElement()

            acc = when (operation.type) {
                TokenType.MUL -> Multiply(acc, primary())

                TokenType.DIV -> Divide(acc, primary())

                else -> {
                    tokens!!.prevElement()
                    return acc
                }
            }
        }

        return acc
    }

    private fun primary(): TripleExpression {
        val token = tokens!!.nextElement()
        val primary: TripleExpression

        when (token.type) {
            TokenType.END, TokenType.RIGHT_BR -> {
                if (tokens!!.isFirst()) {
                    throw ParsingException("no pairing opening parenthesis for closing parenthesis", token.idx)
                }
                if (tokens!!.prevElement().type === TokenType.LEFT_BR) {
                    if (token.type === TokenType.RIGHT_BR) {
                        throw ParsingException("empty parenthesis sequence at positions $lastLeftBracket - ${token.idx}")
                    } else {
                        throw ParsingException("no pairing closing parenthesis for opening parenthesis", lastLeftBracket)
                    }
                }
                throw ParsingException("no last argument for operator ${tokens!!.current()}", token.idx)
            }

            TokenType.CONST -> primary = Const(token.value.toInt())

            TokenType.VARIABLE -> primary = Variable(token.value)

            TokenType.MINUS -> primary = if (tokens!!.hasMoreElements() && tokens!!.nextElement().type === TokenType.CONST) {
                val number = tokens!!.current()
                Const(("-" + number.value).toInt())
            } else {
                tokens!!.prevElement()
                Negate(primary())
            }

            TokenType.ABS -> primary = Abs(primary())

            TokenType.LEFT_BR -> {
                lastLeftBracket = token.idx
                primary = expression(true)
                if (!(tokens!!.current().type === TokenType.RIGHT_BR)) {
                    throw ParsingException("no pairing closing parenthesis for opening parenthesis", lastLeftBracket)
                }
                return primary
            }

            else -> {
                if (token.idx == 0) {
                    throw ParsingException("no first argument for operator " + token.value, token.idx)
                }
                val prev = tokens!!.prevElement()
                if (prev.type === TokenType.LEFT_BR || prev.type === TokenType.MINUS) {
                    throw ParsingException("no first argument for operator " + token.value, token.idx)
                }
                throw ParsingException("no middle argument between operator ${prev.value} at position ${prev.idx + 1} " +
                        "and operator ${token.value}", token.idx)
            }
        }

        return primary
    }
}