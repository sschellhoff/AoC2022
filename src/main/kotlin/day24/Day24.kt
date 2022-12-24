package day24

fun main() {
    part1()
    part2()
}

fun part1() {
    val valley = Valley.from(realInput)
    println(stepsTo(valley.entry, valley.exit, valley))
}

fun stepsTo(from: Pair<Int, Int>, to: Pair<Int, Int>, valley: Valley): Int {
    var possibleLocations = setOf(from)
    var numberOfSteps = 0
    while (to !in possibleLocations) {
        numberOfSteps += 1
        valley.stepBlizzards()
        possibleLocations = calculateNewPossibleLocations(possibleLocations, valley)
    }
    return numberOfSteps
}

fun part2() {
    val valley = Valley.from(realInput)
    var numberOfSteps = 0
    numberOfSteps += stepsTo(valley.entry, valley.exit, valley)
    numberOfSteps += stepsTo(valley.exit, valley.entry, valley)
    numberOfSteps += stepsTo(valley.entry, valley.exit, valley)
    println(numberOfSteps)
}

fun calculateNewPossibleLocations(currentPossibleLocations: Set<Pair<Int, Int>>, valley: Valley): Set<Pair<Int, Int>> {
    val newPossibleLocations = mutableSetOf<Pair<Int, Int>>()
    currentPossibleLocations.forEach { location ->
        (location.neighbours() + location).filter { valley.possibleLocation(it) }
            .forEach { possibleNewPosition ->
                newPossibleLocations.add(possibleNewPosition)
            }
    }
    return newPossibleLocations
}

data class Valley(
    private var currentBlizzards: Set<Blizzard>,
    private var nextBlizzards: Set<Blizzard>,
    val entry: Pair<Int, Int>,
    val exit: Pair<Int, Int>,
    val width: Int,
    val height: Int
) {
    private val minX = entry.x
    private val minY = entry.y + 1
    private val maxX = exit.x
    private val maxY = exit.y - 1
    fun stepBlizzards() {
        val newNextBlizzards = calculateNextBlizzards(nextBlizzards)
        currentBlizzards = nextBlizzards
        nextBlizzards = newNextBlizzards
    }

    fun possibleLocation(to: Pair<Int, Int>): Boolean =
        isInBounds(to) && blizzardFreeMove(to)

    private fun blizzardFreeMove(to: Pair<Int, Int>): Boolean =
        !isPositionOfBlizzard(to, nextBlizzards)

    private fun isPositionOfBlizzard(position: Pair<Int, Int>, blizzards: Set<Blizzard>): Boolean =
        blizzards.any { blizzard -> blizzard.position == position }


    private fun isInBounds(point: Pair<Int, Int>): Boolean =
        (point.x in minX..maxX && point.y in minY..maxY) || point == entry || point == exit

    private fun calculateNextBlizzards(from: Set<Blizzard>): Set<Blizzard> {
        val newNextBlizzards = mutableSetOf<Blizzard>()

        fun wrapX(x: Int): Int {
            return ((maxX + x - 1).rem(maxX) + 1)
        }

        fun wrapY(y: Int): Int {
            return ((maxY + y - 1).rem(maxY) + 1)
        }
        from.forEach { oldBlizzard ->
            val newPosition = when (oldBlizzard.direction) {
                Direction.Left -> wrapX(oldBlizzard.position.x - 1) to oldBlizzard.position.y
                Direction.Right -> wrapX(oldBlizzard.position.x + 1) to oldBlizzard.position.y
                Direction.Up -> oldBlizzard.position.x to wrapY(oldBlizzard.position.y - 1)
                Direction.Down -> oldBlizzard.position.x to wrapY(oldBlizzard.position.y + 1)
            }
            newNextBlizzards.add(Blizzard(newPosition, oldBlizzard.direction))
        }

        return newNextBlizzards
    }

    fun print() {
        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                if (x to y == entry || x to y == exit) {
                    print('.')
                } else if (x < minX || x > maxX || y < minY || y > maxY) {
                    print('#')
                } else {
                    val blizzardsOnSpot = currentBlizzards.filter { it.position == x to y }
                    if (blizzardsOnSpot.isEmpty()) {
                        print('.')
                    } else if (blizzardsOnSpot.size == 1) {
                        print(
                            when (blizzardsOnSpot.first().direction) {
                                Direction.Left -> '<'
                                Direction.Right -> '>'
                                Direction.Up -> '^'
                                Direction.Down -> 'v'
                            }
                        )
                    } else {
                        print(blizzardsOnSpot.size)
                    }
                }
            }
            println()
        }
    }

    companion object {
        fun from(input: String): Valley {
            val lines = input.lines()
            val height = lines.size
            val width = lines.first().length
            val entry = 1 to 0
            val exit = width - 2 to height - 1
            val blizzards = mutableSetOf<Blizzard>()
            fun spawnBlizzard(x: Int, y: Int, direction: Direction) {
                blizzards.add(Blizzard(x to y, direction))
            }
            lines.forEachIndexed { y, line ->
                line.forEachIndexed { x, tile ->
                    when (tile) {
                        '>' -> spawnBlizzard(x, y, Direction.Right)
                        '<' -> spawnBlizzard(x, y, Direction.Left)
                        '^' -> spawnBlizzard(x, y, Direction.Up)
                        'v' -> spawnBlizzard(x, y, Direction.Down)
                    }
                }
            }
            return Valley(emptySet(), blizzards, entry, exit, width, height)
        }
    }
}

