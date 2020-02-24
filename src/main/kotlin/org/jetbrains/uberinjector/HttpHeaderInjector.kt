package org.jetbrains.uberinjector

import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.LocalQuickFixProvider
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.injection.ReferenceInjector
import com.intellij.psi.meta.PsiMetaData
import com.intellij.psi.meta.PsiMetaOwner
import com.intellij.psi.meta.PsiPresentableMetaData
import com.intellij.util.ArrayUtil
import com.intellij.util.ProcessingContext
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.evaluateString
import org.jetbrains.uast.toUElement
import javax.swing.Icon

class HttpHeaderInjector : ReferenceInjector() {
    override fun getId(): String = "uber-http-header"

    override fun getDisplayName(): String = "HTTP Header"

    override fun getIcon(): Icon = UberInjectorIcons.CHAT

    override fun getReferences(element: PsiElement, context: ProcessingContext, range: TextRange): Array<PsiReference> {
        return arrayOf(HttpHeaderReference(element))
    }

    class HttpHeaderReference(element: PsiElement) :
        PsiReferenceBase<PsiElement>(element, ElementManipulators.getValueTextRange(element)),
        EmptyResolveMessageProvider, LocalQuickFixProvider {

        private fun getReferenceTypeName(): String = "HTTP Header"

        private fun getReferenceVariants(): Collection<String> {
            val list = ArrayList(CompletionData.HTTP_HEADERS)
            list.addAll(CustomHttpHeadersStore.getItems())
            return list
        }

        private fun validate(value: String): Boolean {
            return value.isNotBlank()
                    && (CompletionData.HTTP_HEADERS.contains(value) || CustomHttpHeadersStore.getItems().contains(value))
        }

        override fun resolve(): PsiElement? {
            val value = value
            if (!validate(value)) return null

            return HttpHeaderPsiElement(element, getReferenceTypeName(), value)
        }

        override fun getUnresolvedMessagePattern(): String {
            return "Unknown ${getReferenceTypeName()} ''{0}''"
        }

        override fun getQuickFixes(): Array<LocalQuickFix>? {
            return arrayOf(AddCustomHttpHeaderFix(myElement))
        }

        override fun getVariants(): Array<Any> {
            return ArrayUtil.toObjectArray(getReferenceVariants().map {
                LookupElementBuilder.create(it).withIcon(UberInjectorIcons.TEXT)
            })
        }
    }

    class HttpHeaderPsiElement(
        private val parent: PsiElement,
        private val typeName: String,
        private val value: String
    ) : FakePsiElement(), PsiMetaOwner, PsiPresentableMetaData {

        override fun getName(): String {
            return ElementManipulators.getValueText(parent)
        }

        override fun getParent(): PsiElement {
            return parent
        }

        override fun getName(context: PsiElement): String? {
            return name
        }

        override fun getIcon(): Icon? {
            return null
        }

        override fun getDeclaration(): PsiElement {
            return this
        }

        override fun init(element: PsiElement) {}

        override fun getMetaData(): PsiMetaData? {
            return this
        }

        override fun getTypeName(): String? {
            return typeName
        }

        override fun getNavigationElement(): PsiElement {
            return parent
        }

        override fun isEquivalentTo(another: PsiElement?): Boolean {
            return equals(another) ||
                    another != null && another is HttpHeaderPsiElement && another.value == value
        }
    }

    class AddCustomHttpHeaderFix(myElement: PsiElement) : LocalQuickFixOnPsiElement(myElement) {
        override fun getFamilyName(): String = "Register custom HTTP Header"

        override fun getText(): String = familyName

        override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
            val containingFile = startElement.containingFile

            val expr = startElement.toUElement() as? UExpression ?: return
            val value = expr.evaluateString() ?: return

            WriteCommandAction.runWriteCommandAction(project, "Register custom HTTP Header", null, Runnable {
                CustomHttpHeadersStore.addItem(value)
            }, containingFile)
        }
    }
}