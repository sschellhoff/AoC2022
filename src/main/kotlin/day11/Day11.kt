package day11

import mapBlocks
import penultimate
import java.util.*

fun main() {
    println(part1(input.mapBlocks { Monkey.from(it) }))
    println(part2(testInput.mapBlocks { Monkey.from(it) }))
}

fun part1(monkeys: List<Monkey>): Int {
    val inspectionScores = mutableMapOf<Int, Int>()
    repeat(20) {
        calculateRound(monkeys) { worryLevel -> worryLevel / 3 }.forEachIndexed { monkey, score ->
            inspectionScores[monkey] = score + (inspectionScores[monkey] ?: 0)
        }
    }
    return inspectionScores.values.sorted().let { it.penultimate() * it.last() }
}

fun part2(monkeys: List<Monkey>): Long {
    val lcm = monkeys.map { monkey -> (1L..30L).first { monkey.test(it) } }.reduce(Long::times)
    val inspectionScores = mutableMapOf<Int, Long>()
    val shrinkWorryLevel: (Long) -> Long =
        { worryLevel -> worryLevel % lcm }
    repeat(10000) {
        calculateRound(monkeys, shrinkWorryLevel).forEachIndexed { monkey, score ->
            inspectionScores[monkey] = score + (inspectionScores[monkey] ?: 0L)
        }
    }
    return inspectionScores.values.sorted().let { it.penultimate() * it.last() }
}

fun calculateRound(monkeys: List<Monkey>, shrinkWorryLevel: (Long) -> Long): List<Int> =
    monkeys.map { runMonkey(it, monkeys, shrinkWorryLevel) }

fun runMonkey(monkey: Monkey, monkeys: List<Monkey>, shrinkWorryLevel: (Long) -> Long): Int {
    var numberOfInspections = 0
    while (monkey.items.isNotEmpty()) {
        val item = monkey.items.poll()
        val newLevel: Long = shrinkWorryLevel(monkey.operation(item))
        if (monkey.test(newLevel)) {
            monkeys[monkey.nextIfTrue].items.add(newLevel)
        } else {
            monkeys[monkey.nextIfFalse].items.add(newLevel)
        }
        numberOfInspections += 1
    }
    return numberOfInspections
}

data class Monkey(
    val items: Queue<Long>,
    val operation: (Long) -> Long,
    val test: (Long) -> Boolean,
    val nextIfTrue: Int,
    val nextIfFalse: Int
) {
    companion object {
        fun from(input: String): Monkey {
            val (startItems, op, test, ifTrue, ifFalse) = input.lines().drop(1).map { it.split(": ")[1].trim() }
            val items = startItems.split(", ").map { it.toLong() }
            val opRight = op.split("old ")[1]
            val operation: (Long) -> Long = when {
                opRight.endsWith("old") -> { old -> old * old }
                opRight.first() == '+' -> { old -> old + opRight.split(" ").last().toLong() }
                opRight.first() == '-' -> { old -> old - opRight.split(" ").last().toLong() }
                opRight.first() == '*' -> { old -> old * opRight.split(" ").last().toLong() }
                opRight.first() == '/' -> { old -> old / opRight.split(" ").last().toLong() }
                else -> throw IllegalArgumentException("Unknown operation $op")
            }
            check(test.startsWith("divisible by"))
            val testOperation: (Long) -> Boolean = { new -> new % test.split(" ").last().toLong() == 0L }
            return Monkey(
                items = LinkedList(items),
                operation = operation,
                test = testOperation,
                nextIfTrue = ifTrue.split(" ").last().toInt(),
                nextIfFalse = ifFalse.split(" ").last().toInt()
            )
        }
    }
}

private const val testInput = """Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1"""

private const val input = """Monkey 0:
  Starting items: 84, 66, 62, 69, 88, 91, 91
  Operation: new = old * 11
  Test: divisible by 2
    If true: throw to monkey 4
    If false: throw to monkey 7

Monkey 1:
  Starting items: 98, 50, 76, 99
  Operation: new = old * old
  Test: divisible by 7
    If true: throw to monkey 3
    If false: throw to monkey 6

Monkey 2:
  Starting items: 72, 56, 94
  Operation: new = old + 1
  Test: divisible by 13
    If true: throw to monkey 4
    If false: throw to monkey 0

Monkey 3:
  Starting items: 55, 88, 90, 77, 60, 67
  Operation: new = old + 2
  Test: divisible by 3
    If true: throw to monkey 6
    If false: throw to monkey 5

Monkey 4:
  Starting items: 69, 72, 63, 60, 72, 52, 63, 78
  Operation: new = old * 13
  Test: divisible by 19
    If true: throw to monkey 1
    If false: throw to monkey 7

Monkey 5:
  Starting items: 89, 73
  Operation: new = old + 5
  Test: divisible by 17
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 6:
  Starting items: 78, 68, 98, 88, 66
  Operation: new = old + 6
  Test: divisible by 11
    If true: throw to monkey 2
    If false: throw to monkey 5

Monkey 7:
  Starting items: 70
  Operation: new = old + 7
  Test: divisible by 5
    If true: throw to monkey 1
    If false: throw to monkey 3"""