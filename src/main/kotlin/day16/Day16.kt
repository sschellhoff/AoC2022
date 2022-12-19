package day16

fun main() {
    val graph = createGraph(testInput)
    val distances = Distances.from(graph)
    println(step(graph, "AA", 0, graph.filter { it.value.cost > 0 }.map { it.key }.toMutableSet(), distances, 30))

    // two nodes in parallel start with time 26
    // memoization?
    val paths = mutableMapOf<String, Long>()
        calculatePaths(graph, mutableListOf("AA"), paths, graph.filter { it.value.cost > 0 }.map { it.key }.toMutableSet(), distances, 26, 0)

    var currentMax = 0L
    paths.keys.forEach { p1 ->
        paths.keys.forEach { p2 ->
            if (p1 != p2 && p1.toList().chunked(2).toSet().intersect(p2.toList().chunked(2).toSet()).size == 1) {
                val newPotentialScore = paths[p1]!! + paths[p2]!!
                if (newPotentialScore > currentMax) {
                    currentMax = newPotentialScore
                }
            }
        }
    }
    println(currentMax)
}

data class Distances(
    private val nodeIndices: Map<String, Int>, val data: List<List<Int>>
) {
    fun getCost(from: String, to: String): Int = data[nodeIndices[from]!!][nodeIndices[to]!!]

    companion object {
        fun from(graph: Graph): Distances {
            val numberOfNodes = graph.size
            val nodeIndices = graph.keys.mapIndexed { index, node -> node to index }.toMap()
            val data = List(numberOfNodes) { MutableList(numberOfNodes) { 1000 } }

            graph.keys.forEach { nodeI ->
                val i = nodeIndices[nodeI]!!
                graph.keys.forEach { nodeJ ->
                    val j = nodeIndices[nodeJ]!!
                    if (graph[nodeI]?.nextNodes?.contains(nodeJ) == true) {
                        data[i][j] = 1
                        data[j][i] = 1
                    }
                }
            }
            (0 until numberOfNodes).forEach { k ->
                (0 until numberOfNodes).forEach { i ->
                    (0 until numberOfNodes).forEach { j ->
                        if (data[i][k] + data[k][j] < data[i][j]) {
                            data[i][j] = data[i][k] + data[k][j]
                            data[j][i] = data[i][k] + data[k][j]
                        }
                    }
                }
            }

            return Distances(nodeIndices = nodeIndices, data = data)
        }
    }
}

fun createGraph(input: String): Graph {
    val graph = mutableMapOf<Node, NodeInfo>()
    input.lines().forEach { line ->
        val next = if (line.contains("valves ")) {
            line.substringAfterLast("valves ").split(", ")
        } else {
            line.substringAfterLast("to valve ").split(", ")
        }
        val cost = line.substringAfter("=").substringBefore(";").toInt()
        val nodeName = line.substringAfter("Valve ").substringBefore(" has")
        graph[nodeName] = cost to next
    }
    return graph
}

fun hashPath(nodes: List<Node>): String = nodes.sorted().joinToString("")

fun step(
    graph: Graph,
    currentNode: Node,
    currentScore: Long,
    nodesLeft: MutableSet<Node>,
    distances: Distances,
    timeLeft: Int
): Long {
    if (timeLeft <= 0 || nodesLeft.isEmpty()) {
        return currentScore
    }
    var bestScore = currentScore
    nodesLeft.forEach { nextNode ->
        bestScore = singleStepFor(distances, currentNode, nextNode, timeLeft, nodesLeft, currentScore, graph, bestScore)
    }
    return bestScore
}

private fun singleStepFor(
    distances: Distances,
    currentNode: Node,
    nextNode: Node,
    timeLeft: Int,
    nodesLeft: MutableSet<Node>,
    currentScore: Long,
    graph: Graph,
    bestScore: Long
): Long {
    val moveCost = distances.getCost(currentNode, nextNode)
    val cost = moveCost + 1
    if (cost <= timeLeft) {
        val newNodesLeft = nodesLeft.map { it }.toMutableSet()
        newNodesLeft.remove(nextNode)
        val newTime = timeLeft - cost
        val nextScore = currentScore + newTime * graph[nextNode]!!.cost
        val newScore = step(graph, nextNode, nextScore, newNodesLeft, distances, newTime)
        if (newScore > bestScore) {
            return newScore
        }
    }
    return bestScore
}

fun calculatePaths(graph: Graph, currentPath: MutableList<Node>, paths: MutableMap<String, Long>, nodesLeft: MutableSet<Node>, distances: Distances, timeLeft: Int, currentScore: Long) {
    if (timeLeft <= 0 || nodesLeft.isEmpty()) {
        return
    }
    val foundScore = paths[hashPath(currentPath)]
    if (foundScore == null || currentScore > foundScore) {
        paths[hashPath(currentPath)] = currentScore
    }
    nodesLeft.forEach { nextNode ->
        val moveCost = distances.getCost(currentPath.last(), nextNode)
        val cost = moveCost + 1
        if (cost <= timeLeft) {
            val newNodesLeft = nodesLeft.map { it }.toMutableSet()
            newNodesLeft.remove(nextNode)
            currentPath.add(nextNode)
            val newTime = timeLeft - cost
            val nextScore = currentScore + newTime * graph[nextNode]!!.cost
            calculatePaths(graph, currentPath, paths, newNodesLeft, distances, newTime, nextScore)
            currentPath.removeLast()
        }
    }
}

