package day08

import Day
import Lines
import kotlin.math.min

class Day8 : Day() {
    override fun part1(input: Lines): Any {
        val treeHeights = readInput(input)

        val trees = (input.indices).flatMap { x ->
            (input.indices).map { y ->
                Pair(x, y)
            }
        }

        return trees.count { location -> isVisible(location, treeHeights)}
    }

    override fun part2(input: Lines): Any {
        val treeHeights = readInput(input)

        val trees = (input.indices).flatMap { x ->
            (input.indices).map { y ->
                Pair(x, y)
            }
        }

        return trees.maxOf { location -> scenicScore(location, treeHeights) }
    }

    private fun readInput(input: Lines): Array<IntArray> {
        return input.map { line ->
            line.map { it.toString().toInt() }
                .toIntArray()
        }.toTypedArray()
    }

    private fun isVisible(location: Pair<Int, Int>, heights: Array<IntArray>): Boolean {
        val (x, y) = location
        val height = heights[x][y]

        val invisibleFromNorth =
            (0 until y).map { yLoc -> heights[x][yLoc] }
                .any { it >= height }
        val invisibleFromSouth =
            (y+1 until heights.size).map { yLoc -> heights[x][yLoc] }
                .any { it >= height }
        val invisibleFromWest =
            (0 until x).map { xLoc -> heights[xLoc][y] }
                .any { it >= height }
        val invisibleFromEast =
            (x+1 until heights.size).map { xLoc -> heights[xLoc][y] }
                .any { it >= height }

        val invisible = invisibleFromNorth && invisibleFromSouth && invisibleFromWest && invisibleFromEast

        return !invisible
    }

    private fun scenicScore(location: Pair<Int, Int>, heights: Array<IntArray>): Int {
        val (x, y) = location
        val maxCoordinate = heights.size - 1
        val height = heights[x][y]

        val scoreFromNorth = min(
            y,
            (y - 1 downTo 0).takeWhile { yLoc -> heights[x][yLoc] < height }
                .count() + 1
        )
        val scoreFromSouth = min(
            maxCoordinate - y,
            (y + 1..maxCoordinate).takeWhile { yLoc -> heights[x][yLoc] < height }
                .count() + 1
        )
        val scoreFromWest = min(
            x,
            (x - 1 downTo 0).takeWhile { xLoc -> heights[xLoc][y] < height }
                .count() + 1
        )
        val scoreFromEast = min(
            maxCoordinate - x,
            (x + 1..maxCoordinate).takeWhile { xLoc -> heights[xLoc][y] < height }
                .count() + 1
        )

        return scoreFromNorth * scoreFromSouth * scoreFromWest * scoreFromEast
    }
}