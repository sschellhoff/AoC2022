package day10

fun main() {
    part1(testInput).let { check(it == 13140L) { "expected 13140 got $it" } }
    println(part1(input))
    part2(input).forEach {
        println(it)
    }
}

fun part1(input: String): Long {
    return runProgram(input.lines(), false, 1, 1, 0)
}

fun part2(input: String): List<String> {
    return render(input.lines(), false, 1, 1, emptyList())
}

tailrec fun runProgram(
    program: List<String>,
    secondCycleOfCommand: Boolean,
    cycle: Int,
    registerValue: Int,
    acc: Long
): Long {
    if (cycle > 220) {
        return acc
    }
    check(program.isNotEmpty())
    val signal = when (cycle) {
        20, 60, 100, 140, 180, 220 -> registerValue * cycle
        else -> 0
    }
    val cmd = program.first()
    if (cmd == "noop") {
        return runProgram(program.drop(1), false, cycle + 1, registerValue, acc + signal)
    }
    if (!secondCycleOfCommand) {
        return runProgram(program, true, cycle + 1, registerValue, acc + signal)
    }
    val delta = cmd.split(' ')[1].toInt()
    return runProgram(program.drop(1), false, cycle + 1, registerValue + delta, acc + signal)
}

tailrec fun render(
    program: List<String>,
    secondCycleOfCommand: Boolean,
    cycle: Int,
    registerValue: Int,
    display: List<String>
): List<String> {
    if (program.isEmpty()) {
        return display
    }
    val nextDisplay = display.toMutableList()
    if (display.isEmpty() || display.last().length == 40) {
        nextDisplay.add("")
    }
    val column = nextDisplay.last().length
    nextDisplay[nextDisplay.size - 1] = if (column in registerValue - 1..registerValue + 1) {
        nextDisplay.last() + "#"
    } else {
        nextDisplay.last() + "."
    }
    val cmd = program.first()
    if (cmd == "noop") {
        return render(program.drop(1), false, cycle + 1, registerValue, nextDisplay)
    }
    if (!secondCycleOfCommand) {
        return render(program, true, cycle + 1, registerValue, nextDisplay)
    }
    val delta = cmd.split(' ')[1].toInt()
    return render(program.drop(1), false, cycle + 1, registerValue + delta, nextDisplay)
}

private const val testInput = """addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop"""

private const val input = """addx 1
noop
addx 2
addx 11
addx -4
noop
noop
noop
noop
addx 3
addx -3
addx 10
addx 1
noop
addx 12
addx -8
addx 5
noop
noop
addx 1
addx 4
addx -12
noop
addx -25
addx 14
addx -7
noop
addx 11
noop
addx -6
addx 3
noop
addx 2
addx 22
addx -12
addx -17
addx 15
addx 2
addx 10
addx -9
noop
noop
noop
addx 5
addx 2
addx -33
noop
noop
noop
noop
addx 12
addx -9
addx 7
noop
noop
addx 3
addx -2
addx 2
addx 26
addx -31
addx 14
addx 3
noop
addx 13
addx -1
noop
addx -5
addx -13
addx 14
noop
addx -20
addx -15
noop
addx 7
noop
addx 31
noop
addx -26
noop
noop
noop
addx 5
addx 20
addx -11
addx -3
addx 9
addx -5
addx 2
noop
addx 4
noop
addx 4
noop
noop
addx -7
addx -30
noop
addx 7
noop
noop
addx -2
addx -4
addx 11
addx 14
addx -9
addx -2
noop
addx 7
noop
addx -11
addx -5
addx 19
addx 5
addx 2
addx 5
noop
noop
addx -2
addx -27
addx -6
addx 1
noop
noop
addx 4
addx 1
addx 4
addx 5
noop
noop
noop
addx 1
noop
addx 4
addx 1
noop
noop
addx 5
noop
noop
addx 4
addx 1
noop
addx 4
addx 1
noop
noop
noop
noop"""