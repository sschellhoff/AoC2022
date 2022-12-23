package day23

fun main() {
    part1()
    part2()
}

fun part1() {
    var currentElvPositions = getInitialElvPositions(realInput)
    val directionsToTest = getDirectionsToTest()
    repeat(10) {
        currentElvPositions = round(currentElvPositions, directionsToTest).first
    }
    currentElvPositions.print()
    println(currentElvPositions.surfaceArea() - currentElvPositions.size)
}

fun part2() {
    var currentElvPositions = getInitialElvPositions(realInput)
    val directionsToTest = getDirectionsToTest()
    var round = 0L
    while (true) {
        round += 1L
        val (newElvPositions, numberOfElvesWhoDontMove) = round(currentElvPositions, directionsToTest)
        currentElvPositions = newElvPositions
        if (numberOfElvesWhoDontMove.toInt() == currentElvPositions.size) {
            println(round)
            return
        }
    }
}

private fun getInitialElvPositions(input: String): Set<Vector> {
    val elves = mutableSetOf<Vector>()
    input.lines().forEachIndexed { y, line ->
        line.forEachIndexed { x, tile ->
            if (tile == '#') {
                elves.add(x.toLong() to y.toLong())
            }
        }
    }
    return elves
}

fun round(elves: Set<Vector>, directionsToTest: ArrayDeque<Set<Vector>>): Pair<Set<Vector>, Long> {
    val (proposedPositionFrom, numberOfElvesWhoDontMove) = firstHalf(elves, directionsToTest)
    val newElvPositions = secondHalf(proposedPositionFrom)
    directionsToTest.addLast(directionsToTest.removeFirst())
    return newElvPositions to numberOfElvesWhoDontMove
}

fun firstHalf(elves: Set<Vector>, directionsToTest: ArrayDeque<Set<Vector>>): Pair<Map<Vector, List<Vector>>, Long> {
    val proposedPositionFrom = mutableMapOf<Vector, MutableList<Vector>>()
    var numberOfElvesWhoDontMove = 0L
    elves.forEach { originalPosition ->
        val neighbourDirections = directionsToTest.flatten()
        val directNeighbourPositions = neighbourDirections.map { it + originalPosition }
        val validDirections = if (directNeighbourPositions.intersect(elves).isEmpty()) {
            numberOfElvesWhoDontMove += 1
            null
        } else {
            directionsToTest.firstOrNull { deltas ->
                val newPositions = deltas.map { delta -> originalPosition + delta }
                newPositions.intersect(elves).isEmpty()
            }?.filter { direction -> direction.x == 0L || direction.y == 0L }
        } ?: listOf(0L to 0L)

        check(validDirections.size == 1)
        val validDirection = validDirections.first()
        proposedPositionFrom.append(originalPosition + validDirection, originalPosition)
    }
    return proposedPositionFrom to numberOfElvesWhoDontMove
}

fun secondHalf(proposedPositionFrom: Map<Vector, List<Vector>>): Set<Vector> {
    val newElvPositions = mutableSetOf<Vector>()
    proposedPositionFrom.forEach { (newPosition, oldPositions) ->
        if (oldPositions.size == 1) {
            newElvPositions.add(newPosition)
        } else if (oldPositions.size > 1) {
            oldPositions.forEach { oldPosition ->
                newElvPositions.add(oldPosition)
            }
        } else {
            throw IllegalStateException("there should be no 0 in here!")
        }
    }
    return newElvPositions
}

fun MutableMap<Vector, MutableList<Vector>>.append(key: Vector, value: Vector) {
    if (!containsKey(key)) {
        this[key] = mutableListOf(value)
    } else {
        this[key]!!.add(value)
    }
}

fun getDirectionsToTest(): ArrayDeque<Set<Vector>> {
    val northDelta = 0L to -1L
    val eastDelta = 1L to 0L
    val southDelta = 0L to 1L
    val westDelta = -1L to 0L
    val northEastDelta = northDelta + eastDelta
    val northWestDelta = northDelta + westDelta
    val southEastDelta = southDelta + eastDelta
    val southWestDelta = southDelta + westDelta

    val northDirectionDeltas = setOf(northDelta, northWestDelta, northEastDelta)
    val southDirectionDeltas = setOf(southDelta, southWestDelta, southEastDelta)
    val eastDirectionDeltas = setOf(eastDelta, northEastDelta, southEastDelta)
    val westDirectionDeltas = setOf(westDelta, northWestDelta, southWestDelta)

    val directionsToTest = ArrayDeque<Set<Vector>>()
    directionsToTest.addLast(northDirectionDeltas)
    directionsToTest.addLast(southDirectionDeltas)
    directionsToTest.addLast(westDirectionDeltas)
    directionsToTest.addLast(eastDirectionDeltas)

    return directionsToTest
}