enum class Direction {
    Up,
    Down,
    Left,
    Right
}

data class Blizzard(val position: Pair<Int, Int>, val direction: Direction)

val Pair<Int, Int>.x: Int
    get() = first
val Pair<Int, Int>.y: Int
    get() = second

operator fun Pair<Int, Int>.plus(rhs: Pair<Int, Int>): Pair<Int, Int> = x + rhs.x to y + rhs.y
operator fun Pair<Int, Int>.minus(rhs: Pair<Int, Int>): Pair<Int, Int> = x - rhs.x to y - rhs.y
fun Pair<Int, Int>.neighbours(): Set<Pair<Int, Int>> =
    setOf((0 to -1) + this, (0 to 1) + this, (-1 to 0) + this, (1 to 0) + this)

private const val testInput = """#.######
#>>.<^<#
#.<..<<#
#>v.><>#
#<^v^^>#
######.#"""

private const val realInput =
    """#.########################################################################################################################
#<.^><>^>>>^<v^^v>v^vv<v>.<v^><>^<^.^>^><^^v^>v^v>>v><v<^vv>><^.v<^vv.v<^<^<vv<<vv>..v^><>..<<^.<v^^^vv>.<<^v>^<<v<^v^v>>#
#>^><.^<^v<>^<<vv^<^<>>^v<^vvv>vvv<><v^v<>><<^>.v.>^v^^><^<v^v^v^.>v^<vvv^^.vv^^v^<^^v>>>>.v^^.>v>^<<v>vv<<v>.>>><^>>>>v>#
#>v>^^^^>.^.v<>>>v>v^^v.>v^>><..v<^vv^v^><<>v<^<vv>>^.<.>>>v<>>^^v>>^<^v^<^v^v<v^>^.vvvv.<<.>^<^^>>v>v<>><^^>v^^<v^.vv<>>#
#><<<>v^^v>v>v>^<<<.<^.><<<>v>>>.^<^><>^v<^^>>v.>vv^<<<>^<>>><^^.^vv^<vv<<><.vv^^v^<^><.v^^>^<^<vvv<>><<><>vv^vv^<.^<v^v>#
#>>^^^>.>>><^^<<v^v^v.>^.v<v<vv>vv>^<^^^<>>vv>v^<v^vv<^v^>^>v^>>v<>>>^v>vvv>v>^<<>v^>v><<^<v<<.>v>>><^<<>^<>>v<<v>.<.v<<>#
#>..<>v>^..v<v.<v<vv<><<v^>>v<^.>v^>vv^v<^v>v<vv^<<^.^^^.<^v.<vvv<<<^^^^<v<v.>.^.^>.<.^^><<^.^<>>^v>^vv<<>.^^^..v<<v<<v^.#
#>^..<v^^v^<<v<vv<^^^<>v><^><>>^<v><>vvv..^>v^v<v<.<^^<<v><<><v>vv><vvv.<><^.vv<^.v><><<^vv^v<<v^v>>^v>>>>vvv>^<<v<>^>>^>#
#<<>^><^v>v<v>^^>v<>>>.^^v.>v<>^^v^v>v^v^<^^<v<<.v<^^>^v<.>.^<><<^v>^..<>>><.>^<.<<>>^^v>>v^^<^>.^v<v>>>vvv>>>>vv<vvv.v<>#
#><<v<^v^^^>v>.<.>>>^^^>>..>v^^>>^<^<><.^>>^>.v><^^^>>^vv>^v^v.>v<.^^>><<.<..><>^<v>v<^<^v<^^v<.>vv^^^^<v>>v^<v<<<^vv<.^.#
#<<^><><^v.<v>.^><v<v^^>^>>vvv><<^^<><>.vv^^><v.>vv>^^v>^v>>^<^>..<<^<v<^.v^^<<>><<.<^>^>^<vv><<>^>vvv>^>^^>>>.>vv^<<^<>.#
#<..>^>vv<<^^v>^<vv<v^v<^^<v^^^v^v<^>.^.^^>>>.^<^v^^v><v^^v^^vv^<<<.....>v^<<vv>>.>><v<v>>v..v<^><>vvv<vv<^v<<>>.^^v<v^<<#
#>v>><^v.<v<v>^^>^vv>vvvv<>v><>v.^<>><>^><.<^><.>.<<^^v^^.>v>v^><><vv^><.<>><<v^v.<^^>v>.<>vv.v<^v^v^<^<>>v<><<v.^<.<<^v<#
#<<>^vvv.^<v<.vv<^.v><v<<^vv<<vv<v^v>v<vv^^<>><^v^>^^^^>v.>>.^vv.<v<>^^^><^^>>^<..<.>>^^^<<^^^v^^<v<^.vv<^v>><<^.<<>>><.<#
#..v>.>>v<^vv><vv<v>^<.>>^^>v^><<^>v<^v^..^v>^>.^vvv<v><<.<.<>v>^^<<<v^^^vvvv^.>v>^^>^>>^><<v<>^>.v.^<v<>.^^>><.v^v<.vv^<#
#>^<vv<>v<v^<.>>>v<><vv>^^<v<^v><<v^v^v>^^<>.vv>v.^^<v^v<v><v<v^vv>.<>v<<<>>><<v<<.v>v^<..^v^^<v^v.>vv.^^<>>v^<.<^>v^<^.<#
#<>>>vv.>^>.v>v<v<v><>^>v^v<>^.>>v>^^>vv>><>v.^.>^>>>^<><v^>vv<.>vvv<>^v><v>>><vvv.<v^<>>.vv^v>>>>>v<^>.v<.<<.vv>><.^<<.<#
#<^v^>>>><><v<<.^<<^><.^<^^^<.^.v><>^vv<^<><.><^<v<v<v<<<^<v^>>v<<<.<v>.>^.<><.^^<>v>>^vv<^^<>v<v<v<^<^>v.^<v><<<>>^>v^<<#
#<<><.vv^v<v>v^^<.<^<<<<<<v.v^..><v^^^^^v^<>v^<<^>v<^v^.<.<>^<v^^v<<.><>v>v.v>vv>.><v>^v>.<>>v^<.>v<vvvvvvv<><^^v>>^^<>>>#
#><>v>>v.^<<<<v>>v.<<^^..^<^.v.v^<>v<^^>.^^<.^>^<.vvv.^vv^.>.>^<>v^v<<><^.v>v^^v<v>^v>>.^vv<v^^>><<<^<>v<<v<<^>><v.<v<>^>#
#>><>vvv^>v<>v<v>^.<<>^^<^>>v^.<^>>.<<v<<^<^><^<>>^.vvv>v><<>><>vv.^<v>v><^>.^^^.v>>^^^vv^<^><^>>>.<^v^<vv>>^>>^>v>><.<v>#
#>><<^v<>>>>^<><.<>vvv.<^>v<>.<.<<^>v>v<v^^vv^^>>v<^>^v^<^.>v><vv^^^.^v^>^><<<v^.>^<^>v<^v<>><<vv<^v><.<>^^v.^^.^>^.v<<>>#
#.<<^><v>>>v^>.^v><v^>>vv>^<<v<v.>^^.<v<.<<<v<>><^<v^<v^>^vvv>^<<>>v<<.v^><<vv^<v>v<<^v>vv<<.v^<<^v^<<v<<<>><^^vvvv<v>vv<#
#><>>>^>^^><v^^^<>><>^<.<^^<>^^.>.<>.>v>vv>..><>>^<v>>vv>.v>.v^^<<^v<><^^^vv.^<>.^>v>v>v<<^<v><v.<^v<.^.<><<><>.v^^v^v^.>#
#<<.^.v^><<^vv^v^.^>.<v<v.v^<v^v><.v.^vv^.>.<>>><<>^>v^>.<^>>>v<>^<>^^>..<.^>v^>^v.<<>^v^<>>>v>>v^>^.^<.vv><>^v^v><^^<^<>#
#>v^><v>v<v^^>><vv^^v..>><><.<^vvv>^<>^<>v^..^v.>>v>vvv><v>>v>vv>^^v<<^<^^^>>v<^><v.v<^v^<^.>>v>^v<>^>vv^<.<.<^v<<<><^.^>#
########################################################################################################################.#"""