package semdiff;

import org.junit.Test;
import ucd.UCDMill;
import ucd._ast.ASTUCDArtifact;
import ucd._ast.ASTUseCaseDiagram;
import ucd._parser.UCDParser;
import ucd.semdiff.Scenario;
import ucd.semdiff.SemUCDDiff;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("all")
public class EvaluationTest {

  private final UCDParser parser = UCDMill.parser();

  @Test
  public void sizes() throws IOException {
    ASTUCDArtifact cc1 = parser.parse("src/test/resources/semdiff/CarCharging1.ucd").get();
    ASTUCDArtifact cc2 = parser.parse("src/test/resources/semdiff/CarCharging2.ucd").get();

    ASTUCDArtifact sf1 = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd").get();
    ASTUCDArtifact sf2 = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd").get();

    ASTUCDArtifact fbp = parser.parse("src/test/resources/semdiff/FeatureBroadcastPosition.ucd").get();
    ASTUCDArtifact fn = parser.parse("src/test/resources/semdiff/FeatureNavigation.ucd").get();

    ASTUCDArtifact ov = parser.parse("src/test/resources/semdiff/OperateVehicle.ucd").get();
    ASTUCDArtifact opv = parser.parse("src/test/resources/semdiff/OperatePremiumVehicle.ucd").get();

    ASTUCDArtifact se = parser.parse("src/test/resources/semdiff/SecurityEnterprise.ucd").get();
    ASTUCDArtifact sec = parser.parse("src/test/resources/semdiff/SecurityEnterpriseCorrection.ucd").get();

    ASTUCDArtifact vtol1 = parser.parse("src/test/resources/semdiff/VTOL1.ucd").get();
    ASTUCDArtifact vtol2 = parser.parse("src/test/resources/semdiff/VTOL2.ucd").get();
  }

}
