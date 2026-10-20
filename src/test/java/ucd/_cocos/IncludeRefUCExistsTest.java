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
import static ucd._cocos.IncludeRefUCExists.ERROR_CODE;

/**
 * Tests for the {@link IncludeRefUCExists} context condition.
 *
 * <p>These tests verify that the CoCo reports errors when a
 * referenced use case cannot be resolved in the scope and does not report
 * errors when the corresponding symbol exists.
 *
 * <p>The tests are based on a different semantic interpretation in which
 * use cases referenced by relationships must already exist as symbols in
 * the scope rather than being introduced implicitly by the relationships.
 */
public class IncludeRefUCExistsTest extends UCDTestBase {
  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(String model) {
    Preconditions.checkNotNull(model);

    // Given
    UCDMill.globalScope().add(UCDMill.uCDUseCaseSymbolBuilder().setName("CheckPremium").build());
    UCDMill.globalScope().add(UCDMill.uCDUseCaseSymbolBuilder().setName("BankTransfer").build());
    ASTUCDArtifact ast = compile(model);

    UCDCoCoChecker checker = new UCDCoCoChecker();
    checker.addCoCo(new IncludeRefUCExists());

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
          @AndroidPlayer specializes Player;
          @IOSPlayer specializes Player;
          ShowAd extend Play [!isPremium];
          RegisterScore extend Bank;
          abstract Pay include CheckPremium;
          CreditCard specializes Pay;
          Bank specializes Pay;
          ChangeProfilePicture [isPremium];
          Pay include BankTransfer;
          }
        """)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(String model, String[] expectedErrors) {
    Preconditions.checkNotNull(model);

    // Given
    UCDMill.globalScope().add(UCDMill.uCDUseCaseSymbolBuilder().setName("OrderCheck").build());
    ASTUCDArtifact ast = compile(model);

    UCDCoCoChecker checker = new UCDCoCoChecker();
    checker.addCoCo(new IncludeRefUCExists());

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
            }
          """,
        new String[]{ERROR_CODE}),
      arg("""
            usecasediagram ucd2 {
            @Player -- Play, Pay;
            @AndroidPlayer specializes Player;
            @IOSPlayer specializes actor2;
            ShowAd extend Play [!isPremium];
            RegisterScore extend Scoreboard;
            abstract Pay include CheckPremium;
            CreditCard specializes Pay;
            Bank specializes Pay;
            ChangeProfilePicture [isPremium];
            Pay include BankTransfer;
            }
          """,
        new String[]{ERROR_CODE, ERROR_CODE}),
      arg("""
            usecasediagram ucd3 {
            @Player -- Play, Pay;
            @AndroidPlayer specializes Player;
            @IOSPlayer specializes actor1;
            ShowAd extend Play [!isPremium];
            RegisterScore extend Bank;
            abstract Pay include OrderCheck;
            CreditCard specializes Pay;
            Bank specializes Pay;
            ChangeProfilePicture [isPremium];
            Pay include BankTransfer;
            }
          """,
        new String[]{ERROR_CODE})
    );
  }
}
