package parsers

data class Location(val input: String, val position: Int) {
    fun <A> addOffset(result: Success<A>) = copy(position = position + result.consumed)

    fun toError(msg: String) =
        ParseError(listOf(this to msg))
}

sealed class Result<out A>
data class Success<out A>(val a: A, val consumed: Int) : Result<A>()
data class Failure(val get: ParseError) : Result<Nothing>()

data class ParseError(val stack: List<Pair<Location, String>>)
class ParseException(msg: String) : RuntimeException(msg)

typealias Parser<A> = (Location) -> Result<A>

fun <A> run(p: Parser<A>, input: String): A =
    when (val result = p(Location(input, 0))) {
        is Success -> result.a
        is Failure -> throw ParseException(
            "Failed to parse $input\n" +
                    result.get.stack.joinToString("\n") { "  at ${it.first.position}: ${it.second}" }
        )
    }

fun string(s: String): Parser<String> = { location ->
    if (location.input.startsWith(s, location.position)) {
        Success(s, s.length)
    } else {
        Failure(location.toError("Expected string value $s"))
    }
}

fun int(): Parser<Int> = { loc ->
    val digits = loc.input.substring(loc.position).takeWhile { it.isDigit() }
    if (digits.isNotEmpty()) {
        Success(digits.toInt(), digits.length)
    } else {
        Failure(loc.toError("Expected integer"))
    }
}


fun <A, S> Parser<A>.list(separator: Parser<S>): Parser<List<A>> = { startLocation ->
    val result = mutableListOf<A>()
    var location = startLocation

    do {
        val tryForMore = when (val aResult = this(location)) {
            is Success -> {
                result.add(aResult.a)
                location = location.addOffset(aResult)

                val sepResult = separator(location)
                if (sepResult is Success) {
                    location = location.addOffset(sepResult)
                    true
                } else {
                    false
                }
            }
            is Failure -> false
        }
    } while (tryForMore)

    Success(result.toList(), location.position - startLocation.position)
}

infix fun <A> Parser<A>.or(pb: Parser<A>): Parser<A> = { location ->
    when (val aResult = this(location)) {
        is Success -> aResult
        is Failure -> pb(location)
    }
}

fun <A, B> Parser<A>.map(f: (A) -> B): Parser<B> = { location ->
    when (val aResult = this(location)) {
        is Success -> Success(f(aResult.a), aResult.consumed)
        is Failure -> aResult
    }
}

infix fun <A, B> Parser<A>.seq(pb: Parser<B>): Parser<Pair<A, B>> = { startLocation ->
    when (val aResult = this(startLocation)) {
        is Success -> {
            when (val bResult = pb(startLocation.addOffset(aResult))) {
                is Success -> Success(Pair(aResult.a, bResult.a), aResult.consumed + bResult.consumed)
                is Failure -> bResult
            }
        }
        is Failure -> aResult
    }
}

fun <A> Parser<A>.surround(open: String, close: String): Parser<A> =
    (string(open) seq this seq string(close)).map { it.first.second }

fun <A> succeed(a: A): Parser<A> = { _ -> Success(a, 0) }

fun <A, B> Parser<A>.flatMap(f: (A) -> Parser<B>): Parser<B> = TODO()


fun main() {
    assert(run(string("exact"), "exact   ") == "exact")

    assert(run(int(), "6542342  ") == 6542342)

    assert(run(int().list(string(", ")), "6542342  ") == listOf(6542342))
    assert(run(int().list(string(", ")), "65, 42, 34,2  ") == listOf(65,42,34))

    assert(run(string("alpha") or string("beta"), "alpha ") == "alpha")
    assert(run(string("alpha") or string("beta"), "beta ") == "beta")

    assert(run(string("[") seq int() seq string("]"), "[42]") == Pair(Pair("[", 42), "]"))

    assert(run(int().list(string(",")).surround("[", "]"), "[42]") == listOf(42))
    assert(run(int().list(string(",")).surround("[", "]"), "[42,45,]") == listOf(42,45))
}