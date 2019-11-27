package org.jetbrains.uberinjector

import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.LocalQuickFixProvider
import com.intellij.find.actions.ShowUsagesAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.ui.popup.JBPopupFactory
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
import com.intellij.util.ProcessingContext
import javax.swing.Icon

class TeammateInjector : ReferenceInjector() {
    override fun getId(): String = "uber-teammate"

    override fun getDisplayName(): String = "Teammate Username"

    override fun getReferences(element: PsiElement, context: ProcessingContext, range: TextRange): Array<PsiReference> {
        return arrayOf(TeammateReference(element))
    }

    class TeammateReference(element: PsiElement) :
        PsiReferenceBase<PsiElement>(element, ElementManipulators.getValueTextRange(element)),
        EmptyResolveMessageProvider, LocalQuickFixProvider {

        private fun getReferenceTypeName(): String = "Teammate"

        private fun validate(value: String): Boolean = value.isNotBlank()

        override fun resolve(): PsiElement? {
            val value = value
            if (!validate(value)) return null

            return TeammatePsiElement(element, getReferenceTypeName())
        }

        override fun getUnresolvedMessagePattern(): String {
            return "Incorrect ${getReferenceTypeName()} ''{0}''"
        }

        override fun getQuickFixes(): Array<LocalQuickFix>? {
            return emptyArray()
        }

        override fun isReferenceTo(element: PsiElement): Boolean {
            return element is TeammatePsiElement
        }

        override fun getVariants(): Array<Any> {
            return emptyArray()
        }
    }

    class TeammatePsiElement(
        private val parent: PsiElement,
        private val typeName: String
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
            return this
        }

        override fun canNavigate(): Boolean {
            return true
        }

        override fun navigate(requestFocus: Boolean) {
            if (DumbService.getInstance(project).isDumb) return
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
            val popupPosition = JBPopupFactory.getInstance().guessBestPopupLocation(editor)

            ShowUsagesAction().startFindUsages(
                this, popupPosition, editor,
                ShowUsagesAction.getUsagesPageSize()
            )
        }
    }
}