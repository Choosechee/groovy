/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.groovy.transform.sc.transformers;

import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;

import static org.codehaus.groovy.transform.stc.StaticTypesMarker.DIRECT_METHOD_CALL_TARGET;

class StaticMethodCallExpressionTransformer {

    private final StaticCompilationTransformer scTransformer;

    StaticMethodCallExpressionTransformer(final StaticCompilationTransformer scTransformer) {
        this.scTransformer = scTransformer;
    }

    Expression transformStaticMethodCallExpression(final StaticMethodCallExpression smce) {
        var target = smce.getNodeMetaData(DIRECT_METHOD_CALL_TARGET);
        if (target instanceof MethodNode) {
            var receiver = new ClassExpression(smce.getOwnerType().getPlainNodeReference());
            receiver.setLineNumber(smce.getLineNumber());
            receiver.setColumnNumber(smce.getColumnNumber());
            receiver.setLastLineNumber(receiver.getLineNumber());
            receiver.setLastColumnNumber(receiver.getColumnNumber());

            var mce = new MethodCallExpression(receiver, smce.getMethod(), smce.getArguments());
            mce.setMethodTarget((MethodNode) target);
            mce.setSourcePosition(smce);
            mce.copyNodeMetaData(smce);

            return scTransformer.transform(mce);
        }

        return scTransformer.superTransform(smce);
    }
}
