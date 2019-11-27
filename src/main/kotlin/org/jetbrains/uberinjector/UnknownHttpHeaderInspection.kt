package org.jetbrains.uberinjector

import com.intellij.codeInspection.AbstractBaseUastLocalInspectionTool
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import org.jetbrains.uast.UClass

class UnknownHttpHeaderInspection : AbstractBaseUastLocalInspectionTool(UClass::class.java) {
    override fun checkClass(
        uClass: UClass,
        manager: InspectionManager,
        isOnTheFly: Boolean
    ): Array<ProblemDescriptor>? {

        return ProblemDescriptor.EMPTY_ARRAY
    }
}