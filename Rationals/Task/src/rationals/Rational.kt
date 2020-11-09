package rationals

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext


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

data class Rational(var n: BigInteger, var d: BigInteger): Comparable<Rational> {

    init {
        val gcd1 = n.gcd(d)

        n = n.divide(gcd1)
        d = d.divide(gcd1)

        if(d < 0.toBigInteger()) {
            d *= (-1).toBigInteger()
            n *= (-1).toBigInteger()
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
data class RationalRange(override val endInclusive: Rational,override val start: Rational) : ClosedRange<Rational>

fun String.toRational() : Rational {
    val rational = this.split("/")
    if(rational.size > 1) {
        return Rational(rational[0].toBigInteger(),rational[1].toBigInteger())
    } else {
        return Rational(rational[0].toBigInteger(),1.toBigInteger())
    }

}
infix fun Number.divBy(d: Number) : Rational {
    val gcd1 = this.toLong().toBigInteger().gcd(d.toLong().toBigInteger())

    var n : BigInteger = this.toLong().toBigInteger() / gcd1
    var d : BigInteger = d.toLong().toBigInteger() / gcd1

    if(d < 0.toBigInteger()) {
        d *= (-1).toBigInteger()
        n *= (-1).toBigInteger()
    }

    return Rational(n,d)
}
operator fun Rational.plus(r: Rational) : Rational {
    val n1 = this.n
    val n2 = r.n

    val d1 = this.d
    val d2 = r.d

    val common = d1 * d2
    val s1 = (common / d1) * n1
    val s2 = (common / d2) * n2

    val sum = s1 + s2

    if(sum.mod(common) == 0.toBigInteger()) {
        return Rational(sum / common,1.toBigInteger())
    } else {
        return Rational(sum,common)
    }
}
operator fun Rational.minus(r: Rational) : Rational {
    val n1 = this.n
    val n2 = r.n

    val d1 = this.d
    val d2 = r.d

    val common = d1 * d2
    val s1 = (common / d1) * n1
    val s2 = (common / d2) * n2

    val sum = s1 - s2

    return Rational(sum,common)
}
operator fun Rational.div(r: Rational) : Rational {

    return Rational(this.n * r.d,this.d * r.n)
}
operator fun Rational.times(r: Rational) : Rational {
    return Rational(this.n * r.n,this.d * r.d)
}
operator fun Rational.unaryMinus() : Rational {
    val n = n * -1.toBigInteger()
    var d = d * -1.toBigInteger()
    if(d < 0.toBigInteger()) {
        d *= -1.toBigInteger()
    }
    return Rational(n,d)
}

operator fun Rational.rangeTo(r: Rational) : RationalRange {
    return RationalRange(Rational(r.n,r.d),Rational(n,d))
}