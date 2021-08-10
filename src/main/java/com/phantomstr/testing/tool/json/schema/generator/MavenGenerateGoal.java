package com.phantomstr.testing.tool.json.schema.generator;


import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import static com.phantomstr.testing.tool.json.schema.generator.App.main;
import static java.lang.String.join;


@Mojo(name = "generate",
        defaultPhase = LifecyclePhase.TEST_COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class MavenGenerateGoal extends AbstractMojo {

    public static final String INCLUDE = "i";
    public static final String EXCLUDE = "e";
    public static final String TARGET_DIRECTORY = "td";
    public static final String PACKAGES = "p";

    @Getter
    @Parameter(property = "json.schema.generator.commandline")
    private String commandline;

    @Getter
    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private String pojoClassesDirectory;

    @Getter
    @Parameter
    private String[] packages = new String[]{"**"};

    @Getter
    @Parameter
    private String[] includes = new String[]{"**"};

    @Getter
    @Parameter
    private String[] excludes = new String[]{""};

    @Getter
    @Parameter(defaultValue = "${project.build.sourceDirectory}/schemas")
    private String targetDirectory;

    @SneakyThrows
    @Override
    public void execute() {
        getLog().debug("source classes directory = " + pojoClassesDirectory);
        GlobalParameters.outputDirectory = pojoClassesDirectory;

        String[] args;
        if (commandline != null) {
            args = commandline.split("\\s");
        } else {
            args = new String[]{
                    "-" + PACKAGES, packages == null ? "**" : join(";", packages),
                    "-" + INCLUDE, includes == null ? "**" : join(";", includes),
                    "-" + EXCLUDE, excludes == null ? "" : join(";", excludes),
                    "-" + TARGET_DIRECTORY, targetDirectory};
        }

        main(args);
    }

}
