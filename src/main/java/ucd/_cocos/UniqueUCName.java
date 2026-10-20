/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import ucd._ast.ASTUCDUseCase;
import ucd._symboltable.UCDUseCaseSymbol;

import java.util.List;

/**
 * Context condition that checks whether use case names are unique within
 * the use case diagram.
 *
 * <p>This CoCo resolves all use case symbols with the same name and checks
 * whether another use case with that name has already been defined. If a
 * duplicate definition is found, an error is reported.
 *
 * <p>This CoCo implements a different semantic interpretation of use case
 * names than the language's implicit introduction mechanism. It assumes
 * that use case symbols correspond to explicitly defined use cases and
 * does not account for implicitly introduced use cases when checking name
 * uniqueness.
 */

public class UniqueUCName implements  UCDASTUCDUseCaseCoCo {
  public static final String ERROR_CODE = "0xC1203";

  protected static final String MESSAGE =
    "There is another uc with the name '%s' but it must be unique";

  @Override
  public void check(ASTUCDUseCase node){
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    List<UCDUseCaseSymbol> matchingCandidates = node.getEnclosingScope().resolveUCDUseCaseMany(node.getName());

    if(matchingCandidates.stream().anyMatch(symbol -> alreadyDefined(node, symbol))){
      Log.error(ERROR_CODE + " " + String.format(MESSAGE, node.getName()),
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
      );
    }
  }

  protected boolean alreadyDefined(ASTUCDUseCase node, UCDUseCaseSymbol symbol){
    if(node.getSymbol() == symbol) return false;

    if(!symbol.isPresentAstNode()
      || !symbol.getAstNode().isPresent_SourcePositionStart()
      || !node.isPresent_SourcePositionStart()){
      return true;
    }

    return symbol.getAstNode().get_SourcePositionStart().compareTo(node.get_SourcePositionStart()) < 0;
  }
}
