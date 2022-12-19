package day19

fun main() {
    val blueprints = realInput.lines().map { Blueprint.from(it) }
    val factory = Factory()
    println(blueprints.sumOf { it.id * maxGeodes(24, it, factory, factory) })
    println(blueprints.take(3).map { maxGeodes(32, it, factory, factory).toLong() }.reduce(Long::times))
}

fun maxGeodes(timeLeft: Int, blueprint: Blueprint, factory: Factory, lastFactory: Factory): Int {
    if (timeLeft == 0) {
        return factory.geode
    }
    var maxGeodes = factory.geode
    val geodeFactory = factory.buyGeodeRobot(blueprint)
    if (geodeFactory != null) {
        return maxGeodes(timeLeft - 1, blueprint, geodeFactory, factory)
    }

    val obsidianFactory = factory.buyObsidianRobot(blueprint)
    if (!(factory.boughtNothing && lastFactory.canBuyObsidianRobot(blueprint)) && obsidianFactory != null) {
        val newGeodes = maxGeodes(timeLeft - 1, blueprint, obsidianFactory, factory)
        if (newGeodes > maxGeodes) {
            maxGeodes = newGeodes
        }
    }

    // we only need clay for obsidian production. at this point, we don't need more clay because we cannot buy any obsidion robot
    if (timeLeft > 6) {
        val clayFactory = factory.buyClayRobot(blueprint)
        if (!(factory.boughtNothing && lastFactory.canBuyClayRobot(blueprint)) && clayFactory != null) {
            val newGeodes = maxGeodes(timeLeft - 1, blueprint, clayFactory, factory)
            if (newGeodes > maxGeodes) {
                maxGeodes = newGeodes
            }
        }
    }

    val oreFactory = factory.buyOreRobot(blueprint)
    if (!(factory.boughtNothing && lastFactory.canBuyOreRobot(blueprint)) && oreFactory != null) {
        val newGeodes = maxGeodes(timeLeft - 1, blueprint, oreFactory, factory)
        if (newGeodes > maxGeodes) {
            maxGeodes = newGeodes
        }
    }

    val newGeodes = maxGeodes(timeLeft - 1, blueprint, factory.buyNothing(), factory)
    return if (newGeodes > maxGeodes) {
        newGeodes
    } else {
        maxGeodes
    }
}

class Factory(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,

    val orePerMinute: Int = 1,
    val clayPerMinute: Int = 0,
    val obsidianPerMinute: Int = 0,
    val geodePerMinute: Int = 0,
    val boughtNothing: Boolean = true
) {

    fun canBuyOreRobot(blueprint: Blueprint): Boolean = ore >= blueprint.oreRobotOreCost
    fun buyOreRobot(blueprint: Blueprint) = if (canBuyOreRobot(blueprint)) {
        Factory(
            ore = ore - blueprint.oreRobotOreCost + orePerMinute,
            clay = clay + clayPerMinute,
            obsidian = obsidian + obsidianPerMinute,
            geode = geode + geodePerMinute,
            orePerMinute = orePerMinute + 1,
            clayPerMinute = clayPerMinute,
            obsidianPerMinute = obsidianPerMinute,
            geodePerMinute = geodePerMinute,
            boughtNothing = false
        )
    } else {
        null
    }

    fun canBuyClayRobot(blueprint: Blueprint): Boolean = ore >= blueprint.clayRobotOreCost
    fun buyClayRobot(blueprint: Blueprint) = if (canBuyClayRobot(blueprint)) {
        Factory(
            ore = ore - blueprint.clayRobotOreCost + orePerMinute,
            clay = clay + clayPerMinute,
            obsidian = obsidian + obsidianPerMinute,
            geode = geode + geodePerMinute,
            orePerMinute = orePerMinute,
            clayPerMinute = clayPerMinute + 1,
            obsidianPerMinute = obsidianPerMinute,
            geodePerMinute = geodePerMinute,
            boughtNothing = false
        )
    } else {
        null
    }

    fun canBuyObsidianRobot(blueprint: Blueprint): Boolean =
        ore >= blueprint.obsidianRobotOreCost && clay >= blueprint.obsidianRobotClayCost

    fun buyObsidianRobot(blueprint: Blueprint) = if (canBuyObsidianRobot(blueprint)) {
        Factory(
            ore = ore - blueprint.obsidianRobotOreCost + orePerMinute,
            clay = clay - blueprint.obsidianRobotClayCost + clayPerMinute,
            obsidian = obsidian + obsidianPerMinute,
            geode = geode + geodePerMinute,
            orePerMinute = orePerMinute,
            clayPerMinute = clayPerMinute,
            obsidianPerMinute = obsidianPerMinute + 1,
            geodePerMinute = geodePerMinute,
            boughtNothing = false,
        )
    } else {
        null
    }

    private fun canBuyGeodeRobot(blueprint: Blueprint): Boolean =
        ore >= blueprint.geodeRobotOreCost && obsidian >= blueprint.geodeRobotObsidianCost

    fun buyGeodeRobot(blueprint: Blueprint) = if (canBuyGeodeRobot(blueprint)) {
        Factory(
            ore = ore - blueprint.geodeRobotOreCost + orePerMinute,
            clay = clay + clayPerMinute,
            obsidian = obsidian - blueprint.geodeRobotObsidianCost + obsidianPerMinute,
            geode = geode + geodePerMinute,
            orePerMinute = orePerMinute,
            clayPerMinute = clayPerMinute,
            obsidianPerMinute = obsidianPerMinute,
            geodePerMinute = geodePerMinute + 1,
            boughtNothing = false,
        )
    } else {
        null
    }

    fun buyNothing() = Factory(
        ore = ore + orePerMinute,
        clay = clay + clayPerMinute,
        obsidian = obsidian + obsidianPerMinute,
        geode = geode + geodePerMinute,
        orePerMinute = orePerMinute,
        clayPerMinute = clayPerMinute,
        obsidianPerMinute = obsidianPerMinute,
        geodePerMinute = geodePerMinute,
        boughtNothing = true
    )
}

