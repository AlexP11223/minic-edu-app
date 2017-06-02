
[![Build Status](https://travis-ci.org/AlexP11223/minic-edu-app.svg?branch=master)](https://travis-ci.org/AlexP11223/minic-edu-app)


Very basic IDE and vizualization/simulation of Mini-C compiler (https://github.com/AlexP11223/minic).

Allows to see output for each compilation phase, such as tokens, AST image, generated JVM bytecode.

Implemented in Kotlin, using JavaFX ([TornadoFX](https://github.com/edvin/tornadofx)) for GUI, [RichTextFX](https://github.com/TomasMikula/RichTextFX) text editor.

![](http://i.imgur.com/qbdGc8G.png)

![](http://i.imgur.com/aVnoQXE.png)

![](http://i.imgur.com/0Qcr0FV.png)
  
# How to build

Requirements:
- JDK 8+.
- Maven 3+.

Run Maven **package** phase. This will download all dependencies, run JUnit tests and build JAR file + native application/bundle (such as .exe for Windows). Check Maven output to see if all tests and build steps are completed successfully.

(Maven is included in popular Java IDEs such as IntelliJ Idea or Eclipse. You can run it either via your IDE Maven plugin or from command line in separate [Maven installation](https://maven.apache.org/install.html): `mvn package`.)
 
Some of the tests (as well the application itself) launch `java`, using path from `System.getProperty("java.home")`. Fallbacks to `java` (from PATH environment variable) if it is not found.
