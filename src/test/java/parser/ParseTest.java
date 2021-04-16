package parser;

import de.se_rwth.commons.logging.Log;
import org.junit.Test;
import ucd.UCDMill;
import ucd._ast.ASTUCDArtifact;
import ucd._parser.UCDParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class ParseTest {

  private final UCDParser parser = UCDMill.parser();

  @Test
  public void parseSwimmyFish() throws IOException {
    Log.enableFailQuick(false);
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/parser/SwimmyFish.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseSwimmyFish1() throws IOException {
    Log.enableFailQuick(false);
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseSwimmyFish2() throws IOException {
    Log.enableFailQuick(false);
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd");
    assertTrue(ucd.isPresent());
  }


  @Test
  public void parseBatteryLoading() throws IOException {
    Log.enableFailQuick(false);
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/parser/BatteryLoading.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseFeatureBroadcastPosition() throws IOException {
    Log.enableFailQuick(false);
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/FeatureBroadcastPosition.ucd");
    assertTrue(ucd.isPresent());
  }

  @Test
  public void parseFeatureNavigation() throws IOException {
    Log.enableFailQuick(false);
    Optional<ASTUCDArtifact> ucd = parser.parse("src/test/resources/semdiff/FeatureNavigation.ucd");
    assertTrue(ucd.isPresent());
  }

}
