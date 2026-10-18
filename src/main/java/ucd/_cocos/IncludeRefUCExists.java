/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.misc.NotNull;
import ucd._ast.ASTUCDUseCase;

/**
 * Context condition that checks whether all use cases referenced by an
 * {@code include} relationship exist in the enclosing scope.
 *
 * <p>For each {@code include} relationship of an use case, this CoCo checks
 * whether a corresponding use case symbol can be resolved. If no matching
 * symbol exists, an error is reported.
 *
 * <p>This CoCo implements a different semantic interpretation of use cases and
 * assumes that the referenced use cases are already available as symbols in the scope.
 * It does not handle the implicit introduction of use cases through relationships.
 * Therefore, use cases that would otherwise be introduced implicitly may be reported
 * as missing if their symbols have not been created.
 */
public class IncludeRefUCExists implements UCDASTUCDUseCaseCoCo{
  public static final String ERROR_CODE = "0xC1200";

  protected static final String MESSAGE =
    "The use case '%s' referenced by '%s' does not exist in the use case diagram.";


  @Override
  public void check(@NotNull ASTUCDUseCase node){
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    node.getInclList().forEach(uc -> {
      if (node.getEnclosingScope().resolveUCDUseCaseMany(uc).isEmpty()) {
        Log.error(ERROR_CODE + " " + String.format(MESSAGE, uc, node.getName()),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
        );
      }
    });
  }
}
