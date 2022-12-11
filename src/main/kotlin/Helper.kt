fun String.blocks(): List<String> = this.split("\n\n")

inline fun <R> String.mapBlocks(transform: (String) -> R): List<R> = this.blocks().map { transform(it) }

fun List<String>.sum(): Long = this.sumOf { numberString -> numberString.toLong() }

fun String.sumLines(): Long = this.lines().sum()

fun <T> List<T>.penultimate(): T {
    if (size < 2)
        throw NoSuchElementException("List is empty.")
    return this[lastIndex - 1]
}