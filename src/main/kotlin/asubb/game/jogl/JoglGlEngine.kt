package asubb.game.jogl

import asubb.game.Scene
import com.danielgergely.kgl.DebugKgl
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.KglJogl
import com.jogamp.newt.event.*
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*
import com.jogamp.opengl.util.Animator

class JoglGlEngine(
    title: String,
    private val scene: Scene,
    private val debug: Boolean = false,
) : GLEventListener, KeyListener, MouseListener {

    private val DEBUG = false
    private val window: GLWindow
    private val animator: Animator

    init {
        val glProfile = GLProfile.get(GLProfile.GL3)
        val glCapabilities = GLCapabilities(glProfile)

        window = GLWindow.create(glCapabilities)

        if (DEBUG) {
            window.setContextCreationFlags(GLContext.CTX_OPTION_DEBUG)
        }

        window.isUndecorated = false
        window.isAlwaysOnTop = false
        window.setFullscreen(false)
        window.isPointerVisible = true
        window.confinePointer(false)
        window.title = title
        window.setSize(800, 600)

        window.setVisible(true)

        window.addGLEventListener(this)
        window.addKeyListener(this)
        window.addMouseListener(this)

        animator = Animator()
        animator.add(window)
        animator.start()

        window.addWindowListener(object : WindowAdapter() {
            override fun windowDestroyed(e: WindowEvent?) {
                Thread { //stop the animator thread when user close the window
                    animator.stop()
                    // This is actually redundant since the JVM will terminate when all threads are closed.
                    // It's useful just in case you create a thread and you forget to stop it.
                    System.exit(1)
                }.start()
            }
        })
    }

    private fun kglJogl(drawable: GLAutoDrawable): Kgl {
        val gl = drawable.gl.gL3
        val glu = com.jogamp.opengl.glu.GLU.createGLU(gl)
        return if (debug) {
            DebugKgl(KglJogl(gl),
                {
                },
                {
                    val error = gl.glGetError()
                    if (error != 0) {
                        val stackTrace = Thread.currentThread().stackTrace.toList()
                        println("[ERROR] $it -> [$error]${glu.gluErrorString(error)}: \n${stackTrace.joinToString("\n")}")
                    }
                }
            )
        } else {
            KglJogl(gl)
        }
    }

    override fun init(drawable: GLAutoDrawable) {
        scene.init(kglJogl(drawable), JoglTextureIO(drawable.gl))
    }

    override fun display(drawable: GLAutoDrawable) {
        scene.display(kglJogl(drawable))
    }

    override fun reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int) {
        scene.reshape(kglJogl(drawable), width, height)
    }

    override fun dispose(drawable: GLAutoDrawable) {
        scene.end(kglJogl(drawable))
    }

    override fun keyPressed(e: KeyEvent) {
        scene.keyPressed(e.keyCode)
    }

    override fun keyReleased(e: KeyEvent) {
    }

    override fun mouseClicked(e: MouseEvent) {
    }

    override fun mouseEntered(e: MouseEvent) {
    }

    override fun mouseExited(e: MouseEvent) {
    }

    override fun mousePressed(e: MouseEvent) {
    }

    override fun mouseReleased(e: MouseEvent) {
    }

    override fun mouseMoved(e: MouseEvent) {
        scene.mouseMoved(e.x, e.y)
    }

    override fun mouseDragged(e: MouseEvent) {
    }

    override fun mouseWheelMoved(e: MouseEvent) {
        scene.mouseWheelMoved(e.rotation[0], e.rotation[1])
    }

    protected fun quit() {
        Thread { window.destroy() }.start()
    }
}