package cli;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.AmlTextConstraintValidator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
		name = "AML-SHACL CLI", 
		mixinStandardHelpOptions = true,
		subcommands = {CommandLine.HelpCommand.class},
		description = "Validate natural-language constraints against AutomationML files using ontologies and SHACL."
	)
public class AmlShaclCli {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Command(name="validate", description = "Validates natural language constraints contained in a text file against an AutomationML file.")
	public void map(
		@Parameters(arity = "1..*", paramLabel = "<amlFile>", description = "File name of the AML file that should be mapped") String amlFile,
		@Parameters(arity = "1..*", paramLabel = "<constraintFile>", description = "File name of the text file with textual constraints against the AML file") String constraintFile,
		@Parameters(arity = "0..1", paramLabel = "<baseIri>", description = "Explicit baseIri definition for all individuals that are created. Optional, a default one is used if none is given") String baseIri
	) throws Exception {
		// amlFile and constraintFile are required, rest is optional
		if (amlFile.isBlank() || constraintFile.isBlank()) {
			logger.error("Missing parameter amlFile or constraintFile. Make sure to pass these two files");
			throw new Exception("Missing files. Make sure to pass both an AML file as well as a constraint file.");
		}
		
		logger.info("Started Validation");
		Path amlFilePath = Path.of(amlFile);
		Path constraintFilePath = Path.of(constraintFile);
		logger.info("AML file: " + amlFilePath + "\n" + "Constraint file: " + constraintFilePath);
		
		AmlTextConstraintValidator amlValidator = new AmlTextConstraintValidator();
		
		try {
			amlValidator.validateConstraintsForAmlModel(amlFilePath, constraintFilePath);
			logger.info("Completed validation and wrote results");
		} catch (Exception e) {
			logger.error("Could not complete validation. " + e.toString());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		int exitCode = new CommandLine(new AmlShaclCli()).execute(args);
		System.exit(exitCode);
	}

	
}
