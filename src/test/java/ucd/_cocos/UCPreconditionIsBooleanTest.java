package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ucd.UCDTestBase;
import ucd._ast.ASTUCDArtifact;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ucd._cocos.UCPreconditionIsBoolean.ERROR_CODE;

/**
 * TODO:
 * The test is disabled because the variables used in the expressions should be introduced implicitly, as stated in the resp. grammar, but they are not.
 * Discuss if this implicit introduction should remain:
 * If yes, add the respective symbol.
 * If no, discuss how it should be introduced instead.
 * Furthermore, adjust the test cases accordingly and add more test cases.
  */


public class UCPreconditionIsBooleanTest extends UCDTestBase {
//  @ParameterizedTest
//  @MethodSource("validModels")
//  void shouldNotReportError(String model) {
//    Preconditions.checkNotNull(model);
//
//    // Given
//    ASTUCDArtifact ast = compile(model);
//
//    UCDCoCoChecker checker = new UCDCoCoChecker();
//    checker.addCoCo(new UCPreconditionIsBoolean());
//
//    // When
//    checker.checkAll(ast);
//
//    // Then
//    assertTrue(Log.getFindings().isEmpty());
//
//  }
//
//  static Stream<Arguments> validModels() {
//    return Stream.of(
//      arg("""
//          usecasediagram ucd1 {
//          @Player -- Play, Pay;
//          @AndroidPlayer specializes Player;
//          @IOSPlayer specializes Player;
//          ShowAd extend Play [!isPremium];
//          RegisterScore extend Play;
//          abstract Pay include CheckPremium;
//          CreditCard specializes Pay;
//          Bank specializes Pay;
//          ChangeProfilePicture [isPremium];
//          }
//        """)
//    );
//  }
//
//  @ParameterizedTest
//  @MethodSource("invalidModels")
//  void shouldReportError(String model, String[] expectedErrors) {
//    Preconditions.checkNotNull(model);
//
//    // Given
//    ASTUCDArtifact ast = compile(model);
//
//    UCDCoCoChecker checker = new UCDCoCoChecker();
//    checker.addCoCo(new UCPreconditionIsBoolean());
//
//    // When
//    checker.checkAll(ast);
//
//    // Then
//    assertEquals(
//      expectedErrors.length,
//      Log.getFindings().size(),
//      () -> Log.getFindings().toString()
//    );
//
//    assertArrayEquals(
//      expectedErrors,
//      getLoggedErrorCodes(),
//      () -> Log.getFindings().toString()
//    );
//
//    for (Finding finding : Log.getFindings()) {
//      assertTrue(finding.isError());
//    }
//  }
//
//  static Stream<Arguments> invalidModels() {
//    return Stream.of(
//      arg("""
//          usecasediagram ucd1 {
//          @Player -- Play, Pay;
//          @AndroidPlayer specializes Player;
//          @IOSPlayer specializes Player;
//          ShowAd extend Play [!isPremium];
//          RegisterScore extend Play;
//          abstract Pay include CheckPremium;
//          CreditCard specializes Pay;
//          Bank specializes Pay;
//          ChangeProfilePicture [isPremium];
//          Pay;
//          }
//        """,
//        new String[] {ERROR_CODE})
//    );
//  }
}
