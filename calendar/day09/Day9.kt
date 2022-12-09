package day09

import Day
import Lines
import day09.Move.*
import kotlin.math.abs

class Day9 : Day() {
    override fun part1(input: Lines): Any {
        val moves = readMoves(input)

        val board = Board(arrayOf(0 to 0, 0 to 0))

        val allTailPositions = moves.map { move ->
            board.execute(move)
            board.knots.last()
        }

        return allTailPositions.toSet().size
    }

    override fun part2(input: Lines): Any {
        val moves = readMoves(input)

        val rope = Array(10) { 0 to 0 }
        val board = Board(rope)

        val allTailPositions = moves.map { move ->
            board.execute(move)
            board.knots.last()
        }

        return allTailPositions.toSet().size
    }

    private fun readMoves(input: Lines): List<Move> {
        return input.flatMap { line ->
            val direction = line[0].toMove()
            val count = line.substring(2).toInt()
            (1..count).map { direction }
        }
    }

    private fun Char.toMove() =
        when (this) {
            'U' -> Up
            'D' -> Down
            'L' -> Left
            'R' -> Right
            else -> throw AssertionError("Invalid input direction $this")
        }
}

typealias Position = Pair<Int, Int>

class Board(val knots: Array<Position>) {
    fun execute(move: Move) {
        knots[0] = when (move) {
            Up -> knots[0].copy(second = knots[0].second + 1)
            Down -> knots[0].copy(second = knots[0].second - 1)
            Left -> knots[0].copy(first = knots[0].first - 1)
            Right -> knots[0].copy(first = knots[0].first + 1)
        }

        for (i in 1 until knots.size) {
            if (!knots[i].isAdjacentTo(knots[i - 1])) {
                knots[i] = knots[i].moveTowards(knots[i - 1])
            }
        }
    }
}

private fun Position.isAdjacentTo(target: Position) =
    abs(this.first - target.first) <= 1 &&
            abs(this.second - target.second) <= 1

private fun Position.moveTowards(target: Position): Position {
    val newFirst = when {
        first < target.first -> first + 1
        first > target.first -> first - 1
        else -> first
    }
    val newSecond = when {
        second < target.second -> second + 1
        second > target.second -> second - 1
        else -> second
    }

    return Position(newFirst, newSecond)
}

enum class Move {
    Up, Down, Left, Right
}