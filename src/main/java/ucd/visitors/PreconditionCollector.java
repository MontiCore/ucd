package ucd.visitors;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteralBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import ucd._ast.ASTUCDUseCase;
import ucd._ast.ASTUseCaseDiagram;
import ucd._visitor.UCDVisitor2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreconditionCollector implements UCDVisitor2 {

  private final Map<String, ASTExpression> uc2Precondition;
  private final ASTExpression trueExpression;
  private final Set<String> allUCNames;

  public PreconditionCollector(Set<String> allUCNames) {
    this.allUCNames = allUCNames;
    this.uc2Precondition = new HashMap<>();

    ASTBooleanLiteral trueLit = new ASTBooleanLiteralBuilder().setSource(ASTConstantsMCCommonLiterals.TRUE).build();
    this.trueExpression = new ASTLiteralExpressionBuilder().setLiteral(trueLit).build();
  }

  @Override
  public void endVisit(ASTUseCaseDiagram ast) {
    Set<String> nonMappedNames = new HashSet<>(allUCNames);
    nonMappedNames.removeAll(uc2Precondition.keySet());
    for (String uc : nonMappedNames) {
      uc2Precondition.put(uc, this.trueExpression);
    }
  }

  @Override
  public void visit(ASTUCDUseCase node) {
    String name = node.getName();
    if (node.isPresentExpression()) {
      uc2Precondition.put(name, node.getExpression());
    }
    else {
      uc2Precondition.put(name, this.trueExpression);
    }
  }

  public Map<String, ASTExpression> getUc2Precondition() {
    return uc2Precondition;
  }
}
