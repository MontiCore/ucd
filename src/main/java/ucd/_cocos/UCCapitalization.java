/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import de.se_rwth.commons.logging.Log;

/**
 * Context condition that checks whether use case names start with an
 * uppercase letter.
 *
 * <p>For each use case, this CoCo checks whether the first character of its
 * name is uppercase. If the name does not start with an uppercase letter,
 * an error is reported.
 */
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
