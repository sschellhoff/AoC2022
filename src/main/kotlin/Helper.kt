fun String.blocks(): List<String> = this.split("\n\n")

inline fun <R> String.mapBlocks(transform: (String) -> R): List<R> = this.blocks().map { transform(it) }

fun List<String>.sum(): Long = this.sumOf { numberString -> numberString.toLong() }

fun String.sumLines(): Long = this.lines().sum()