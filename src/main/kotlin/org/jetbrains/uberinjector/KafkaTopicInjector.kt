package org.jetbrains.uberinjector

import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.LocalQuickFixProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.injection.ReferenceInjector
import com.intellij.psi.meta.PsiMetaData
import com.intellij.psi.meta.PsiMetaOwner
import com.intellij.psi.meta.PsiPresentableMetaData
import com.intellij.psi.search.GlobalSearchScope.projectScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.util.ArrayUtil
import com.intellij.util.ProcessingContext
import org.jetbrains.uast.*
import javax.swing.Icon

class KafkaTopicInjector : ReferenceInjector() {
    override fun getId(): String = "uber-kafka-topic"

    override fun getDisplayName(): String = "Kafka Topic"

    override fun getIcon(): Icon = UberInjectorIcons.MESSAGES

    override fun getReferences(element: PsiElement, context: ProcessingContext, range: TextRange): Array<PsiReference> {
        return arrayOf(KafkaTopicReference(element))
    }

    class KafkaTopicReference(element: PsiElement) :
        PsiReferenceBase<PsiElement>(element, ElementManipulators.getValueTextRange(element)),
        EmptyResolveMessageProvider, LocalQuickFixProvider {

        private fun getReferenceTypeName(): String = "Kafka Topic"

        private fun getReferenceVariants(): Collection<String> {
            val uAnnotation = element.toUElement()?.getParentOfType<UAnnotation>() ?: return emptyList()
            val psiAnnotation = uAnnotation.javaPsi ?: return emptyList()
            val annotationClass = psiAnnotation.nameReferenceElement?.resolve() as? PsiClass ?: return emptyList()

            val annotationFqn = annotationClass.qualifiedName ?: return emptyList()

            val topics = mutableListOf<String>()
            for (member in AnnotatedElementsSearch.searchPsiMembers(
                annotationClass,
                projectScope(element.project)
            )) {
                val attributeValue = member.getAnnotation(annotationFqn)?.findAttributeValue("value")
                val value = attributeValue.toUElementOfExpectedTypes(UExpression::class.java) ?: continue
                val stringValue = value.evaluateString()
                if (stringValue != null && stringValue.isNotBlank()) {
                    topics.add(stringValue)
                }
            }

            return topics
        }

        private fun validate(value: String): Boolean = value.isNotBlank() && !value.contains("\\s")

        override fun resolve(): PsiElement? {
            val value = value
            if (!validate(value)) return null

            return KafkaTopicPsiElement(element, value, getReferenceTypeName())
        }

        override fun getUnresolvedMessagePattern(): String {
            return "Incorrect ${getReferenceTypeName()} ''{0}''"
        }

        override fun getQuickFixes(): Array<LocalQuickFix>? {
            return emptyArray()
        }

        override fun isReferenceTo(element: PsiElement): Boolean {
            return element is KafkaTopicPsiElement && this.value == element.topicId
        }

        override fun getVariants(): Array<Any> {
            return ArrayUtil.toObjectArray(getReferenceVariants().map {
                LookupElementBuilder.create(it).withIcon(UberInjectorIcons.MESSAGES)
            })
        }
    }

    class KafkaTopicPsiElement(
        private val parent: PsiElement,
        val topicId: String,
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
            return parent
        }

        override fun isEquivalentTo(another: PsiElement?): Boolean {
            return equals(another) ||
                    another != null && another is KafkaTopicPsiElement && another.topicId == topicId
        }
    }
}