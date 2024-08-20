package asubb.game.ecs.system

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import asubb.game.ecs.*
import asubb.game.ecs.component.Camera
import asubb.game.ecs.component.Motion
import asubb.game.ecs.component.Transform
import asubb.game.ecs.system.ActorMoveDirection.*
import asubb.game.ecs.types.*
import glm_.glm.sqrt
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import kotlin.math.max
import kotlin.math.min

class ActorSystemSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Actor system") {
        val world = World(EntityManager())
        val actorSystem = ActorSystem(world, speed = 1f, vertSensitivity = 1f, horizSensitivity = 1f)

        describe("Camera") {
            actorSystem.init()
            val actor = world.get<Transform>().single().first
            describe("Horizontal rotation mapping") {
                mapOf(
                    0.degrees to 0.degrees,
                    45.degrees to -45.degrees,
                    90.degrees to 270.degrees,
                    180.degrees to 180.degrees,
                ).forEach { (transformRotation, cameraRotation) ->
                    it("should map transformation=$transformRotation to camera=$cameraRotation") {
                        world.set(actor, Transform(position = vector(0, 0, 0), rotation = vector(0, transformRotation.radians, 0)))
                        actorSystem.update(TimeSpan(1000, 1))
                        world.assert {
                            get<Camera>().single().component().all {
                                position().isVectorWith(0, 0, 0)
                                viewHorizontalAngle().isAngle(cameraRotation)
                                viewVerticalAngle().isAngle(0.degrees)
                            }
                        }
                    }
                }
            }
            describe("Vertical rotation mapping") {
                mapOf(
                    -180.degrees to -180.degrees,
                    -90.degrees to -90.degrees,
                    0.degrees to 0.degrees,
                    45.degrees to 45.degrees,
                    90.degrees to 90.degrees,
                    180.degrees to 180.degrees,
                ).forEach { (transformRotation, cameraRotation) ->
                    it("should map transformation=$transformRotation to camera=$cameraRotation") {
                        world.set(actor, Transform(position = vector(0, 0, 0), rotation = vector(transformRotation.radians, 0, 0)))
                        actorSystem.update(TimeSpan(1000, 1))
                        world.assert {
                            get<Camera>().single().component().all {
                                position().isVectorWith(0, 0, 0)
                                viewHorizontalAngle().isAngle(0.degrees)
                                viewVerticalAngle().isAngle(cameraRotation)
                            }
                        }
                    }
                }
            }
        }
        describe("No actions") {
            actorSystem.init()
            it("should register Transform and no Motion, no Camera components on init()") {
                world.assert {
                    var entity: Entity = UndefinedEntity
                    get<Camera>().isEmpty()
                    get<Transform>().single().all {
                        entity = entity().takeValue()
                        component().all {
                            position().isVectorWith(0, 0, 0)
                            scale().isVectorWith(1, 1, 1)
                            rotation().isVectorWith(0, 0, 0)
                        }
                    }
                    get<Motion>(entity).isNull()
                }
            }
            it("should not update Actor components if no action is made but add Camera") {
                repeat(10) {
                    actorSystem.update(TimeSpan(1000L + it, 1))
                }

                world.assert {
                    var entity: Entity = UndefinedEntity
                    get<Camera>().single().all {
                        component().all {
                            position().isVectorWith(0, 0, 0)
                            viewHorizontalAngle().isAngle(0.degrees)
                            viewVerticalAngle().isAngle(0.degrees)
                        }
                        entity().isNotEqualTo(UndefinedEntity)
                        entity = entity().takeValue()
                    }
                    get<Transform>(entity).isNotNull().all {
                        position().isVectorWith(0, 0, 0)
                        scale().isVectorWith(1, 1, 1)
                        rotation().isVectorWith(0, 0, 0)
                    }
                    get<Motion>(entity).isNull()
                }
            }
        }
        describe("Moving actions") {
            actorSystem.init()
            val actor = world.get<Transform>().single().first
            // right triangle with sides `a * sqrt(3)`, `a` and `2*a` has angles 30, 60, 90
            // right triangle with sides `a`, `a` and `a * sqrt(2)` has angles 45, 45, 90
            describe("Moving forward") {
                mapOf(
                    0.degrees to vector(1, 0, 0),
                    30.degrees to vector(sqrt(3.0), 0, -1.0),
                    45.degrees to vector(1, 0, -1),
                    60.degrees to vector(1.0, 0, -sqrt(3.0)),
                    90.degrees to vector(0, 0, -1),
                    120.degrees to vector(-1.0, 0, -sqrt(3.0)),
                    135.degrees to vector(-1, 0, -1),
                    150.degrees to vector(-sqrt(3.0), 0, -1.0),
                    180.degrees to vector(-1, 0, 0),
                    210.degrees to vector(-sqrt(3.0), 0, 1.0),
                    225.degrees to vector(-1, 0, 1),
                    240.degrees to vector(-1.0, 0, sqrt(3.0)),
                    270.degrees to vector(0, 0, 1),
                    300.degrees to vector(1.0, 0, sqrt(3.0)),
                    315.degrees to vector(1, 0, 1),
                    330.degrees to vector(sqrt(3.0), 0, 1.0),
                    360.degrees to vector(1, 0, 0),
                ).forEach { (angle, direction) ->
                    it("should add motion with direction ${direction.normalize().asString()} on start() while oriented $angle") {
                        world.set(actor, Transform(rotation = vector(0, angle.radians, 0)))
                        actorSystem.newAction(ActorMoveStart(Forward))
                        world.assert {
                            get<Motion>(actor)
                                .isNotNull()
                                .direction().isVector(direction.normalize())
                        }
                    }

                }
            }
            describe("Moving backwards") {
                // right triangle with sides `a * sqrt(3)`, `a` and `2*a` has angles 30, 60, 90
                // right triangle with sides `a`, `a` and `a * sqrt(2)` has angles 45, 45, 90
                mapOf(
                    0.degrees to vector(1, 0, 0),
                    30.degrees to vector(sqrt(3.0), 0, -1.0),
                    45.degrees to vector(1, 0, -1),
                    60.degrees to vector(1.0, 0, -sqrt(3.0)),
                    90.degrees to vector(0, 0, -1),
                    120.degrees to vector(-1.0, 0, -sqrt(3.0)),
                    135.degrees to vector(-1, 0, -1),
                    150.degrees to vector(-sqrt(3.0), 0, -1.0),
                    180.degrees to vector(-1, 0, 0),
                    210.degrees to vector(-sqrt(3.0), 0, 1.0),
                    225.degrees to vector(-1, 0, 1),
                    240.degrees to vector(-1.0, 0, sqrt(3.0)),
                    270.degrees to vector(0, 0, 1),
                    300.degrees to vector(1.0, 0, sqrt(3.0)),
                    315.degrees to vector(1, 0, 1),
                    330.degrees to vector(sqrt(3.0), 0, 1.0),
                    360.degrees to vector(1, 0, 0),
                ).forEach { (angle, direction) ->
                    it("should add motion with direction ${direction.normalize().asString()} on start() while oriented $angle") {
                        world.set(actor, Transform(rotation = vector(0, angle.radians, 0)))
                        actorSystem.newAction(ActorMoveStart(Backward))
                        world.assert {
                            get<Motion>(actor)
                                .isNotNull()
                                // the same as forward but in the opposite direction
                                .direction().isVector(-direction.normalize())
                        }
                    }

                }
            }
            describe("Strafing left") {
                mapOf(
                    0.degrees to vector(0, 0, -1),
                    30.degrees to vector(-1.0, 0, -sqrt(3.0)),
                    45.degrees to vector(-1, 0, -1),
                    60.degrees to vector(-sqrt(3.0), 0, -1.0),
                    90.degrees to vector(-1, 0, 0),
                    120.degrees to vector(-sqrt(3.0), 0, 1.0),
                    135.degrees to vector(-1, 0, 1),
                    150.degrees to vector(-1.0, 0, sqrt(3.0)),
                    180.degrees to vector(0, 0, 1),
                    210.degrees to vector(1.0, 0, sqrt(3.0)),
                    225.degrees to vector(1, 0, 1),
                    240.degrees to vector(sqrt(3.0), 0, 1.0),
                    270.degrees to vector(1, 0, 0),
                    300.degrees to vector(sqrt(3.0), 0, -1.0),
                    315.degrees to vector(1, 0, -1),
                    330.degrees to vector(1.0, 0, -sqrt(3.0)),
                    360.degrees to vector(0, 0, -1),
                ).forEach { (angle, direction) ->
                    it("should add motion with direction ${direction.normalize().asString()} on start() while oriented $angle") {
                        world.set(actor, Transform(rotation = vector(0, angle.radians, 0)))
                        actorSystem.newAction(ActorMoveStart(Left))
                        world.assert {
                            get<Motion>(actor)
                                .isNotNull()
                                .direction().isVector(direction.normalize())
                        }
                    }

                }
            }
            describe("Strafing right") {
                mapOf(
                    0.degrees to vector(0, 0, -1),
                    30.degrees to vector(-1.0, 0, -sqrt(3.0)),
                    45.degrees to vector(-1, 0, -1),
                    60.degrees to vector(-sqrt(3.0), 0, -1.0),
                    90.degrees to vector(-1, 0, 0),
                    120.degrees to vector(-sqrt(3.0), 0, 1.0),
                    135.degrees to vector(-1, 0, 1),
                    150.degrees to vector(-1.0, 0, sqrt(3.0)),
                    180.degrees to vector(0, 0, 1),
                    210.degrees to vector(1.0, 0, sqrt(3.0)),
                    225.degrees to vector(1, 0, 1),
                    240.degrees to vector(sqrt(3.0), 0, 1.0),
                    270.degrees to vector(1, 0, 0),
                    300.degrees to vector(sqrt(3.0), 0, -1.0),
                    315.degrees to vector(1, 0, -1),
                    330.degrees to vector(1.0, 0, -sqrt(3.0)),
                    360.degrees to vector(0, 0, -1),
                ).forEach { (angle, direction) ->
                    it("should add motion with direction ${direction.normalize().asString()} on start() while oriented $angle") {
                        world.set(actor, Transform(rotation = vector(0, angle.radians, 0)))
                        actorSystem.newAction(ActorMoveStart(Right))
                        world.assert {
                            get<Motion>(actor)
                                .isNotNull()
                                // just the opposite of the left
                                .direction().isVector(-direction.normalize())
                        }
                    }

                }
            }
        }
        describe("View moving actions") {
            actorSystem.init()
            val actor = world.get<Transform>().single().first

            describe("Vertically") {
                mapOf(
                    10f to 10.degrees,
                    20f to 20.degrees,
                    40f to 40.degrees,
                    80f to 80.degrees,
                    89f to 89.degrees,
                    90f to 89.degrees,
                    100f to 89.degrees,
                    200f to 89.degrees,
                    -10f to -10.degrees,
                    -20f to -20.degrees,
                    -40f to -40.degrees,
                    -80f to -80.degrees,
                    -89f to -89.degrees,
                    -90f to -89.degrees,
                    -100f to -89.degrees,
                    -200f to -89.degrees,
                ).forEach { (delta, verticalAngle) ->
                    it("should set vertical angle to $verticalAngle with delta $delta") {
                        actorSystem.newAction(ActorViewMove(0f, delta))
                        actorSystem.update(TimeSpan(1000, 1))
                        world.assert {
                            get<Transform>(actor)
                                .isNotNull()
                                .rotation().isVector(vector(verticalAngle.radians, 0, 0))
                            world.assert {
                                get<Camera>(actor)
                                    .isNotNull()
                                    .viewVerticalAngle().isAngle(verticalAngle)
                            }
                        }
                    }
                }
                describe("Accumulative") {
                    it("should move the view up to a maximum") {
                        val delta = 10f
                        repeat(10) {
                            val verticalAngle = min(delta * (it + 1), 89f).degrees
                            actorSystem.newAction(ActorViewMove(0f, delta))
                            actorSystem.update(TimeSpan(1000L + it, 1L))
                            assertThat(world, "world[iteration=$it]").all {
                                get<Camera>(actor)
                                    .isNotNull()
                                    .viewVerticalAngle().isAngle(verticalAngle)
                            }
                        }
                    }
                    it("should move the view down to a minimum") {
                        val delta = -10f
                        repeat(10) {
                            val verticalAngle = max(delta * (it + 1), -89f).degrees
                            actorSystem.newAction(ActorViewMove(0f, delta))
                            actorSystem.update(TimeSpan(1000L + it, 1L))
                            assertThat(world, "world[iteration=$it]").all {
                                get<Camera>(actor)
                                    .isNotNull()
                                    .viewVerticalAngle().isAngle(verticalAngle)
                            }
                        }
                    }

                }
            }

            describe("Horizontally") {
                mapOf(
                    10f to 10.degrees,
                    20f to 20.degrees,
                    40f to 40.degrees,
                    80f to 80.degrees,
                    90f to 90.degrees,
                    100f to 100.degrees,
                    200f to 200.degrees,
                    300f to 300.degrees,
                    400f to 40.degrees,
                    -10f to 350.degrees,
                    -20f to 340.degrees,
                    -40f to 320.degrees,
                    -80f to 280.degrees,
                    -90f to 270.degrees,
                    -100f to 260.degrees,
                    -200f to 160.degrees,
                    -300f to 60.degrees,
                    -400f to 320.degrees,
                ).forEach { (delta, horizontalAngle) ->
                    it("should set horizontal angle to $horizontalAngle with delta $delta") {
                        actorSystem.newAction(ActorViewMove(delta, 0f))
                        actorSystem.update(TimeSpan(1000, 1))
                        world.assert {
                            get<Transform>(actor).isNotNull()
                                .rotation().isVector(vector(0, (-horizontalAngle).normalize().radians, 0))
                            get<Camera>(actor).isNotNull()
                                .viewHorizontalAngle().isAngle(horizontalAngle)
                        }
                    }
                }

                describe("Accumulative") {
                    it("should move the view right") {
                        val delta = 10f
                        repeat(100) {
                            val horizontalAngle = (delta * (it + 1)).degrees
                            actorSystem.newAction(ActorViewMove(delta, 0f))
                            actorSystem.update(TimeSpan(1000L + it, 1L))
                            assertThat(world, "world[iteration=$it]").all {
                                get<Camera>(actor)
                                    .isNotNull()
                                    .viewHorizontalAngle().isAngle(horizontalAngle)
                            }
                        }
                    }
                    it("should move the view left") {
                        val delta = 10f
                        repeat(100) {
                            val horizontalAngle = (-delta * (it + 1)).degrees
                            actorSystem.newAction(ActorViewMove(-delta, 0f))
                            actorSystem.update(TimeSpan(1000L + it, 1L))
                            assertThat(world, "world[iteration=$it]").all {
                                get<Camera>(actor)
                                    .isNotNull()
                                    .viewHorizontalAngle().isAngle(horizontalAngle)
                            }
                        }
                    }
                }
            }
        }
    }
})
