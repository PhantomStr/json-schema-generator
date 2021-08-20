# Json schema generator plugin

The module provide generation of json schemas by POJO models with using "javax.validation"

## Getting Started

execute generation: mvn json-schema-generator:generate <br>

### setup

add plugin in pom<br>

**configuration example:**

```xml

<project>
    <build>
        <plugins>
            <plugin>
                <groupId>io.github.phantomstr.testing-tools</groupId>
                <artifactId>json-schema-generator</artifactId>
                <version>1.0.0</version>
                <configuration>
                    <packages>
                        <package>my.project.models</package>
                    </packages>
                    <includes>
                        <include>Dto.*</include>
                        <include>.*Model</include>
                    </includes>
                    <excludes>
                        <exclude>.*Assert</exclude>
                    </excludes>
                    <targetDirectory>/src/main/resources/model/schema</targetDirectory>
                    <pojoClassesDirectory>${project.build.outputDirectory}</pojoClassesDirectory>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

package - using for filtering classes. <br>
If found class start with one of package - it will be used for generation.<br>
default value "**" - all packages will be involved in generation <br>

include - using for filtering classes. <br>
If a class match is found with one of the "include" regular expressions, it will be used for generation. <br>
default value "**" - all packages will be involved in generation <br>

exclude - using for filtering classes. <br>
If a class match is found with one of the "exclude" regular expressions, it will be ignored. <br>
default value "" - all packages will be involved in generation <br>

targetDirectory - path to directory for schemas generation. <br>
defaultValue = ${project.build.sourceDirectory}/schemas <br>

pojoClassesDirectory - root directory with generated classes for models <br>
default value "${project.build.outputDirectory}" <br>

### Prerequisites

- Classes for that schemas are generating should be already compiled

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Versioning

We use [SemVer](http://semver.org/) for versioning.