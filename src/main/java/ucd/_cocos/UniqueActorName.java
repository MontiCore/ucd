/* (c) https://github.com/MontiCore/monticore */
package ucd._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import ucd._ast.ASTUCDActor;
import ucd._symboltable.UCDActorSymbol;

import java.util.List;

/**
 * Context condition that checks whether actor names are unique within the
 * model.
 *
 * <p>For each actor, this CoCo checks whether another actor with the same
 * name has already been defined. If a duplicate actor name is found, an
 * error is reported.
 */
public class UniqueActorName implements UCDASTUCDActorCoCo {
  public static final String ERROR_CODE = "0xC1204";

  protected static final String MESSAGE =
    "There is another actor with the name '%s' but it must be unique.";

  @Override
  public void check(ASTUCDActor node){
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    List<UCDActorSymbol> matchingCandidates = node.getEnclosingScope().resolveUCDActorMany(node.getName());

    if(matchingCandidates.stream().anyMatch(symbol -> alreadyDefined(node, symbol))){
      Log.error(ERROR_CODE + " " + String.format(MESSAGE, node.getName()),
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
      );
    }
  }

  protected boolean alreadyDefined(ASTUCDActor node, UCDActorSymbol symbol){
    if(node.getSymbol() == symbol) return false;

    if(!symbol.isPresentAstNode()
      || !symbol.getAstNode().isPresent_SourcePositionStart()
      || !node.isPresent_SourcePositionStart()){
      return true;
    }

    return symbol.getAstNode().get_SourcePositionStart().compareTo(node.get_SourcePositionStart()) < 0;
  }
}
