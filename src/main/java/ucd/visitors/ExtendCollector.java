package ucd.visitors;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteralBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import ucd._ast.ASTUCDExtend;
import ucd._ast.ASTUCDUseCase;
import ucd._ast.UCDEdge;
import ucd._visitor.UCDVisitor2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtendCollector implements UCDVisitor2 {

  private Map<UCDEdge, ASTExpression> guardedExtendRelation;
  private Set<UCDEdge> unguardedExtendRelation;

  public ExtendCollector() {
    this.guardedExtendRelation = new HashMap<>();
    this.unguardedExtendRelation = new HashSet<>();
  }

  @Override
  public void visit(ASTUCDUseCase node) {
    String name = node.getName();
    for (ASTUCDExtend extend : node.getUCDExtendList()) {
      UCDEdge extendEdge = new UCDEdge(name, extend.getName());
      if (extend.isPresentExpression()) {
        guardedExtendRelation.put(extendEdge, extend.getExpression());
      }
      else {
        unguardedExtendRelation.add(extendEdge);
      }
    }
  }

  public Map<UCDEdge, ASTExpression> getGuardedExtendRelation() {
    return guardedExtendRelation;
  }

  public Set<UCDEdge> getUnguardedExtendRelation() {
    return unguardedExtendRelation;
  }

}
