/* (c) https://github.com/MontiCore/monticore */
package ucd.visitors;

import ucd._ast.ASTUCDActor;
import ucd._ast.ASTUseCaseDiagram;
import ucd._ast.UCDEdge;
import ucd._visitor.UCDVisitor2;

import java.util.HashSet;
import java.util.Set;

public class ActorGenRelCalc implements UCDVisitor2 {

  private final Set<UCDEdge> actorGeneralizationRelation = new HashSet<>();
  Set<String> allActorNames = new HashSet<>();

  public ActorGenRelCalc(Set<String> allActorNames) {
    this.allActorNames.addAll(allActorNames);
  }

  @Override
  public void endVisit(ASTUseCaseDiagram node) {
    makeTransitive(actorGeneralizationRelation);
    makeReflexive(actorGeneralizationRelation);
  }

  private void makeTransitive(Set<UCDEdge> ucGeneralizationRelation) {
    Set<UCDEdge> prev = new HashSet<>();
    while (!prev.equals(ucGeneralizationRelation)) {
      prev = new HashSet<>(ucGeneralizationRelation);
      for (String uc1 : allActorNames) {
        for (String uc2 : allActorNames) {
          for (String uc3 : allActorNames) {
            if (ucGeneralizationRelation.contains(new UCDEdge(uc1, uc2)) && ucGeneralizationRelation.contains(new UCDEdge(uc2, uc3))) {
              ucGeneralizationRelation.add(new UCDEdge(uc1, uc3));
            }
          }
        }
      }
    }
  }

  private void makeReflexive(Set<UCDEdge> ucGeneralizationRelation) {
    for (String ucName : allActorNames) {
      ucGeneralizationRelation.add(new UCDEdge(ucName, ucName));
    }
  }

  @Override
  public void visit(ASTUCDActor node) {
    String name = node.getName();
    for (String superUC : node.getSupList()) {
      UCDEdge e = new UCDEdge(name, superUC);
      actorGeneralizationRelation.add(e);
    }
  }

  public Set<UCDEdge> getActorGeneralizationRelation() {
    return new HashSet<>(this.actorGeneralizationRelation);
  }
}
