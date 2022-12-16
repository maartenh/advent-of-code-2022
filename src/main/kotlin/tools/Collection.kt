package tools

fun <E> List<E>.splitAt(predicate: (E) -> Boolean): List<List<E>> {
    val partitions = mutableListOf<List<E>>()

    var source = this
    while (source.isNotEmpty()) {
        val part = source.takeWhile { !predicate(it) }
        source = source.subList(Integer.min(part.size + 1, source.size), source.size)

        if (part.isNotEmpty()) {
            partitions.add(part)
        }
    }

    return partitions.toList()
}
