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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.xquery.XQueryIcons;
import org.intellij.xquery.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ligasgr
 * Date: 08/06/13
 * Time: 22:16
 */
public class XQueryVariableReference extends PsiReferenceBase<XQueryVarRef> implements PsiPolyVariantReference {

    private final String checkedNamespace;

    public XQueryVariableReference(@NotNull XQueryVarRef element, TextRange textRange) {
        super(element, textRange);
        checkedNamespace = myElement.getVarName().getVarNamespace() != null ? myElement.getVarName().getVarNamespace().getText() : null;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        XQueryFile file = (XQueryFile) myElement.getContainingFile();
        if (myElement.getVarName() != null) {
            VariableReferenceScopeProcessor processor = new VariableReferenceScopeProcessor();
            PsiTreeUtil.treeWalkUp(processor, myElement, null, ResolveState.initial());
            Map<String, ResolveResult> scopeProcessorResults = processor.getResults();

            Map<String, ResolveResult> variableDeclarationResults = getVariableDeclarationReferences(file, scopeProcessorResults, checkedNamespace);

            Map<String, ResolveResult> externalVariableDeclarationResults = getExternalVariableDeclarationReferences(file, variableDeclarationResults);

            return externalVariableDeclarationResults.values().toArray(new ResolveResult[externalVariableDeclarationResults.size()]);
        }
        return new ResolveResult[0];
    }

    private Map<String, ResolveResult> getExternalVariableDeclarationReferences(XQueryFile file, Map<String, ResolveResult> results) {
        final String referenceNamespace = myElement.getVarName().getVarNamespace() != null ? myElement.getVarName().getVarNamespace().getText() : null;

        if (referenceNamespace != null) {
            addReferencesFromImportedModules(file, results, referenceNamespace);
        }
        return results;
    }

    private void addReferencesFromImportedModules(XQueryFile file, Map<String, ResolveResult> results, String referenceNamespace) {
        for (XQueryModuleImport moduleImport : file.getModuleImports()) {
            if (referenceNamespace.equals(moduleImport.getNamespaceName().getText())) {
                addReferencesFromAllFilesInImport(moduleImport, results);
            }
        }
    }

    private void addReferencesFromAllFilesInImport(XQueryModuleImport moduleImport, Map<String, ResolveResult> results) {
        List<XQueryModuleImportPath> importPaths = moduleImport.getModuleImportPathList();
        for (XQueryModuleImportPath path : importPaths) {
            if (path.getReference() != null) {
                XQueryFile xQueryFile = (XQueryFile) path.getReference().resolve();
                if (xQueryFile != null) {
                    getVariableDeclarationReferences(xQueryFile, results, xQueryFile.getModuleNamespaceName().getText());
                }
            }
        }
    }

    private Map<String, ResolveResult> getVariableDeclarationReferences(XQueryFile file, Map<String, ResolveResult> results, String checkedNamespace) {

        for (XQueryVarDecl varDecl : file.getVariableDeclarations()) {
            if (variableNameExists(varDecl)) {
                addReferenceIfNotAlreadyAdded(results, varDecl, checkedNamespace);
            }
        }
        return results;
    }

    private boolean variableNameExists(XQueryVarDecl varDecl) {
        return varDecl.getVarName() != null && varDecl.getVarName().getTextLength() > 0;
    }

    private void addReferenceIfNotAlreadyAdded(Map<String, ResolveResult> results, XQueryVarDecl varDecl, String checkedNamespace) {
        String key = varDecl.getVarName().getText();
        if (!results.containsKey(key)) {
            addElementToResultsIfMatching(results, varDecl.getVarName(), varDecl.getVarName(), key, checkedNamespace);
        }
    }

