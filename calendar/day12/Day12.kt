package day12

import Day
import Lines
import kotlin.math.max

class Day12 : Day() {
    override fun part1(input: Lines): Any {
        val map = parseMap(input)

        map.calculateStepsToGoal()

        println(map)

        return map.stepsToGoal(map.start)
    }

    override fun part2(input: Lines): Any {
        val map = parseMap(input)

        map.calculateStepsToGoal()

        val shortestPathStart = map.shortestPathStart()

        return map.stepsToGoal(shortestPathStart)
    }

    private fun parseMap(input: Lines): Map {
        val joinedLines = input.joinToString(separator = "")
        val startIndex = joinedLines.indexOf("S")
        val goalIndex = joinedLines.indexOf("E")
        val width = input[0].length

        val elevations = joinedLines.map {
            when (it) {
                'S' -> 0
                'E' -> 25
                else -> it.code - 'a'.code
            }
        }.toIntArray()

        return Map(
            elevations,
            width,
            input.size,
            startIndex.toPosition(width),
            goalIndex.toPosition(width)
        )
    }
}

private const val NotCalculated = -1

class Map(
    private val elevations: IntArray,
    private val width: Int,
    private val height: Int,
    val start: Position,
    val goal: Position
) {
    private val steps = IntArray(elevations.size) { NotCalculated }
    private val indexRange = elevations.indices
    private val xRange = 0 until width
    private val yRange = 0 until height

    fun calculateStepsToGoal() {
        steps[goal.toIndex()] = 0

        var step = 1
        var lastPositions = setOf(goal)

        while (steps[start.toIndex()] == NotCalculated) {
            val candidates = findNotCalculatedNeighbours(lastPositions)
            val nextSteps = candidates.filter { candidate ->
                lastPositions.any { it.isAccessibleFrom(candidate)}
            }
            nextSteps.forEach { steps[it.toIndex()] = step }

            lastPositions = nextSteps.toSet()
            step += 1
        }
    }

    fun stepsToGoal(position: Position) = steps[position.toIndex()]

    fun shortestPathStart(): Position {
        val potentialStartIndices = elevations.withIndex().filter { it.value == 0 }.map { it.index }
        val calculatedStartIndices = potentialStartIndices.filter { steps[it] >= 0 }

        return calculatedStartIndices.minBy { steps[it] }.toPosition(width)
    }

    private fun pathTo(target: Position): List<Position> {
        val path = mutableListOf(target)

        var position: Position? = start
        var stepValue = steps[start.toIndex()]

        while (stepValue > 0 && position != null) {
            stepValue -= 1
            val nextPosition = position.neighbours().firstOrNull {
                steps[it.toIndex()] == stepValue && it.isAccessibleFrom(position!!)
            }
            if (nextPosition != null) {
                path.add(position)
            }
            position = nextPosition
        }

        return path
    }

    private fun findNotCalculatedNeighbours(positions: Set<Position>) =
        positions.flatMap { it.neighbours() }
            .toSet()
            .filter { it.toIndex() in indexRange && it.steps() == NotCalculated }
            .toSet()

    private fun Position.isAccessibleFrom(from: Position): Boolean =
        this.elevation() <= from.elevation() + 1 && this in from.neighbours()

    private fun Position.neighbours() =
        listOf(
            Position(x, y - 1),
            Position(x + 1, y),
            Position(x, y + 1),
            Position(x - 1, y)
        ).filter {
            it.x in xRange && it.y in yRange
        }

    private inline fun Position.toIndex() =
        y * width + x

    private inline fun Position.elevation() =
        elevations[this.toIndex()]

    private inline fun Position.steps() =
        steps[this.toIndex()]

    override fun toString(): String {
        val aCode = 'a'.code
        val cellWidth = max(steps.max().toString().length + 1, 3)

        val startIndex = start.toIndex()
        val goalIndex = goal.toIndex()
        val pathIndices = pathTo(goal).map { it.toIndex() }.toSet()

        val elevationLines = elevations.withIndex().joinToString(separator = "") { (index, elevation) ->
            val elevationChar = (elevation + aCode).toChar()
            val specialChar = when (index) {
                startIndex -> 'S'
                goalIndex -> 'E'
                in pathIndices -> '*'
                else -> ' '
            }
            "$elevationChar$specialChar".padEnd(cellWidth, ' ')
        }.chunked(width * cellWidth)

        val stepsLines = steps.joinToString(separator = "") {
            "$it".padEnd(cellWidth, ' ')
        }.chunked(width * cellWidth)

        return elevationLines.zip(stepsLines)
            .flatMap { (e, s) -> listOf(e, s, "") }
            .joinToString(separator = "\n")
            .plus("\n\n${pathTo(goal)}")
    }


}

data class Position(val x: Int, val y: Int)

fun Int.toPosition(width: Int) = Position(this % width, this / width)

