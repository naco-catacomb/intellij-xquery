// This is a generated file. Not intended for manual editing.
package org.intellij.xquery.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.intellij.xquery.psi.XQueryTypes.*;
import org.intellij.xquery.psi.*;

public class XQueryRelativePathExprImpl extends XQueryExprSingleImpl implements XQueryRelativePathExpr {

  public XQueryRelativePathExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public List<XQueryStepExpr> getStepExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, XQueryStepExpr.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XQueryVisitor) ((XQueryVisitor)visitor).visitRelativePathExpr(this);
    else super.accept(visitor);
  }

}
