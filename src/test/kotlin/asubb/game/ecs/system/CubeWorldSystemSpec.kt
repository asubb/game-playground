package asubb.game.ecs.system

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import asubb.game.ecs.*
import asubb.game.ecs.component.Motion
import asubb.game.ecs.component.Render
import asubb.game.ecs.component.Transform
import asubb.game.ecs.component.render.CubeRender
import asubb.game.ecs.system.CubeWorldSystem
import asubb.game.ecs.types.*
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec

class CubeWorldSystemSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    val initialPosition = vector(0, 0, 0)
    val initialRotation = vector(0, 0, 0)
    val initialScale = vector(1, 1, 1)
    val expectedRotationMotion = vector(10, 0, 0)

    describe("A cube world system") {
        val world = World(EntityManager())
        val gameRandom = TestGameRandom()
        val system = CubeWorldSystem(world, listOf(initialPosition to initialScale), random = gameRandom)

        describe("Initialization") {
            system.init()

            it("should init Transform") {
                val cubes = world.get<Transform>()
                assertThat(cubes).all {
                    size().isEqualTo(1)
                    index(0).all {
                        prop(Pair<Entity, Transform>::second).all {
                            prop(Transform::position).isVector(initialPosition)
                            prop(Transform::rotation).isVector(initialRotation)
                            prop(Transform::scale).isVector(initialScale)
                        }
                    }
                }
            }

            it("should init Motion") {
                val cubes = world.get<Motion>()
                assertThat(cubes).all {
                    size().isEqualTo(1)
                    index(0).all {
                        prop(Pair<Entity, Motion>::second).all {
                            prop(Motion::rotation).isVector(expectedRotationMotion)
                            prop(Motion::direction).isVector(initialRotation)
                        }
                    }
                }
            }

            it("should init CubeRender") {
                val cubes = world.get<Render>()
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

        describe("Update cycle: no changes") {
            system.init()

            describe("1st iteration") {
                val timeSpan = TimeSpan(0, 1)
                system.update(timeSpan)

                it("should keep Transform the same") {
                    val cubes = world.get<Transform>()
                    assertThat(cubes, "cubes").all {
                        size().isEqualTo(1)
                        index(0).all {
                            component().all {
                                prop(Transform::rotation).isVector(initialRotation)
                                prop(Transform::position).isVector(initialPosition)
                            }
                        }
                    }
                }
            }

            describe("2nd iteration") {
                system.update(TimeSpan(0, 1))
                system.update(TimeSpan(1, 1))

                it("should keep Transform the same") {
                    val cubes = world.get<Transform>()
                    assertThat(cubes, "cubes").all {
                        size().isEqualTo(1)
                        index(0).all {
                            component().all {
                                prop(Transform::rotation).isVector(initialRotation)
                                prop(Transform::position).isVector(initialPosition)
                            }
                        }
                    }
                }

            }
        }
    }
})

