/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.misc.NotNull;
import ucd._ast.ASTUCDActor;

public class UC2ActorExists implements UCDASTUCDActorCoCo{
  public static final String ERROR_CODE = "0xC1199";

  protected static final String MESSAGE =
    "The referenced use case '%s' does not exist in the use case diagram";


  @Override
  public void check(@NotNull ASTUCDActor node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if(!node.getUcList().isEmpty()){
      for(String name : node.getUcList()) {
        if (node.getEnclosingScope().resolveUCDUseCaseMany(name).isEmpty()) {
          Log.error(ERROR_CODE + " " + String.format(MESSAGE, name),
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd());
        }
      }
    }
  }
}
