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

public class XQueryComputedConstructorImpl extends XQueryElementImpl implements XQueryComputedConstructor {

  public XQueryComputedConstructorImpl(ASTNode node) {
    super(node);
  }

  @Override
  @Nullable
  public XQueryCompAttrConstructor getCompAttrConstructor() {
    return findChildByClass(XQueryCompAttrConstructor.class);
  }

  @Override
  @Nullable
  public XQueryCompCommentConstructor getCompCommentConstructor() {
    return findChildByClass(XQueryCompCommentConstructor.class);
  }

  @Override
  @Nullable
  public XQueryCompDocConstructor getCompDocConstructor() {
    return findChildByClass(XQueryCompDocConstructor.class);
  }

  @Override
  @Nullable
  public XQueryCompElemConstructor getCompElemConstructor() {
    return findChildByClass(XQueryCompElemConstructor.class);
  }

  @Override
  @Nullable
  public XQueryCompMapConstructor getCompMapConstructor() {
    return findChildByClass(XQueryCompMapConstructor.class);
  }

  @Override
  @Nullable
  public XQueryCompNamespaceConstructor getCompNamespaceConstructor() {
    return findChildByClass(XQueryCompNamespaceConstructor.class);
  }

  @Override
  @Nullable
  public XQueryCompPIConstructor getCompPIConstructor() {
    return findChildByClass(XQueryCompPIConstructor.class);
  }

  @Override
  @Nullable
  public XQueryCompTextConstructor getCompTextConstructor() {
    return findChildByClass(XQueryCompTextConstructor.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XQueryVisitor) ((XQueryVisitor)visitor).visitComputedConstructor(this);
    else super.accept(visitor);
  }

}
