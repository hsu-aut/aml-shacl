<p align="center">
  <img src="https://github.com/hsu-aut/aml-shacl/blob/documentation/images/images/AML-SHACL-Logo.png?raw=true" alt="AML-SHACL Logo" width="500"/>
</p>

# Validation of Textual Constraints Against AML Models

## Motivation
We previously developed [AML2OWL](https://github.com/hsu-aut/aml2owl), a tool to convert AutomationML models into ontologies and check these ontologies against SHACL shapes. But who is going to create these SHACL constraints? This is exactly where this tool comes into play. 
It generates SHACL shapes for textual constraints and validates these shapes against the AML models. And if there are any validation errors, it gives easy-to-understand explanations as well.

## How to Use

### As a Command-Line Tool
Download the latest release version (the .jar file) and open a shell inside the folder that contains the jar. The tool can the be started using the following command:

```
java -jar '.\AML SHACL CLI-0.0.2.jar' validate "<AML file to validate>" "<File with textual constraints>"
```

Make sure to use the correct jar name, i.e., the version you downloaded. And the constraint file may have multiple constraints, but they must be separated by empty lines like so:

```
constraint 1....

constraint 2...

constraint 3...
```


## Tests
documentation coming sooon...
