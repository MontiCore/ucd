/* (c) https://github.com/MontiCore/monticore */
package ucd;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.misc.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import ucd._ast.ASTUCDArtifact;
import ucd.check.UCDTypeCheck;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class UCDTestBase {
  /**
   * We initialize the log before all tests. The log is therefore available
   * to the parameter provider of parameterized tests.
   */
  @BeforeAll
  protected static void initLog() {
    LogStub.init();
  }

  /**
   * We catch errors logged before test execution. These may indicate erroneous
   * parameter providers and log-caches persisting across tests.
   */
  @BeforeEach
  protected void assertLogCleared() {
    assertTrue(Log.getFindings().isEmpty());
  }

  @BeforeAll
  protected static void initMill() {
    UCDMill.init();
    UCDMill.globalScope().clear();

    UCDTypeCheck.init();
  }

  @BeforeEach
  protected void init() {
    BasicSymbolsMill.initializePrimitives();
    BasicSymbolsMill.initializeObject();
    BasicSymbolsMill.initializeString();
  }

  /**
   * We clear the log after every test ensuring findings are removed between
   * tests. We do not clear the log before tests so that errors logged by
   * parameter providers may be caught.
   */
  @AfterEach
  protected void clearLog() {
    LogStub.clearFindings();
    LogStub.clearPrints();
  }

  @AfterEach
  protected void clearGlobalScope() {
    UCDMill.globalScope().clear();
  }

  public static ASTUCDArtifact compile(@NotNull String model) {
    Preconditions.checkNotNull(model);
    try {
      ASTUCDArtifact ast = UCDMill.parser().parse_StringUCDArtifact(model)
        .orElseThrow(() -> new IllegalStateException(Log.getFindings().toString()));
      UCDMill.scopesGenitorDelegator().createFromAST(ast);
      return ast;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public static Arguments arg(Object... objects) {
    return Arguments.of(objects);
  }

  public static String[] getLoggedErrorCodes() {
    return Log.getFindings().stream()
      .map(Finding::getMsg)
      .map(msg -> msg.substring(0, 7))
      .filter(Pattern.compile("0x[0-9a-fA-F]{5}").asPredicate())
      .toArray(String[]::new);
  }
}

