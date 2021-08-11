package com.phantomstr.testing.tool.json.schema.generator.mapping;

import com.phantomstr.testing.tool.json.schema.generator.reporter.Reporter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static com.phantomstr.testing.tool.json.schema.generator.GlobalParameters.excludes;
import static com.phantomstr.testing.tool.json.schema.generator.GlobalParameters.includes;
import static com.phantomstr.testing.tool.json.schema.generator.GlobalParameters.outputDirectory;
import static com.phantomstr.testing.tool.json.schema.generator.GlobalParameters.packages;
import static java.util.Arrays.stream;

@Accessors(chain = true)
public class ClassMapping {

    public final static Set<Class<?>> classSet = new HashSet<>();

    @Setter
    private Reporter reporter;
    private URLClassLoader urlClassLoader;

    public ClassMapping() {
        if (reporter == null) {
            reporter = new Reporter("default reporter");
        }

        try {
            urlClassLoader = URLClassLoader.newInstance(new URL[]{Paths.get(outputDirectory).toUri().toURL()}, getClass().getClassLoader());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void collectModels() throws IOException {
        FileVisitor<Path> classCollecctor = new ClassCollector()
                .setReporter(reporter)
                .setClassLoader(urlClassLoader);
        Files.walkFileTree(Paths.get(outputDirectory), classCollecctor);

    }

    @Accessors(chain = true)
    private static class ClassCollector implements FileVisitor<Path> {

        @Setter
        private Reporter reporter;
        @Setter
        private URLClassLoader classLoader;

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }


        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
            File file = path.toFile();
            if (!file.isDirectory()) {

                String canonicalName = getCanonicalName(path);

                if (!"**".equals(packages) &&
                        packages != null &&
                        stream(packages.split(":")).noneMatch(canonicalName::startsWith)) {
                    return FileVisitResult.CONTINUE;
                }

                if (!"**".equals(includes) && includes != null) {
                    stream(includes.split(";")).forEach(include -> {
                        if (Pattern.compile(include).matcher(canonicalName).find()) {
                            registerClass(canonicalName);
                        }
                    });
                } else {
                    registerClass(canonicalName);
                }

            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        private String getCanonicalName(Path path) {
            String fullPath = Paths.get(outputDirectory).relativize(path).toString().replaceAll("[\\\\/]", ".");
            if (fullPath.contains(".")) {
                fullPath = StringUtils.substringBeforeLast(fullPath, ".");
            }
            return fullPath;
        }

        private void registerClass(String canonicalName) {
            if (excludes != null &&
                    !excludes.isBlank() &&
                    stream(excludes.split(";")).anyMatch(
                            exclude -> Pattern.compile(exclude).matcher(canonicalName).find())) {
                return;
            }

            try {
                classSet.add(classLoader.loadClass(canonicalName));
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                reporter.warn("can't load class " + canonicalName);
            }
        }

    }

}
