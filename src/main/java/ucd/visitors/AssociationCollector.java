/* (c) https://github.com/MontiCore/monticore */
package ucd.visitors;

import com.google.common.collect.HashMultimap;
import ucd._ast.ASTUCDActor;
import ucd._ast.ASTUseCaseDiagram;
import ucd._ast.UCDEdge;
import ucd._visitor.UCDVisitor2;

import java.util.Set;

public class AssociationCollector implements UCDVisitor2 {

  private final Set<UCDEdge> ucGeneralizationRelation;
  HashMultimap<String, String> associations;

  public AssociationCollector(Set<UCDEdge> ucGeneralizationRelation) {
    this.ucGeneralizationRelation = ucGeneralizationRelation;
    this.associations = HashMultimap.create();
  }

  @Override
  public void endVisit(ASTUseCaseDiagram node) {
    propagateInheritance();
  }

  private void propagateInheritance() {
    HashMultimap<String, String> propagatedAssociations = HashMultimap.create(this.associations);
    for (UCDEdge e : ucGeneralizationRelation) {
      String subUC = e.getFrom();
      String superUC = e.getTo();
      for (String actor : this.associations.keySet()) {
        for (String uc : this.associations.get(actor)) {
          if (superUC.equals(uc)) {
            propagatedAssociations.put(actor, subUC);
          }
        }
      }
    }
    this.associations = propagatedAssociations;
  }

  @Override
  public void visit(ASTUCDActor node) {
    String name = node.getName();
    for (String uc : node.getUcList()) {
      associations.put(name, uc);
    }
  }

  public HashMultimap<String, String> getAssociations() {
    return this.associations;
  }
}
