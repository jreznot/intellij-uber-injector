package org.jetbrains.uberinjector

import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.LocalQuickFixProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.injection.ReferenceInjector
import com.intellij.psi.meta.PsiMetaData
import com.intellij.psi.meta.PsiMetaOwner
import com.intellij.psi.meta.PsiPresentableMetaData
import com.intellij.util.ArrayUtil
import com.intellij.util.ProcessingContext
import javax.swing.Icon

class MimeTypeInjector : ReferenceInjector() {
    override fun getId(): String = "uber-mime-type"

    override fun getDisplayName(): String = "MIME Type"

    override fun getIcon(): Icon = UberInjectorIcons.TEXT

    override fun getReferences(element: PsiElement, context: ProcessingContext, range: TextRange): Array<PsiReference> {
        return arrayOf(MimeTypeReference(element))
    }

    class MimeTypeReference(element: PsiElement) :
        PsiReferenceBase<PsiElement>(element, ElementManipulators.getValueTextRange(element)),
        EmptyResolveMessageProvider, LocalQuickFixProvider {

        private fun getReferenceTypeName(): String = "MIME Type"

        private fun getReferenceVariants(): Collection<String> = CompletionData.MIME_TYPES

        private fun validate(value: String): Boolean = CompletionData.MIME_PATTERN.matcher(value).matches()

        override fun resolve(): PsiElement? {
            val value = value
            if (!validate(value)) return null

            return MimeTypePsiElement(element, getReferenceTypeName(), value)
        }

        override fun getUnresolvedMessagePattern(): String {
            return "Incorrect ${getReferenceTypeName()} ''{0}''"
        }

        override fun getQuickFixes(): Array<LocalQuickFix>? {
            return emptyArray()
        }

        override fun isReferenceTo(element: PsiElement): Boolean {
            return element is MimeTypePsiElement
        }

        override fun getVariants(): Array<Any> {
            return ArrayUtil.toObjectArray(getReferenceVariants().map {
                LookupElementBuilder.create(it).withIcon(UberInjectorIcons.TEXT)
            })
        }
    }

    class MimeTypePsiElement(
        private val parent: PsiElement,
        private val typeName: String,
        val value: String
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
                    another != null && another is MimeTypePsiElement && another.value == value
        }
    }
}