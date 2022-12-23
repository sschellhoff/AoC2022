package day22

import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

fun main() {
//    part1()
    part2()
}

fun part1() {
    val (grid, commands) = parse(realInput)
    var position = grid.findStart()
    var direction = FaceDirection.Right

    commands.forEach { command ->
        when (command) {
            is Command.Turn -> direction += command.direction
            is Command.Move -> position = movePart1(grid, position, direction, command.amount)
        }
    }
    val normalizedCoordinates = position + (1 to 1)
    println(normalizedCoordinates.second * 1000 + normalizedCoordinates.first * 4 + direction.ordinal)
}

fun part2() {
    val (grid, commands) = parse(realInput)
    var position = grid.findStart()
    var direction = FaceDirection.Right

    commands.forEach { command ->
        when (command) {
            is Command.Turn -> direction += command.direction
            is Command.Move -> {
                val (newPosition, newDirection) = movePart2(grid, position, direction, command.amount)
                position = newPosition
                direction = newDirection
            }
        }
    }
    val normalizedCoordinates = position + (1 to 1)
    println(normalizedCoordinates.second * 1000 + normalizedCoordinates.first * 4 + direction.ordinal)
}

fun movePart1(grid: Grid, position: Pair<Int, Int>, direction: FaceDirection, amount: Int): Pair<Int, Int> {
    return when (direction) {
        FaceDirection.Left -> grid.moveLeft(position, amount)
        FaceDirection.Right -> grid.moveRight(position, amount)
        FaceDirection.Up -> grid.moveUp(position, amount)
        FaceDirection.Down -> grid.moveDown(position, amount)
    }
}

fun movePart2(
    grid: Grid,
    position: Pair<Int, Int>,
    direction: FaceDirection,
    amount: Int
): Pair<Pair<Int, Int>, FaceDirection> =
    grid.moveOnCube(position, direction, amount)

enum class Tile {
    Open,
    Wall,
    Void
}

enum class FaceDirection {
    Right,
    Down,
    Left,
    Up
}

enum class TurnDirection {
    Right,
    Left
}

sealed interface Command {
    data class Move(val amount: Int) : Command
    data class Turn(val direction: TurnDirection) : Command
}

operator fun FaceDirection.plus(other: TurnDirection): FaceDirection {
    val delta = if (other == TurnDirection.Left) {
        -1
    } else {
        1
    }
    return FaceDirection.values()[(this.ordinal + delta).mod(FaceDirection.values().size)]
}

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    (first + other.first) to (second + other.second)

data class Grid(private val gridLines: List<List<Tile>>) {
    fun findStart(): Pair<Int, Int> {
        val x = gridLines.first().indexOfFirst { it == Tile.Open }
        return x to 0
    }

    fun moveLeft(position: Pair<Int, Int>, amount: Int): Pair<Int, Int> = move(position, amount, -1 to 0)
    fun moveRight(position: Pair<Int, Int>, amount: Int): Pair<Int, Int> = move(position, amount, 1 to 0)
    fun moveUp(position: Pair<Int, Int>, amount: Int): Pair<Int, Int> = move(position, amount, 0 to -1)
    fun moveDown(position: Pair<Int, Int>, amount: Int): Pair<Int, Int> = move(position, amount, 0 to 1)

    private fun move(position: Pair<Int, Int>, amount: Int, direction: Pair<Int, Int>): Pair<Int, Int> {
        var currentPosition = position
        repeat(amount) {
            var newPosition = wrap(currentPosition + direction)

            when (get(newPosition)) {
                Tile.Wall -> return currentPosition
                Tile.Open -> currentPosition = newPosition
                Tile.Void -> {
                    while (get(newPosition) == Tile.Void) {
                        newPosition = wrap(newPosition + direction)
                    }
                    if (get(newPosition) == Tile.Wall) {
                        return currentPosition
                    } else {
                        currentPosition = newPosition
                    }
                }
            }
        }
        return currentPosition
    }

    private fun wrap(position: Pair<Int, Int>): Pair<Int, Int> {
        val y = position.second.mod(gridLines.size)
        val x = position.first.mod(gridLines[y].size)
        return x to y
    }

