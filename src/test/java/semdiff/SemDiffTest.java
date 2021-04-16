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
public class SemDiffTest {

  private final UCDParser parser = UCDMill.parser();

  @Test
  public void initSwimmyFish2() throws IOException {
    ASTUCDArtifact swimmyFish2 = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd").get();
    ASTUseCaseDiagram ucd = swimmyFish2.getUseCaseDiagram();
    Set<String> vars = ucd.getVariables();
    assertTrue(vars.contains("isPremium"));
    assertEquals(2, vars.size());
    assertEquals(7, ucd.getAssociations().size());
  }

  @Test
  public void execSwimmyFish2() throws IOException {
    ASTUCDArtifact swimmyFish2 = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd").get();
    ASTUseCaseDiagram ucd = swimmyFish2.getUseCaseDiagram();

    Set<String> exec = SemUCDDiff.exec(ucd, "Play", new HashSet<>());
    assertEquals(2, exec.size());
    assertTrue(exec.contains("Play"));
    assertTrue(exec.contains("ShowAd"));

    Set<String> satVars = new HashSet<>();
    satVars.add("isPremium");
    exec = SemUCDDiff.exec(ucd, "Play", satVars);
    assertEquals(1, exec.size());
    assertTrue(exec.contains("Play"));

    satVars = new HashSet<>();
    satVars.add("gameFinished");
    exec = SemUCDDiff.exec(ucd, "Play", satVars);
    assertEquals(3, exec.size());
    assertTrue(exec.contains("Play"));
    assertTrue(exec.contains("ShowAd"));
    assertTrue(exec.contains("RegisterScore"));

    satVars = new HashSet<>();
    satVars.add("gameFinished");
    satVars.add("isPremium");
    exec = SemUCDDiff.exec(ucd, "Play", satVars);
    assertEquals(2, exec.size());
    assertTrue(exec.contains("Play"));
    assertTrue(exec.contains("RegisterScore"));

    satVars = new HashSet<>();
    exec = SemUCDDiff.exec(ucd, "Pay", satVars);
    assertEquals(2, exec.size());
    assertTrue(exec.contains("Pay"));
    assertTrue(exec.contains("CheckPremium"));

    satVars = new HashSet<>();
    exec = SemUCDDiff.exec(ucd, "CreditCard", satVars);
    assertEquals(1, exec.size());
    assertTrue(exec.contains("CreditCard"));

    satVars = new HashSet<>();
    exec = SemUCDDiff.exec(ucd, "Bank", satVars);
    assertEquals(1, exec.size());
    assertTrue(exec.contains("Bank"));

    satVars = new HashSet<>();
    exec = SemUCDDiff.exec(ucd, "CheckPremium", satVars);
    assertEquals(1, exec.size());
    assertTrue(exec.contains("CheckPremium"));

    satVars = new HashSet<>();
    exec = SemUCDDiff.exec(ucd, "RegisterScore", satVars);
    assertEquals(1, exec.size());
    assertTrue(exec.contains("RegisterScore"));

    satVars = new HashSet<>();
    exec = SemUCDDiff.exec(ucd, "ShowAd", satVars);
    assertEquals(1, exec.size());
    assertTrue(exec.contains("ShowAd"));

    satVars = new HashSet<>();
    exec = SemUCDDiff.exec(ucd, "ChangeProfilePicture", satVars);
    assertEquals(0, exec.size());

    satVars = new HashSet<>();
    satVars.add("isPremium");
    exec = SemUCDDiff.exec(ucd, "ChangeProfilePicture", satVars);
    assertEquals(1, exec.size());
    assertTrue(exec.contains("ChangeProfilePicture"));
  }

  @Test
  public void closureSwimmyFish2() throws IOException {
    ASTUCDArtifact swimmyFish2 = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd").get();
    ASTUseCaseDiagram ucd = swimmyFish2.getUseCaseDiagram();

    Set<Set<String>> closure = SemUCDDiff.closure(ucd, "Pay", new HashSet<>());
    assertEquals(3, closure.size());

    closure = SemUCDDiff.closure(ucd, "ChangeProfilePicture", new HashSet<>());
    assertEquals(0, closure.size());
  }

  @Test
  public void scenariosSwimmyFish2() throws IOException {
    ASTUCDArtifact swimmyFish2 = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd").get();
    ASTUseCaseDiagram ucd = swimmyFish2.getUseCaseDiagram();

    Set<Scenario> scenarios = SemUCDDiff.scn(ucd, "Pay", new HashSet<>());
    assertEquals(3, scenarios.size());

    scenarios = SemUCDDiff.scn(ucd, "ChangeProfilePicture", new HashSet<>());
    assertEquals(0, scenarios.size());

    Set<String> satVars = new HashSet<>();
    satVars.add("isPremium");
    scenarios = SemUCDDiff.scn(ucd, "ChangeProfilePicture", satVars);
    assertEquals(1, scenarios.size());
  }

  @Test
  public void closureSwimmyFish1() throws IOException {
    ASTUCDArtifact swimmyFish1 = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd").get();
    ASTUseCaseDiagram ucd = swimmyFish1.getUseCaseDiagram();

    Set<Set<String>> closure = SemUCDDiff.closure(ucd, "Bank", new HashSet<>());
    assertEquals(1, closure.size());
  }

  @Test
  public void fromSwimmyFish1ToSwimmyFish1() throws IOException {
    ASTUCDArtifact swimmyFish1 = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd").get();
    ASTUCDArtifact swimmyFish11 = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd").get();

    Set<Scenario> diff = SemUCDDiff.diff(swimmyFish1, swimmyFish11);
    assertTrue(diff.isEmpty());
  }

  @Test
  public void fromSwimmyFish1ToSwimmyFish2() throws IOException {
    ASTUCDArtifact swimmyFish1 = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd").get();
    ASTUCDArtifact swimmyFish2 = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd").get();

    Set<Scenario> diff = SemUCDDiff.diff(swimmyFish1, swimmyFish2);
    assertEquals(16, diff.size());
  }

  @Test
  public void fromSwimmyFish2ToSwimmyFish1() throws IOException {
    ASTUCDArtifact swimmyFish1 = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd").get();
    ASTUCDArtifact swimmyFish2 = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd").get();

    Set<Scenario> diff = SemUCDDiff.diff(swimmyFish2, swimmyFish1);
    assertTrue(diff.isEmpty());
  }

  @Test
  public void fromCarCharging1ToCarCharging2() throws IOException {
    ASTUCDArtifact ast1 = parser.parse("src/test/resources/semdiff/CarCharging1.ucd").get();
    ASTUCDArtifact ast2 = parser.parse("src/test/resources/semdiff/CarCharging2.ucd").get();

    Set<Scenario> diff = SemUCDDiff.diff(ast1, ast2);
    assertEquals(3, diff.size());
  }

  @Test
  public void fromCarCharging2ToCarCharging1() throws IOException {
    ASTUCDArtifact ast1 = parser.parse("src/test/resources/semdiff/CarCharging1.ucd").get();
    ASTUCDArtifact ast2 = parser.parse("src/test/resources/semdiff/CarCharging2.ucd").get();

    Set<Scenario> diff = SemUCDDiff.diff(ast2, ast1);
    assertEquals(4, diff.size());
  }

}