data class Blueprint(
    val id: Int,
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int
) {
    companion object {
        fun from(line: String): Blueprint {
            val values = Regex(pattern = "[0-9]+").findAll(line).map { it.value.toInt() }.toList()
            return Blueprint(values[0], values[1], values[2], values[3], values[4], values[5], values[6])
        }
    }
}

private const val testInput =
    """Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian."""

private const val realInput =
    """Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 17 clay. Each geode robot costs 4 ore and 20 obsidian.
Blueprint 2: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 3 ore and 17 clay. Each geode robot costs 3 ore and 8 obsidian.
Blueprint 3: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 7 clay. Each geode robot costs 4 ore and 13 obsidian.
Blueprint 4: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 10 clay. Each geode robot costs 3 ore and 14 obsidian.
Blueprint 5: Each ore robot costs 4 ore. Each clay robot costs 3 ore. Each obsidian robot costs 2 ore and 17 clay. Each geode robot costs 3 ore and 16 obsidian.
Blueprint 6: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 16 clay. Each geode robot costs 2 ore and 15 obsidian.
Blueprint 7: Each ore robot costs 2 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 15 clay. Each geode robot costs 2 ore and 15 obsidian.
Blueprint 8: Each ore robot costs 2 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 19 clay. Each geode robot costs 2 ore and 18 obsidian.
Blueprint 9: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 7 clay. Each geode robot costs 2 ore and 19 obsidian.
Blueprint 10: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 6 clay. Each geode robot costs 3 ore and 16 obsidian.
Blueprint 11: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 8 clay. Each geode robot costs 3 ore and 19 obsidian.
Blueprint 12: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 19 clay. Each geode robot costs 2 ore and 12 obsidian.
Blueprint 13: Each ore robot costs 4 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 4 ore and 17 obsidian.
Blueprint 14: Each ore robot costs 2 ore. Each clay robot costs 2 ore. Each obsidian robot costs 2 ore and 20 clay. Each geode robot costs 2 ore and 14 obsidian.
Blueprint 15: Each ore robot costs 2 ore. Each clay robot costs 2 ore. Each obsidian robot costs 2 ore and 10 clay. Each geode robot costs 2 ore and 11 obsidian.
Blueprint 16: Each ore robot costs 2 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 13 clay. Each geode robot costs 3 ore and 11 obsidian.
Blueprint 17: Each ore robot costs 4 ore. Each clay robot costs 3 ore. Each obsidian robot costs 2 ore and 19 clay. Each geode robot costs 3 ore and 10 obsidian.
Blueprint 18: Each ore robot costs 2 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 20 clay. Each geode robot costs 2 ore and 17 obsidian.
Blueprint 19: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 11 clay. Each geode robot costs 4 ore and 12 obsidian.
Blueprint 20: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 7 clay. Each geode robot costs 3 ore and 10 obsidian.
Blueprint 21: Each ore robot costs 3 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 13 clay. Each geode robot costs 3 ore and 7 obsidian.
Blueprint 22: Each ore robot costs 2 ore. Each clay robot costs 2 ore. Each obsidian robot costs 2 ore and 15 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 23: Each ore robot costs 3 ore. Each clay robot costs 3 ore. Each obsidian robot costs 2 ore and 20 clay. Each geode robot costs 3 ore and 18 obsidian.
Blueprint 24: Each ore robot costs 4 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 18 clay. Each geode robot costs 4 ore and 8 obsidian.
Blueprint 25: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 4 ore and 15 obsidian.
Blueprint 26: Each ore robot costs 4 ore. Each clay robot costs 3 ore. Each obsidian robot costs 2 ore and 20 clay. Each geode robot costs 3 ore and 9 obsidian.
Blueprint 27: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 5 clay. Each geode robot costs 3 ore and 7 obsidian.
Blueprint 28: Each ore robot costs 3 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 11 clay. Each geode robot costs 2 ore and 8 obsidian.
Blueprint 29: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 2 ore and 12 clay. Each geode robot costs 3 ore and 15 obsidian.
Blueprint 30: Each ore robot costs 4 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 10 clay. Each geode robot costs 3 ore and 10 obsidian."""