    private fun get(position: Pair<Int, Int>): Tile = gridLines[position.second][position.first]

    private fun isOnUnwrappedCube(position: Pair<Int, Int>): Boolean {
        if (position.first < 0 || position.second < 0 || position.second >= gridLines.size || position.first >= gridLines[position.second].size) {
            return false
        }
        return gridLines[position.second][position.first] != Tile.Void
    }

    tailrec fun moveOnCube(
        position: Pair<Int, Int>,
        direction: FaceDirection,
        amount: Int
    ): Pair<Pair<Int, Int>, FaceDirection> {
        val directionVector = when (direction) {
            FaceDirection.Right -> 1 to 0
            FaceDirection.Left -> -1 to 0
            FaceDirection.Up -> 0 to -1
            FaceDirection.Down -> 0 to 1
        }
        var currentPosition = position
        var amountLeft = amount
        while (amountLeft > 0) {
            val nextPosition = currentPosition + directionVector
            if (isOnUnwrappedCube(nextPosition)) {
                if (get(nextPosition) == Tile.Wall) {
                    return currentPosition to direction
                }
                currentPosition += directionVector
                amountLeft -= 1
            } else {
                val (p, d) = translateOnCube(currentPosition, nextPosition, direction)
                try {
                    if (get(p) == Tile.Wall) {
                        return currentPosition to direction
                    }
                } catch (e: Exception) {
                    println ()
                }
                return moveOnCube(p, d, amountLeft - 1)
            }
        }

        return currentPosition to direction
    }

    private fun translateOnCube(
        oldPosition: Pair<Int, Int>,
        newPosition: Pair<Int, Int>,
        direction: FaceDirection
    ): Pair<Pair<Int, Int>, FaceDirection> {
        val sideLength = 50
        if (oldPosition.first >= sideLength && oldPosition.first < 2 * sideLength) {
            when (oldPosition.second) {
                in 0 until sideLength -> {
                    // inside 1
                    if (direction in listOf(FaceDirection.Right, FaceDirection.Down)) {
                        return newPosition to direction
                    }
                    if (direction == FaceDirection.Left) {
                        return (0 to ((sideLength - 1) - newPosition.second + 2 * sideLength)) to FaceDirection.Right
                    }
                    if (direction == FaceDirection.Up) {
                        return (0 to (newPosition.first + 2 * sideLength)) to FaceDirection.Right
                    }
                }

                in sideLength until (2 * sideLength) -> {
                    // inside 3
                    if (direction in listOf(FaceDirection.Up, FaceDirection.Down)) {
                        return newPosition to direction
                    }
                    if (direction == FaceDirection.Left) {
                        return ((newPosition.second - sideLength) to (2 * sideLength)) to FaceDirection.Down
                    }
                    if (direction == FaceDirection.Right) {
                        return ((newPosition.second + sideLength) to sideLength - 1) to FaceDirection.Up
                    }
                }

                in (2 * sideLength) until (3 * sideLength) -> {
                    // inside 5
                    if (direction in listOf(FaceDirection.Left, FaceDirection.Up)) {
                        return newPosition to direction
                    }
                    if (direction == FaceDirection.Down) {
                        return ((sideLength - 1) to (newPosition.first + 2 * sideLength)) to FaceDirection.Left
                    }
                    if (direction == FaceDirection.Right) {
                        return ((3 * sideLength - 1) to ((sideLength - 1) - (newPosition.second - 2 * sideLength))) to FaceDirection.Left
                    }
                }

                else -> {
                    throw IllegalStateException("$oldPosition is not on a face")
                }
            }
        } else if (oldPosition.first < sideLength) {
            when (oldPosition.second) {
                in (2 * sideLength) until (3 * sideLength) -> {
                    // inside 4
                    if (direction in listOf(FaceDirection.Right, FaceDirection.Down)) {
                        return newPosition to direction
                    }
                    if (direction == FaceDirection.Up) {
                        return (sideLength to (newPosition.first + sideLength)) to FaceDirection.Right
                    }
                    if (direction == FaceDirection.Left) {
                        return (sideLength to ((sideLength - 1) - (newPosition.second - 2 * sideLength))) to FaceDirection.Right
                    }
                }

                in (3 * sideLength) until (4 * sideLength) -> {
                    // inside 6
                    if (direction == FaceDirection.Up) {
                        return newPosition to direction
                    }
                    if (direction == FaceDirection.Right) {
                        return ((newPosition.second - 2 * sideLength) to (3 * sideLength - 1)) to FaceDirection.Up
                    }
                    if (direction == FaceDirection.Down) {
                        return ((newPosition.first + 2 * sideLength) to 0) to FaceDirection.Down
                    }
                    if (direction == FaceDirection.Left) {
                        return ((newPosition.second - 2 * sideLength) to 0) to FaceDirection.Down
                    }
                }

                else -> {
                    throw IllegalStateException("$oldPosition is not on a face")
                }
            }
        } else if (oldPosition.first in (2 * sideLength) until (3 * sideLength)) {
            when (oldPosition.second) {
                in (0 until sideLength) -> {
                    // inside 2
                    if (direction == FaceDirection.Left) {
                        return newPosition to direction
                    }
                    if (direction == FaceDirection.Down) {
                        return ((2 * sideLength - 1) to (newPosition.first - sideLength) to FaceDirection.Left)
                    }
                    if (direction == FaceDirection.Up) {
                        return ((newPosition.first - 2 * sideLength) to (4 * sideLength - 1)) to FaceDirection.Up
                    }
                    if (direction == FaceDirection.Right) {
                        return ((newPosition.first - sideLength - 1) to ((sideLength - 1) - newPosition.second + 2 * sideLength)) to FaceDirection.Left
                    }
                }

                else -> {
                    throw IllegalStateException("$oldPosition is not on a face")
                }
            }
        }
        /*
 12
 3
45
6
         */
        throw IllegalStateException("$newPosition $direction missing?")
    }
}

