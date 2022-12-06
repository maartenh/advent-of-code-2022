package day05

import Day
import Lines
import java.util.Stack

class Day5 : Day() {
    override fun part1(input: Lines): Any {
        val stacks = initStacks(input.filter { it.contains('[') })

        input.filter { it.contains("move") }
            .forEach {command ->
                val move = parseMove(command)
                stacks.runMove(move)
            }

        return stacks.tops().joinToString(separator = "")
    }

    override fun part2(input: Lines): Any {
        val stacks = initStacks(input.filter { it.contains('[') })

        input.filter { it.contains("move") }
            .forEach {command ->
                val move = parseMove(command)
                stacks.runMoveGroup(move)
            }

        return stacks.tops().joinToString(separator = "")
    }

    private fun initStacks(lines: Lines) : Map<Int, Stack<Char>> {
        val stacks = mutableMapOf<Int, Stack<Char>>()

        lines.reversed().forEach { line ->
            for (i in 1 until  line.length step 4) {
                val stackNumber = 1 + (i - 1) / 4
                val container = line[i]
                if (container.isLetter()) {
                    stacks.addContainer(stackNumber, container)
                }
            }
        }

        return stacks.toMap()
    }

    private fun parseMove(line: String): Move {
        val pattern = Regex("""move (\d+) from (\d+) to (\d+)""")
        val (count, from, to) = pattern.find(line)!!.destructured

        return Move(from.toInt(), to.toInt(), count.toInt())
    }
}

private fun MutableMap<Int, Stack<Char>>.addContainer(stackNumber: Int, container: Char) {
    if (!containsKey(stackNumber)) {
        put(stackNumber, Stack())
    }

    this[stackNumber]!!.push(container)
}

private fun Map<Int, Stack<Char>>.runMove(move: Move) {
    for (i in 1..move.count) {
        this[move.to]!!.push(this[move.from]!!.pop())
    }
}

private fun Map<Int, Stack<Char>>.runMoveGroup(move: Move) {
    val holder = Stack<Char>()
    for (i in 1..move.count) {
        holder.push(this[move.from]!!.pop())
    }
    for (i in 1..move.count) {
        this[move.to]!!.push(holder.pop())
    }
}

private fun Map<Int, Stack<Char>>.tops(): List<Char> {
    return keys.sorted().map { get(it)!!.peek() }
}

data class Move(val from: Int, val to: Int, val count: Int)