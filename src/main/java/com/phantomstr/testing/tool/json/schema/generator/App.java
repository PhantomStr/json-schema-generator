package com.phantomstr.testing.tool.json.schema.generator;

import com.phantomstr.testing.tool.json.schema.generator.reporter.Reporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.phantomstr.testing.tool.json.schema.generator.MavenGenerateGoal.EXCLUDE;
import static com.phantomstr.testing.tool.json.schema.generator.MavenGenerateGoal.INCLUDE;
import static com.phantomstr.testing.tool.json.schema.generator.MavenGenerateGoal.PACKAGES;
import static com.phantomstr.testing.tool.json.schema.generator.MavenGenerateGoal.TARGET_DIRECTORY;


@Slf4j
public final class App {

    public static void main(String[] args) {
        SchemasGenerator generator = new SchemasGenerator();
        Reporter reporter = new Reporter("Json schema generator").setRowFormat("%s");

        readArgs(args);

        generator.setReporter(reporter).generate();
    }

    private static void readArgs(String[] args) {
        CommandLine cmd = getCommandLine(args);

        log.debug("{}", Arrays.stream(cmd.getOptions())
                .map(option -> option.getLongOpt() + " = " + option.getValue())
                .collect(Collectors.joining("; ")));

        GlobalParameters.packages = cmd.getOptionValue(PACKAGES);

        if (cmd.hasOption(INCLUDE)) {
            GlobalParameters.includes = cmd.getOptionValue(INCLUDE);
        }

        if (cmd.hasOption(EXCLUDE)) {
            GlobalParameters.excludes = cmd.getOptionValue(EXCLUDE);
        }

        if (cmd.hasOption(TARGET_DIRECTORY)) {
            GlobalParameters.targetDirectory = cmd.getOptionValue(TARGET_DIRECTORY);
        }

    }

    private static CommandLine getCommandLine(String[] args) {
        Options options = getOptions();
        try {
            return new DefaultParser().parse(options, args);
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("codegen", options);
            throw new RuntimeException(pe);
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        Option packagesOption = new Option(PACKAGES, "packages", true, "package of source models");
        packagesOption.setRequired(false);
        options.addOption(packagesOption);

        Option includeOption = new Option(INCLUDE, "include", true, "includes");
        includeOption.setRequired(false);
        options.addOption(includeOption);

        Option excludeOption = new Option(EXCLUDE, "exclude", true, "excludes");
        excludeOption.setRequired(false);
        options.addOption(excludeOption);

        Option targetDirOption = new Option(TARGET_DIRECTORY, "targetDirectory", true, "scheme output directory");
        targetDirOption.setRequired(false);
        options.addOption(targetDirOption);

        return options;
    }

}
