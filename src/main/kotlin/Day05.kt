import java.util.*

fun main() {
    taskOne(input)
    taskTwo(input)
}

fun taskOne(input: String) {
    val (state, commands) = prepareInput(input)
    makeMoves(state, commands)
    println(state.map { if (it.isEmpty()) ' ' else it.pop() })
}

fun taskTwo(input: String) {
    val (state, commands) = prepareInput(input)
    makeMoves2(state, commands)
    println(state.map { if (it.isEmpty()) ' ' else it.pop() })
}

fun prepareInput(input: String): Pair<List<Stack<Char>>, List<String>> {
    val (stateString, commands) = input.split("\n\n")
    val state = initState(stateString.replace("[", " ").replace("]", " ").lines().dropLast(1).reversed())
    return state to commands.lines()
}

fun makeMoves(state: List<Stack<Char>>, moves: List<String>) {
    moves.forEach { makeMove(state, it) }
}

fun makeMove(state: List<Stack<Char>>, move: String) {
    val (count, from, to) = splitMove(move)
    val fromStack = state[from]
    val toStack = state[to]
    repeat(count) {
        toStack.push(fromStack.pop())
    }
}

fun splitMove(move: String): Triple<Int, Int, Int> {
    val moveSplit = move.split(" ")
    check(moveSplit.size == 6)
    val count = moveSplit[1].toInt()
    val from = moveSplit[3].toInt() - 1
    val to = moveSplit[5].toInt() - 1
    return Triple(count, from, to)
}

fun makeMoves2(state: List<Stack<Char>>, moves: List<String>) {
    moves.forEach { makeMove2(state, it) }
}

fun makeMove2(state: List<Stack<Char>>, move: String) {
    val (count, from, to) = splitMove(move)
    val fromStack = state[from]
    val toStack = state[to]
    List(count) { 0 }.map { fromStack.pop() }.reversed().map { toStack.push(it) }
}

fun initState(buildCommands: List<String>): List<Stack<Char>> {
    val stacks = List(9) { Stack<Char>() }
    buildCommands.forEach { line ->
        initState(line, stacks)
    }
    return stacks
}

fun initState(line: String, stacks: List<Stack<Char>>) {
    if (line.isEmpty()) {
        return
    }
    if (!line.startsWith("  ")) {
        val symbol = line[1]
        val stack = stacks.first()
        stack.push(symbol)
    }
    if (line.length == 3) {
        return
    }
    initState(line.substring(4), stacks.subList(1, stacks.size))
}

