package ucd.visitors;

import ucd._ast.ASTUCDActor;
import ucd._ast.ASTUCDArtifact;
import ucd._ast.ASTUCDUseCase;
import ucd._ast.UCDEdge;
import ucd._visitor.UCDVisitor2;

import java.util.HashSet;
import java.util.Set;

public class UCCollector implements UCDVisitor2 {

  private Set<String> ucNames = new HashSet<>();
  private Set<String> abstractUCNames = new HashSet<>();

  @Override
  public void visit(ASTUCDUseCase node) {
    ucNames.add(node.getName());
    if (node.isAbstract()) {
      this.abstractUCNames.add(node.getName());
    }

    ucNames.addAll(node.getSupList());
    node.getUCDExtendList().stream().forEach(x -> ucNames.add(x.getName()));
    ucNames.addAll(node.getInclList());
  }

  @Override
  public void visit(ASTUCDActor node) {
    ucNames.addAll(node.getUcList());
  }

  public Set<String> getUCNames() {
    return this.ucNames;
  }

  public Set<String> getAbstractUCNames() {
    return this.abstractUCNames;
  }
}
