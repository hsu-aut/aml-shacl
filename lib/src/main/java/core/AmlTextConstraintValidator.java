package core;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aml2owl.checking.ShaclValidator;
import aml2owl.core.ShaclReportUtil;
import aml2owl.mapping.AmlOwlMapper;
import shacl.ShaclGenerator;
import shacl.ShaclResultInterpreter;

public class AmlTextConstraintValidator {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
    private final ShaclGenerator shaclGenerator;
    private final AmlOwlMapper amlMapper;
    private final ShaclValidator validator;
    private final ShaclResultInterpreter shaclInterpreter; 

    public AmlTextConstraintValidator() {
        this.shaclGenerator = new ShaclGenerator();
        this.amlMapper = new AmlOwlMapper();
        this.validator = new ShaclValidator();
        this.shaclInterpreter = new ShaclResultInterpreter();
    }

    /**
     * Full validation process:
     * 1. Read constraints
     * 2. Generate SHACL shapes using GPT
     * 3. Load AML model from file (placeholder)
     * 4. Parse SHACL shapes into a model
     * 5. Validate AML model against SHACL model (placeholder)
     *
     * @param amlFilePath Path to the AML model file
     * @param constraintsFilePath Path to the natural language constraints
     * @throws Exception on failure
     */
    public void validateConstraintsForAmlModel(Path amlFilePath, Path constraintsFilePath) throws Exception {
        logger.info("Reading constraints and generating SHACL shapes...");
        List<String> shapeStrings = shaclGenerator.processConstraintFile(constraintsFilePath);

        // Combine all SHACL shapes into one Jena Model
        Model shapeModel = ModelFactory.createDefaultModel();
        for (String shapeTurtle : shapeStrings) {
            shapeModel.read(new StringReader(shapeTurtle), null, "TURTLE");
        }
        
        // Store shapes in file (mostly for debugging)
        try (OutputStream out = new FileOutputStream("generated-shapes.ttl")) {
            shapeModel.write(out, "TURTLE");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("SHACL shapes generated successfully.");

        // Load AML model from file and validate against generated shapes
        Model amlModel = this.amlMapper.executeMapping(amlFilePath, null);
        Model resultModel = this.validator.checkConformance(amlModel, shapeModel);

    	boolean isConforming = ShaclReportUtil.isConforming(resultModel);
        
         if (isConforming) {
             logger.info("Model conforms to all SHACL constraints.");
         } else {
        	 this.shaclInterpreter.interpretResults(resultModel);
        	 logger.info("An explanation of all errors was stored.");
         }
    }
}
