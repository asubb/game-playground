package asubb.game.ecs

import asubb.game.ecs.types.Vector
import asubb.game.ecs.types.vector
import kotlin.random.Random

interface GameRandom {

    companion object {
        val default = GameRandomImpl()
    }

    fun withProbability(probability: Double, body: () -> Unit)

    fun nextVector(a: ClosedRange<Double> = 0.0..0.0): Vector = nextVector(a, a, a)

    fun nextVector(
        x: ClosedRange<Double> = 0.0..0.0,
        y: ClosedRange<Double> = 0.0..0.0,
        z: ClosedRange<Double> = 0.0..0.0,
    ): Vector

    fun <T> nextElement(a: List<T>): Pair<Int, T>
}

class GameRandomImpl(
    private val rnd: Random = Random.Default,
) : GameRandom {

    override fun withProbability(probability: Double, body: () -> Unit) {
        if (rnd.nextDouble() > 1.0 - probability) body()
    }

    override fun nextVector(a: ClosedRange<Double>): Vector = nextVector(a, a, a)

    override fun nextVector(
        x: ClosedRange<Double>,
        y: ClosedRange<Double>,
        z: ClosedRange<Double>,
    ): Vector = vector(
        rnd.nextDouble(x.start, x.endInclusive),
        rnd.nextDouble(y.start, y.endInclusive),
        rnd.nextDouble(z.start, z.endInclusive)
    )

    override fun <T> nextElement(a: List<T>): Pair<Int, T> {
        val size = a.size
        require(size > 0) { "Specified list is empty" }
        val idx = rnd.nextInt(size)
        return idx to a[idx]
    }
}