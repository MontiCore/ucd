/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import ucd._ast.ASTUCDUseCase;

public class ExtendRefUCExists implements UCDASTUCDUseCaseCoCo{
  public static final String ERROR_CODE = "0xC1201";

  protected static final String MESSAGE =
    "The use case '%s' referenced by '%s' does not exist in the use case diagram.";

  @Override
  public void check(ASTUCDUseCase node){
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    node.getUCDExtendList().forEach(extend -> {
      if(node.getEnclosingScope()
        .resolveUCDUseCaseMany(extend.getName())
        .isEmpty()){
        Log.error(ERROR_CODE + " " + String.format(MESSAGE, extend.getName(), node.getName()),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
        );
      }
    });
  }
}
