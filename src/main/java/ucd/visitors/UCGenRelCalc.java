/* (c) https://github.com/MontiCore/monticore */
package ucd.visitors;

import ucd._ast.ASTUCDUseCase;
import ucd._ast.ASTUseCaseDiagram;
import ucd._ast.UCDEdge;
import ucd._visitor.UCDVisitor2;

import java.util.LinkedHashSet;
import java.util.Set;

public class UCGenRelCalc implements UCDVisitor2 {

  private final Set<UCDEdge> ucGeneralizationRelation = new LinkedHashSet<>();
  private final Set<String> allUCNames = new LinkedHashSet<>();

  public UCGenRelCalc(Set<String> allUCNames) {
    this.allUCNames.addAll(allUCNames);
  }

  @Override
  public void endVisit(ASTUseCaseDiagram node) {
    makeTransitive(ucGeneralizationRelation);
    makeReflexive(ucGeneralizationRelation);
  }

  private void makeTransitive(Set<UCDEdge> ucGeneralizationRelation) {
    Set<UCDEdge> prev = new LinkedHashSet<>();
    while (!prev.equals(ucGeneralizationRelation)) {
      prev = new LinkedHashSet<>(ucGeneralizationRelation);
      for (String uc1 : allUCNames) {
        for (String uc2 : allUCNames) {
          for (String uc3 : allUCNames) {
            if (ucGeneralizationRelation.contains(new UCDEdge(uc1, uc2)) && ucGeneralizationRelation.contains(new UCDEdge(uc2, uc3))) {
              ucGeneralizationRelation.add(new UCDEdge(uc1, uc3));
            }
          }
        }
      }
    }
  }

  private void makeReflexive(Set<UCDEdge> ucGeneralizationRelation) {
    for (String ucName : allUCNames) {
      ucGeneralizationRelation.add(new UCDEdge(ucName, ucName));
    }
  }

  @Override
  public void visit(ASTUCDUseCase node) {
    String name = node.getName();
    for (String superUC : node.getSupList()) {
      UCDEdge e = new UCDEdge(name, superUC);
      ucGeneralizationRelation.add(e);
    }
  }

  public Set<UCDEdge> getUCGeneralizationRelation() {
    return new LinkedHashSet<>(this.ucGeneralizationRelation);
  }
}
