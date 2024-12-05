package asubb.game.ecs

import asubb.game.ecs.types.Vector

open class TestGameRandom: GameRandom {
    override fun withProbability(probability: Double, body: () -> Unit) {
        // never invoke the body
    }

    override fun nextVector(x: ClosedRange<Double>, y: ClosedRange<Double>, z: ClosedRange<Double>): Vector {
        throw IllegalStateException("Can't be called")
    }

    override fun <T> nextElement(a: List<T>): Pair<Int, T> {
        throw IllegalStateException("Can't be called")
    }
}