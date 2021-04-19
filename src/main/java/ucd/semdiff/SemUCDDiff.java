package ucd.semdiff;

import com.google.common.collect.HashMultimap;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import ucd._ast.ASTUCDArtifact;
import ucd._ast.ASTUseCaseDiagram;
import ucd._ast.UCDEdge;

import java.util.*;

public class SemUCDDiff {

  public static Set<Scenario> diff(ASTUCDArtifact art1, ASTUCDArtifact art2) {
    return diff(art1.getUseCaseDiagram(), art2.getUseCaseDiagram());
  }

  public static Set<Scenario> diff(ASTUseCaseDiagram ast1, ASTUseCaseDiagram ast2) {
    Set<String> vars = new HashSet<>(ast1.getVariables());
    vars.addAll(ast2.getVariables());

    Set<Scenario> semDiff = sem(ast1, vars);
    semDiff.removeAll(sem(ast2, vars));

    return semDiff;
  }

  public static Set<String> exec(ASTUseCaseDiagram d, String u, Set<String> val) {
    Set<String> exec = new HashSet<>();

    ASTExpression precon = d.getPrecondition(u);
    if (Formulas.evaluate(precon, val)) {
      exec.add(u);
    }
    Deque<String> toProcess = new ArrayDeque<>();
    exec.forEach(toProcess::push);
    Set<String> processed = new HashSet<>();
    while (!toProcess.isEmpty()) {
      String cur = toProcess.pop();
      processed.add(cur);
      for (UCDEdge edge : d.getGuardedExtendRelation()) {
        if (edge.getTo().equals(cur) && !processed.contains(edge.getFrom()) && Formulas.evaluate(d.getGuard(edge), val) && Formulas.evaluate(d.getPrecondition(edge.getFrom()), val)) {
          exec.add(edge.getFrom());
          toProcess.push(edge.getFrom());
        }
      }
    }
    return exec;
  }

  public static Set<Set<String>> closure(ASTUseCaseDiagram d, String u, Set<String> val) {
    Set<Set<String>> cls = new HashSet<>();
    Set<String> exec = exec(d, u, val);
    if (!exec.isEmpty()) {
      cls.add(exec(d, u, val));
    }
    Deque<Set<String>> toProcess = new ArrayDeque<>();
    cls.forEach(toProcess::push);
    Set<Set<String>> processed = new HashSet<>();
    while (!toProcess.isEmpty()) {
      Set<String> c = toProcess.pop();
      processed.add(c);
      for (String w : c) {
        for (UCDEdge gen : d.getUCGeneralizationRelation()) {
          if (gen.getTo().equals(w)) {
            if (Formulas.evaluate(d.getPrecondition(gen.getFrom()), val)) {
              Set<String> newSet = new HashSet<>(c);
              newSet.remove(gen.getTo());
              newSet.addAll(exec(d, gen.getFrom(), val));
              if (!processed.contains(newSet)) {
                cls.add(newSet);
                toProcess.push(newSet);
              }
            }
          }
        }
        for (UCDEdge e : d.getUnguardedExtendRelation()) {
          if (e.getTo().equals(w)) {
            if (Formulas.evaluate(d.getPrecondition(e.getFrom()), val)) {
              Set<String> newSet = new HashSet<>();
              newSet.addAll(c);
              newSet.addAll(exec(d, e.getFrom(), val));
              if (!processed.contains(newSet)) {
                cls.add(newSet);
                toProcess.push(newSet);
              }
            }
          }
        }
      }
    }
    return cls;
  }

  public static Set<Scenario> scn(ASTUseCaseDiagram d, String u, Set<String> val) {
    Set<Scenario> scn = new HashSet<>();
    for (Set<String> c : closure(d, u, val)) {
      scn.add(new Scenario(val, c, d.getAssociations(c)));
    }
    Deque<Scenario> toProcess = new ArrayDeque<>(scn);
    Set<Scenario> processed = new HashSet<>();
    while (!toProcess.isEmpty()) {
      Scenario curScenario = toProcess.pop();
      processed.add(curScenario);
      for (Map.Entry<String, String> entry : curScenario.getActor2uc().entries()) {
        for (UCDEdge e : d.getActorGeneralizationRelation()) {
          if (e.getTo().equals(entry.getValue())) {
            HashMultimap<String, String> newRel = HashMultimap.create(curScenario.getActor2uc());
            newRel.remove(entry.getKey(), entry.getValue());
            newRel.put(entry.getKey(), e.getFrom());
            Scenario newScenario = new Scenario(val, curScenario.getUcs(), newRel);
            if (!processed.contains(newScenario)) {
              scn.add(newScenario);
              toProcess.push(newScenario);
            }
          }
        }
      }
    }
    return scn;
  }

  public static Set<Scenario> sem(ASTUseCaseDiagram d, Set<String> vars) {
    Set<Scenario> res = new HashSet<>();
    for (Set<String> val : Formulas.allValuations(vars)) {
      for (String u : d.getUseCases()) {
        for (Scenario s : scn(d, u, val)) {
          if (d.getAllNonAbstractUCs().containsAll(s.getUcs()) && d.getAllNonAbstractActors().containsAll(s.getLinkedActors())) {
            res.add(s);
          }
        }
      }
    }
    return res;
  }
}
