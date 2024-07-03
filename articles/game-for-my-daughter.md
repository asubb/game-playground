# Game for my daughter

One morning my daughter came by and asked: "Can we make a game?". She's almost 9 years old and enjoy video games the same as I do. But I didn't try to develop any game for like 15 years? And also have all these other things that comes along with adult-hood.

While giving it another thought, it comes to me, that might be fun. What we're going to develop? No idea. I know what I do enjoy playing myself, and I know what she enjoys playing. Can we come up with something that we both can enjoy? Probably. The only thing I know for sure we can try to build it together, I'm coding, she's throwing in these childish crazy ideas that the grown man can only smile upon, but then actually does. It will take probably forever, but who cares? The path is the most entertaining part of the journey. And she might learn a thing or two.

As I'm still Kotlin fan and enthusiast, the thing that I'm pretty confident about is that I want to use Kotlin mostly and from the very beginning. It should be fully multiplatform as I never really got involved in it deeply. And it should be a 3D game, hence OpenGL.

Looking around for the solutions, which are plenty on the surface:
* [korge](https://korge.org/)
* [libGDX](https://libgdx.com/)
* [kool](https://github.com/fabmax/kool)
* [LittleKt](https://littlekt.com/)
* And probably even more.

But some of the frameworks while being fully multiplatform lacking 3D support (LittleKt), or it's rather on the very early stages like Korge. Some of them do not support all platforms (like kool), or locked in with JVM -- libGDX.

Even though, I'd rather use some existing framework, currently I'm lacking OpenGL knowledge, and better focus on the basics first. It's been so long since I last used it, it goes back to the time when the "shader" was a word that meant something unreachable due to lack of such things on our Video Cards, but with modern versions of OpenGL you shouldn't even attempt to draw a simple 3-color triangle without them.

So here we are at the very beginning, trying to remember how to draw simple triangle. And perhaps a little more.

The first question is how do I use OpenGL from Kotlin? Java bindings to C-library AKA [jogl or jogamp as of now](https://jogamp.org/)? That is JVM only. Checking around the internet hit upon the project [kgl](https://github.com/gergelydaniel/kgl) which provides OpenGL bindings to most of the platforms, good start. Forked, built, it works!

The next question is how do I learn things? For the start I'll stick with JVM as it seems to have better coverage for the topic, at least I won't have so many unknowns.