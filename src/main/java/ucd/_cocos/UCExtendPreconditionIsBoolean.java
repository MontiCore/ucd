/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import ucd._ast.ASTUCDExtend;

import static de.monticore.codegen.CodeGenVisitorState.LOG_NAME;

/**
 * Context condition that checks the extend precondition of an use case is of
 * type {@code boolean}.
 *
 * <p>This CoCo determines the type of the precondition expression and checks
 * whether it is a boolean type. If the type is not boolean, an error is
 * reported. Expressions with an obscure type are skipped.
 *
 * <p>This CoCo implements a different semantic interpretation of use cases and
 * assumes that preconditions are introduced explicitly.
 * It does not handle the implicit introduction of preconditions.
 * Therefore, preconditions that would otherwise be introduced implicitly may not be
 * considered by this CoCo.
 */
public class UCExtendPreconditionIsBoolean implements UCDASTUCDExtendCoCo {
  public static final String ERROR_CODE = "0xC1209";

  protected static final String MESSAGE =
    "The type of the precondition must be of type boolean but it is of type '%s'.";

  @Override
  public void check(ASTUCDExtend node) {
    Preconditions.checkNotNull(node);

    if(node.isPresentExpression()){
      SymTypeExpression type = TypeCheck3.typeOf(node.getExpression());

      if(type.isObscureType()){
        Log.debug(() -> "Skip CoCo check, the type of the constraint is obscure.", LOG_NAME);
      } else if(!SymTypeRelations.isBoolean(type)){
        Log.error(ERROR_CODE + " " + String.format(MESSAGE, node.getExpression().getClass().getTypeName()),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
      }
    }
  }
}
