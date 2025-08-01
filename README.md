# Cyclomatic Complexity ErrorProne Plugin  

A Maven plugin that analyzes and warns about excessive cyclomatic complexity in Java methods using ErrorProne.

---

## **Prerequisites**  
- **Java 17+** (required for compilation)  
- **Maven 3.6+** (build tool)  
- **ErrorProne** (static analysis framework)  

---

## **Setup Instructions**  

## **1. Clone and Install the Plugin**  
First, clone this repository and install the plugin locally:  

```bash
git clone <your-repo-url>
cd <repo-directory>
mvn install  # Installs the plugin in your local Maven repository
```
## **2. Configure JVM for ErrorProne**  
Create a `.mvn/jvm.config` file in your project's root directory with the following exports:  

```text
--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-opens jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-opens jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED
```

## 3. Update Your Project's pom.xml
Add the following plugin configuration to the `<build><plugins>` section:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.14.0</version>
  <configuration>
    <source>17</source>
    <target>17</target>
    <annotationProcessorPaths>
      <path>
        <groupId>com.google.errorprone</groupId>
        <artifactId>error_prone_core</artifactId>
        <version>2.40.0</version>
      </path>
      <path>
        <groupId>edu.appstate.cs.checker</groupId>
        <artifactId>BadCyclomaticComplexity-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
      </path>
    </annotationProcessorPaths>
    <compilerArgs>
      <arg>-Xplugin:ErrorProne -XepDisableAllChecks -Xep:BadCyclomaticComplexity:WARN</arg>
      <arg>-XDcompilePolicy=simple</arg>
      <arg>--should-stop=ifError=FLOW</arg>
    </compilerArgs>
  </configuration>
</plugin>
```

## Run the Cyclomatic Complexity Checker

After completing the setup, execute the following command to run the analysis:

```bash
mvn clean compile
```
## Expected Output

The checker will generate warnings when methods exceed these thresholds:
- **Loops**: 
  - Warning if > 5 loops
  - Strong warning if > 8 loops
- **Branches**: 
  - Warning if > 8 branches 
  - Strong warning if > 12 branches
- **Total Complexity** (loops + branches):
  - Warning if > 15
---

### All Set!

Give the tool a try on your codebase. If you have any problems, open a GitHub issue.