fun parse(input: String): Pair<Grid, List<Command>> {
    val (gridInput, moveInput) = input.split("\n\n")
    val gridLines = gridInput.lines().map { line ->
        line.map {
            when (it) {
                ' ' -> Tile.Void
                '.' -> Tile.Open
                '#' -> Tile.Wall
                else -> throw IllegalArgumentException("Unknown tile '$it'")
            }
        }
    }
    val maxLineLength = gridLines.maxBy { it.size }.size
    val normalizedGridLines = gridLines.map { line ->
        if (line.size < maxLineLength) {
            line + List(maxLineLength - line.size) { Tile.Void }
        } else {
            line
        }
    }

    val commands = mutableListOf<Command>()
    var index = 0
    while (index < moveInput.length) {
        val lexemeStart = index
        val command = if (moveInput[lexemeStart].isDigit()) {
            while (index < moveInput.length && moveInput[index].isDigit()) {
                index += 1
            }
            Command.Move(moveInput.substring(lexemeStart, index).toInt())
        } else if (moveInput[lexemeStart] == 'R') {
            index += 1
            Command.Turn(TurnDirection.Right)
        } else if (moveInput[lexemeStart] == 'L') {
            index += 1
            Command.Turn(TurnDirection.Left)
        } else {
            throw IllegalArgumentException("unknown character '${moveInput[lexemeStart]}")
        }
        commands.add(command)
    }
    return Grid(normalizedGridLines) to commands
}

/*
1 ^ void -> (x, allHeight + y) ^
1 v      -> (x, y) v
1 < void -> (sideLength + y, sideLength) v
1 > void ->

3 ^ void -> (2 * sideLength, x - sideLength) >
3 v void -> (2 * sideLength, (allHeight - 1) - (x - sideLength)) >
3 <      -> (x, y) <
3 >      -> (x, y) >

4 ^      -> (x, y) ^
4 v      -> (x, y) v
4 > void -> (y + 2*sideLength, 2*sideLength + 1) v
4 <      -> (x, y) <

5 ^      -> (x, y) ^
5 v void -> (x, y - allHeight) v
5 < void -> (y - sideLength, 2 * sideLength - 1) ^
5 >      -> (x, y) >



  1
234
  56

    11
    11
223344
223344
    5566
    5566
 */
private const val testInput = """        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5"""

/*
 12
 3
45
6
  1122
  1122
  33
  33
4455
4455
66
66
 */

