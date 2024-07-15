package asubb.game.ecs

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec

class CubeWorldSystemSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    val initialPosition = vector(0, 0, 0)
    val initialScale = vector(1, 1, 1)
    val expectedRotationMotion = vector(10, 0, 0)

    describe("A cube world system") {
        val state = State(EntityManager())
        val system = CubeWorldSystem(state, listOf(initialPosition to initialScale))

        describe("Initialization") {
            system.init()

            it("should init Transform") {
                val cubes = state.get<Transform>()
                assertThat(cubes).all {
                    size().isEqualTo(1)
                    index(0).all {
                        prop(Pair<Entity, Transform>::second).all {
                            prop(Transform::position).isVector(initialPosition)
                            prop(Transform::rotation).isVectorOf(0, 0, 0)
                            prop(Transform::scale).isVector(initialScale)
                        }
                    }
                }
            }

            it("should init Motion") {
                val cubes = state.get<Motion>()
                assertThat(cubes).all {
                    size().isEqualTo(1)
                    index(0).all {
                        prop(Pair<Entity, Motion>::second).all {
                            prop(Motion::rotation).isVector(expectedRotationMotion)
                            prop(Motion::direction).isVectorOf(0, 0, 0)
                        }
                    }
                }
            }

            it("should init CubeRender") {
                val cubes = state.get<Render>()
                assertThat(cubes).all {
                    size().isEqualTo(1)
                    index(0).all {
                        prop(Pair<Entity, Render>::second)
                            .isInstanceOf(CubeRender::class)
                            .prop(CubeRender::initialized).isEqualTo(false)
                    }
                }
            }
        }

        describe("Update cycle") {
            system.init()

            describe("1st iteration") {
                val timeSpan = TimeSpan(0, 1)
                system.update(timeSpan)

                it("should update Transform") {
                    val cubes = state.get<Transform>()
                    assertThat(cubes).all {
                        size().isEqualTo(1)
                        index(0).all {
                            prop(Pair<Entity, Transform>::second).all {
                                prop(Transform::rotation).isVectorOf(10 * 0.001, 0, 0)
                                prop(Transform::position).isVector(initialPosition)
                            }
                        }
                    }
                }
            }

            describe("2nd iteration") {
                system.update(TimeSpan(0, 1))
                system.update(TimeSpan(1, 1))

                it("should update Transform") {
                    val cubes = state.get<Transform>()
                    assertThat(cubes).all {
                        size().isEqualTo(1)
                        index(0).all {
                            prop(Pair<Entity, Transform>::second).all {
                                prop(Transform::rotation).isVectorOf(10 * 0.001 * 2, 0, 0)
                                prop(Transform::position).isVector(initialPosition)
                            }
                        }
                    }
                }

            }
        }
    }
})

fun Assert<Vector>.isVectorOf(x: Number? = null, y: Number? = null, z: Number? = null) {
    all {
        x?.let { prop(Vector::x).isEqualTo(it.toFloat()) }
        y?.let { prop(Vector::y).isEqualTo(it.toFloat()) }
        z?.let { prop(Vector::z).isEqualTo(it.toFloat()) }
    }
}

fun Assert<Vector>.isVector(v: Vector) {
    all {
        prop(Vector::x).isEqualTo(v.x)
        prop(Vector::y).isEqualTo(v.y)
        prop(Vector::z).isEqualTo(v.z)
    }
}
