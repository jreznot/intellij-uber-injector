package org.jetbrains.uberinjector

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.injection.ReferenceInjector
import com.intellij.util.ProcessingContext

class TeammateInjector : ReferenceInjector() {
    override fun getId(): String {
        TODO("not implemented")
    }

    override fun getDisplayName(): String {
        TODO("not implemented")
    }

    override fun getReferences(element: PsiElement, context: ProcessingContext, range: TextRange): Array<PsiReference> {
        TODO("not implemented")
    }
}