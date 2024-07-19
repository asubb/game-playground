Game Playground
====================

Motto: Think first, try then, and only after implement.

Project to verify a few things around game development. Along with it writing blog posts with thoughts 
and findings.

Structure of the repository:
1. The `main` branch will always contain the latest state of the mind.
2. The specific tag will contain the state of things and related article. Follow [history](#history)
for the chronological order.

The `main` against specific tag may have a tremendous amount of difference and may change the whole 
picture and be completely incompatible. Tags are not releases in a way, just a snapshots of evolution.

Description of the state
--------------------

Related article: [Game for my Daughter](articles/game-for-my-daughter.md)

![Hello, triangles!](assets/triangles.png)

1. The entry point is [Game.kt](src/main/kotlin/asubb/game/Game.kt), it launches the 
[Scene.kt](src/main/kotlin/asubb/game/Scene.kt).
2. The scenes are hardcoded, and it is required to uncomment the specific one to launch it.
3. The scenes are following the articles on [learnopengl.com](https://learnopengl.com/)
 
| Scene source file                                               | Following articles                                                                                                                             |
|-----------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| [HelloTriangle.kt](src/main/kotlin/asubb/game/HelloTriangle.kt) | [Hello triangle](https://learnopengl.com/Getting-started/Hello-Triangle) <br/> [Shaders](https://learnopengl.com/Getting-started/Shaders)      |
| [TexRect.kt](src/main/kotlin/asubb/game/TexRect.kt)             | [Textures](https://learnopengl.com/Getting-started/Textures) <br/> [Transformations](https://learnopengl.com/Getting-started/Transformations)  |
| [CoordSystem.kt](src/main/kotlin/asubb/game/CoordSystem.kt)     | [Coordinate System](https://learnopengl.com/Getting-started/Coordinate-Systems) <br/> [Camera](https://learnopengl.com/Getting-started/Camera) |

History
--------------------

* [Game for my Daughter](articles/game-for-my-daughter.md): [`game-for-my-daughter` tag](https://github.com/asubb/game-playground/tree/game-for-my-daughter)

Building and running
--------------------

Project relies on JDK 21.

Build [dependencies](#dependencies) and then to run:

```shell
./gradlew run
```

Dependencies
--------------------

Currently, I have to build dependencies locally from forked repositories:

1. [glm](https://github.com/asubb/glm/tree/fix1) follow the tag `fix1`
2. [kgl](https://github.com/asubb/kgl/tree/fix1) follow the tag `fix1`  

To build from the respective project directory:

```shell
./gradlew publishToMavenLocal
```