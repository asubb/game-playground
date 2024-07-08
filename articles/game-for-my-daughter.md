# The Game for my daughter

One morning my daughter came by and asked: "Can we make a game?". She's almost 9 years old and enjoy video games the same as I do. But I didn't try to develop any game for like 15 years? And also have all these other things that comes along with adult-hood.

While giving it another thought, it comes to me, that might be fun. What we're going to develop? No idea. I know what I do enjoy playing myself, and I know what she enjoys playing. Can we come up with something that we both can enjoy? Probably. The only thing I know for sure we can try to build it together, I'm coding, she's throwing in these childish crazy ideas that the grown man can only smile upon, but then actually does. It will take probably forever, but who cares? The path is the most entertaining part of the journey. And she might learn a thing or two.

As I'm still Kotlin fan and enthusiast, the thing that I'm pretty confident about is that I want to use Kotlin mostly and from the very beginning. It should be fully multiplatform as I never really got involved in it deeply. And it should be a 3D game, hence OpenGL.

Looking around for the solutions, which are plenty on the surface:
* [korge](https://korge.org/)
* [libGDX](https://libgdx.com/)
* [kool](https://github.com/fabmax/kool)
* [LittleKt](https://littlekt.com/)
* And probably even more.

But some of the frameworks while being fully multiplatform lacking 3D support (LittleKt), or it's rather on the very early stages like Korge. Some of them do not support all platforms like kool -- though looks the most promising, or locked in with JVM -- libGDX.

Even though, I'd rather use some existing framework, currently I'm lacking significant OpenGL knowledge, and better focus on the basics first. It's been so long since I last used it, it goes back to the time when the "shader" was a word that meant something unreachable due to lack of such things on your Video Card, but with modern versions of OpenGL you shouldn't even attempt to draw a simple 3-color triangle without them.

So here we are at the very beginning, trying to remember how to draw simple triangle. And perhaps a little more.

The first question is how do I use OpenGL from Kotlin? Java bindings to C-library AKA [jogl or jogamp as of now](https://jogamp.org/)? That is JVM only. Checking around the internet hit upon the project [kgl](https://github.com/gergelydaniel/kgl) which provides OpenGL bindings to most of the platforms while being in very early stages, good start. Forked, built, it works!

The next question is how do I learn things? For the start I'll stick with JVM as it seems to have better coverage for the topic, at least I won't have so many unknowns. Most of the information about OpenGL is for C/C++, thus I need to learn how to search for information and read C-code and map it to Kotlin. 

Wvhile browsing over the net, one website stands out [learnopengl.com](https://learnopengl.com/). It looks very well-structured, have covered a variety of different topics and have links to the source code. And that is not only about the code, but building an important understanding what is going on behind those lines. I'm pretty sure I will go over the articles over and over again to refresh certain aspects when I actually need them.

Using kgl seems to shine here as it doesn't create a new API and require additional documentation, it can just refer to existing one. The difference seems to be cosmetic (assuming `vertices` is defined appropriately):

* C-code:
```cpp
unsigned int VAO;
glGenVertexArrays(1, &VAO);  
glBindVertexArray(VAO);
unsigned int VBO;
glGenBuffers(1, &VBO);  
glBindBuffer(GL_ARRAY_BUFFER, VBO);
glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
glEnableVertexAttribArray(0);  
```
*  Kotlin-code:
```kotlin
with(gl) {
    val vao = createVertexArray()
    bindVertexArray(vao)
    val vbo = createBuffer()
    bindBuffer(GL.GL_ARRAY_BUFFER, vbo)
    bufferData(GL.GL_ARRAY_BUFFER, vertices, Buffers.SIZEOF_FLOAT * 9, GL.GL_STATIC_DRAW)
    vertexAttribPointer(0, 3, GL.GL_FLOAT, false, Buffers.SIZEOF_FLOAT * 3, 0)
    enableVertexAttribArray(0)
}
```

Such code doesn't look very much like Kotlin, but I believe I can abstract this code away enough and keep it isolated to a certain places.

The biggest difference between the tutorial and what I need to do is how do you create a canvas to draw in and listen to key/mouse/touch events what's so ever. That part is very platform specific. As I'm currently on JVM (less moving parts for now, remember?), jogl may have some tutorials that I can take an inspiration from. 
Jogl wiki refers to multiple tutorials but there is one written on Java and Kotlin and is easy to read, it even follows the same articles for it to be even better [Hello triangle](https://github.com/jvm-graphics-labs/hello-triangle).

So, currently it seems I have all the pieces to the puzzle, and can finish "Quick Start" tutorials. And,... it turns out that is not quite true.

While working on "Transformations" there is another library being used to implement vectors and matrices math on CPU -- `glm`. And while I did search there are multiple JVM based like these:
1. Java native [JOML](https://github.com/JOML-CI/JOML)
2. Kotlin port of [glm](https://github.com/java-graphics/glm)

While JVM locked libraries is not what I'm looking for, Kotlin port of glm while currently is working on JVM, it has also work in progress for multiplatform support. Quite promising, though it's been in works for quite a while. I did try to revive it locally and see if I can make it work, but that tuned out to be not an easy job at this point. The decision was made to use it for now as I stick with JVM, but later on address this issue by either duplicating required functionality within my own application, or spend more time trying to figure out how to move the multiplatform support for the library forward.

And that is all what was required for me to finish with the tutorials. You can find [my attempts on the GitHub](https://github.com/asubb/game-playground/tree/game-for-my-daughter).

Current state of things for Kotlin Multiplatform game development seems to be at the very early stages, while being interesting as an idea -- write everything on one language, it is lacking robust and wide community, the information is quite scarce as well as the choice of the libraries. But it is definitely has already something to start with.

As they say, the harder the journey, the better the story, right?