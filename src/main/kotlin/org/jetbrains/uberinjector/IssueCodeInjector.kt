package org.jetbrains.uberinjector

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.injection.ReferenceInjector
import com.intellij.util.ProcessingContext

class IssueCodeInjector : ReferenceInjector() {
    override fun getId(): String = "uber-issue-code"

    override fun getDisplayName(): String = "Issue Code"

    override fun getReferences(element: PsiElement, context: ProcessingContext, range: TextRange): Array<PsiReference> {
        return emptyArray()
    }
}