/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ucd.UCDMill;
import ucd.UCDTestBase;
import ucd._ast.ASTUCDArtifact;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ucd._cocos.UniqueUCName.ERROR_CODE;

/**
 * Tests for the {@link UniqueUCName} context condition.
 *
 * <p>These tests verify that the CoCo reports errors when a use case name
 * is used more than once in the use case diagram and does not report errors
 * when all use case names are unique.
 *
 * <p>The tests are based on a different semantic interpretation in which
 * use cases introduced implicitly through relationships are not considered
 * by this CoCo. Therefore, the CoCo checks only explicitly defined use cases
 * for duplicate names.
 */
public class UniqueUCNameTest extends UCDTestBase {
  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(String model) {
    Preconditions.checkNotNull(model);

    // Given
    UCDMill.globalScope().add(UCDMill.uCDUseCaseSymbolBuilder().setName("Play").build());
    UCDMill.globalScope().add(UCDMill.uCDUseCaseSymbolBuilder().setName("CheckPremium").build());
    ASTUCDArtifact ast = compile(model);

    UCDCoCoChecker checker = new UCDCoCoChecker();
    checker.addCoCo(new UniqueUCName());

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty());

  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      arg("""
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
        """),
      arg("""
          usecasediagram ucd2 {
          @Player -- Play, Pay;
          @Actor2;
          @AndroidPlayer specializes Player;
          @IOSPlayer specializes Actor2;
          ShowAd extend Play [!isPremium];
          RegisterScore extend Play;
          abstract Pay include CheckPremium;
          CreditCard specializes Pay;
          Bank specializes Pay;
          ChangeProfilePicture [isPremium];
          }
        """),
      arg("""
          usecasediagram ucd3 {
          @Player -- Play, Pay;
          @Player;
          @AndroidPlayer specializes Player;
          @IOSPlayer specializes Player;
          ShowAd extend Play [!isPremium];
          RegisterScore extend Play;
          abstract Pay include CheckPremium;
          CreditCard specializes Pay;
          Bank specializes Pay;
          ChangeProfilePicture [isPremium];
          }
        """)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(String model, String[] expectedErrors) {
    Preconditions.checkNotNull(model);

    // Given
    UCDMill.globalScope().add(UCDMill.uCDUseCaseSymbolBuilder().setName("Play").build());
    UCDMill.globalScope().add(UCDMill.uCDUseCaseSymbolBuilder().setName("CheckPremium").build());
    ASTUCDArtifact ast = compile(model);

    UCDCoCoChecker checker = new UCDCoCoChecker();
    checker.addCoCo(new UniqueUCName());

    // When
    checker.checkAll(ast);

    // Then
    assertEquals(
      expectedErrors.length,
      Log.getFindings().size(),
      () -> Log.getFindings().toString()
    );

    assertArrayEquals(
      expectedErrors,
      getLoggedErrorCodes(),
      () -> Log.getFindings().toString()
    );

    for (Finding finding : Log.getFindings()) {
      assertTrue(finding.isError());
    }
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("""
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
          Pay;
          }
        """,
        new String[] {ERROR_CODE}),
      arg("""
          usecasediagram ucd2 {
          @Player -- Play, Pay;
          @AndroidPlayer specializes Player;
          @IOSPlayer specializes Player;
          ShowAd extend Play [!isPremium];
          RegisterScore extend Play;
          abstract Pay include CheckPremium;
          CreditCard specializes Pay;
          Bank specializes Pay;
          ChangeProfilePicture [isPremium];
          Pay;
          RegisterScore;
          }
        """,
        new String[] {ERROR_CODE, ERROR_CODE}),
      arg("""
          usecasediagram ucd3 {
          @Player -- Play, Pay;
          @AndroidPlayer specializes Player;
          @IOSPlayer specializes Player;
          ShowAd extend Play [!isPremium];
          RegisterScore extend Play;
          abstract Pay include CheckPremium;
          CreditCard specializes Pay;
          Bank specializes Pay;
          ChangeProfilePicture [isPremium];
          Pay;
          Play;
          }
        """,
        new String[] {ERROR_CODE})
    );
  }
}
