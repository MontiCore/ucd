package ucd.visitors;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteralBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import ucd._ast.ASTUCDUseCase;
import ucd._visitor.UCDVisitor2;

import java.util.HashMap;
import java.util.Map;

public class PreconditionCollector implements UCDVisitor2 {

  private Map<String, ASTExpression> uc2Precondition;
  private ASTExpression trueExpression;

  public PreconditionCollector() {
    this.uc2Precondition = new HashMap<>();

    ASTBooleanLiteral trueLit = new ASTBooleanLiteralBuilder().setSource(ASTConstantsMCCommonLiterals.TRUE).build();
    this.trueExpression = new ASTLiteralExpressionBuilder().setLiteral(trueLit).build();
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
