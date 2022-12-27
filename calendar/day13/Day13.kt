package day13

import Day
import Lines
import tools.*
import kotlin.math.min

class Day13 : Day() {
    override fun part1(input: Lines): Any {
        val packetPairs = parsePacketPairs(input)

        return packetPairs
            .map { it.first < it.second }
            .zip(1..packetPairs.size)
            .filter { it.first }
            .sumOf { it.second }
    }

    override fun part2(input: Lines): Any {
        val dividerPackets = listOf(2, 6)
            .map { PacketList(listOf(PacketList(listOf(IntPacket(it))))) }
        val sortedPackets = (parsePackets(input) + dividerPackets).sorted()

        return (sortedPackets.indexOf(dividerPackets[0]) + 1) *
                (sortedPackets.indexOf(dividerPackets[1]) + 1)
    }


    private fun parsePacketPairs(input: Lines) = input
        .splitAt { it.isEmpty() }
        .map {
            Pair(
                runParser(packet(), it.first()),
                runParser(packet(), it.last())
            )
        }

    private fun parsePackets(input: Lines) = input
        .filter { it.isNotEmpty() }
        .map {
            runParser(packet(), it)
        }

    sealed interface Packet : Comparable<Packet>
    data class PacketList(val packets: List<Packet>): Packet {
        override fun compareTo(other: Packet): Int =
            when (other) {
                is IntPacket -> this.compareTo(PacketList(listOf(other)))
                is PacketList -> this.packets.compareTo(other.packets)
            }
    }
    data class IntPacket(val value: Int): Packet {
        override fun compareTo(other: Packet): Int =
            when (other) {
                is IntPacket -> this.value.compareTo(other.value)
                is PacketList -> PacketList(listOf(this)).compareTo(other)
            }
    }

    private fun packet(): Parser<Packet> = intPacket() or { packetList() }
    private fun intPacket(): Parser<Packet> = int().map { i -> IntPacket(i) }
    private fun packetList(): Parser<Packet> = packet().list(string(",")).surround("[", "]").map { pl -> PacketList(pl) }
}

fun <T: Comparable<T>> List<T>.compareTo(other: List<T>): Int {
    val maxIndex = min(this.size, other.size) - 1

    for (index in 0..maxIndex) {
        val elementResult = this[index].compareTo(other[index])
        if (elementResult != 0) {
            return elementResult
        }
    }

    return this.size.compareTo(other.size)
}