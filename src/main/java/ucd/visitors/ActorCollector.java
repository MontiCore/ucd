/* (c) https://github.com/MontiCore/monticore */
package ucd.visitors;

import ucd._ast.ASTUCDActor;
import ucd._visitor.UCDVisitor2;

import java.util.HashSet;
import java.util.Set;

public class ActorCollector implements UCDVisitor2 {

  private final Set<String> actors;
  private final Set<String> abstractActors;

  public ActorCollector() {
    this.actors = new HashSet<>();
    this.abstractActors = new HashSet<>();
  }

  @Override
  public void visit(ASTUCDActor node) {
    String name = node.getName();
    if (node.isAbstract()) {
      this.abstractActors.add(name);
    }
    this.actors.add(name);
    this.actors.addAll(node.getSupList());
  }

  public Set<String> getActors() {
    return new HashSet<>(actors);
  }

  public Set<String> getAbstractActors() {
    return new HashSet<>(abstractActors);
  }

}
