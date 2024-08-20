package asubb.game.ecs.types

import assertk.assertThat
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import asubb.game.ecs.isAngle
import io.kotest.core.spec.style.DescribeSpec

class AngleSpec : DescribeSpec({
    describe("Angle") {
        describe("equality") {
            it("should wrap around on 360") {
                assertThat(0.degrees).isAngle(360.degrees)
            }
            it("should wrap the negative around on 360") {
                assertThat(-90.degrees).isAngle(270.degrees)
            }

            val a = 90.degrees
            context("'$a'") {
                it("should be equal to 90") {
                    assertThat(a).isAngle(90.degrees)
                }
                it("should be equal to -270") {
                    assertThat(a).isAngle(-270.degrees)
                }
            }
            val b = -90.degrees
            context("'$b'") {
                it("should be equal to 90") {
                    assertThat(b).isAngle(-90.degrees)
                }
                it("should be equal to -270") {
                    assertThat(b).isAngle(270.degrees)
                }
            }
        }
        describe("comparison happens within the normalized range [0,360)") {
            val a = 90.degrees
            context("'$a'") {
                it("should be greater than 0") {
                    assertThat(a).isGreaterThan(0.degrees)
                }
                it("should be greater than 45") {
                    assertThat(a).isGreaterThan(45.degrees)
                }
                it("should be less than 120") {
                    assertThat(a).isLessThan(120.degrees)
                }
                it("should be less that 180") {
                    assertThat(a).isLessThan(180.degrees)
                }
            }
            val b = -90.degrees // that is 270
            context("'$b'") {
                it("should be greater than 0") {
                    assertThat(b).isGreaterThan(0.degrees)
                }
                it("should be greater than 45") {
                    assertThat(b).isGreaterThan(45.degrees)
                }
                it("should be greater than 120") {
                    assertThat(b).isGreaterThan(120.degrees)
                }
                it("should be greater than 180") {
                    assertThat(b).isGreaterThan(180.degrees)
                }
                it("should be less than 300") {
                    assertThat(b).isLessThan(300.degrees)
                }
            }
        }
    }
})