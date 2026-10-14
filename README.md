# Slime and Soil

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and a main class extending `Game` that sets the first screen.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

## 🚀 How to Setup and Run the Game Locally

To work on this project or test the game on your own machine, you must open it as a Gradle project in IntelliJ IDEA. You can do this by either cloning the repository directly or downloading the ZIP file.

### Option A: Cloning directly via IntelliJ (Recommended)
1. Open IntelliJ IDEA.
2. On the welcome screen, click **Get from VCS** (or go to `File` > `New` > `Project from Version Control` in the top menu).
3. Paste the repository URL: `https://github.com/nitipats993-cmyk/Slime-Soil_game.git`
4. Click **Clone**.
5. Once opened, IntelliJ will automatically detect the `build.gradle` file and begin downloading dependencies. Wait for the background tasks (bottom right corner) to finish syncing.

### Option B: Downloading the ZIP file
1. Click the green **<> Code** button at the top of this GitHub repository and select **Download ZIP**.
2. Extract the downloaded ZIP file to a permanent folder on your computer.
3. Open IntelliJ IDEA and click **Open**.
4. Navigate to the extracted folder and select the `build.gradle` file (or the root `Slime-Soil_game` folder). 
5. If prompted, choose **Open as Project** and click **Trust Project**.
6. Wait for Gradle to finish syncing all project dependencies before trying to run the code.

### 🎮 How to Launch the Game
Once the Gradle sync is complete, follow these steps to play:
1. In the Project tool window on the left side of IntelliJ, navigate to this exact path: 
   `lwjgl3` > `src` > `main` > `java` > `com.yourname.slimeandsoil.lwjgl3`
2. Open the **`Lwjgl3Launcher.java`** file.
3. Click the green **Play** icon in the left margin next to the `main` method (or next to `public class Lwjgl3Launcher`).
4. Select **Run 'Lwjgl3Launcher.main()'**.

> **Note on "Missing" Folders:** If you browse the repository files here on GitHub, you will notice that folders like `.idea/`, `build/`, and `.gradle/` are not listed. This is intentional. The `.gitignore` file hides these local cache files to prevent merge conflicts across different computers. When you open the project in IntelliJ and let Gradle sync, your computer will automatically generate its own local versions of these folders.
