package io.github.phantomstr.testing.tool.json.schema.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.customProperties.ValidationSchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import io.github.phantomstr.testing.tool.json.schema.generator.mapping.ClassMapping;
import io.github.phantomstr.testing.tool.json.schema.generator.reporter.Reporter;
import io.github.phantomstr.testing.tool.json.schema.generator.schema.GenerateSchemas;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static io.github.phantomstr.testing.tool.json.schema.generator.mapping.ClassMapping.classSet;

@Accessors(chain = true)
@Slf4j
public class SchemasGenerator {

    private ClassMapping classMapping;

    @Setter
    private Reporter reporter;

    public void generate() {
        init();
        collectModels();
        generateSchemas();
        report();
    }

    private void init() {
        if (reporter == null) {
            reporter = new Reporter("default reporter");
        }
        classMapping = new ClassMapping().setReporter(reporter);
    }

    private void collectModels() {
        try {
            classMapping.collectModels();
        } catch (IOException e) {
            reporter.warn("class loading failed: " + e.getMessage());
        } finally {
            classSet.stream().map(Class::getName)
                    .forEach(className -> reporter.debug("loaded: " + className));
        }
    }

    private void generateSchemas() {
        classSet.forEach(generatedModelClass -> {
            SchemaFactoryWrapper validatorVisitor = new ValidationSchemaFactoryWrapper();
            try {
                JsonSchema jsonSchema = GenerateSchemas.generateSchema(validatorVisitor, generatedModelClass);
                writeSchema(jsonSchema, generatedModelClass);
            } catch (JsonProcessingException e) {
                reporter.warn("can't generate scheme for class " + generatedModelClass, e);

            }
        });
    }

    private void writeSchema(JsonSchema jsonSchema, Class<?> modelClass) {
        try {
            String jsonSchemaStr = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
            File file = new File(Paths.get(GlobalParameters.targetDirectory, modelClass.getSimpleName() + ".json").toUri());
            FileUtils.write(file, jsonSchemaStr);
            reporter.info("created schema: " + file);
        } catch (IOException e) {
            reporter.warn("can't write scheme for class " + modelClass, e);
        }
    }


    private void report() {
        reporter.print(log);
    }

}
