/*
 * Copyright 2013 Grzegorz Ligas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.xquery.reference;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.intellij.xquery.psi.XQueryModuleDecl;
import org.intellij.xquery.psi.XQueryModuleImport;
import org.intellij.xquery.psi.XQueryNamespaceDecl;

import java.util.List;

/**
 * User: ligasgr
 * Date: 06/07/13
 * Time: 01:13
 */
public class XQueryFunctionNamespaceNameReferenceTest extends LightPlatformCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/org/intellij/xquery/reference/namespace";
    }

    public void testFunctionNamespaceCompletion() {
        myFixture.configureByFiles("FunctionNamespaceNameCompletion.xq");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertEquals(0, strings.size());
    }

    public void testFunctionNamespaceReferenceForModuleDeclaration() {
        myFixture.configureByFiles("FunctionNamespaceNameReference_Module.xq");
        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = element.getReferences();
        PsiReference reference = references[0];
        PsiElement resolvedReference = reference.resolve();
        XQueryModuleDecl referencedModule = (XQueryModuleDecl) resolvedReference.getParent();
        assertEquals("xxx", referencedModule.getNamespaceName().getName());
    }

    public void testFunctionNamespaceReferenceForModuleImport() {
        myFixture.configureByFiles("FunctionNamespaceNameReference_Import.xq");
        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = element.getReferences();
        PsiReference reference = references[0];
        PsiElement resolvedReference = reference.resolve();
        XQueryModuleImport referencedModuleImport = (XQueryModuleImport) resolvedReference.getParent();
        assertEquals("yyy", referencedModuleImport.getNamespaceName().getName());
    }

    public void testFunctionNamespaceReferenceForNamespaceDeclaration() {
        myFixture.configureByFiles("FunctionNamespaceNameReference_Declaration.xq");
        PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent();
        PsiReference[] references = element.getReferences();
        PsiReference reference = references[0];
        PsiElement resolvedReference = reference.resolve();
        XQueryNamespaceDecl referencedDeclaration = (XQueryNamespaceDecl) resolvedReference.getParent();
        assertEquals("zzz", referencedDeclaration.getNamespaceName().getName());
    }

    public void testFunctionNamespaceRename() {
        myFixture.configureByFiles("FunctionNamespaceNameRename.xq");
        myFixture.renameElementAtCaret("aaa");
        myFixture.checkResultByFile("FunctionNamespaceNameRename.xq", "FunctionNamespaceNameRenameAfter.xq", false);
    }
}
