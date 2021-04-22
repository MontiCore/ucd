/* (c) https://github.com/MontiCore/monticore */
package ucd;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import ucd._ast.ASTUCDArtifact;
import ucd._parser.UCDParser;
import ucd._symboltable.*;
import ucd.semdiff.Scenario;
import ucd.semdiff.SemUCDDiff;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CLI tool providing functionality for processing Sequence Diagram (SD) artifacts.
 */
public class UCDCLI {

  /*
   * Main method of the CLI.
   */
  public static void main(String[] args) {
    UCDCLI cli = new UCDCLI();
    cli.run(args);
  }

  public void run(String[] args) {
    Options options = initOptions();

    CommandLineParser cliparser = new DefaultParser();
    try {
      CommandLine cmd = cliparser.parse(options, args);

      // help or no input
      if (cmd.hasOption("h") || !cmd.hasOption("i")) {
        printHelp(options);
        return;
      }

      // disable debug messages
      Log.initWARN();

      // disable fail quick to log as much errors as possible
      Log.enableFailQuick(false);

      // Parse input SDs
      List<ASTUCDArtifact> inputUCDs = new ArrayList<>();
      for (String inputFileName : cmd.getOptionValues("i")) {
        Optional<ASTUCDArtifact> ast = parseUCDArtifact(inputFileName);
        if (!ast.isPresent()) {
          Log.error(String.format("Parsing the input UCD '%s' was not successful", inputFileName));
          return;
        }
        inputUCDs.add(ast.get());
      }

      // semantic differencing
      if (cmd.hasOption("sd")) {
        if (inputUCDs.size() != 2) {
          Log.error(String.format("Received %s input UCDs. However, the option 'semdiff' requires exactly two input UCDs.", inputUCDs.size()));
          return;
        }
        Set<Scenario> witnesses = semDiff(inputUCDs.get(0), inputUCDs.get(1));
        if (!witnesses.isEmpty()) {
          System.out.println("Diff witnesses:");
          for (Scenario witness : witnesses) {
            System.out.println(witness);
            System.out.println();
          }
        }
        else {
          System.out.println(String.format("The input UCD '%s' is a refinement of the input UCD '%s'", cmd.getOptionValues("i")[0], cmd.getOptionValues("i")[1]));
        }
      }

      // we need the global scope for symbols and cocos
      ModelPath modelPath = new ModelPath(Paths.get(""));
      if (cmd.hasOption("path")) {
        modelPath = new ModelPath(Arrays.stream(cmd.getOptionValues("path")).map(x -> Paths.get(x)).collect(Collectors.toList()));
      }

      IUCDGlobalScope globalScope = UCDMill.globalScope();
      globalScope.setModelPath(modelPath);

      if (cmd.hasOption("s")) {
        for (ASTUCDArtifact ucd : inputUCDs) {
          deriveSymbolSkeleton(ucd);
        }
      }

      if (Log.getErrorCount() > 0) {
        // if the model is not well-formed, then stop before generating anything
        return;
      }

      // fail quick in case of symbol storing
      Log.enableFailQuick(true);

      // store symbols
      if (cmd.hasOption("s")) {
        if (cmd.getOptionValues("s") == null || cmd.getOptionValues("s").length == 0) {
          for (int i = 0; i < inputUCDs.size(); i++) {
            ASTUCDArtifact ucd = inputUCDs.get(i);
            UCDDeSer deSer = new UCDDeSer();
            String serialized = deSer.serialize((UCDArtifactScope) ucd.getEnclosingScope());

            String fileName = cmd.getOptionValues("i")[i];
            String symbolFile = FilenameUtils.getName(fileName) + "sym";
            String symbol_out = "target/symbols";
            String packagePath = ucd.isPresentMCPackageDeclaration() ? ucd.getMCPackageDeclaration().getMCQualifiedName().getQName().replace('.', '/') : "";
            Path filePath = Paths.get(symbol_out, packagePath, symbolFile);
            FileReaderWriter.storeInFile(filePath, serialized);
          }
        }
        else if (cmd.getOptionValues("s").length != inputUCDs.size()) {
          Log.error(String.format("Received '%s' output files for the symboltable option. " + "Expected that '%s' many output files are specified. " + "If output files for the symboltable option are specified, then the number " + " of specified output files must be equal to the number of specified input files.", cmd.getOptionValues("s").length, inputUCDs.size()));
        }
        else {
          for (int i = 0; i < inputUCDs.size(); i++) {
            ASTUCDArtifact ucd_i = inputUCDs.get(i);
            storeSymbols(ucd_i, cmd.getOptionValues("s")[i]);
          }
        }
      }
    }
    catch (ParseException e) {
      // unexpected error from apache CLI parser
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("SD4DevelopmentCLI", options);
  }

  private Options initOptions() {
    Options options = new Options();

    // help info
    options.addOption(Option.builder("h").longOpt("help").desc("Prints this help informations.").build());

    // inputs
    options.addOption(Option.builder("i").longOpt("input").hasArgs().desc("Processes the list of SD input artifacts. " + "Argument list is space separated. " + "CoCos are not checked automatically (see -c).").build());

    // semantic diff
    options.addOption(Option.builder("sd").longOpt("semdiff").desc("Computes a diff witness showing the asymmetrical semantic difference " + "of two SD. Requires two " + "SDs as inputs. See se-rwth.de/topics for scientific foundation.").build());

    // store symbols
    options.addOption(Option.builder("s").longOpt("symboltable").optionalArg(true).hasArgs().desc("Stores the symbol tables of the input SDs in the specified files. " + "The n-th input " + "SD is stored in the file as specified by the n-th argument. " + "Default is 'target/symbols/{packageName}/{artifactName}.sdsym'.").build());

    // model paths
    options.addOption(Option.builder("path").hasArgs().desc("Sets the artifact path for imported symbols, space separated.").build());

    return options;
  }

  /**
   * Parses UCD artifacts (*.ucd files).
   *
   * @param fileName full-qualified name of the file.
   * @return Non-empty optional iff parsing was successful.
   * @throws IOException If something goes wrong while reading the file.
   */
  public Optional<ASTUCDArtifact> parseUCDArtifact(String fileName) throws IOException {
    UCDParser parser = UCDMill.parser();
    return parser.parse(fileName);
  }

  /**
   * Derives symbols for ast and adds them to the globalScope.
   *
   * @param ast The ast.
   */
  public void deriveSymbolSkeleton(ASTUCDArtifact ast) {
    UCDScopesGenitorDelegator genitor = UCDMill.scopesGenitorDelegator();
    genitor.createFromAST(ast);
  }

  /**
   * Stores the symbols for ast in the symbol file filename.
   * For example, if filename = "target/symbolfiles/file.ucdsym", then the symbol file corresponding to
   * ast is stored in the file "target/symbolfiles/file.ucdsym".
   *
   * @param ast      The ast.
   * @param filename The name of the produced symbol file.
   */
  public void storeSymbols(ASTUCDArtifact ast, String filename) {
    UCDDeSer deSer = new UCDDeSer();
    String serialized = deSer.serialize((UCDArtifactScope) ast.getEnclosingScope());
    FileReaderWriter.storeInFile(Paths.get(filename), serialized);
  }

  /**
   * Loads the symbols from the symbol file filename and returns the symbol table.
   *
   * @param filename Name of the symbol file to load.
   * @return Artifact scope of loaded symbol table.
   */
  public IUCDArtifactScope loadSymbols(String filename) {
    UCDSymbols2Json deSer = new UCDSymbols2Json();
    return deSer.load(filename);
  }

  /**
   * Checks whether the UCD "from" is a refinement of the UCD "to".
   * Returns an empty set if "from" is a refinement of "to".
   * Returns an element in the semantics of "from" that is no element in the semantics of "to" if
   * "from" is no refinement of "to".
   *
   * @param from UCD for which it checked whether it refines the UCD "to"
   * @param to   UCD for which it is checked whether "from" refines it
   * @return Diff witness contained in the semantic difference from "from" to "to"
   */
  public Set<Scenario> semDiff(ASTUCDArtifact from, ASTUCDArtifact to) {
    return SemUCDDiff.diff(from, to);
  }

}
