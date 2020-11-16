package rationals

import java.lang.IllegalStateException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO
import java.math.MathContext


/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}

data class Rational
    private constructor(val n: BigInteger, val d: BigInteger): Comparable<Rational> {

    companion object {
        fun create(n: BigInteger, d: BigInteger) : Rational {
            return normalize(n,d)
        }
        private fun normalize(n: BigInteger, d: BigInteger) : Rational {
            require(d != ZERO) { "Denominator must not be zero" }
            val gcd1 = n.gcd(d)
            val sign = d.signum().toBigInteger()
            return Rational(n.divide(gcd1) * sign ,d.divide(gcd1) * sign)
        }
    }
    override fun compareTo(r: Rational): Int {
        val div1 : BigDecimal = (n.toBigDecimal().divide(d.toBigDecimal(), MathContext(40)))
        val div2 : BigDecimal = (r.n.toBigDecimal().divide(r.d.toBigDecimal(), MathContext(40)))

        return div1.compareTo(div2)
    }
    override fun toString(): String {
        if(d == 1.toBigInteger()) {
            return "$n"
        }
        return "${n}/${d}"
    }
}
fun String.toRational() : Rational {
    fun String.toBigIntegerOrFail()  = toBigIntegerOrNull() ?: throw IllegalStateException(
            "Expecting rational in the form of 'n/d' or 'n', was: '${this@toRational}'")

    if(!contains("/")) {
        return Rational.create(toBigIntegerOrFail(), ONE)
    }
    val part = this.split("/")
    return Rational.create(part[0].toBigIntegerOrFail(), part[1].toBigIntegerOrFail())
}

infix fun Number.divBy(d: Number) : Rational {

    var n : BigInteger = this.toLong().toBigInteger()
    var d : BigInteger = d.toLong().toBigInteger()

    return Rational.create(n,d)
}
operator fun Rational.plus(r: Rational) : Rational {
    return Rational.create(n * r.d + r.n * d, d * r.d)
}
operator fun Rational.minus(r: Rational) : Rational {
    return Rational.create(n * r.d - r.n * d, d * r.d)
}
operator fun Rational.div(r: Rational) : Rational {
    return Rational.create(this.n * r.d,this.d * r.n)
}
operator fun Rational.times(r: Rational) : Rational {
    return Rational.create(this.n * r.n,this.d * r.d)
}
operator fun Rational.unaryMinus() : Rational {
    return Rational.create(-n,d)
}