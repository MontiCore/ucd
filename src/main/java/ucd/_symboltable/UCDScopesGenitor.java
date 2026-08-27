/* (c) https://github.com/MontiCore/monticore */
package ucd._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import ucd._ast.ASTUCDArtifact;

import java.util.ArrayList;
import java.util.List;

public class UCDScopesGenitor extends UCDScopesGenitorTOP {

  public ucd._symboltable.IUCDArtifactScope createFromAST(ASTUCDArtifact rootNode) {
    Preconditions.checkNotNull(rootNode, "0xA7004x13505 Error by creating of the UCDScopesGenitor symbol table: top ast node is null");
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTMCImportStatement importStatement : rootNode.getMCImportStatementList()) {
      imports.add(new ImportStatement(importStatement.getQName(), importStatement.isStar()));
    }

    IUCDArtifactScope artifactScope = ucd.UCDMill.artifactScope();
    artifactScope.setPackageName(rootNode.isPresentMCPackageDeclaration() ? rootNode.getMCPackageDeclaration().getMCQualifiedName().getQName() : "");
    artifactScope.setImportsList(imports);
    artifactScope.setAstNode(rootNode);
    putOnStack(artifactScope);
    initArtifactScopeHP1(artifactScope);
    rootNode.accept(getTraverser());
    initArtifactScopeHP2(artifactScope);
    scopeStack.remove(artifactScope);
    return artifactScope;
  }
}