typealias Node = String
typealias NodeInfo = Pair<Int, List<Node>>
typealias Graph = MutableMap<Node, NodeInfo>

val NodeInfo.nextNodes: List<Node>
    get() = second
val NodeInfo.cost: Int
    get() = first

private const val testInput = """Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II"""

private const val realInput = """Valve JI has flow rate=21; tunnels lead to valves WI, XG
Valve DM has flow rate=3; tunnels lead to valves JX, NG, AW, BY, PF
Valve AZ has flow rate=0; tunnels lead to valves FJ, VC
Valve YQ has flow rate=0; tunnels lead to valves TE, OP
Valve WI has flow rate=0; tunnels lead to valves JI, VC
Valve NE has flow rate=0; tunnels lead to valves ZK, AA
Valve FM has flow rate=0; tunnels lead to valves LC, DU
Valve QI has flow rate=0; tunnels lead to valves TE, JW
Valve OY has flow rate=0; tunnels lead to valves XS, VF
Valve XS has flow rate=18; tunnels lead to valves RR, OY, SV, NQ
Valve NU has flow rate=0; tunnels lead to valves IZ, BD
Valve JX has flow rate=0; tunnels lead to valves DM, ZK
Valve WT has flow rate=23; tunnels lead to valves OV, QJ
Valve KM has flow rate=0; tunnels lead to valves TE, OL
Valve NG has flow rate=0; tunnels lead to valves II, DM
Valve FJ has flow rate=0; tunnels lead to valves AZ, II
Valve QR has flow rate=0; tunnels lead to valves ZK, KI
Valve KI has flow rate=9; tunnels lead to valves ZZ, DI, TL, AJ, QR
Valve ON has flow rate=0; tunnels lead to valves LC, QT
Valve AW has flow rate=0; tunnels lead to valves DM, AA
Valve HI has flow rate=0; tunnels lead to valves TE, VC
Valve XG has flow rate=0; tunnels lead to valves II, JI
Valve II has flow rate=19; tunnels lead to valves LF, NG, OL, FJ, XG
Valve VC has flow rate=24; tunnels lead to valves WI, HI, AZ
Valve VJ has flow rate=0; tunnels lead to valves UG, AA
Valve IZ has flow rate=0; tunnels lead to valves VF, NU
Valve EJ has flow rate=0; tunnels lead to valves ZK, LC
Valve DU has flow rate=12; tunnels lead to valves TC, UG, FM
Valve ZK has flow rate=10; tunnels lead to valves JX, EJ, JW, QR, NE
Valve XF has flow rate=25; tunnels lead to valves OP, VT
Valve LC has flow rate=4; tunnels lead to valves FM, EJ, ON, AJ, PF
Valve SV has flow rate=0; tunnels lead to valves XS, IY
Valve LF has flow rate=0; tunnels lead to valves II, OV
Valve DI has flow rate=0; tunnels lead to valves KI, BY
Valve OP has flow rate=0; tunnels lead to valves YQ, XF
Valve NQ has flow rate=0; tunnels lead to valves TC, XS
Valve QJ has flow rate=0; tunnels lead to valves VT, WT
Valve IY has flow rate=22; tunnel leads to valve SV
Valve AJ has flow rate=0; tunnels lead to valves LC, KI
Valve TE has flow rate=11; tunnels lead to valves QI, HI, KM, YQ
Valve ZZ has flow rate=0; tunnels lead to valves KI, AA
Valve VT has flow rate=0; tunnels lead to valves XF, QJ
Valve OL has flow rate=0; tunnels lead to valves KM, II
Valve TC has flow rate=0; tunnels lead to valves NQ, DU
Valve TL has flow rate=0; tunnels lead to valves VF, KI
Valve QT has flow rate=0; tunnels lead to valves AA, ON
Valve BY has flow rate=0; tunnels lead to valves DM, DI
Valve OV has flow rate=0; tunnels lead to valves LF, WT
Valve VN has flow rate=0; tunnels lead to valves RR, BD
Valve VF has flow rate=13; tunnels lead to valves OY, IZ, TL
Valve BD has flow rate=17; tunnels lead to valves NU, VN
Valve UG has flow rate=0; tunnels lead to valves VJ, DU
Valve PF has flow rate=0; tunnels lead to valves LC, DM
Valve RR has flow rate=0; tunnels lead to valves XS, VN
Valve AA has flow rate=0; tunnels lead to valves QT, ZZ, AW, VJ, NE
Valve JW has flow rate=0; tunnels lead to valves ZK, QI"""