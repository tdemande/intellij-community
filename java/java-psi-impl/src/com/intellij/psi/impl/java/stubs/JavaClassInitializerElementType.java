/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.psi.impl.java.stubs;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.impl.java.stubs.impl.PsiClassInitializerStubImpl;
import com.intellij.psi.impl.source.PsiClassInitializerImpl;
import com.intellij.psi.impl.source.tree.java.ClassInitializerElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author max
 */
public class JavaClassInitializerElementType extends JavaStubElementType<PsiClassInitializerStub, PsiClassInitializer> {
  public JavaClassInitializerElementType() {
    super("CLASS_INITIALIZER");
  }

  @NotNull
  @Override
  public ASTNode createCompositeNode() {
    return new ClassInitializerElement();
  }

  @Override
  public PsiClassInitializer createPsi(final PsiClassInitializerStub stub) {
    return getPsiFactory(stub).createClassInitializer(stub);
  }

  @Override
  public PsiClassInitializer createPsi(final ASTNode node) {
    return new PsiClassInitializerImpl(node);
  }

  @Override
  public PsiClassInitializerStub createStub(final LighterAST tree,
                                            final LighterASTNode node,
                                            final StubElement parentStub) {
    return new PsiClassInitializerStubImpl(parentStub);
  }

  @Override
  public void serialize(final PsiClassInitializerStub stub, final StubOutputStream dataStream) throws IOException {
  }

  @Override
  public PsiClassInitializerStub deserialize(final StubInputStream dataStream, final StubElement parentStub) throws IOException {
    return new PsiClassInitializerStubImpl(parentStub);
  }

  @Override
  public void indexStub(final PsiClassInitializerStub stub, final IndexSink sink) {
  }
}
