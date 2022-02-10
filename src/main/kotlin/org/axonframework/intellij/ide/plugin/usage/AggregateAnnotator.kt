/*
 *  Copyright (c) 2022. Axon Framework
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.axonframework.intellij.ide.plugin.usage

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.axonframework.intellij.ide.plugin.api.AxonAnnotation
import org.axonframework.intellij.ide.plugin.util.isAggregate
import org.axonframework.intellij.ide.plugin.util.isAnnotated
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.toUElement

/**
 * Shows a warning on classes annotated with `@Aggregate` when:
 * - They have no zero-argument constructor. This constructor is required by Axon Framework to construct the aggregate.
 * - They have no AggregateIdentifier field. This is required by Axon Framework.
 *
 */
class AggregateAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.toUElement(UIdentifier::class.java) == null) {
            return
        }
        val clazz = element.parent.toUElement(UClass::class.java) ?: return
        if (!clazz.isAggregate()) {
            return
        }
        val isMissingEmptyConstructor = clazz.constructors.none { !it.hasParameters() }
        if (isMissingEmptyConstructor) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Axon Framework requires an empty constructor on aggregates.")
                    .needsUpdateOnTyping()
                    .create()
        }
        val isMissingFieldWithAnnotation = clazz.fields.none { uField ->
            uField.isAnnotated(AxonAnnotation.ENTITY_ID)
        }

        if (isMissingFieldWithAnnotation) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Axon Framework requires a field annotated with @AggregateIdentifier (or another meta-annotation based on @EntityId) in Aggregates.")
                    .needsUpdateOnTyping()
                    .create()
        }
    }
}
