/* (c) https://github.com/MontiCore/monticore */
package cli;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucd.UCDTool;
import ucd._ast.ASTUCDArtifact;
import ucd._symboltable.IUCDArtifactScope;
import ucd._symboltable.UCDActorSymbol;
import ucd._symboltable.UCDArtifactScope;
import ucd._symboltable.UCDUseCaseSymbol;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UCDCLITest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.initWARN();
  }

  @Test
  public void parse1() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/parser/BatteryLoading.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse2() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/parser/SwimmyFish.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse3() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/CarCharging1.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse4() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/CarCharging2.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse5() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/FeatureBroadcastPosition.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse6() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/FeatureNavigation.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse7() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/OperatePremiumVehicle.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse8() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/OperateVehicle.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse9() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/SecurityEnterprise.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse10() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/SecurityEnterpriseCorrection.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse11() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/SwimmyFish1.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse12() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/SwimmyFish2.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse13() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/VTOL1.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void parse14() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/VTOL2.ucd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void toolDeSerTest() throws IOException {
    UCDTool cli = new UCDTool();
    Set<String> testModels = new LinkedHashSet<>();
    testModels.add("CarCharging1");
    testModels.add("CarCharging2");
    testModels.add("FeatureBroadcastPosition");
    testModels.add("FeatureNavigation");
    testModels.add("OperatePremiumVehicle");
    testModels.add("OperateVehicle");
    testModels.add("SecurityEnterprise");
    testModels.add("SecurityEnterpriseCorrection");
    testModels.add("SwimmyFish1");
    testModels.add("SwimmyFish2");
    testModels.add("VTOL1");
    testModels.add("VTOL2");

    for (String model : testModels) {
      ASTUCDArtifact ast = cli.parse("src/test/resources/semdiff/" + model +".ucd");
      assertNotNull(ast);
      String symbolFileName = "target/symbols/" + ast.getUseCaseDiagram().getName() + ".ucdsym";
      cli.createSymbolTable(ast);
      UCDArtifactScope artifactScope = (UCDArtifactScope) ast.getEnclosingScope();

      cli.storeSymbols((UCDArtifactScope) ast.getEnclosingScope(), symbolFileName);
      IUCDArtifactScope loadedST = cli.loadSymbols(symbolFileName);

      assertEquals(0, loadedST.getSubScopes().size());
      assertEquals(1, loadedST.getLocalDiagramSymbols().size());
      assertEquals(1, artifactScope.getLocalDiagramSymbols().size());
      assertEquals(artifactScope.getLocalDiagramSymbols().get(0).getName(), loadedST.getLocalDiagramSymbols().get(0).getName());
      for (UCDUseCaseSymbol ucSym : artifactScope.getLocalUCDUseCaseSymbols()) {
        assertTrue(loadedST.getLocalUCDUseCaseSymbols().stream().anyMatch(x -> x.getFullName().equals(ucSym.getFullName())));
      }
      for (UCDUseCaseSymbol ucSym : loadedST.getLocalUCDUseCaseSymbols()) {
        assertTrue(artifactScope.getLocalUCDUseCaseSymbols().stream().anyMatch(x -> x.getFullName().equals(ucSym.getFullName())));
      }
      for (UCDActorSymbol actSym : artifactScope.getLocalUCDActorSymbols()) {
        assertTrue(loadedST.getLocalUCDActorSymbols().stream().anyMatch(x -> x.getFullName().equals(actSym.getFullName())));
      }
      for (UCDActorSymbol actSym : loadedST.getLocalUCDActorSymbols()) {
        assertTrue(artifactScope.getLocalUCDActorSymbols().stream().anyMatch(x -> x.getFullName().equals(actSym.getFullName())));
      }
      assertEquals(0, Log.getErrorCount());
    }
  }

  @Test
  public void semDiff1() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/SwimmyFish1.ucd", "src/test/resources/semdiff/SwimmyFish2.ucd", "-sd" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void semDiff2() {
    new UCDTool().run(new String[] { "-i", "src/test/resources/semdiff/SwimmyFish2.ucd", "src/test/resources/semdiff/SwimmyFish1.ucd", "-sd" });
    assertEquals(0, Log.getErrorCount());
  }

}
