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

public class XQueryParenthesizedItemTypeImpl extends XQueryElementImpl implements XQueryParenthesizedItemType {

  public XQueryParenthesizedItemTypeImpl(ASTNode node) {
    super(node);
  }

  @Override
  @NotNull
  public XQueryItemType getItemType() {
    return findNotNullChildByClass(XQueryItemType.class);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof XQueryVisitor) ((XQueryVisitor)visitor).visitParenthesizedItemType(this);
    else super.accept(visitor);
  }

}
