/* (c) https://github.com/MontiCore/monticore */
package parser;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import ucd.UCDMill;
import ucd._ast.ASTUCDArtifact;
import ucd._parser.UCDParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class ParseTest {

  private final UCDParser parser = UCDMill.parser();

  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    UCDMill.reset();
    UCDMill.init();
  }


  @Test
  public void parseSwimmyFish() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/parser/SwimmyFish.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseSwimmyFish1() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseSwimmyFish2() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd");
    assertTrue(ucd.isPresent());
  }


  @Test
  public void parseBatteryLoading() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/parser/BatteryLoading.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseExample() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/parser/Example.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseFeatureBroadcastPosition() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/FeatureBroadcastPosition.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseFeatureNavigation() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/FeatureNavigation.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseCarCharging1() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/CarCharging1.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseCarCharging2() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/CarCharging2.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseVTOL1() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/VTOL1.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseVTOL2() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/VTOL2.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseOperateVehicle() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/OperateVehicle.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseOperatePremiumVehicle() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/OperatePremiumVehicle.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseSecurityEnterpriseUseCases() throws IOException {
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/SecurityEnterprise.ucd");
    assertTrue(ucd.isPresent());
  }

}
