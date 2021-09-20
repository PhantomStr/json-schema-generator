package io.github.phantomstr.testing.tool.json.schema.generator;

import io.github.phantomstr.testing.tool.json.schema.generator.reporter.Reporter;


public class GlobalParameters {

    public static String outputDirectory;

    public static String packages;
    public static String includes;
    public static String excludes;
    public static String targetDirectory;

    public static void print(Reporter reporter) {
        reporter.debug("outputDirectory = " + outputDirectory);
        reporter.debug("packages - " + packages);
        reporter.debug("includes - " + includes);
        reporter.debug("excludes - " + excludes);
        reporter.debug("targetDirectory - " + targetDirectory);
    }

}
