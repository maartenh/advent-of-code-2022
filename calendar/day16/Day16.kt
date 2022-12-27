package day16

import Day
import Lines
import tools.*
import kotlin.math.min

class Day16 : Day() {
    override fun part1(input: Lines): Any {
        val tunnelMap = parseMap(input)
        val openableValves = tunnelMap.values.filter { it.flowRate > 0 }

        val reducedTunnelMap = reduceMap(map = tunnelMap, toNodes = openableValves, startNode = tunnelMap["AA"]!!)

        val bestVentActions = findMaxVent(
            listOf(Path("AA", 0)),
            tunnelMap = reducedTunnelMap,
            openableValves = openableValves.map { it.name }.toSet(),
            openedValves = setOf(),
            minutesLeft = 30,
            bestFound = 0,
            maxFlow = totalFlow(openableValves)
        )

        return if (bestVentActions != null) {
            println(bestVentActions.second.reversed().joinToString("\n"))
            bestVentActions.first
        } else {
            -1
        }
    }

    override fun part2(input: Lines): Any {
        val tunnelMap = parseMap(input)
        val openableValves = tunnelMap.values.filter { it.flowRate > 0 }

        val reducedTunnelMap = reduceMap(map = tunnelMap, toNodes = openableValves, startNode = tunnelMap["AA"]!!)

        val bestVentActions = findMaxVent(
            listOf(Path("AA", 0), Path("AA", 0)),
            tunnelMap = reducedTunnelMap,
            openableValves = openableValves.map { it.name }.toSet(),
            openedValves = setOf(),
            minutesLeft = 26,
            bestFound = 0,
            maxFlow = totalFlow(openableValves)
        )

        return if (bestVentActions != null) {
            println(bestVentActions.second.reversed().joinToString("\n"))
            bestVentActions.first
        } else {
            -1
        }
    }

    private fun reduceMap(map: Map<String, Valve>, toNodes: List<Valve>, startNode: Valve): Map<String, Valve> {
        val targetNodes = toNodes.toSet()

        val updatedNodes = (toNodes + startNode).map { fromNode ->
            val visitedNodes = mutableSetOf(fromNode)
            visitedNodes.remove(startNode)
            var currentNodes = setOf(fromNode)
            val newPaths = mutableSetOf<Path>()
            var steps = 1

            while (!visitedNodes.containsAll(toNodes)) {
                val nextNodes = currentNodes
                    .flatMap { node ->
                        node.pathsTo.map { path ->
                            map[path.destination]!!
                        }
                    }
                    .filter { it !in visitedNodes && it !in currentNodes }
                    .toSet()

                nextNodes
                    .filter { it in targetNodes }
                    .forEach { target ->
                        newPaths.add(Path(target.name, steps))
                    }

                visitedNodes.addAll(nextNodes)
                currentNodes = nextNodes
                steps += 1
            }

            fromNode.copy(pathsTo = newPaths.toList())
        }

        return updatedNodes.associateBy { it.name }
    }

    private fun findMaxVent(
        currentMoves: List<Path>,
        tunnelMap: Map<String, Valve>,
        openableValves: Set<String>,
        openedValves: Set<String>,
        minutesLeft: Int,
        bestFound: Int,
        maxFlow: Int
    ): Pair<Int, List<String>>? {
        if (minutesLeft <= 0) {
            return null
        }

        if (maxFlow * minutesLeft < bestFound) {
            return null
        }

        var bestResult: Pair<Int, List<String>>? = null

        val currentMove = currentMoves.first { it.length == 0 }
        val otherMoves = currentMoves - currentMove
        val currentValve = tunnelMap[currentMove.destination]!!

        val newOpenedValuves = openedValves + currentValve.name
        val newOpenableValves = openableValves - currentValve.name
        val currentFlow = totalFlow(newOpenedValuves.map { tunnelMap[it]!! })

        if (newOpenableValves.isEmpty()) {
            return (minutesLeft * currentFlow) to listOf("Wait $minutesLeft minutes with flow = $currentFlow")
        }

        currentValve.pathsTo
            .filter { it.destination in newOpenableValves }
            .forEach { tunnelPath ->
                val nextEventTime = min(tunnelPath.length + 1, otherMoves.minOfOrNull { it.length } ?: Int.MAX_VALUE)

                val flowDuringMove = currentFlow * nextEventTime
                val newMoves = (otherMoves + tunnelPath.copy(length = tunnelPath.length + 1)).map { it.copy(length = it.length - nextEventTime) }

                if (nextEventTime < minutesLeft) {
                    val moveResult = findMaxVent(
                        newMoves,
                        tunnelMap,
                        newOpenableValves,
                        newOpenedValuves,
                        minutesLeft - nextEventTime,
                        (bestResult?.first ?: bestFound) - flowDuringMove,
                        maxFlow
                    )
                    if (moveResult != null && moveResult.first + flowDuringMove > (bestResult?.first ?: bestFound)) {
                        bestResult =
                            (moveResult.first + flowDuringMove) to (moveResult.second.plusElement("Opened ${currentValve.name}, moving to ${tunnelPath.destination}, duration = ${tunnelPath.length + 1} flow = $currentFlow"))
                    }
                } else {
                    val timeoutFlow = currentFlow * minutesLeft
                    if (timeoutFlow > (bestResult?.first ?: bestFound)) {
                        bestResult = currentFlow * minutesLeft to listOf("Out of time moving to valve ${tunnelPath.destination}, duration = $minutesLeft, flow = $currentFlow")
                    }
                }
            }

        return bestResult
    }

    private fun totalFlow(valves: List<Valve>): Int =
        valves.sumOf { it.flowRate }

    private fun parseMap(input: Lines): Map<String, Valve> {
        return input.map { runParser(valve(), it) }.associateBy { valve -> valve.name }
    }

    data class Valve(val name: String, val flowRate: Int, val pathsTo: List<Path>)
    data class Path(val destination: String, val length: Int)

    private fun valve(): Parser<Valve> {
        val name = (string("Valve ") seq string(2)).map { it.second }
        val flow = (string(" has flow rate=") seq int()).map { it.second }
        val tunnel = (string("; tunnel leads to valve ") seq string(2)).map { listOf(Path(it.second, 1)) }
        val tunnels = (string("; tunnels lead to valves ") seq (string(2).map {
            Path(
                it,
                1
            )
        }).list(string(", "))).map { it.second }

        return (name seq flow seq (tunnel or { tunnels })).map { Valve(it.first.first, it.first.second, it.second) }
    }
}