/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.misc.NotNull;
import ucd._ast.ASTUCDUseCase;

public class SpecializesRefUCExists implements UCDASTUCDUseCaseCoCo {
  public static final String ERROR_CODE = "0xC1202";

  protected static final String MESSAGE =
    "The use case '%s' referenced by '%s' does not exist in the use case diagram.";


  @Override
  public void check(@NotNull ASTUCDUseCase node){
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    node.getSupList().forEach(uc -> {
      if (node.getEnclosingScope().resolveUCDUseCaseMany(uc).isEmpty()) {
        Log.error(ERROR_CODE + " " + String.format(MESSAGE, uc, node.getName()),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
        );
      }
    });
  }
}