private const val realInput =
    """                                                  ...#...#..........#..........#......#.....#.........................#.................#.............
                                                  ............#.........#...#...................#.#.....#.............#...#...........................
                                                  ........#.....##.#...............#......#.......#........#....#...#............#....................
                                                  ...##.#.....#....#....#.....................##...............................##..........##.##......
                                                  ......#.#...........#..............#...........#..#........#.............................#......#...
                                                  ..........#.#...#...#.#........#....#.............................##......#......#.#..#.....#...#...
                                                  .........................#........................#......................#.#........................
                                                  ..................#..........##.....#....#..##........#......#....#..#.#...............#.......#....
                                                  #..#.#.........#...........#...............................##......#........#..........##......#....
                                                  .......#...#.#.#..........#....#................................................#......#............
                                                  ..#....#...............#.#..........................................#.......................#.......
                                                  .......#......................#.#...........##......##..#........#..............##....#.............
                                                  ............#.#.......##.........#........#..........................#.....#................#....#..
                                                  ..#..........#.........#...............##............#.........#..#...........#....#.#.......#..#...
                                                  .................#.........#............#..........#...................#.............#..............
                                                  ...............................#.............................................#...#...#......#.......
                                                  .................................#.........#..........#..........#....#..#..........................
                                                  ...................#............#..#...#.#.........#..........................#.....................
                                                  #......#..........#...###.....##.....................#...#.......#.#.#.....#............#...........
                                                  ....#..##..#....#.#.........#..........#....#...#.......#......#............#.#.#.#.................
                                                  ........##.....#.............................................#...........................#..........
                                                  .....#...........................................#.....................#........................#...
                                                  #.#..#.#....#.......#..................##...............#....#........#.#.....#...........##........
                                                  .....#..........#.....#....................................#...#.........#........#......#..........
                                                  ...#.........#..#......#..................#............................#...........#......#.........
                                                  .......#.........#....#...........#........................#........#...........#....#.............#
                                                  ...................#..#........#..#........#...#.......#......................#..........#..........
                                                  ...#.........................#...#...#.....#.............#.......#..................##......#.......
                                                  ..#.................#..........................#.......................#....#....................#..
                                                  .................#..................#..........#.#......###.....#.##..#...................#.........
                                                  #.....#......#.#.........#.............#......##........#.....................#..#.........#.....#..
                                                  ..#...............#........................##..#..........#..#.............#....#.#..#..#........#..
                                                  .................#....#....#....#..#.......................#.......#.#...............#...#.#........
                                                  .#.#.....#............#....#...##.#...#....................................#.#.......#...#.#....#...
                                                  .................#.............##.............#.........#.......#.................#..............#..
                                                  .#...#..........##...#....#.....#.............##.#......#...........#....................#..........
                                                  ...........#........#......#........#.....#.#.#.#...........................................#.......
                                                  ........#.....#......#.....##...................#.#.......#........#............................##.#
                                                  ...#.....#.............#.....................##.....#.#.............##...............#....##..#.....
                                                  ..................#...........##.#.....#....#..........#.#...................#..........#..#....#...
                                                  #............#..#........#...................#...................#.......#.....#....................
                                                  ..........................................#.....#.................#.......#.........##.............#
                                                  ...................#................................................................................
                                                  ..#.........#.....#..............#...#............#.#.....#.#..........#.......#...#................
                                                  ....................................#...........................#...#.....#................#........
                                                  .....#...#......#.......................................#..............#.........##.................
                                                  .....#.............#...............#.......#.....#......#...........................................
                                                  #####.#.................#.........#.......#..#....#...............#..#..........#........#...#......
                                                  ...........##.....#...##...#............#............................#.#...#...............#....#...
                                                  #.................#.........#.......#....#.............#............#........#......................
                                                  #.............#......................#....#..#..#.
                                                  ....#.............#.........#...#.......#.........
                                                  ...#..................##......#...................
                                                  .........#...#..#..##............#................
                                                  .......#.......#.#.......#.....................#.#
                                                  .......................#.....#....#....#..........
                                                  #.....................#.........#.................
                                                  ............#.................##.....##........#..
                                                  ................#...............#.........#......#
                                                  ..........................#...#....#.....#.....#..
                                                  #.......#..#......#.....#.........#.....#......#..
                                                  ..........................#...#................#..
                                                  #...#...#..............#.......#..............#...
                                                  ........................##........#........#.##...
                                                  .........#........#..#.....#......................
                                                  ......#...#......#..........#......#.......#.#....
                                                  .........#......................#..........#......
                                                  ..............................#.......#.#.........
                                                  .........##.................#................#....
                                                  ..........#.............#......#.#...##.........#.
                                                  ..........#.#.....#.............................#.
                                                  ...#...#.##......#.#......#............#...#......
                                                  ...#.#.........#...#.#...#..#....#.............#.#
                                                  .....#............................................
                                                  .....#.#......#....#.....#......#.......#.........
                                                  .................................#................
                                                  ..................................................
                                                  .................#...............#................
                                                  .#.#..#.....#..#.............#.##.........#.......
                                                  ..#............#.................#...#............
                                                  ..............................#.#.#..#..#....#....
                                                  .............#.................#..#...............
                                                  ............#............#...........#..#.........
                                                  .....#.....#..........#....#..........##....##....
                                                  .........#........................................
                                                  ..#........#.....#.......#............#....#......
                                                  ...#...#....#...........#...........#..........#..
                                                  ................#........#.#......................
                                                  ...............##....#...............#............
                                                  .##.....................#....................#....
                                                  ............................##....#.....##......#.
                                                  ........#.....#.................#.#..#......#..##.
                                                  .........#..............#..#......................
                                                  #.............#......#................#....#..#...
                                                  ............#.................#..........#.#..#.##
                                                  .#.....#.....#.....................#..............
                                                  ............#...#..............#...#..............
                                                  .........#..............#.#...#............#......
                                                  ..#.#.......#.#.........##.#...........#.......#..
                                                  ..........#..#........##.............#....#.......
.........#..###.................#......................#...............##...........................
#..#.#...#..........#............#......#..##.........#.##.#........................................
.........................#..................#.#......#..........................#.#..............#..
....#.......#..#.#...#.#..#..............#.........................................#......#....#....
.#..........#.....#...#..#.#...........#............#...............................................
#..................#.........#........#...#.............#......#.....................#.#............
.........#............#........#.............................#..#..............................#....
#.#...............................................#.#...............................................
...#..#........##......#...#..#.....#..................................#..............#.....#.......
.........................................#............#..................................#..#.......
#...#......#..#.....................#.........#.....................................................
.##.................#...#..........#......#...#....#..#....#...#.#.........#............#.....#.....
...#....#.......#............#..........##..................#.....#..............##..........#......
...............#..#.........................#.......................#........##.....................
....#........#..###................#.....#.#.........#...............#...................#.........#
..#...............#.##...........#.#.....#............#.....#.............#.###.#..........#........
.....#...#......................................#.#..............................#...............#..
.......#.....#..#.............#.............................#..###...............#........#..#......
.....#..#.....................#.#.....#...##...............................#.......#................
....................#.#.................#................#..#......................#.##.............
.....#..#...........#.#.....................#..........#...#...........................#.#..#.......
.............#..............#.....#...........#..........#......##..................................
.........#....#..........#............#.##.#.................#............................#.........
...#.....................................#........#..#.#..........................................##
...................#..#.#...................#...##.#.#...#..................#.......#...............
.............#.......................................#........#.#.....#.......................#....#
.....#....#..........#........#...##...........#....#......#......................#.......#.#..#.#..
#...........................#.#..#.............#...........#...................#..........#.........
........#........#........#...#..............##.........#.................................##........
...............................##...............#....#................................#.......#.....
..#.#........#..................#............#....#.........#....##.....#.................#........#
.#......................#....#........##............................#...........#.#...#.............
.......#.....#.......#.......#...................................#....#...#............##....#......
...#................................#.......#..##...................................#...##....#.....
..#.#..........#..........#........#..........#...#......#..##............#.....................#...
......#..............#.............................................#...#.......#.#.......#..........
...........#............#.#..#.........#.........#..........#............................#..........
....#..#......##........#..........#...........#.....................#..........#..............#....
...............................................#...................#.#.........#........#..#.#......
.........#.............#.......#........#.....#..............#...............#......................
.....#...#......#..#........##.........#......#................#........#.......................#..#
#....#.....#..............#..#....#.............#................#..........#..........#...........#
.#..........#...#.....#............#..............##......#.......#...#...........#......#..........
...#.................................#................#.........#..........................#....#...
..#..........#....................................#.......#............#............................
............#......#.........#....#.........#..................#..........#....#..#.................
..........#....#...#.............................................##.....#.....#......#..#.........#.
.....................#............#..........#..#....#.#........#..#.....#......#.#.............##..
....##............#..............#..................##.................#.............#.#............
..#..........#.......................................................................#..............
.#...#......#.....................##..........##..
....#.....................#.......#...............
...#...#.#....#..#............#..#....#..#.#......
#.##..#....#........#.......................#.....
..........#....#.....#.#......#......#.......#....
..#.....##........##......#....#...#.#...#........
...#..........#..#..........#.....................
......#...#....##.#.#.#...........#......#......#.
..................................................
.......#......#.......#..........##...#...........
....#.....#....................#..................
.##...............#..........#..........#......#..
.........#.#.......#..............................
...........#........#....................#........
..#...#...#...................#...................
....#.........#.#............#........#......#.##.
....................................#......#..###.
......#...#..................#...#................
................................................#.
.............#.......#.#.....#.....##.......##....
......#..#.............#.....................#....
............#.........#....#...........##..#......
..#................#...#..............#.........#.
....#.......#............#.....#......#...........
........##..#.......#...#.........................
.............#.......#........##............#....#
#........................#............#...........
......#............#..###..##.#.#.................
...#.#................#......#....................
..#.......#............##....#...#........#.......
........#..#....#............................#...#
......................#.#.............#.......#..#
..........#.#...................#.#.......###..#..
.#......................#................#........
....#...#..............#......##...#...........#..
....#..........#......#.....#.....................
.#.....#..........................................
..#...............................................
......#...#.............#........#........#.....#.
......#.#..#..........##.......###................
................#.#..#.......#..#.................
.................###.................#............
.............#..#...............#........##.......
...............#..#...............................
......#........................#...........#....#.
...#.#...........................#.#...........#..
.............#.#........................#...#.....
..#..........#..........#.........................
.#....#.....#...#............#.........#..........
....#.#...#...................#.#.....#.#.........

42L12R17L16L29R20R13L15R44L30R44R27L9L23R49R17L19L50L46L5R17L4R17R41R4R7R13R41R29R17R13R27R30L2R49R24L19L41L2L43L26R46L34R3L37L4L18L12L18L37R17R47L33L9R16L7R30R44L5L30R12R22L33L12R12R40R6R48L37R50L41L38L6R33L24L49L11L29L31L21R24R11L46R22L50R34R31L2L23R29L32R46L2L49L23L48L44R22R30L21L41R43R41L44R19R7R3R1R19R3R35L39R13R20L10R35L34L7R14L42R25L33L7L13R14R1L19R30L11L33R32L33R14R50R18L48R8L11L16L21L34R2L9L24L12R27L19R1L32R36L34L14L5L38L33L12R43L46R13R26R6R9L49R42R8L29R45L47R30R30R9R17R29R9R44L20L41R32L32L18R29R1L32R5R17R50R45R7R43R10R40R48L28R4L22R5L5L5R11L23R34L12R24R19L25R16L29L33R18L41L38R47L10R49L39R30L37R31L21L33L24L30L21L47R41L25L21R15L39R31R47L38L46R4R13L27L38R35L5L31L20L8R43L14L43L20L5R33L6L39L46R41L10R24L21L9R43R23L1L3R34R10L8L34R22R2L14L11R41L24L29L47L12L36L24L9R19L16R41L49R32R32R44R13R6R21R17R18L1L6R5L2L50R32L24L4R2R9R38R32R41L21L48L48R39R41R48R16R5L46L25R20R9R38R26L34R30L8L38R32R49R5L41L28R40R13R11L13R43R6R21R20L17R46R40L20L24L27L45R40R13R27R16R36R1R32L40L28L12L30L1R24L44L8R10R14R30L12R44R48L49R46R30L18R24L15R18R8R38R26L3L6L24R2R38R29L29R26R46L8L44L13L26R28L38L2R12L14R46L47R42L15L26R43L46R37L50L10L38R24R22L49R18R16R37R12R20R33L35R27R41R37R7R45R4L4L9L37L44L22R18L34R21R6L39L38L47R24L48L19R32L21R32L48R13L20R15L42R1R6L39L2R8L3L47R14L29L12L15R39R34L40R39R27L46R30L38R18R10L40L20R37R44R16L37L45R12R50L8L21R46R19L29L9R21L1R23L23L43R3R42L45L17R26L32R34R15R4L45L7R23R5L21R24R30L41R37L18R34L12L27L39R28R47L50R35L13L40L13R48L47R16R35R2L32L41R45R32L38L4R4L9L7R2R21L33L27L46L39R5R46L1R39L23R18L33L10L3L5L45L35L27L19R44L24R17R39L16R40R8L36R31R41L23R9R45R29R48R24L23R49L16L27R16R19R27R44L9R48R42L6R37R25L1L46R7L25R33R32R26L9L48R7R44L40L10R49R9L34L29R6R30L18R8R46R44R17R37L1L30R12L8R12R18R38R18R22R40R47L7L28R34L42L4L43L46L29L35L33L22R6R30L1R42L17R27L6L11R36L36L38L24L9R36R32R21R50R2R2R46R12L11R15L40R17L7R20R39L27L13R27R22L7L17L49L5R5L7R42R32R27L27L46R48L3R34L13R16R27R37R27L6R40L33R7R45R44L9L45R44L12R17R46R22R19R13R25R4L25R34R42L38R37L32R14L15L37R10L38R42R12R8L5R7R46R49L20L29R26R24L18R22R38L5R4L42R29R41L29R49L2R21L22L7R31R37R45R24R12R44L29L36R15L4L4R26R46R9L4R43R33L40R5L23R25R3R26L1R35L40R38R12R11R1L15L9L19R10R39R10R31R30R19L39L24L44R13L36R32L27R43L4L38R39L24L32R11L3R22R24R6R36L20R43R6L23L30R18R45R31R32R4R5R37R33R49L20L49R11R15R1R42L10R19R9L31L41R44R9R25L28R4L7L35L11L29L2L19L12L27R46R29L1L5L18R19L27L3R12R43R50R21R4L35R42R3R28R1R38R32L33R41L2L41L41R17L3R35L38R28L23R34L14L35R32R12R12R47L3L7R32R4L1R33L5L13R23R26L5R21L27R47R37R8R38R25R44L50R48L42L33L39R33R42L26R50L2L27R26R50L18R40L41R36R11L50L50R7R33L9L21L15L43R18L10R15R44L48L6L16L17R5R20L35R20L9L37R20L17R9L4L22R23L12R49R44L22L3L3R23R6L42L18L49L42L17L43L50L23L20R40R47L33L26R29R15L39R25R33R29L20L50L12R34R2R38L44L37L8L30R40L18R49R10L17L23R8L39R47L16L31R46L24R24R7L28R48R13R1L42L48R49R5R10R19L18L33R26L44R23L17L18L10R28L33L33L34R35R28R42R45L21L32L25R50R42L38R9R29L42R8L23R41R6L1L10R38R7L2L27R45L36L30L48R5R15R35R16R20L48R32R5R21L23R29L48L39R31L30L39R22R42L12R46R39L9L10R48R37R40L35L40R48R13R30R12L40R27L47R17L5L25R36L35R27L44L48L19L16L44L17L36L12L45L30R29L42R39L16R16R50L25R45L45R37L37R36R36R8R12R1L21R12R30L5L6L13L40L39R37L49L9L3L45R21L1R18L9L38L3R48L38R11R25L36L33L26R46L50L19L28L7L4R32R25L30L28L42L10R38R40R36L48R40R14R32R26R16L15R43R30L48R12R16L48R45R23L6L12L46L12R34R26R26L29R20L30L20L2L24L31R35L50L39L29R9R10L42R31L6L47L10R34L40R34R39R37R37R11R39L1R50R22R40R10R24L30L43L7L9R32L7R31R7R16R29R42R38R12L17L12R7L40L38L25L3L12L9L21R20L41L6R32L12L22R8L23R15L15L19R44L12L39R46R35R33L36R28R6L43L45L27R17L4R1L31L8R23R36R21R39R4L45R11R31L41R41R7R48L1L23R15R9L15L39R40L20L29L20L39R15R3L34L18L32R16R5R22L46R11L17R13L19R36L16L43R9L14L5L8L30R21R29L1R33R39L24L31R18R48R15L49R42R28R22L14R7R4R1R23R2L36R27L27R36L3R38L33L39R9L6L45L8L7L49L17L11L31L11L26L26L42R40L2R26R50R45L38L47R28R28R4R43R9L7R27R28R1L31L30L31L50R34R38R16L5R12L43L10L6L6L20L30R29L42R23R3R22R21R45L13R26L41L19L4R39R34L39R26L8R8L36L34L10L23R37R35R6L49L45L43L9R24R42L42R37R3L17L7L30L3R14R40R2R4R7L20L48L46L43L50R34R40L8L37L29R11R16L21R41L31R24L2L34L28L5L3R45L18R19L39L16L42R7L9R30R41R48L17L49R42R38L11R19R41R13L43R1L6R39R31R39R39R31L8L10L27L22R36L47L31L13L35R19R13R35R44R12R2R33L14R36R29L12L29R5R11R11L47L8L23L38R28R48L26R44R21L46R32L37R31R25R36L12R12L20R6L44R8R12R43L49L42L36R45L25R19L10L42L30R33L34R26R45R31R16R11R8R12L41L39L16L22L32R41R5L12R8R26R5L21L35L17L25L20L26L36R10L26L23R37R28R34L29R40R47L41R36R16R36L48R7R4L47L44L46L38R2L21R3L8L24L37R39L21L37L50L9R48R28L37L10R39R43L43R42R16R47R27R4R33L25L40R48R3R10L41L22L47R14L40L42L42L7R34L41L22L33L17R4R34R47R46L26R30R22R4R12R19L41L14L40L45L31L3L13R31L5L38L25L11R24L27L33L14R47L16L44R25R14R10L3R17R20R34R49L31R8L3L28R21R3R50L19L6R11L29R1R37R38L34L6L11L33L47R11L9L16R29L29L21L29L47R30L25L37R15L19R27L37R23L43R6L39R22R14R42L21R24R32R50R34L45R6L41R14L33R18L20L18L35L5R37L35R17R24R30L32L38R44R43R50L17R7R48R48R43L9L9L37L1L34L35R44R22L16L44R33R15R35L30L15R6R22R50L18L37L26L4L8L7R14L40L18R39R10L35R48R43L14L15L24R45L39R50L29R17L49L16L24L7R38R46L3L26R46R19R22R15R32L33L18L12R35L24L26R38L40L21R12R36L50R5R33L33R38R20L48L30L25L12R35L8R5R7L9L1L50L25R3R32L12L4L16R20L17R10L24L15R47L31R23L32R42R6L47L20L8L23L15R48L20R26L34R13R42L42R47L43L15R20L27L30L34L37R16L33R30R4L36L43L27L31R30L27R30R39R50L37L42R42L11L15L27L29R21R43R23R50R38L6L43L48R1L17L3R1L12R3R21L2R37L49L17L12L32L46R34R40L1L2R50L18L15R1L28R14L16L30R34L38L38R40R31L21R47R37L20R21L16L16R42L39R39L10R47R36R9R4R41L44L25R39L11L33L45L42L31R16L45L22L40R24R39R3R16R8L5L44L39L15L3L31L42L14L35L7R9L41L25R26L46L31R14R17L3L40L12L36L17R30L1L40L8L48L19R44R20L41R38R46L4L34L23R41R24R17L17L47L42L14L40R27L15R39R45R48L30R10L34L10R1L2R4R8R45L1L15L36R16L30L1R9R31R13L19L34R8R26L46R44L20R24L38R17L48L31R49R43R21L1R16L37R4R29R40R41L46R16R4L33"""