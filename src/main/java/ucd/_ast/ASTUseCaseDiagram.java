package ucd._ast;

import com.google.common.collect.HashMultimap;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import ucd._visitor.UCDTraverser;
import ucd._visitor.UCDTraverserImplementation;
import ucd.semdiff.Formulas;
import ucd.visitors.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ASTUseCaseDiagram extends ASTUseCaseDiagramTOP {

  boolean isInitialized = false;
  private Map<String, ASTExpression> uc2Precondition;
  private Map<UCDEdge, ASTExpression> guardedExtendRelation;
  private Set<UCDEdge> unguardedExtendRelation;
  private Set<UCDEdge> ucGeneralizationRelation;
  private Set<String> allUCNames;
  private Set<String> nonAbstractUCNames;
  private Set<String> nonAbstractActorNames;
  private Set<UCDEdge> actorGeneralizationRelation;
  private Set<String> allVariables;
  private Set<String> allActorNames;

  private HashMultimap<String, String> associations;

  public void init() {
    if (!this.isInitialized) {
      UCDTraverser preTravUCNames = new UCDTraverserImplementation();
      UCCollector ucNameCalc = new UCCollector();
      preTravUCNames.add4UCD(ucNameCalc);
      this.accept(preTravUCNames);
      this.allUCNames = new HashSet<>(ucNameCalc.getUCNames());

      UCDTraverser preTrav = new UCDTraverserImplementation();
      UCGenRelCalc ucGenRelCalc = new UCGenRelCalc(this.allUCNames);
      preTrav.add4UCD(ucGenRelCalc);
      ActorCollector actorCollector = new ActorCollector();
      preTrav.add4UCD(actorCollector);
      this.accept(preTrav);
      this.allActorNames = new HashSet<>(actorCollector.getActors());
      this.ucGeneralizationRelation = new HashSet<>(ucGenRelCalc.getUCGeneralizationRelation());

      UCDTraverser t = new UCDTraverserImplementation();

      PreconditionCollector preconditionCollector = new PreconditionCollector(allUCNames);
      t.add4UCD(preconditionCollector);

      ExtendCollector extendCollector = new ExtendCollector();
      t.add4UCD(extendCollector);

      ActorGenRelCalc actorGenRelCalc = new ActorGenRelCalc(allActorNames);
      t.add4UCD(actorGenRelCalc);

      AssociationCollector associationCollector = new AssociationCollector(this.ucGeneralizationRelation);
      t.add4UCD(associationCollector);
      this.accept(t);

      this.uc2Precondition = new HashMap<>(preconditionCollector.getUc2Precondition());
      this.guardedExtendRelation = new HashMap<>(extendCollector.getGuardedExtendRelation());
      this.unguardedExtendRelation = new HashSet<>(extendCollector.getUnguardedExtendRelation());
      this.nonAbstractUCNames = new HashSet<>(allUCNames);
      this.nonAbstractUCNames.removeAll(ucNameCalc.getAbstractUCNames());
      this.nonAbstractActorNames = new HashSet<>(allActorNames);
      this.nonAbstractActorNames.removeAll(actorCollector.getAbstractActors());
      this.actorGeneralizationRelation = new HashSet<>(actorGenRelCalc.getActorGeneralizationRelation());
      this.allVariables = new HashSet<>();
      this.uc2Precondition.values().forEach(x -> allVariables.addAll(Formulas.allUsedVariables(x)));
      this.guardedExtendRelation.values().forEach(x -> allVariables.addAll(Formulas.allUsedVariables(x)));
      this.associations = associationCollector.getAssociations();

      this.isInitialized = true;
    }
  }

  /**
   * Returns the precondition of the use case useCase.
   *
   * @param useCase use case
   * @return Precondition
   */
  public ASTExpression getPrecondition(String useCase) {
    init();
    return uc2Precondition.get(useCase);
  }

  public Set<UCDEdge> getGuardedExtendRelation() {
    init();
    return new HashSet<>(this.guardedExtendRelation.keySet());
  }

  public ASTExpression getGuard(UCDEdge e) {
    init();
    return this.guardedExtendRelation.get(e);
  }

  public Set<UCDEdge> getUCGeneralizationRelation() {
    init();
    return new HashSet<>(this.ucGeneralizationRelation);
  }

  public Set<UCDEdge> getUnguardedExtendRelation() {
    init();
    return new HashSet<>(this.unguardedExtendRelation);
  }

  /**
   * Returns the associations between actors and the use cases contained in useCases.
   * Indirect associations existing because of generalizations of use cases
   * are also included.
   *
   * @param useCases set of use cases
   * @return Multimap mapping each actor to the set of use cases associated
   * with the actor.
   */
  public HashMultimap<String, String> getAssociations(Set<String> useCases) {
    init();
    HashMultimap<String, String> res = HashMultimap.create();
    for (String uc : useCases) {
      for (String actor : associations.keySet()) {
        Set<String> valuesForActor = associations.get(actor);
        if (valuesForActor.contains(uc)) {
          res.put(actor, uc);
        }
      }
    }
    return res;
  }

  public HashMultimap<String, String> getAssociations() {
    return this.associations;
  }

  public Set<UCDEdge> getActorGeneralizationRelation() {
    init();
    return new HashSet<>(this.actorGeneralizationRelation);
  }

  /**
   * Returns the set of all use cases contained in this UCD.
   *
   * @return use cases
   */
  public Set<String> getUseCases() {
    init();
    return new HashSet<>(this.allUCNames);
  }

  /**
   * returns set of all non-abstract use cases
   *
   * @return non-abstract actor names
   */
  public Set<String> getAllNonAbstractUCs() {
    init();
    return new HashSet<>(this.nonAbstractUCNames);
  }

  /**
   * Returns set of all non-abstract actors
   *
   * @return Actors names
   */
  public Set<String> getAllNonAbstractActors() {
    init();
    return new HashSet<>(this.nonAbstractActorNames);
  }

  /**
   * Returns the set of all variables used in this UCD
   *
   * @return Variables
   */
  public Set<String> getVariables() {
    init();
    return new HashSet<>(this.allVariables);
  }

  public Set<String> getAllActorNames() {
    init();
    return new HashSet<>(this.allActorNames);
  }
}
