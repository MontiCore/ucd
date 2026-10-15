/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import de.se_rwth.commons.logging.Log;

public class ActorCapitalization implements UCDASTUCDActorCoCo{
  public static final String ERROR_CODE = "0xC1207";

  protected static final String MESSAGE =
    "The name of Actor '%s' does not start with an uppercase letter";

  @Override
  public void check(ucd._ast.ASTUCDActor node) {
    if(!Character.isUpperCase(node.getName().charAt(0))){
      Log.error(ERROR_CODE + " " + String.format(MESSAGE, node.getName()),
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
      );
    }
  }
}
