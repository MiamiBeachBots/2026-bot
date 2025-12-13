package frc.robot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure that the codebase follows the team's style guide and casing conventions.
 * These tests validate naming conventions, formatting rules, and documentation standards
 * as defined in styleguide.md.
 */
public class StyleGuideTest {

  private static final String SOURCE_DIR = "src/main/java";

  /**
   * Gets all Java source files in the project.
   *
   * @return List of paths to all .java files
   */
  private List<Path> getAllJavaFiles() throws IOException {
    Path sourcePath = Paths.get(SOURCE_DIR);
    try (Stream<Path> paths = Files.walk(sourcePath)) {
      return paths
          .filter(Files::isRegularFile)
          .filter(p -> p.toString().endsWith(".java"))
          .collect(Collectors.toList());
    }
  }

  /**
   * Reads the content of a file.
   *
   * @param path Path to the file
   * @return String content of the file
   */
  private String readFile(Path path) throws IOException {
    return Files.readString(path);
  }

  @Test
  @DisplayName("No wildcard imports should be used")
  public void testNoWildcardImports() throws IOException {
    List<Path> javaFiles = getAllJavaFiles();
    List<String> violations = new ArrayList<>();
    
    Pattern wildcardImportPattern = Pattern.compile("^import\\s+[a-zA-Z0-9_.]+\\.\\*;", Pattern.MULTILINE);
    
    for (Path file : javaFiles) {
      String content = readFile(file);
      Matcher matcher = wildcardImportPattern.matcher(content);
      
      while (matcher.find()) {
        violations.add(file.toString() + ": " + matcher.group());
      }
    }
    
    assertTrue(violations.isEmpty(), 
        "Wildcard imports found (styleguide.md requires explicit imports):\n" + 
        String.join("\n", violations));
  }

  @Test
  @DisplayName("Class names should use PascalCase")
  public void testClassNamesPascalCase() throws IOException {
    List<Path> javaFiles = getAllJavaFiles();
    List<String> violations = new ArrayList<>();
    
    // Match class declarations: public class ClassName, class ClassName, etc.
    Pattern classPattern = Pattern.compile("^\\s*(?:public\\s+)?(?:final\\s+)?(?:abstract\\s+)?class\\s+([A-Za-z0-9_]+)", Pattern.MULTILINE);
    Pattern pascalCasePattern = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    
    for (Path file : javaFiles) {
      String content = readFile(file);
      Matcher matcher = classPattern.matcher(content);
      
      while (matcher.find()) {
        String className = matcher.group(1);
        if (!pascalCasePattern.matcher(className).matches()) {
          violations.add(file.toString() + ": Class '" + className + "' does not follow PascalCase");
        }
      }
    }
    
    assertTrue(violations.isEmpty(), 
        "Class names not in PascalCase (styleguide.md section 1):\n" + 
        String.join("\n", violations));
  }

  @Test
  @DisplayName("Interface names should use PascalCase")
  public void testInterfaceNamesPascalCase() throws IOException {
    List<Path> javaFiles = getAllJavaFiles();
    List<String> violations = new ArrayList<>();
    
    Pattern interfacePattern = Pattern.compile("^\\s*(?:public\\s+)?interface\\s+([A-Za-z0-9_]+)", Pattern.MULTILINE);
    Pattern pascalCasePattern = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    
    for (Path file : javaFiles) {
      String content = readFile(file);
      Matcher matcher = interfacePattern.matcher(content);
      
      while (matcher.find()) {
        String interfaceName = matcher.group(1);
        if (!pascalCasePattern.matcher(interfaceName).matches()) {
          violations.add(file.toString() + ": Interface '" + interfaceName + "' does not follow PascalCase");
        }
      }
    }
    
    assertTrue(violations.isEmpty(), 
        "Interface names not in PascalCase (styleguide.md section 1):\n" + 
        String.join("\n", violations));
  }

  @Test
  @DisplayName("Method names should use camelCase")
  public void testMethodNamesCamelCase() throws IOException {
    List<Path> javaFiles = getAllJavaFiles();
    List<String> violations = new ArrayList<>();
    
    // Match method declarations
    Pattern methodPattern = Pattern.compile(
        "^\\s*(?:public|private|protected)?\\s*(?:static\\s+)?(?:final\\s+)?(?:synchronized\\s+)?[A-Za-z<>\\[\\]0-9_,\\s]+\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\([^)]*\\)\\s*(?:throws\\s+[A-Za-z0-9_,\\s]+)?\\s*\\{?",
        Pattern.MULTILINE
    );
    Pattern camelCasePattern = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    
    for (Path file : javaFiles) {
      String content = readFile(file);
      // Remove comments to avoid false positives (using (?s) for DOTALL mode)
      String contentNoComments = content.replaceAll("//.*", "").replaceAll("(?s)/\\*.*?\\*/", "");
      Matcher matcher = methodPattern.matcher(contentNoComments);
      
      while (matcher.find()) {
        String methodName = matcher.group(1);
        // Skip constructors (same name as class, starts with uppercase)
        if (methodName.matches("^[A-Z].*")) {
          continue;
        }
        // Skip if it's not a method declaration (could be a variable)
        if (!camelCasePattern.matcher(methodName).matches()) {
          violations.add(file.toString() + ": Method '" + methodName + "' does not follow camelCase");
        }
      }
    }
    
    assertTrue(violations.isEmpty(), 
        "Method names not in camelCase (styleguide.md section 1):\n" + 
        String.join("\n", violations));
  }

