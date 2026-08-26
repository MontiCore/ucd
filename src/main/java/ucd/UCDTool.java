/* (c) https://github.com/MontiCore/monticore */
package ucd;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import ucd._ast.ASTUCDArtifact;
import ucd._symboltable.IUCDArtifactScope;
import ucd._symboltable.IUCDGlobalScope;
import ucd._symboltable.UCDArtifactScope;
import ucd._symboltable.UCDSymbols2Json;
import ucd.semdiff.Scenario;
import ucd.semdiff.SemUCDDiff;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CLI tool providing functionality for processing Sequence Diagram (SD) artifacts.
 */
public class UCDTool extends UCDToolTOP {

  @Override
  protected void doRun(CommandLine cmd) {
    // Mandatory option
    if (!cmd.hasOption("i")) {
      printHelp(initOptions());
      return;
    }

    // Parse input SDs
    List<ASTUCDArtifact> inputUCDs = new ArrayList<>();
    for (String inputFileName : cmd.getOptionValues("i")) {
      ASTUCDArtifact ast = parse(inputFileName);
      if (ast != null) inputUCDs.add(ast);
    }

    // semantic differencing
    if (cmd.hasOption("sd")) {
      if (inputUCDs.size() != 2) {
        Log.error(String.format("Received %s input UCDs. However, the option 'semdiff' requires exactly two input UCDs.", inputUCDs.size()));
        return;
      }
      Set<Scenario> witnesses = semDiff(inputUCDs.get(0), inputUCDs.get(1));
      if (!witnesses.isEmpty()) {
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
    MCPath modelPath = new MCPath(Paths.get(""));
    if (cmd.hasOption("path")) {
      modelPath = new MCPath(Arrays.stream(cmd.getOptionValues("path")).map(x -> Paths.get(x)).collect(Collectors.toList()));
    }

    IUCDGlobalScope globalScope = UCDMill.globalScope();
    globalScope.setSymbolPath(modelPath);

    if (cmd.hasOption("s")) {
      for (ASTUCDArtifact ucd : inputUCDs) {
        createSymbolTable(ucd);
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
          UCDSymbols2Json deSer = new UCDSymbols2Json();
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
          storeSymbols((UCDArtifactScope) ucd_i.getEnclosingScope(), cmd.getOptionValues("s")[i]);
        }
      }
    }
  }

  @Override
  public Options addAdditionalOptions(Options options) {
    // inputs
    options.addOption(Option.builder("i").longOpt("input").hasArgs().desc("Processes the list of UCD input artifacts. " +
        "Argument list is space separated.").build());

    // semantic diff
    options.addOption(Option.builder("sd").longOpt("semdiff").desc("Computes a diff witness showing the asymmetrical semantic difference " +
        "of two UCDs. Requires two " +
        "UCDs as inputs. See se-rwth.de/topics for scientific foundation.").build());

    // store symbols
    options.addOption(Option.builder("s").longOpt("symboltable").optionalArg(true).hasArgs()
        .desc("Stores the symbol tables of the input UCDs in the specified files. " + "The n-th input " +
            "UCD is stored in the file as specified by the n-th argument. " +
            "Default is 'target/symbols/{packageName}/{artifactName}.ucdsym'.").build());

    // model paths
    //    options.addOption(Option.builder("path").hasArgs().desc("Sets the artifact path for imported symbols, space separated.").build());

    return options;
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
