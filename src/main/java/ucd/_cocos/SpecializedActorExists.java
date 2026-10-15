/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.misc.NotNull;
import ucd._ast.ASTUCDActor;
import ucd._symboltable.UCDActorSymbol;

import java.util.List;

public class SpecializedActorExists implements UCDASTUCDActorCoCo{
  public static final String ERROR_CODE = "0xC1206";

  protected static final String MESSAGE =
    "The referenced actor %s does not exist in the model.";


  @Override
  public void check(@NotNull ASTUCDActor node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if(!node.getSupList().isEmpty()){
      for(String name : node.getSupList()) {
        node.getEnclosingScope().resolveUCDActorMany(name);

        List<UCDActorSymbol> actor = node.getEnclosingScope().resolveUCDActorMany(name);
        if (actor.isEmpty()) {
          Log.error(ERROR_CODE + " " + String.format(MESSAGE, name),
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd());
        }
      }
    }
  }
}
