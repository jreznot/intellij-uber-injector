package org.jetbrains.uberinjector

import com.intellij.openapi.paths.WebReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.injection.ReferenceInjector
import com.intellij.util.ProcessingContext
import javax.swing.Icon

class IssueCodeInjector : ReferenceInjector() {
    override fun getId(): String = "uber-issue-code"

    override fun getDisplayName(): String = "Issue Code"

    override fun getIcon(): Icon = UberInjectorIcons.LINK

    override fun getReferences(element: PsiElement, context: ProcessingContext, range: TextRange): Array<PsiReference> {
        val value = range.substring(element.text)
        if (value.isBlank()) return emptyArray()

        val template = "https://youtrack.jetbrains.com/issue/"

        val url = if (template.contains("{}")) template.replace("{}", value) else template + value

        return arrayOf(WebReference(element, url))
    }
}