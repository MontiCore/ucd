/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
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
import static ucd._cocos.UCPreconditionIsBoolean.ERROR_CODE;

/**
 * Tests for the {@link UCPreconditionIsBoolean} context condition.
 *
 * <p>These tests verify that the CoCo reports errors when a use case
 * precondition is not of type {@code boolean} and does not report errors
 * when the precondition has the correct type.
 *
 * <p>The tests are based on a different semantic interpretation in which
 * variables used in preconditions are not implicitly introduced as boolean
 * variables. Instead, these variables must already exist as symbols in the
 * scope with an explicitly defined type.
 */
public class UCPreconditionIsBooleanTest extends UCDTestBase {
  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(String model) {
    Preconditions.checkNotNull(model);

    // Given
    VariableSymbol isPremium = BasicSymbolsMill.variableSymbolBuilder()
      .setName("isPremium")
      .setType(SymTypeExpressionFactory.createPrimitive("boolean"))
      .build();

    UCDMill.globalScope().add(isPremium);
    ASTUCDArtifact ast = compile(model);



    UCDCoCoChecker checker = new UCDCoCoChecker();
    checker.addCoCo(new UCPreconditionIsBoolean());

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
        """)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(String model, String[] expectedErrors) {
    Preconditions.checkNotNull(model);

    // Given
    VariableSymbol isPremium = BasicSymbolsMill.variableSymbolBuilder()
      .setName("isPremium")
      .setType(SymTypeExpressionFactory.createPrimitive("boolean"))
      .build();

    UCDMill.globalScope().add(isPremium);
    ASTUCDArtifact ast = compile(model);

    UCDCoCoChecker checker = new UCDCoCoChecker();
    checker.addCoCo(new UCPreconditionIsBoolean());

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
          ChangeProfilePicture [1];
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
          ChangeProfilePicture [1];
          Pay [2];
          }
        """,
        new String[] {ERROR_CODE,ERROR_CODE})
    );
  }
}
