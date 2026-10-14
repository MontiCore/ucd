package ucd._cocos;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import ucd._ast.ASTUCDUseCase;

import static de.monticore.codegen.CodeGenVisitorState.LOG_NAME;

public class UCPreconditionIsBoolean implements UCDASTUCDUseCaseCoCo{
  public static final String ERROR_CODE = "0xC1205";

  protected static final String MESSAGE =
    "The type of the precondition must be of type boolean but it is of type '%s'.";

  @Override
  public void check(ASTUCDUseCase node){
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

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
