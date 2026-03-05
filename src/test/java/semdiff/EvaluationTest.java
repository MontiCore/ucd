/* (c) https://github.com/MontiCore/monticore */
package semdiff;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucd.UCDMill;
import ucd._ast.ASTUCDArtifact;
import ucd._ast.ASTUseCaseDiagram;
import ucd._parser.UCDParser;
import ucd.semdiff.SemUCDDiff;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("all")
public class EvaluationTest {

  private final UCDParser parser = UCDMill.parser();
  private static ASTUCDArtifact cc1, cc2, sf1, sf2, fbp, fn, ov, opv, se, sec, vtol1, vtol2;
  
  @BeforeAll
  static void beforeAll() throws IOException{
    UCDMill.init();
    UCDParser parser = UCDMill.parser();
    
    cc1 = parser.parse("src/test/resources/semdiff/CarCharging1.ucd").get();
    cc2 = parser.parse("src/test/resources/semdiff/CarCharging2.ucd").get();
    sf1 = parser.parse("src/test/resources/semdiff/SwimmyFish1.ucd").get();
    sf2 = parser.parse("src/test/resources/semdiff/SwimmyFish2.ucd").get();
    fbp = parser.parse("src/test/resources/semdiff/FeatureBroadcastPosition.ucd").get();
    fn = parser.parse("src/test/resources/semdiff/FeatureNavigation.ucd").get();
    ov = parser.parse("src/test/resources/semdiff/OperateVehicle.ucd").get();
    opv = parser.parse("src/test/resources/semdiff/OperatePremiumVehicle.ucd").get();
    se = parser.parse("src/test/resources/semdiff/SecurityEnterprise.ucd").get();
    sec = parser.parse("src/test/resources/semdiff/SecurityEnterpriseCorrection.ucd").get();
    vtol1 = parser.parse("src/test/resources/semdiff/VTOL1.ucd").get();
    vtol2 = parser.parse("src/test/resources/semdiff/VTOL2.ucd").get();
  }
  
  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    UCDMill.reset();
    UCDMill.init();
  }


  public EvaluationTest() throws IOException {
  }

  @Test
  public void sizes() throws IOException {
    String sizeTable = "UCD & $|U|$ & $|A|$ & $|Abs|$ & $|R|$ & $|G_U|$ & $|G_A|$ & $|E|$ & $|G|$ \\\\ \\cline{1-9} \\\\ \r\n ";
    sizeTable += makeSizeRow("CC1", cc1);
    sizeTable += makeSizeRow("CC2", cc2);
    sizeTable += makeSizeRow("SF1", sf1);
    sizeTable += makeSizeRow("SF2", sf2);
    sizeTable += makeSizeRow("FBP", fbp);
    sizeTable += makeSizeRow("FN", fn);
    sizeTable += makeSizeRow("OV", ov);
    sizeTable += makeSizeRow("OPV", opv);
    sizeTable += makeSizeRow("SE", se);
    sizeTable += makeSizeRow("SEC", sec);
    sizeTable += makeSizeRow("VTOL1", vtol1);
    sizeTable += makeSizeRow("VTOL2", vtol2);

    System.out.println(sizeTable);
  }

  public String makeSizeRow(String name, ASTUCDArtifact ast) {
    ASTUseCaseDiagram ucd = ast.getUseCaseDiagram();

    Set<String> abstractUCs = new LinkedHashSet<>(ucd.getUseCases());
    abstractUCs.removeAll(ucd.getAllNonAbstractUCs());

    Set<String> abstractActors = new LinkedHashSet<>(ucd.getAllActorNames());
    abstractActors.removeAll(ucd.getAllNonAbstractActors());

    String res = name + " & ";
    res += ucd.getUseCases().size() + " & ";
    res += ucd.getAllActorNames().size() + " & ";
    res += (abstractUCs.size() + abstractActors.size()) + " & ";
    res += ucd.getAssociations().size() + " & ";
    res += ucd.getUCGeneralizationRelation().size() + " & ";
    res += ucd.getActorGeneralizationRelation().size() + " & ";
    res += ucd.getUnguardedExtendRelation().size() + " & ";
    res += ucd.getGuardedExtendRelation().size() + "\\\\ \r\n";

    return res;
  }

  @Test
  public void computationTimes() throws IOException {
    // one diff for initializations
    SemUCDDiff.diff(cc1, cc1);

    String sizeTable = "\\ & CC1 & CC2 & SF1 & SF2 & FBP & FN & OV & OPV & SE & SEC & VTOL1 & VTOL2 \\\\ \\cline{1-13} \\\\ \r\n";
    sizeTable += makeTimeEvalRow("CC1", cc1);
    sizeTable += makeTimeEvalRow("CC2", cc2);
    sizeTable += makeTimeEvalRow("SF1", sf1);
    sizeTable += makeTimeEvalRow("SF2", sf2);
    sizeTable += makeTimeEvalRow("FBP", fbp);
    sizeTable += makeTimeEvalRow("FN", fn);
    sizeTable += makeTimeEvalRow("OV", ov);
    sizeTable += makeTimeEvalRow("OPV", opv);
    sizeTable += makeTimeEvalRow("SE", se);
    sizeTable += makeTimeEvalRow("SEC", sec);
    sizeTable += makeTimeEvalRow("VTOL1", vtol1);
    sizeTable += makeTimeEvalRow("VTOL2", vtol2);

    System.out.println(sizeTable);
  }

  public String makeTimeEvalRow(String name, ASTUCDArtifact ast) {
    ASTUseCaseDiagram ucd = ast.getUseCaseDiagram();

    String res = name + " & ";

    long timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, cc1);
    long duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, cc2);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, sf1);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, sf2);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, fbp);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, fn);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, ov);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, opv);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, se);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, sec);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, vtol1);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " & ";

    timeStart = System.currentTimeMillis();
    SemUCDDiff.diff(ast, vtol2);
    duration = System.currentTimeMillis() - timeStart;
    res += duration + " \\\\ \r\n";

    return res;
  }

}
