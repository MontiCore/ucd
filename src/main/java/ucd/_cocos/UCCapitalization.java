/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import de.se_rwth.commons.logging.Log;

public class UCCapitalization implements UCDASTUCDUseCaseCoCo {
  public static final String ERROR_CODE = "0xC1208";

  protected static final String MESSAGE =
    "The name of UC '%s' does not start with an uppercase letter";


  @Override
  public void check(ucd._ast.ASTUCDUseCase node) {
    if (!node.getName().isEmpty() && !Character.isUpperCase(node.getName().charAt(0))) {
      Log.error(ERROR_CODE + " " + String.format(MESSAGE, node.getName()),
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
      );
    }
  }
}