    private void addElementToResultsIfMatching(Map<String, ResolveResult> results, PsiElement referenceTarget, XQueryVarName comparedVarName, String key, String checkedNamespace) {
        final String varDeclNamespace = comparedVarName.getVarNamespace() != null ? comparedVarName.getVarNamespace().getText() : null;
        final String referenceNamespace = checkedNamespace;
        final String varDeclLocalName = comparedVarName.getVarLocalName().getText();
        final String referenceLocalName = myElement.getVarName().getVarLocalName().getText();
        boolean namespacesAndLocalNamesMatch = referenceNamespace != null && referenceNamespace.equals(varDeclNamespace) && referenceLocalName.equals(varDeclLocalName);
        boolean namespacesAreEmptyAndLocalNamesMatch = referenceNamespace == null && varDeclNamespace == null && varDeclLocalName.equals(referenceLocalName);

        if (namespacesAndLocalNamesMatch) {
            results.put(key, new PsiElementResolveResult(referenceTarget));
        } else if (namespacesAreEmptyAndLocalNamesMatch) {
            results.put(key, new PsiElementResolveResult(referenceTarget));
        }
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        XQueryFile file = (XQueryFile) myElement.getContainingFile();
        Map<String, LookupElement> variants = new HashMap<String, LookupElement>();

        VariableVariantsScopeProcessor processor = new VariableVariantsScopeProcessor(variants);
        PsiTreeUtil.treeWalkUp(processor, myElement, null, ResolveState.initial());

        return getVariantsFromVariableDeclarations(file, variants);
    }

    private Object[] getVariantsFromVariableDeclarations(XQueryFile file, Map<String, LookupElement> variants) {
        for (final XQueryVarDecl varDecl : file.getVariableDeclarations()) {
            if (variableNameExists(varDecl)) {
                addVariantIfNotAlreadyAdded(variants, varDecl);
            }
        }
        return variants.values().toArray();
    }

    private void addVariantIfNotAlreadyAdded(Map<String, LookupElement> variants, XQueryVarDecl varDecl) {
        String key = varDecl.getVarName().getText();
        if (!variants.containsKey(key)) {
            variants.put(key, LookupElementBuilder.create(varDecl.getVarName(), key).
                    withIcon(XQueryIcons.FILE).
                    withTypeText(varDecl.getContainingFile().getName())

            );
        }
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        myElement.getVarName().getVarLocalName().replace(getUpdatedRef(newElementName).getVarLocalName());
        return myElement;
    }

    private XQueryVarName getUpdatedRef(String newName) {
        XQueryVarName varName = XQueryElementFactory.createVariableReference(myElement.getProject(), newName);
        return varName;
    }

    private boolean isNotVariableReference(PsiElement psiElement) {
        return !(psiElement.getParent() instanceof XQueryVarRef);
    }

    private class VariableVariantsScopeProcessor extends BaseScopeProcessor {
        private final Map<String, LookupElement> myResult;

        public VariableVariantsScopeProcessor(Map<String, LookupElement> result) {
            myResult = result;
        }

        @Override
        public boolean execute(@NotNull PsiElement psiElement, ResolveState resolveState) {
            boolean elementIsAGoodCandidate = !psiElement.equals(myElement)
                    && psiElement instanceof XQueryVarName
                    && isNotVariableReference(psiElement);

            if (elementIsAGoodCandidate) {
                addElementIfNotAlreadyAdded(psiElement);
            }
            return true;
        }

        private void addElementIfNotAlreadyAdded(PsiElement psiElement) {
            String key = psiElement.getText();
            if (!myResult.containsKey(key)) {
                myResult.put(key, LookupElementBuilder.create(key).
                        withIcon(XQueryIcons.FILE).
                        withTypeText(psiElement.getContainingFile().getName()));
            }
        }
    }

    private class VariableReferenceScopeProcessor extends BaseScopeProcessor {
        private Map<String, ResolveResult> results = new HashMap<String, ResolveResult>();

        public Map<String, ResolveResult> getResults() {
            return results;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, ResolveState state) {
            boolean elementIsGoodCandidate = !element.equals(myElement) && element instanceof XQueryVarName && isNotVariableReference(element);
            if (elementIsGoodCandidate) {
                addElementIfNotAlreadyAdded(element);
            }
            return true;
        }

        private void addElementIfNotAlreadyAdded(PsiElement element) {
            String key = element.getText();
            if (!results.containsKey(key)) {
                addElementToResultsIfMatching(results, element, (XQueryVarName) element, key, checkedNamespace);
            }
        }
    }
}
