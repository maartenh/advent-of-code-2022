package day10

import Day
import Lines

class Day10 : Day() {
    override fun part1(input: Lines): Any {
        val cpu = CPU()

        val statesOverTime =
            listOf(cpu.state) +
                    input.map { instruction ->
                        cpu.execute(instruction)
                        cpu.state
                    }

        val measurementCycles = 20..220 step 40

        return measurementCycles.sumOf { cycle ->
            statesOverTime.stateAt(cycle).x * cycle
        }
    }

    override fun part2(input: Lines): Any {
        val cpu = CPU()

        val statesOverTime =
            listOf(cpu.state) +
                    input.map { instruction ->
                        cpu.execute(instruction)
                        cpu.state
                    }

        val cycles = 1..240

        val output = cycles
            .map { cycle ->
                val x = statesOverTime.stateAt(cycle).x
                val position = (cycle - 1) % 40

                if (position in x - 1..x + 1) {
                    '#'
                } else {
                    '.'
                }
            }
            .chunked(40)
            .joinToString(separator = "\n") {
                it.joinToString(separator = "")
            }

        return "\n" + output
    }
}

private fun List<CPU.State>.stateAt(targetCycle: Int): CPU.State =
    last { it.time <= targetCycle }

class CPU(var state: State = State(1, 1)) {

    fun execute(instruction: String) {
        when {
            instruction == "noop" ->
                state = state.copy(
                    time = state.time + 1
                )

            instruction.startsWith("addx ") ->
                state = state.copy(
                    time = state.time + 2,
                    x = state.x + instruction.substring(5).toInt()
                )
        }
    }

    data class State(val time: Int, val x: Int)
}