private const val input = """            [M] [S] [S]            
        [M] [N] [L] [T] [Q]        
[G]     [P] [C] [F] [G] [T]        
[B]     [J] [D] [P] [V] [F] [F]    
[D]     [D] [G] [C] [Z] [H] [B] [G]
[C] [G] [Q] [L] [N] [D] [M] [D] [Q]
[P] [V] [S] [S] [B] [B] [Z] [M] [C]
[R] [H] [N] [P] [J] [Q] [B] [C] [F]
 1   2   3   4   5   6   7   8   9 

move 1 from 7 to 4
move 3 from 4 to 7
move 4 from 3 to 4
move 5 from 6 to 9
move 1 from 8 to 1
move 2 from 3 to 2
move 3 from 4 to 6
move 1 from 3 to 6
move 9 from 7 to 1
move 1 from 2 to 4
move 3 from 4 to 9
move 4 from 9 to 8
move 6 from 8 to 2
move 1 from 8 to 6
move 1 from 4 to 1
move 11 from 1 to 7
move 1 from 4 to 7
move 7 from 2 to 5
move 5 from 6 to 3
move 2 from 4 to 3
move 2 from 5 to 9
move 1 from 8 to 6
move 3 from 1 to 5
move 2 from 6 to 9
move 1 from 4 to 8
move 2 from 2 to 1
move 7 from 5 to 9
move 6 from 3 to 6
move 1 from 2 to 5
move 1 from 3 to 8
move 12 from 7 to 3
move 1 from 1 to 8
move 2 from 1 to 9
move 20 from 9 to 5
move 1 from 1 to 7
move 5 from 5 to 3
move 1 from 8 to 7
move 2 from 8 to 3
move 2 from 6 to 5
move 1 from 6 to 4
move 18 from 3 to 2
move 1 from 4 to 2
move 1 from 7 to 9
move 1 from 1 to 9
move 1 from 6 to 1
move 8 from 5 to 2
move 1 from 1 to 6
move 19 from 5 to 2
move 5 from 2 to 6
move 2 from 9 to 7
move 20 from 2 to 1
move 1 from 9 to 4
move 8 from 6 to 2
move 5 from 1 to 3
move 27 from 2 to 1
move 34 from 1 to 7
move 1 from 2 to 6
move 2 from 3 to 1
move 1 from 4 to 9
move 1 from 2 to 6
move 2 from 1 to 7
move 1 from 6 to 7
move 1 from 9 to 3
move 2 from 6 to 3
move 1 from 6 to 4
move 5 from 3 to 4
move 5 from 4 to 2
move 4 from 1 to 4
move 4 from 1 to 4
move 2 from 3 to 6
move 1 from 6 to 9
move 25 from 7 to 5
move 1 from 6 to 4
move 17 from 5 to 2
move 14 from 7 to 6
move 1 from 7 to 3
move 1 from 9 to 3
move 10 from 2 to 3
move 2 from 2 to 9
move 12 from 3 to 5
move 18 from 5 to 8
move 1 from 4 to 2
move 5 from 2 to 1
move 1 from 5 to 3
move 3 from 2 to 1
move 1 from 2 to 7
move 2 from 2 to 6
move 9 from 6 to 7
move 16 from 8 to 2
move 6 from 7 to 3
move 2 from 8 to 9
move 1 from 1 to 8
move 16 from 2 to 6
move 1 from 8 to 3
move 2 from 1 to 2
move 1 from 9 to 3
move 7 from 4 to 1
move 7 from 3 to 7
move 9 from 7 to 8
move 1 from 5 to 6
move 2 from 9 to 7
move 1 from 9 to 5
move 11 from 6 to 8
move 1 from 3 to 1
move 1 from 4 to 2
move 2 from 8 to 5
move 3 from 5 to 8
move 2 from 1 to 9
move 5 from 1 to 4
move 3 from 4 to 1
move 1 from 3 to 2
move 3 from 2 to 1
move 1 from 9 to 1
move 1 from 2 to 5
move 2 from 4 to 7
move 20 from 8 to 5
move 1 from 9 to 7
move 11 from 6 to 1
move 17 from 1 to 5
move 1 from 8 to 2
move 7 from 5 to 8
move 1 from 4 to 5
move 2 from 1 to 2
move 2 from 8 to 4
move 4 from 7 to 6
move 2 from 6 to 8
move 2 from 1 to 2
move 1 from 1 to 4
move 4 from 8 to 9
move 2 from 1 to 9
move 3 from 8 to 1
move 25 from 5 to 2
move 23 from 2 to 1
move 1 from 7 to 1
move 6 from 9 to 8
move 6 from 8 to 3
move 3 from 6 to 2
move 10 from 1 to 2
move 1 from 6 to 3
move 2 from 3 to 6
move 2 from 3 to 2
move 2 from 6 to 8
move 1 from 4 to 6
move 14 from 1 to 9
move 2 from 3 to 4
move 14 from 2 to 4
move 1 from 6 to 9
move 17 from 4 to 3
move 1 from 8 to 6
move 2 from 7 to 2
move 1 from 4 to 2
move 1 from 5 to 9
move 9 from 2 to 4
move 17 from 3 to 7
move 3 from 4 to 2
move 1 from 8 to 3
move 4 from 5 to 7
move 1 from 3 to 6
move 1 from 4 to 5
move 14 from 7 to 9
move 2 from 1 to 9
move 3 from 2 to 1
move 1 from 2 to 5
move 1 from 3 to 7
move 4 from 1 to 2
move 2 from 6 to 7
move 3 from 9 to 8
move 4 from 2 to 4
move 17 from 9 to 7
move 1 from 2 to 8
move 8 from 9 to 6
move 1 from 8 to 2
move 19 from 7 to 9
move 9 from 4 to 2
move 5 from 7 to 3
move 3 from 5 to 9
move 6 from 2 to 5
move 1 from 9 to 4
move 3 from 2 to 9
move 25 from 9 to 5
move 1 from 3 to 6
move 2 from 5 to 8
move 6 from 6 to 7
move 1 from 3 to 4
move 2 from 3 to 4
move 1 from 8 to 2
move 2 from 2 to 9
move 2 from 8 to 3
move 5 from 7 to 6
move 3 from 7 to 9
move 7 from 5 to 8
move 2 from 3 to 5
move 1 from 3 to 5
move 1 from 6 to 2
move 6 from 9 to 5
move 1 from 9 to 2
move 1 from 6 to 9
move 2 from 5 to 6
move 2 from 9 to 8
move 11 from 8 to 1
move 2 from 5 to 9
move 3 from 6 to 5
move 1 from 4 to 7
move 5 from 5 to 7
move 1 from 4 to 8
move 7 from 7 to 2
move 12 from 5 to 2
move 10 from 1 to 8
move 1 from 9 to 6
move 3 from 8 to 1
move 1 from 1 to 6
move 10 from 2 to 3
move 8 from 8 to 7
move 1 from 9 to 8
move 2 from 3 to 5
move 14 from 5 to 8
move 1 from 3 to 2
move 3 from 8 to 1
move 3 from 8 to 4
move 3 from 2 to 4
move 5 from 6 to 4
move 8 from 7 to 9
move 6 from 8 to 7
move 1 from 5 to 7
move 6 from 2 to 9
move 4 from 4 to 6
move 4 from 4 to 9
move 3 from 9 to 3
move 1 from 8 to 6
move 1 from 5 to 6
move 2 from 7 to 2
move 1 from 3 to 4
move 3 from 4 to 1
move 3 from 4 to 3
move 5 from 6 to 4
move 4 from 3 to 8
move 1 from 6 to 4
move 8 from 3 to 2
move 2 from 8 to 5
move 11 from 9 to 7
move 9 from 1 to 9
move 2 from 7 to 3
move 1 from 6 to 8
move 1 from 6 to 5
move 5 from 9 to 8
move 3 from 9 to 7
move 2 from 9 to 1
move 2 from 3 to 7
move 12 from 7 to 1
move 2 from 8 to 9
move 5 from 4 to 5
move 4 from 9 to 4
move 1 from 1 to 3
move 7 from 1 to 3
move 7 from 5 to 6
move 1 from 9 to 1
move 1 from 5 to 1
move 5 from 7 to 8
move 4 from 6 to 7
move 5 from 1 to 8
move 1 from 4 to 3
move 12 from 8 to 7
move 2 from 2 to 4
move 2 from 8 to 9
move 3 from 8 to 2
move 2 from 6 to 7
move 4 from 7 to 8
move 1 from 6 to 8
move 4 from 3 to 2
move 15 from 7 to 8
move 1 from 7 to 6
move 3 from 3 to 5
move 2 from 3 to 4
move 5 from 2 to 5
move 3 from 1 to 5
move 4 from 5 to 6
move 4 from 5 to 9
move 1 from 5 to 7
move 4 from 9 to 4
move 2 from 2 to 9
move 2 from 5 to 2
move 2 from 2 to 1
move 3 from 4 to 9
move 2 from 9 to 4
move 2 from 8 to 5
move 2 from 5 to 2
move 8 from 2 to 4
move 2 from 1 to 3
move 2 from 3 to 5
move 3 from 6 to 9
move 2 from 6 to 1
move 2 from 1 to 4
move 1 from 2 to 4
move 1 from 5 to 7
move 2 from 2 to 7
move 18 from 4 to 2
move 1 from 5 to 9
move 2 from 7 to 9
move 18 from 8 to 4
move 1 from 7 to 8
move 22 from 4 to 8
move 6 from 2 to 6
move 3 from 6 to 8
move 3 from 6 to 4
move 3 from 4 to 7
move 3 from 7 to 1
move 14 from 2 to 3
move 10 from 3 to 2
move 27 from 8 to 1
move 1 from 7 to 6
move 1 from 3 to 7
move 2 from 2 to 8
move 2 from 9 to 8
move 18 from 1 to 4
move 6 from 1 to 5
move 10 from 4 to 7
move 1 from 3 to 7
move 4 from 7 to 2
move 3 from 9 to 7
move 1 from 6 to 5
move 1 from 2 to 7
move 2 from 5 to 6
move 2 from 6 to 5
move 3 from 5 to 1
move 6 from 1 to 3
move 4 from 5 to 9
move 11 from 2 to 9
move 2 from 1 to 6
move 3 from 4 to 6
move 5 from 7 to 3
move 2 from 6 to 1
move 2 from 1 to 5
move 1 from 8 to 2
move 1 from 1 to 8
move 1 from 6 to 4
move 2 from 4 to 5
move 4 from 5 to 9
move 11 from 3 to 6
move 1 from 3 to 6
move 8 from 6 to 5
move 1 from 3 to 5
move 4 from 4 to 8
move 21 from 9 to 6
move 2 from 9 to 5
move 1 from 9 to 3
move 1 from 2 to 6
move 7 from 8 to 6
move 12 from 6 to 5
move 1 from 8 to 2
move 10 from 6 to 7
move 15 from 7 to 2
move 2 from 7 to 3
move 13 from 6 to 8
move 9 from 5 to 1
move 12 from 5 to 3
move 1 from 2 to 3
move 1 from 9 to 7
move 9 from 3 to 4
move 3 from 4 to 6
move 1 from 7 to 6
move 6 from 4 to 1
move 2 from 5 to 2
move 6 from 1 to 8
move 9 from 8 to 6
move 7 from 3 to 2
move 1 from 2 to 9
move 9 from 6 to 1
move 13 from 1 to 7
move 4 from 8 to 5
move 2 from 7 to 1
move 3 from 6 to 4
move 3 from 5 to 8
move 3 from 2 to 6
move 1 from 5 to 3
move 1 from 3 to 4
move 1 from 9 to 8
move 3 from 8 to 7
move 12 from 2 to 9
move 10 from 7 to 4
move 5 from 8 to 4
move 1 from 8 to 5
move 11 from 4 to 7
move 8 from 9 to 7
move 1 from 6 to 2
move 8 from 2 to 6
move 1 from 5 to 8
move 4 from 1 to 5
move 4 from 9 to 6
move 3 from 1 to 3
move 2 from 8 to 4
move 1 from 7 to 6
move 1 from 2 to 7
move 2 from 3 to 7
move 4 from 4 to 9
move 11 from 6 to 9
move 10 from 7 to 8
move 1 from 3 to 4
move 1 from 6 to 4
move 4 from 5 to 7
move 6 from 7 to 4
move 1 from 8 to 7
move 4 from 6 to 7
move 12 from 4 to 8
move 12 from 8 to 1
move 1 from 8 to 2
move 10 from 1 to 7
move 2 from 4 to 1
move 8 from 8 to 3
move 4 from 1 to 6
move 8 from 7 to 6
move 2 from 6 to 5
move 2 from 5 to 2
move 13 from 9 to 3
move 3 from 2 to 5
move 8 from 3 to 4
move 7 from 6 to 7
move 1 from 9 to 2
move 1 from 9 to 1
move 2 from 6 to 4
move 3 from 4 to 8
move 1 from 1 to 7
move 4 from 4 to 6
move 3 from 8 to 7
move 1 from 2 to 9
move 1 from 5 to 2
move 1 from 2 to 5
move 2 from 4 to 5
move 1 from 7 to 2
move 13 from 3 to 4
move 7 from 4 to 3
move 4 from 5 to 9
move 1 from 4 to 7
move 5 from 6 to 3
move 3 from 9 to 7
move 10 from 7 to 8
move 3 from 4 to 8
move 1 from 5 to 4
move 2 from 3 to 1
move 3 from 7 to 4
move 4 from 8 to 6
move 2 from 9 to 3
move 2 from 4 to 5
move 4 from 4 to 3
move 8 from 8 to 3
move 3 from 6 to 8
move 1 from 2 to 6
move 5 from 7 to 9
move 1 from 4 to 3
move 3 from 7 to 5
move 3 from 8 to 4
move 7 from 7 to 5
move 3 from 7 to 8
move 1 from 9 to 8
move 3 from 4 to 1
move 1 from 5 to 8
move 3 from 7 to 1
move 6 from 8 to 3
move 3 from 9 to 5
move 2 from 6 to 5
move 2 from 1 to 6
move 16 from 3 to 8
move 4 from 5 to 8
move 4 from 3 to 8
move 1 from 9 to 5
move 1 from 6 to 5
move 3 from 3 to 7
move 6 from 1 to 6
move 1 from 5 to 4
move 3 from 5 to 2
move 2 from 7 to 4
move 1 from 2 to 8
move 6 from 8 to 1
move 2 from 4 to 5
move 2 from 2 to 3
move 7 from 8 to 7
move 1 from 4 to 6
move 3 from 6 to 4
move 3 from 4 to 9
move 3 from 6 to 3
move 11 from 8 to 6
move 12 from 5 to 4
move 5 from 6 to 1
move 9 from 3 to 2
move 7 from 6 to 1
move 7 from 7 to 8
move 5 from 8 to 3
move 2 from 3 to 6
move 2 from 8 to 1
move 1 from 7 to 2
move 7 from 3 to 8
move 1 from 9 to 1
move 14 from 1 to 3
move 9 from 2 to 8
move 11 from 3 to 4
move 22 from 4 to 1
move 2 from 3 to 1
move 16 from 8 to 4
move 1 from 9 to 2
move 3 from 6 to 9
move 3 from 9 to 5
move 1 from 2 to 6
move 1 from 5 to 7"""