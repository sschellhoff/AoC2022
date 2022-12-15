package day15

import kotlin.math.absoluteValue

fun main() {
    part1()
    part2()
}

fun part1() {
    println(testInput.lines().map { testFrom(it) }.noBeaconsAtY(10))
    println(input.lines().map { testFrom(it) }.noBeaconsAtY(2000000))
}

fun part2() {
    testInput.lines().map { testFrom(it) }.findPossibleBeaconSpot(20, 20)
    input.lines().map { testFrom(it) }.findPossibleBeaconSpot(4000000, 4000000)
}


typealias Point = Pair<Long, Long>

val Point.x: Long
    get() = first
val Point.y: Long
    get() = second

fun pointFrom(input: String): Point {
    val x = input.substringAfter("x=").substringBefore(",").toLong()
    val y = input.substringAfter("y=").toLong()
    return x to y
}

typealias Test = Pair<Point, Point>

val Test.sensorLocation: Point
    get() = first
val Test.beaconLocation: Point
    get() = second

fun testFrom(input: String): Test {
    val (sensorInput, beaconInput) = input.split(": ")
    return pointFrom(sensorInput) to pointFrom(beaconInput)
}

fun Test.manhattanDistance(): Long = manhattanDistance(beaconLocation, sensorLocation)
fun Test.noBeaconsAtY(y: Long): LongRange? {
    val testsManhattanDistance = manhattanDistance()
    if (manhattanDistance(sensorLocation, sensorLocation.x to y) > testsManhattanDistance) {
        return null
    }
    val deltaY = (y - sensorLocation.y).absoluteValue
    val x = sensorLocation.x
    val deltaX = testsManhattanDistance - deltaY
    val minX = x - deltaX
    val maxX = x + deltaX
    return minX..maxX
}

fun List<Test>.noBeaconsAtY(y: Long): Int {
    val ranges = mapNotNull { it.noBeaconsAtY(y) }
    val minX = ranges.minBy { it.first }.first
    val maxX = ranges.maxBy { it.last }.last
    val m = (maxX - minX + 1)
    val line = MutableList(m.toInt()) { index ->
        val x = minX + index
        ranges.any { it.contains(x) }
    }
    forEach { test ->
        if (test.beaconLocation.y == y) {
            val x = test.beaconLocation.x - minX
            line[x.toInt()] = false
        }
    }
    return line.count { it }
}

fun List<Test>.findPossibleBeaconSpot(maxX: Int, maxY: Int) {
    (0..maxY).forEach { y ->
        val ranges =
            this.mapNotNull { test -> test.noBeaconsAtY(y.toLong()) }.filter { it.last >= 0 && it.first <= maxX }
        var currentMax = ranges.minBy { it.first }.last
        while (true) {
            val newMax = ranges.filter { it.last > currentMax && (it.contains(currentMax) || it.first-1 == currentMax)}.maxByOrNull { it.last }?.last
            if (newMax != null) {
                currentMax = newMax
            } else if (currentMax > maxX) {
                break
            } else {
                val foundX = currentMax + 1
                val foundY = y
                println("$foundX $foundY ${foundX * 4000000 + foundY}")
                break
            }
        }
    }
}

fun manhattanDistance(p1: Point, p2: Point): Long = (p1.x - p2.x).absoluteValue + (p1.y - p2.y).absoluteValue

private const val testInput = """Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3"""

private const val input = """Sensor at x=1363026, y=2928920: closest beacon is at x=1571469, y=3023534
Sensor at x=2744178, y=3005943: closest beacon is at x=3091714, y=3106683
Sensor at x=223983, y=2437431: closest beacon is at x=-278961, y=3326224
Sensor at x=2454616, y=2576344: closest beacon is at x=2885998, y=2387754
Sensor at x=1551436, y=29248: closest beacon is at x=1865296, y=-1279130
Sensor at x=2997120, y=2493979: closest beacon is at x=2885998, y=2387754
Sensor at x=1588355, y=3153332: closest beacon is at x=1571469, y=3023534
Sensor at x=3539081, y=3302128: closest beacon is at x=3309042, y=3583067
Sensor at x=3973905, y=60392: closest beacon is at x=3515381, y=-806927
Sensor at x=3305001, y=3120691: closest beacon is at x=3091714, y=3106683
Sensor at x=3859262, y=2668840: closest beacon is at x=3574747, y=2000000
Sensor at x=2475557, y=3997856: closest beacon is at x=2364210, y=4052453
Sensor at x=2775306, y=3668540: closest beacon is at x=3309042, y=3583067
Sensor at x=3018235, y=2285225: closest beacon is at x=2885998, y=2387754
Sensor at x=3033163, y=3294719: closest beacon is at x=3091714, y=3106683
Sensor at x=3079956, y=3215569: closest beacon is at x=3091714, y=3106683
Sensor at x=3994355, y=1831842: closest beacon is at x=3574747, y=2000000
Sensor at x=1741021, y=3231978: closest beacon is at x=1571469, y=3023534
Sensor at x=1873455, y=3917294: closest beacon is at x=2364210, y=4052453
Sensor at x=3128140, y=2938277: closest beacon is at x=3091714, y=3106683
Sensor at x=732217, y=3603298: closest beacon is at x=-278961, y=3326224
Sensor at x=3884431, y=3834735: closest beacon is at x=3309042, y=3583067
Sensor at x=3679358, y=1029949: closest beacon is at x=3574747, y=2000000
Sensor at x=2260133, y=3563353: closest beacon is at x=2364210, y=4052453
Sensor at x=60149, y=3320681: closest beacon is at x=-278961, y=3326224
Sensor at x=3132535, y=2405693: closest beacon is at x=2885998, y=2387754
Sensor at x=3028313, y=2829410: closest beacon is at x=3091714, y=3106683
Sensor at x=3142423, y=3921417: closest beacon is at x=3309042, y=3583067
Sensor at x=2636416, y=939525: closest beacon is at x=2885998, y=2387754
Sensor at x=524530, y=681397: closest beacon is at x=-1031499, y=681463
Sensor at x=3155000, y=1666362: closest beacon is at x=3574747, y=2000000
Sensor at x=2169350, y=3040469: closest beacon is at x=1571469, y=3023534
Sensor at x=1663350, y=1595182: closest beacon is at x=1571469, y=3023534
Sensor at x=3311582, y=3386773: closest beacon is at x=3309042, y=3583067"""