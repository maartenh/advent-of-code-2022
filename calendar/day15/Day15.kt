package day15

import Day
import Lines
import tools.*
import java.lang.Integer.max
import kotlin.math.abs

class Day15 : Day() {
    override fun part1(input: Lines): Any {
        val sensors = input.map { run(sensor(), it) }

        val mergedRanges = rowCoverage(sensors, 2_000_000)

        return mergedRanges.sumOf { it.last - it.first + 1 } -
                sensors.map { it.nearestBeacon }.filter { it.y == 2_000_000 }.distinct().count()
    }

    private fun rowCoverage(
        sensors: List<Sensor>,
        targetRow: Int
    ): List<IntRange> {
        val coverageRanges = sensors
            .map {
                val verticalDistance = abs(it.location.y - targetRow)
                val extend = it.beaconDistance - verticalDistance
                it.location.x - extend..it.location.x + extend
            }
            .filter { !it.isEmpty() }
            .sortedBy { it.first }

        val mergedRanges =
            coverageRanges.drop(1).fold(listOf(coverageRanges.first())) { acc: List<IntRange>, range: IntRange ->
                if (acc.last().last + 1 >= range.first) {
                    val mergedRange = acc.last().first..max(acc.last().last, range.last)
                    acc.dropLast(1).plusElement(mergedRange)
                } else {
                    acc.plusElement(range)
                }
            }
        return mergedRanges
    }

    override fun part2(input: Lines): Any {
        val sensors = input.map { run(sensor(), it) }

        val maxCoordinate = 4_000_000
        val range = 0..maxCoordinate

        val distressRowCoverage = range.asSequence()
            .map { row -> row to rowCoverage(sensors, row) }
            .first { it.second.size > 1 }

        val distressLocation = Point(distressRowCoverage.second[0].last + 1, distressRowCoverage.first)
        return distressLocation.x.toBigInteger() * 4000000.toBigInteger() + distressLocation.y.toBigInteger()
    }

    data class Point(val x: Int, val y: Int) {
        fun distanceTo(target: Point): Int =
            abs(x - target.x) + abs(y - target.y)
    }
    data class Sensor(val location: Point, val nearestBeacon: Point) {
        val beaconDistance = location.distanceTo(nearestBeacon)
        override fun toString(): String {
            return "Sensor(location=$location, nearestBeacon=$nearestBeacon, beaconDistance=$beaconDistance)"
        }
    }

    private fun point(): Parser<Point> =
        (string("x=") seq int() seq string(", y=") seq int()).map {
            Point(it.first.first.second, it.second)
        }
    private fun sensor(): Parser<Sensor> =
        (string("Sensor at ") seq point() seq string(": closest beacon is at ") seq point()).map {
            Sensor(it.first.first.second, it.second)
        }

}