fun Set<Vector>.print() {
    val minX = minBy { it.x }.x
    val minY = minBy { it.y }.y
    val maxX = maxBy { it.x }.x
    val maxY = maxBy { it.y }.y

    println()
    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->
            print(if (contains(x to y)) '#' else '.')
        }
        println()
    }
}

fun Set<Vector>.surfaceArea(): Long {
    val minX = minBy { it.x }.x
    val maxX = maxBy { it.x }.x
    val sideLengthX = maxX - minX + 1
    val minY = minBy { it.y }.y
    val maxY = maxBy { it.y }.y
    val sideLengthY = maxY - minY + 1

    return sideLengthX * sideLengthY
}

typealias Vector = Pair<Long, Long>

val Vector.x: Long
    get() = first
val Vector.y: Long
    get() = second

operator fun Vector.plus(rhs: Vector): Vector {
    return x + rhs.x to y + rhs.y
}

private const val testInput = """.....
..##.
..#..
.....
..##.
....."""

private const val largeTestInput = """..............
..............
.......#......
.....###.#....
...#...#.#....
....#...##....
...#.###......
...##.#.##....
....#..#......
..............
..............
.............."""

private const val realInput = """.#...#.##...#..##.#.#####....#.##.#.#.##.#...###....###...#.#..##.##..
.###.##.####...###.......#...###.##..##.##....#.##.#####....#.#.#.##.#
###..##....#.#########.#.#.###...#.###...####...###...#..##...##..#.#.
.#.#########..##.##.##...##..#..#.#....#..######..###....##..##.#####.
#.#####.###.#..##.##.#.##..#..#...##...#...#####..#...##.#.#....###...
.#..#..##.....##.###..#............#####..#.#...###..#..#..####..##..#
#.......##..#.#.###..#..#####.##....###.###.#.#.#.#.####......#.#....#
####....###.#....#...#.#..#...##.#.#####...##..#..######...#..#......#
.###.#.....##.#.....#.#...#####..#.##.#..#....#.......###...#.######.#
.###..#####..###..#.##......###...#.#..###.##.##.#.###..#.###.##....#.
##...#..#...#.##.#..#####..##.#..##.#.#.#.#.#..#.#.#.##.####.....##.#.
.#...#.....#..##..###.##.#.##.#.#..#.##.#...#..#######..#..###.#######
##.#.#.#.....##..#.......######......###......#...#.##..#..##...#.#.#.
...#.###..##.####..##.###...#####....#.###.#.##.#...####.#..#..#.##.##
#..###.###.####.#.....##.###...##.#.....#.#.##.#.##.#..#.#.#...#.#####
..#.#.#.##.###.##..##.#.....##..######.##...###.###.#.###........#.#..
..###.##...##...##...##.#...#..##..####.##..#.#.#.#.##.#.#..##.##.##.#
#.#..###.#.#..####..#.###.##....#..#......#.#...###..###.#..##...##.#.
##.###..#.#..#...#.#....##.###.#..#..#.###.##.##.#.#.##...##...###..#.
..#.#.##.##.###.#.#...#.###...##.#.#.#.##..#.##...#.....#.#..##.#####.
..#.....##.##.##.#.#.##.#..#.###.###..#.......###.##.#..#..#..#...#.##
#.##.##.####.##....#..#.#.#....####.#....##.##...#.###.##.......####..
#.#.#.....#..#..#...#.#..###.......#.#....#.....#...#..#####...#.####.
#...##.##.#####..#.#......###.##..#....###.#..#.#..##.....########...#
..#.#.##....##.##..#.....#.....#.##....#..##....##.#.....##.....#...#.
##.##.#.###....#.##...##.##..##..#.#...#..#.###.#..##..#######..#..##.
#.###..#.#..#.....##.##.####..##.#.#.###...#####.##..#.#.#.#..#...##.#
....##...######...#..#.###...###.#.#.#....#.##.#.##.#.#.###.......#...
#..###...#####.#.##...#.#.#..#..#####.##..#######....#.##..#..###..#..
#.#..#.#####....#.#..#..#....##.#.#...#.#..#.#..####.....#..#.#.#..#..
...####.#.#......#.##..#.#.##..##..####.##..##....#..#.....#.#.#..#...
...###...#.#..###..#.#.#.#.#.#...##.#######...#####.#.#....##..#.#...#
..#.#.###.####.##..#.###..#..#.#.###.####.##.#..##..##.#.##..####.#.##
#.....##.#####.######.###..#...#####.##..#....#..#..##..##...##..##..#
.....##.##.##.##...###...##...#.#...#...#....#..#......#.##...###.###.
...#####.######.###..####.#....##..##.##..#.##...#.##...#.#.#..##.##.#
##..#...##....##.###...##.##...#.####...#.##.#..#...##..##...##.....#.
#####..######...#...##..#.#..####..#.#.#..#..#.....#...####...#..###.#
.....##....####.##.###..##.#..#######.#.###..#.#.####.###....#.###..#.
.#.##.###.#.##..###..####.#.#...###.....#..##.#...#.#..#.####...##.#.#
....#.....#...#.#.#.###..#...##.....###.#.##..##.#.#...#.#.###..##.###
##...#####.#####.##.#......###.##..#..###...####.###...#.#....#..#.###
#######...#.....#.....#.#.##.######..##.####..###.#..#.##..##.#.....#.
##.##..#.##...#.#...#.#...#.###.#...#..#.#...#.#.....###.##.#.....##.#
....##.#....#..###.#.#..#.#.##..#....###....#.#.###.......#..#.###.#.#
...#.#..##..#.##.#...#.#.###...##.#.#....####..##.#.##...##.#...####..
..####.####..##....#..######.#..###.###...#...#.#....###....###.#....#
.#.#..#..###..#####.......#.####.......#####.#.##.##.#.#.#.#.###...#.#
##.#..#.##..###...###...##...#..#....####.#.....#.#.#.####........#.#.
##.........##....#.......#....###..#.######..####.#.#..#.###.#.#..#.##
...#.##.#..#..####.#.###.####.#...###.#.#.#.#.###.##.#.#...#####..##.#
##.##.##.#.#...#.#......#....#...#####..###....##...####....####...###
..###.##.#.#.###.........#######...#..#######...###.##.#.####.##.##...
#...##..###......###.#.###.##..#.#..#..###..#...###.####.....#...##...
#.##.#.###...#####..###.#######..#.####..#.##.#.#.....#.###.#.#..#.#.#
...#####....#.#.##..#.#...####.#..###.....##.#.#....#.###.......#.#.##
..#....#.....#..####.##.###..###.#..##.##...##..###.#...###.#....##...
..##....#...##.###.####..##.##.#.##..#####..###.###.#..#.##.##.#....##
#.#.....##....##.#...#####..###.#...#.#..##...###.....#....#...#.###..
#####.##.###.#.#.#..#..#..##.##.#####.##.#.#.#####..##..####.##..##...
#..#.#..###.##.###.##.#.#.##.#..#.####.#.##.###..#.......#.....#...##.
.##.#..#.#####.#.#.######..#..#.##.....#.#.#.#..#.#.##.##...##.#..#..#
.##..###.#####.##.#...#.##..#..####..###...###.##.##...##..#....#..##.
#..#..###......#..##.##.#.##..##..##.##.####.#.##.##.###.#.....#....#.
#.##...##..#.#...####...#.#.#..##..######........#..#.######.........#
.#.#.##..##.###..#.#....#.....#.###...#.....#..###.#.##........#.###..
...#..#.#..#...##..#####..#.#.#.#.#..#.....##.####.#...#......###.....
#...####.#..#..#.....##...##..####..#.##..#.##..#...#..#..##....#...##
###...#.###..#####..#..##...#.####..#...####...#....##...###....#.####
...#..##.##.###.#..#.#.###.#.#...#.#.#.##.#.#.#..#....######.#.##....."""