package day07

import Day
import Lines

class Day7 : Day() {
    override fun part1(input: Lines): Any {
        val fsTree = buildFS(input)

        return fsTree.allDirectories
            .map(Directory::size)
            .filter { it <= 100_000 }
            .sum()
    }

    override fun part2(input: Lines): Any {
        val diskSize = 70_000_000
        val requiredSpace = 30_000_000

        val fsTree = buildFS(input)
        val freeSpace = diskSize - fsTree.size

        val neededSpace = requiredSpace - freeSpace

        return fsTree.allDirectories
            .map(Directory::size)
            .filter { it >= neededSpace }
            .min()
    }

    private fun buildFS(input: Lines): Directory {
        val root = Directory("/")

        var currentDir = root
        val pathToCurrent = mutableListOf<Directory>()

        input.forEach {
            when {
                it == "$ ls" -> {}
                it == "$ cd /" -> {
                    currentDir = root
                    pathToCurrent.clear()
                }
                it == "$ cd .." -> {
                    currentDir = pathToCurrent.last()
                    pathToCurrent.removeLast()
                }
                it.startsWith("$ cd ") -> {
                    val dirName = it.substring(5)
                    pathToCurrent.add(currentDir)
                    currentDir = currentDir.directories.find { dir -> dir.name == dirName }!!
                }
                it.startsWith("dir ") -> {
                    currentDir.directories.add(Directory(it.substring(4)))
                }
                it[0].isDigit() -> {
                    val splitter = it.indexOf(' ')
                    val size = it.substring(0, splitter).toInt()
                    val name = it.substring(splitter + 1)
                    currentDir.files.add(File(name, size))
                }
                else -> {
                    throw AssertionError("Unhandled input line: $it")
                }
            }
        }

        return root
    }
}

data class File(val name: String, val size: Int)

data class Directory(
    val name: String,
    val directories: MutableList<Directory> = mutableListOf(),
    val files: MutableList<File> = mutableListOf()
) {

    private var dirSize: Int? = null
    val size: Int
        get() {
            if (dirSize == null) {
                dirSize = files.map(File::size).sum() + directories.map(Directory::size).sum()
            }
            return dirSize!!
        }

    val allDirectories: List<Directory>
        get() = listOf(this) + directories.flatMap(Directory::allDirectories)
}