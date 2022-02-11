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

package org.axonframework.intellij.ide.plugin.handlers.searchers

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import org.axonframework.intellij.ide.plugin.api.Handler
import org.axonframework.intellij.ide.plugin.api.MessageHandlerType
import org.axonframework.intellij.ide.plugin.handlers.types.SagaEventHandler
import org.axonframework.intellij.ide.plugin.util.findProcessingGroup
import org.axonframework.intellij.ide.plugin.util.resolvePayloadType
import org.axonframework.intellij.ide.plugin.util.toQualifiedName

/**
 * Searches for any saga event handlers.
 *
 * @see org.axonframework.intellij.ide.plugin.handlers.types.SagaEventHandler
 */
class SagaEventHandlerSearcher : AbstractHandlerSearcher(MessageHandlerType.SAGA) {
    override fun createMessageHandler(method: PsiMethod, annotation: PsiClass?): Handler? {
        val payloadType = method.resolvePayloadType()?.toQualifiedName() ?: return null
        return SagaEventHandler(method, payloadType, method.findProcessingGroup())
    }
}
