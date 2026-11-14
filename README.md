# BlueJ Telemetry Plugin

A Java extension for [BlueJ](https://www.bluej.org/) developed for **COMP3607 - Object-Oriented Programming II**.

The plugin monitors selected coding activities in BlueJ and automatically generates JSON and PDF reports when a monitored project is closed. These reports provide information about compilation attempts, editing activity, class modifications, execution results, and estimated coding time.

## Features

- Tracks class creation, deletion, and modification events.
- Records editor interactions, including text insertion, deletion, and replacement.
- Tracks user-initiated compilation attempts, success rates, and time intervals between compilations.
- Records compilation error messages and affected line numbers.
- Tracks method execution results and detects abnormal execution or runtime exceptions.
- Estimates active coding and idle time.
- Generates timestamped JSON reports containing recorded telemetry events.
- Generates PDF reports containing session metrics, an active-versus-idle chart, a compilation-error frequency table, and a class-creation timeline.
- Allows students to enter their Student ID through BlueJ's Tools menu.
- Automatically saves JSON and PDF reports inside the monitored BlueJ project directory.

## Project Documentation

For detailed technical documentation, including explanations of the Java classes, methods, system architecture, design patterns, and implementation details, visit our GitHub Wiki.

**[View Project Wiki](https://github.com/nirv-exe/bluej-telemetry-plugin/wiki)**

This README focuses on installing, configuring, running, and testing the extension.

## Prerequisites

Before building and installing the extension, ensure that the following software is installed:

- **Java Development Kit (JDK) 21** — Used to compile the project.
- **Apache Maven 3.x** — Used to manage dependencies, compile the code, and run tests.
- **BlueJ 5.0.3 or later** — Required to run the extension and support the editor-listener API.
- **Git** — Optional, for cloning the repository.

Verify that Java and Maven are installed by running:

```bash
java -version
javac -version
mvn -version
```

If these commands execute successfully, the development environment is ready.

## Installation and Setup

### Step 1 - Clone the Repository

Open a terminal or Command Prompt and run:

```bash
git clone https://github.com/nirv-exe/bluej-telemetry-plugin.git
cd bluej-telemetry-plugin
```

Alternatively, download the repository directly from GitHub using **Code → Download ZIP**, extract it, and open a terminal in the extracted project directory.

All Maven commands must be executed from the directory containing `pom.xml`.

### Step 2 - Build the Extension

Run:

```bash
mvn package
```

This command will:

1. Compile the Java source files.
2. Run the JUnit tests.
3. Package the extension and its required libraries into a JAR file.

**Important:** If there are errors and Maven reports that it cannot resolve the dependancies, see [Step 3](#step-3---resolve-bluej-dependency-issues-if-necessary)

After a successful build, the generated JAR can be found at:

```text
target/bluej-telemetry-plugin-1.0-SNAPSHOT.jar
```

**Important:** Use `bluej-telemetry-plugin-1.0-SNAPSHOT.jar`, not `original-bluej-telemetry-plugin-1.0-SNAPSHOT.jar`.

The Maven Shade plugin packages the required Gson and PDFBox dependencies into the extension.

The JAR is loaded by BlueJ and should not be executed using `java -jar`.

### Step 3 - Resolve BlueJ Dependency Issues (If Necessary)

If Maven reports that it cannot resolve:

```text
org.bluej:bluej-ext:5.0
```

The repository includes a BlueJ JAR inside the `lib` directory.

Run the following command from the project root:

```bash
mvn install:install-file -Dfile=lib/bluej.jar -DgroupId=org.bluej -DartifactId=bluej-ext -Dversion=5.0 -Dpackaging=jar
```

This installs the BlueJ dependency into your local Maven repository.

Afterward, run:

```bash
mvn package
```

This step is only necessary if Maven cannot resolve the dependency automatically.

### Step 4 - Install the Extension in BlueJ

Close BlueJ before installing the extension.

Locate the generated JAR:

```text
target/bluej-telemetry-plugin-1.0-SNAPSHOT.jar
```

Copy it into the appropriate BlueJ extensions directory.

**Windows:**

```text
%USERPROFILE%\bluej\extensions2
```

**macOS:**

```text
~/Library/Preferences/org.bluej/extensions2
```

**Linux:**

```text
~/.bluej/extensions2
```

Create the `extensions2` directory if it does not already exist.

On Windows, you can create the folder using:

```cmd
mkdir "%USERPROFILE%\bluej\extensions2"
```

Then copy the JAR:

```cmd
copy /Y "target\bluej-telemetry-plugin-1.0-SNAPSHOT.jar" "%USERPROFILE%\bluej\extensions2\"
```

Alternatively, to install the extension for only one BlueJ project, place the JAR inside:

```text
<BlueJ Project Directory>/extensions2
```

For more information, see the [official BlueJ extension installation guide](https://www.bluej.org/extensions/extensions2.html).

### Step 5 - Verify Installation

1. Launch BlueJ.
2. Open a BlueJ project.
3. Navigate to **Help → Installed Extensions**.
4. Verify that **BlueJ Telemetry Plugin** appears in the list.

If the plugin appears, it has been recognized by BlueJ.

## Configuring the Student ID

Before recording a session, students should configure their Student ID.

1. Open a project in BlueJ.
2. Navigate to **Tools → Set Student ID**.
3. Enter the Student ID in the dialog box.
4. Click **OK**.

The ID is stored in BlueJ's extension settings and is remembered between application restarts.

Students can change their ID at any time by selecting **Tools → Set Student ID** again.

The Student ID is included in the exported JSON report. If no ID has been configured, the default value is `STUDENT_PLACEHOLDER`.

## Using the Extension

Once installed, the extension operates automatically in BlueJ. No additional terminal commands are required.

It monitors supported activities such as editing Java classes, compiling code, creating and modifying classes, and invoking methods.

### Example Test

Create a new BlueJ project and add a Java class named `Calculator`:

```java
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int divide(int a, int b) {
        return a / b;
    }
}
```

Perform the following actions:

1. Compile the class successfully.
2. Remove the semicolon from `return a + b;` and compile again.
3. Restore the semicolon and compile successfully.
4. Create a `Calculator` object in BlueJ.
5. Invoke `add(2, 3)` to produce the result `5`.
6. Invoke `divide(10, 0)` to trigger a runtime exception.
7. Close the BlueJ project using **Project → Close**.

The extension records the available events during the session.

When BlueJ sends its package-closing notification, the plugin automatically generates the telemetry reports.

No manual export button is required.

## Generated Reports

The extension generates two reports, which are saved inside the monitored BlueJ project directory.

### JSON Report

The JSON report contains:

- Student ID and project name.
- Estimated session duration.
- Active coding and idle-time estimates.
- Total compilation attempts and success rate.
- Time intervals between compilations.
- Timestamped telemetry events.
- Compilation diagnostics, including error messages and line numbers.
- Available execution information.

Example:

```json
{
  "studentId": "00123456",
  "projectName": "Calculator",
  "totalSessionDurationSeconds": 68,
  "activeCodingTimeSeconds": 21,
  "idleTimeSeconds": 0,
  "totalCompilationAttempts": 3,
  "compileSuccessRatePercent": 66.67,
  "compilationIntervalsSeconds": [9, 6]
}
```

This example shows selected report fields. Actual reports also contain the recorded event history.

### PDF Report

The PDF provides a visual summary of the coding session.

**Page 1 - Session Summary**

- Total events recorded.
- Session duration and estimated active/idle times.
- Compilation attempts and success rate.
- Active-versus-idle coding activity chart.
- Compilation-error frequency table.

**Page 2 - Class Creation Timeline**

- Timestamps of recorded class creation events.
- A visual timeline showing when classes were created.

The PDF is limited to a maximum of two pages.

### Report Filenames

Both reports use the following naming convention:

```text
<ProjectName>_yyyyMMdd_HHmmss_SSS_Telemetry.json
<ProjectName>_yyyyMMdd_HHmmss_SSS_Telemetry.pdf
```

For example:

```text
Calculator_20261113_215146_257_Telemetry.json
Calculator_20261113_215146_257_Telemetry.pdf
```

The timestamp allows reports from different sessions to be distinguished.

After both reports export successfully, the collected telemetry events are cleared from temporary memory.

**Note:** Reports are generated only when telemetry events have been recorded.

## Running Tests

The project uses JUnit 5 and Mockito for automated testing.

From the root directory, run:

```bash
mvn test
```

Maven compiles the project and executes the available JUnit tests.

A successful run displays:

```text
BUILD SUCCESS
```

## Generating Test Coverage Reports

JaCoCo is used to measure automated test coverage.

To run the tests, build the extension, and generate the coverage report, execute:

```bash
mvn verify
```

Alternatively, perform a clean build:

```bash
mvn clean verify
```

After the command succeeds, open the coverage report:

```text
target/site/jacoco/index.html
```

This HTML report displays instruction, branch, line, method, and class coverage.

## Useful Maven Commands

| Command | Description |
|---|---|
| `mvn test` | Compile and run JUnit tests |
| `mvn package` | Build the installable extension JAR |
| `mvn verify` | Build, test, and generate JaCoCo coverage reports |
| `mvn clean verify` | Delete previous build output and perform a fresh verified build |

All commands must be executed from the directory containing `pom.xml`.

## Troubleshooting

**Maven reports that no POM file was found**

Ensure the terminal is in the repository root rather than the `target` directory.

**BlueJ extension does not appear**

Confirm that the generated JAR was copied into an `extensions2` folder. Restart BlueJ and check **Help → Installed Extensions**.

**BlueJ dependency cannot be resolved**

Use the `mvn install:install-file` command provided in Step 3.

**Changes to the extension do not appear in BlueJ**

Rebuild the project using `mvn package`, replace the installed JAR, and restart BlueJ.

**JavaFX compilation errors**

Verify that the JavaFX dependency is available through Maven and that the Java project configuration has been refreshed.

**Telemetry reports are not generated**

Make a few changes to your code or compile a class, then close the BlueJ project. The JSON and PDF reports should appear in your project folder.

**Maven cannot delete the `target` directory**

Close BlueJ and any application accessing the build files. Alternatively, run `mvn verify` without `clean`.

## Known Limitations

- Active coding time and idle time are estimates derived from editor activity.
- Compiler diagnostics are recorded using a generic `COMPILER_ERROR` category rather than compiler-specific error codes.
- Runtime exception type, message, or method name may be unavailable through the BlueJ API.
- The PDF class creation timeline displays up to 12 events; the JSON contains the complete recorded event history.
- The Student ID appears in the JSON report but is not currently included in the PDF.

## Technologies Used

- Java 21
- BlueJ Extensions API
- Apache Maven
- JUnit 5
- Mockito
- Gson
- Apache PDFBox
- JaCoCo
- Maven Shade Plugin

## Repository Structure

```text
bluej-telemetry-plugin/
├── src/
│   ├── main/java/com/uwi/comp3607/
│   └── test/java/com/uwi/comp3607/
├── lib/
│   └── bluej.jar
├── pom.xml
└── README.md
```

For detailed information about the implementation of individual classes and methods, refer to the [Project Wiki](https://github.com/nirv-exe/bluej-telemetry-plugin/wiki).
