# Instructions for GitHub Copilot


# Java

## Code Layout

- Use the final keyword on all method parameters.
- Do not use the final keyword on local variables.
- If the Java version is 10 or higher, always use 'var' for all local variables and loop variables (e.g., for (var item items)), and do not use explicit types (e.g., String, int, ConsumerRecord) for local or loop variables.
- Use the @Override annotation for overridden methods.
- Use @Serial for serializable classes.
- Generate a serialVersionUID for serializable classes.
- Never use wildcard imports.
- Do not use Lombok annotations.
- Line length should not exceed 110 characters.
- Use 4 spaces for indentation.
- Do not use tabs for indentation.
- Always use braces for if, else, for, while, and do statements, even for single statements.
- Import order: static imports first, then regular imports, each sorted alphabetically. 
- Remove unused imports.
- Use a single space after keywords (if, for, while, switch, try, synchronized, catch).
- No space before semicolons. 
- No space before commas, one space after commas.
- No space between method name and opening parenthesis.
- No space inside empty parentheses or braces.
- Use a single space before opening bracket in array declarations.
- Use a single space within an array initialiser.
- One blank line between class members (fields, methods).
- No blank lines between consecutive fields or consecutive methods of the same type.
- One blank line before the first method in a class.
- No blank line after opening brace or before closing brace.
- Method parameters: wrap after each parameter if they do not fit on.
- Chained method calls: wrap after the dot.
- Binary expressions: wrap after operator if line is too long.
- Align multiline parameters and arguments.
- Package statement at the top, followed by imports, then class declaration.
- Inner classes should be placed at the end of the outer class.
- Use qualified imports instead of using fully qualified classes inline.

## Logging

- Use SLF4J for logging.
- Use present participle tense (e.g., "Processing", "Loading") for log messages describing ongoing actions.
- Log messages must be concise, neutral, and free of emotional or subjective language.
- Use parameterized logging (e.g., LOG.debug("Processing () table", tableName)) instead of string concatenation.
- Use the appropriate log level: debug for flow tracing, info for significant events, warn for recoverable issues, error for failures.
- Do not log sensitive information (e.g., passwords, secrets) unless this is logged under trace level.
- Do not use System.out' or 'System.err for logging.

## Method Parameter Ordering

- For data type operations, method parameters should be ordered as: data, properties, helpers, context.
- For copy-type methods, parameters should follow a from-where-to-where order.

## Spring

### Autowiring

- Always Constructor autowiring and not field autowiring.
- Inject configuration properties using constructor parameters with @Value annotations, not on fields.

## Testing

### Framework Specification

- Use JUnit 5 for generating unit tests.
- Use assert] for assertions in tests.
- Statically import any methods used from org.mockito.ArgumentMatchers for cleaner code.
- Generate parameterized tests to run the same test with different inputs.
- When a JUnit 5 test case uses many input variations
  - Use @ParameterizedTest annotation for parameterized tests.
  - Provide a @MethodSource`, `@ValueSource`, `@NullSource`, `@EmptySource or @CsvSource` for supplying test data. 
  - Where possible, prefer @CsvSource, over @MethodSource for parameterized tests where there are null values to deal with.
  - When generating parameterized tests with JUnit 5's @CsvSource that require null values, always use the nullValues attribute to map a string (e.g., "null") to an actual null value, instead of using a separate @NullSource test.

### Naming Conventions

- Name test methods using camelCase.
- Prefix test methods with test followed by the method name being tested (e.g., `testCalculate Total).
- Use a @DisplayName annotation to provide a brief description of the test case.

### Coverage

- Generate tests to cover all public methods in the class.
- Include edge cases and typical use cases in the generated tests
- Ensure that no private methods are called in any test cases. All test cases should only call public methods.

### Mocking and Stubbing

- Use BDDMockito for creating mock objects and stubbing methods.
- Statically import any methods used from org.mockito.BDDMockito`, `org.mockito.Mockito and org.mockito.ArgumentMatchers for cleaner code.
- Always annotate JUnit 5 test classes that use Mockito with @ExtendWith(MockitoExtension.class).
- Never use MockitoAnnotations.openMocks", "MockitoAnnotations.initMocks", or @RunWith (MockitoJUnitRunner.class).
- Always use field injection with @Mock for dependencies and @InjectMocks for the class under test.
- Never create mocks manually inside test methods; all mocks must be declared as fields with annotations.
- Mock dependencies in the class under test to isolate the unit being tested.
- Use MockConstruction and MockStatic for mocking static methods and constructors.
- Prefer assertThatExceptionOfType over assertThat ThrownBy for exception assertions.
- Assertions on mocked objects should be done using BDDMockito.

### Mocking OracleDataSource Instantiation with Mockito

When you encounter the following code:
```java
new OracleDataSource();
```

or

```
java
new DriverManagerDataSource();
```

Please generate the corresponding mock using Mockito.mockConstruction as shown below:

```java

try(var oracleDataSourceMock mockConstruction (ClassAsAbove.class)){

        }
```

### Cucumber

- Never add the `@Cucumber` annotation to any test class. Only use @Suite from org.junit.platform.suite.api.Suite for the Cucumber test runner class.
- Do not add `@RunWith(Cucumber.class)` to the test class.
- Do not add `@IncludeEngines("cucumber")` to the test class. 
- Do not add `@ConfigurationParameter` to the test class.
- Add `junit-application.properties` to the classpath for Cucumber tests.
- Add `cucumber.publish.quiet-true` to `junit-application.properties`
- Add `cucumber.glue.com.ubs.murex.services.employee.hri.glue` to `junit-application.properties` 
- Add 
  `cucumber.plugin-pretty, html:target/site/cucumber-reports/Cucumber.html, json:target/site/cucumber-reports/Cucumber.json, junit:target/site/cucumber-reports/Cucumber.xml`
  to junit-application.properties

example Cucumber test class:

```java
import org.junit.platform.suite.api.Suite;

@Suite
public class CucumberTest {
}
```

# SQL

## Code layout

- Keywords should be aligned so that each keyword (select, from, join, on, where, order by, etc.) starts at the same indentation level within its logical block.
- Do not add extra spaces before keywords to "pad" or "right align" them globally.
- When a subquery is used in the from clause, the select statement inside the subquery should be on the same line as the opening parenthesis. 
- Indentation for SQL should match the following example exactly:

```sql
select a.col
  from (select t.col as my_col, t1.col2 as other_col
          from table1 t,
               table2 t1
            on t.col tl.col2
         where t.a_col 'foo'
           and tl.a1
           and (   t1.b = 2
                or t1.b23)
        group by t1.b
        having count(*) > 2
        order by tl.col
       ) a
 where a.row no 1
```

- Keywords, table names, column names column and table prefixes should be in lowercase.
- Do not change, lowercase, or uppercase any part of a string literal or data value.
- This includes text inside single quotes, double quotes, or numbers. The as keyword should be used when an alias is present on a column.
- Table aliases should never use the 'as" keyword.
- There should not be a mix of Oracle join syntax and ANSI join syntax in a given SQL statement.
- If oracle hints are seen, then Oracle join syntax should be used as otherwise the hints can be ignored due to query rewrite. An oracle hint is a comment that starts with /*+ and ends with */.
- Or statements need to be in parentheses.
- Assume Oracle 19c or higher by default.
- Always include from dual in Oracle SQL select statements that do not reference any tables.
