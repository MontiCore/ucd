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
import static ucd._cocos.UCExtendPreconditionIsBoolean.ERROR_CODE;


public class UCExtendPreconditionIsBooleanTest extends UCDTestBase {
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
      checker.addCoCo(new UCExtendPreconditionIsBoolean());

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
      ASTUCDArtifact ast = compile(model);

      UCDCoCoChecker checker = new UCDCoCoChecker();
      checker.addCoCo(new UCExtendPreconditionIsBoolean());

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
            ShowAd extend Play [1];
            RegisterScore extend Play;
            abstract Pay include CheckPremium;
            CreditCard specializes Pay;
            Bank specializes Pay;
            ChangeProfilePicture [isPremium];
            Pay;
            }
          """,
          new String[] {ERROR_CODE})
      );
    }
}
