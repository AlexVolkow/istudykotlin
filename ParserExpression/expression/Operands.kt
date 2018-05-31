package expression

import java.lang.Math.abs

data class Const(private val value: Int) : TripleExpression {
    override fun evaluate(x: Int, y: Int, z: Int): Int = value

    override fun toString(): String = value.toString()

    companion object {
        val ONE = Const(1)
        val ZERO = Const(0)
        val MINUS_ONE = Const(-1)
    }
}

data class Variable(private val name: String) : TripleExpression {
    override fun evaluate(x: Int, y: Int, z: Int): Int {
        return when (name) {
            "x" -> x
            "y" -> y
            "z" -> z
            else -> 0
        }
    }

    override fun toString(): String = name
}

abstract class UnaryOperator(private val value: TripleExpression) : TripleExpression {
    abstract fun compute(value: Int): Int

    abstract fun operator(): String

    override fun evaluate(x: Int, y: Int, z: Int): Int = compute(value.evaluate(x, y, z))

    override fun toString(): String = "(${operator()}$value)"
}

abstract class BinaryOperator(private val left: TripleExpression,
                              private val right: TripleExpression) : TripleExpression {
    abstract fun compute(left: Int, right: Int): Int

    abstract fun operator(): String

    override fun evaluate(x: Int, y: Int, z: Int): Int = compute(left.evaluate(x, y, z), right.evaluate(x, y, z))

    override fun toString(): String = "($left ${operator()} $right)"
}

data class Add(private val left: TripleExpression,
               private val right: TripleExpression) : BinaryOperator(left, right) {
    override fun compute(left: Int, right: Int): Int = left + right

    override fun operator(): String = "+"

    override fun toString(): String = super.toString()
}

data class Subtract(private val left: TripleExpression,
                     private val right: TripleExpression) : BinaryOperator(left, right) {
    override fun compute(left: Int, right: Int): Int = left - right

    override fun operator(): String = "-"

    override fun toString(): String = super.toString()
}

data class Multiply(private val left: TripleExpression,
                    private val right: TripleExpression) : BinaryOperator(left, right) {
    override fun compute(left: Int, right: Int): Int = left * right

    override fun operator(): String = "*"

    override fun toString(): String = super.toString()
}

data class Divide(private val left: TripleExpression,
                  private val right: TripleExpression) : BinaryOperator(left, right) {
    override fun compute(left: Int, right: Int): Int = left / right

    override fun operator(): String = "/"

    override fun toString(): String = super.toString()
}

data class Abs(private val value: TripleExpression) : UnaryOperator(value) {
    override fun compute(value: Int): Int = abs(value)

    override fun operator(): String = "abs"

    override fun toString(): String = super.toString()
}

data class Negate(private val value: TripleExpression) : UnaryOperator(value) {
    override fun compute(value: Int): Int = -value

    override fun operator(): String = "-"

    override fun toString(): String = super.toString()
}

operator fun TripleExpression.plus(v: TripleExpression) = Add(this, v)
operator fun TripleExpression.minus(v: TripleExpression) = Subtract(this, v)
operator fun TripleExpression.times(v: TripleExpression) = Multiply(this, v)
operator fun TripleExpression.div(v: TripleExpression) = Divide(this, v)
operator fun TripleExpression.unaryMinus() = Negate(this)
operator fun TripleExpression.inc() = Add(this, Const.ONE)
operator fun TripleExpression.dec() = Subtract(this, Const.ONE)