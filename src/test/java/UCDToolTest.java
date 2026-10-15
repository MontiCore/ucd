/* (c) https://github.com/MontiCore/monticore */
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import ucd.UCDTestBase;
import ucd.UCDTool;
import ucd._ast.ASTUCDArtifact;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UCDToolTest extends UCDTestBase {
  @Test
  public void testUCDTool() {
    String model = """
          usecasediagram ucd1 {
          @Player -- Play, Pay;
          @AndroidPlayer specializes Player;
          @IOSPlayer specializes Player;
          ShowAd extend Play [!isPremium];
          RegisterScore extend Play;
          abstract Pay include CheckPremium;
          CreditCard specializes Pay;
          Bank specializes Pay;
          ChangeProfilePicture [isPremium];
          }
        """;

    // Given
    ASTUCDArtifact ast = compile(model);

    UCDTool tool = new UCDTool();

    // When
    tool.runAdditionalCoCos(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty());
  }
}
