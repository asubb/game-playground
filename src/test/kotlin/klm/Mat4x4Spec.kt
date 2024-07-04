package klm

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import io.kotest.core.spec.style.DescribeSpec

class Mat4x4Spec : DescribeSpec({
    describe("A 4x4 matrix") {
        it("should create an identity matrix") {
            assertThat(Matrix4x4.identity()).all {
                isEqualTo(
                    Matrix4x4(
                        floatArrayOf(
                            1f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f,
                            0f, 0f, 1f, 0f,
                            0f, 0f, 0f, 1f
                        )
                    )
                )
            }
        }
        describe("Transformation") {
            it("should transform the vector") {
                val vec4 = Vec4(1.0f, 0.0f, 0.0f, 1.0f)
                var trans = Mat4(1.0f)
                trans = glm.translate(trans, Vec3(1.0f, 1.0f, 0.0f))
                val vec = trans * vec4
                assertThat(vec).all {
                    prop(Vec4::x).isEqualTo(2.0f)
                    prop(Vec4::y).isEqualTo(1.0f)
                    prop(Vec4::z).isEqualTo(0.0f)
                    prop(Vec4::w).isEqualTo(1.0f)
                }
            }
            it("should rotate the vector") {
                var trans = Mat4(1.0f);
                trans = glm.rotate(trans, glm.radians(90.0f), Vec3(0.0, 0.0, 1.0));
                trans = glm.scale(trans, Vec3(0.5, 0.5, 0.5));
            }
        }
    }
})