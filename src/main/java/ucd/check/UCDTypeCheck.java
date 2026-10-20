/* (c) https://github.com/MontiCore/monticore */
package ucd.check;

import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisCTTIVisitor;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import ucd.UCDMill;
import ucd._visitor.UCDTraverser;

public class UCDTypeCheck extends MapBasedTypeCheck3 {
  public static void init() {
    initTC3Delegate();

    TypeContextCalculator.init();
    TypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    TypeParameterRelations.init();
  }

  public static void reset() {
    TypeCheck3.resetDelegate();

    TypeContextCalculator.reset();
    TypeVisitorOperatorCalculator.reset();
    CommonExpressionsLValueRelations.reset();
    TypeParameterRelations.reset();
  }

  protected static void initTC3Delegate() {
    Log.trace("init UCDTypeCheck", "TypeCheck setup");

    UCDTraverser traverser = UCDMill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();

    // CommonExpressions
    CommonExpressionsCTTIVisitor visCommonExpressions =
      new CommonExpressionsCTTIVisitor();
    visCommonExpressions.setType4Ast(type4Ast);
    visCommonExpressions.setContext4Ast(ctx4Ast);
    traverser.add4CommonExpressions(visCommonExpressions);
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    // ExpressionsBasis
    ExpressionBasisCTTIVisitor visExpressionBasis =
      new ExpressionBasisCTTIVisitor();
    visExpressionBasis.setType4Ast(type4Ast);
    visExpressionBasis.setContext4Ast(ctx4Ast);
    traverser.add4ExpressionsBasis(visExpressionBasis);
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    // MCCommonLiterals
    MCCommonLiteralsTypeVisitor visLiterals =
      new MCCommonLiteralsTypeVisitor();
    visLiterals.setType4Ast(type4Ast);
    traverser.add4MCCommonLiterals(visLiterals);

    // MCBasicTypes
    MCBasicTypesTypeVisitor visBasicTypes =
      new MCBasicTypesTypeVisitor();
    visBasicTypes.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(visBasicTypes);

    // Delegate registrieren
    UCDTypeCheck delegate =
      new UCDTypeCheck(traverser, type4Ast, ctx4Ast);
    delegate.setThisAsDelegate();
  }

  protected UCDTypeCheck(
    ITraverser typeTraverser,
    Type4Ast type4Ast,
    InferenceContext4Ast ctx4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
  }
}
