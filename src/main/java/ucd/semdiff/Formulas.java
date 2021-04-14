package ucd.semdiff;

import com.google.common.collect.Sets;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

public class Formulas {

  public static final String UNKNOWN_EXPRESSION_KIND = "0xC0001: Unsupported expression kind used on guard. Please use a boolean expression.";

  public static Set<String> allUsedVariables(ASTExpression ast) {
    Set<String> res = new HashSet<>();
    if (ast instanceof ASTBooleanAndOpExpression) {
      res.addAll(allUsedVariables(((ASTBooleanAndOpExpression) ast).getLeft()));
      res.addAll(allUsedVariables(((ASTBooleanAndOpExpression) ast).getRight()));
      return res;
    }
    else if (ast instanceof ASTBooleanOrOpExpression) {
      res.addAll(allUsedVariables(((ASTBooleanOrOpExpression) ast).getLeft()));
      res.addAll(allUsedVariables(((ASTBooleanOrOpExpression) ast).getRight()));
      return res;
    }
    else if (ast instanceof ASTBracketExpression) {
      res.addAll(allUsedVariables(((ASTBracketExpression) ast).getExpression()));
      return res;
    }
    else if (ast instanceof ASTEqualsExpression) {
      res.addAll(allUsedVariables(((ASTEqualsExpression) ast).getLeft()));
      res.addAll(allUsedVariables(((ASTEqualsExpression) ast).getRight()));
      return res;
    }
    else if (ast instanceof ASTNotEqualsExpression) {
      res.addAll(allUsedVariables(((ASTNotEqualsExpression) ast).getLeft()));
      res.addAll(allUsedVariables(((ASTNotEqualsExpression) ast).getRight()));
      return res;
    }
    else if (ast instanceof ASTLogicalNotExpression) {
      res.addAll(allUsedVariables(((ASTLogicalNotExpression) ast).getExpression()));
      return res;
    }
    else if (ast instanceof ASTNameExpression) {
      res.add(((ASTNameExpression) ast).getName());
      return res;
    }
    else if (ast instanceof ASTLiteralExpression) {
      // nothing to do here
      return res;
    }
    Log.error(UNKNOWN_EXPRESSION_KIND, ast.get_SourcePositionStart(), ast.get_SourcePositionEnd());
    throw new RuntimeException();
  }

  public static boolean evaluate(ASTExpression ast, Set<String> val) {
    if (ast instanceof ASTBooleanAndOpExpression) {
      ASTExpression left = ((ASTBooleanAndOpExpression) ast).getLeft();
      ASTExpression right = ((ASTBooleanAndOpExpression) ast).getRight();
      return evaluate(left, val) && evaluate(right, val);
    }
    else if (ast instanceof ASTBooleanOrOpExpression) {
      ASTExpression left = ((ASTBooleanOrOpExpression) ast).getLeft();
      ASTExpression right = ((ASTBooleanOrOpExpression) ast).getRight();
      return evaluate(left, val) || evaluate(right, val);
    }
    else if (ast instanceof ASTBracketExpression) {
      return evaluate(((ASTBracketExpression) ast).getExpression(), val);
    }
    else if (ast instanceof ASTEqualsExpression) {
      ASTExpression left = ((ASTEqualsExpression) ast).getLeft();
      ASTExpression right = ((ASTEqualsExpression) ast).getRight();
      return evaluate(left, val) == evaluate(right, val);
    }
    else if (ast instanceof ASTNotEqualsExpression) {
      ASTExpression left = ((ASTNotEqualsExpression) ast).getLeft();
      ASTExpression right = ((ASTNotEqualsExpression) ast).getRight();
      return evaluate(left, val) != evaluate(right, val);
    }
    else if (ast instanceof ASTLogicalNotExpression) {
      ASTExpression expr = ((ASTLogicalNotExpression) ast).getExpression();
      return !evaluate(expr, val);
    }
    else if (ast instanceof ASTNameExpression) {
      String name = ((ASTNameExpression) ast).getName();
      return val.contains(name);
    }
    else if (ast instanceof ASTLiteralExpression) {
      ASTLiteral literal = ((ASTLiteralExpression) ast).getLiteral();
      if (literal instanceof ASTBooleanLiteral) {
        return ((ASTBooleanLiteral) literal).getValue();
      }
    }
    Log.error(UNKNOWN_EXPRESSION_KIND, ast.get_SourcePositionStart(), ast.get_SourcePositionEnd());
    throw new RuntimeException();
  }

  public static Set<Set<String>> allValuations(Set<String> vars) {
    return Sets.powerSet(vars);
  }

}