  @Test
  @DisplayName("Constants should use SCREAMING_SNAKE_CASE")
  public void testConstantsScreamingSnakeCase() throws IOException {
    List<Path> javaFiles = getAllJavaFiles();
    List<String> violations = new ArrayList<>();
    
    // Match constants: static final TYPE NAME = value;
    Pattern constantPattern = Pattern.compile(
        "^\\s*(?:(?:public|private)\\s+)?static\\s+final\\s+[A-Za-z<>\\[\\]0-9_,\\s]+\\s+([A-Z_][A-Z0-9_]*)\\s*=",
        Pattern.MULTILINE
    );
    Pattern screamingSnakePattern = Pattern.compile("^[A-Z][A-Z0-9_]*$");
    
    for (Path file : javaFiles) {
      String content = readFile(file);
      Matcher matcher = constantPattern.matcher(content);
      
      while (matcher.find()) {
        String constantName = matcher.group(1);
        if (!screamingSnakePattern.matcher(constantName).matches()) {
          violations.add(file.toString() + ": Constant '" + constantName + "' does not follow SCREAMING_SNAKE_CASE");
        }
      }
    }
    
    assertTrue(violations.isEmpty(), 
        "Constants not in SCREAMING_SNAKE_CASE (styleguide.md section 1):\n" + 
        String.join("\n", violations));
  }

  @Test
  @DisplayName("No tab characters should be present (use 2 spaces)")
  public void testNoTabs() throws IOException {
    List<Path> javaFiles = getAllJavaFiles();
    List<String> violations = new ArrayList<>();
    
    for (Path file : javaFiles) {
      String content = readFile(file);
      if (content.contains("\t")) {
        long lineNumber = 1;
        for (String line : content.split("\n")) {
          if (line.contains("\t")) {
            violations.add(file.toString() + ":" + lineNumber);
            break;
          }
          lineNumber++;
        }
      }
    }
    
    assertTrue(violations.isEmpty(), 
        "Tab characters found (styleguide.md requires 2 spaces for indentation):\n" + 
        String.join("\n", violations));
  }

  @Test
  @DisplayName("Public classes should have Javadoc comments")
  public void testPublicClassesHaveJavadoc() throws IOException {
    List<Path> javaFiles = getAllJavaFiles();
    List<String> violations = new ArrayList<>();
    
    for (Path file : javaFiles) {
      String content = readFile(file);
      
      // Find all public class declarations
      Pattern publicClassPattern = Pattern.compile("^\\s*public\\s+(?:final\\s+)?(?:abstract\\s+)?class\\s+([A-Za-z0-9_]+)", Pattern.MULTILINE);
      Matcher matcher = publicClassPattern.matcher(content);
      
      while (matcher.find()) {
        String className = matcher.group(1);
        int classStart = matcher.start();
        
        // Look backwards from the class declaration to find if there's a Javadoc comment
        String beforeClass = content.substring(0, classStart);
        
        // Check if there's a Javadoc comment immediately before (allowing whitespace)
        // Pattern: /** ... */ followed by optional whitespace, then the class
        if (!beforeClass.matches("(?s).*?/\\*\\*.*?\\*/\\s*$")) {
          violations.add(file.toString() + ": Public class '" + className + "' is missing Javadoc");
        }
      }
    }
    
    assertTrue(violations.isEmpty(), 
        "Public classes without Javadoc (styleguide.md section 3 requires Javadoc for all public classes):\n" + 
        String.join("\n", violations));
  }

  @Test
  @DisplayName("Variable names should use camelCase")
  public void testVariableNamesCamelCase() throws IOException {
    List<Path> javaFiles = getAllJavaFiles();
    List<String> violations = new ArrayList<>();
    
    // Match field declarations (class-level variables)
    Pattern fieldPattern = Pattern.compile(
        "^\\s*(?:public|private|protected)?\\s*(?:static\\s+)?(?:final\\s+)?(?!class|interface)[A-Za-z<>\\[\\]0-9_,\\s]+\\s+([a-z][a-zA-Z0-9_]*)\\s*[;=]",
        Pattern.MULTILINE
    );
    // Allow camelCase or m_camelCase (WPILib convention for member variables)
    Pattern camelCasePattern = Pattern.compile("^[a-z][a-zA-Z0-9]*$|^m_[a-z][a-zA-Z0-9]*$");
    
    for (Path file : javaFiles) {
      String content = readFile(file);
      // Remove comments to avoid false positives (using (?s) for DOTALL mode)
      String contentNoComments = content.replaceAll("//.*", "").replaceAll("(?s)/\\*.*?\\*/", "");
      Matcher matcher = fieldPattern.matcher(contentNoComments);
      
      while (matcher.find()) {
        String varName = matcher.group(1);
        // Skip constants (would be caught by constant test)
        if (varName.matches("^[A-Z_]+$")) {
          continue;
        }
        if (!camelCasePattern.matcher(varName).matches()) {
          violations.add(file.toString() + ": Variable '" + varName + "' does not follow camelCase (or m_camelCase for members)");
        }
      }
    }
    
    assertTrue(violations.isEmpty(), 
        "Variable names not in camelCase (styleguide.md section 1):\n" + 
        String.join("\n", violations));
  }
}
