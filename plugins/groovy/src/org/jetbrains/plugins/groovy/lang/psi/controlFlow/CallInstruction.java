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
package org.jetbrains.plugins.groovy.lang.psi.controlFlow;

import org.jetbrains.plugins.groovy.lang.psi.controlFlow.impl.InstructionImpl;

import java.util.Collections;

/**
 * @author ven
 */
public class CallInstruction extends InstructionImpl {
  private final InstructionImpl myCallee;

  public CallInstruction(int num, InstructionImpl callee) {
    super(null, num);
    myCallee = callee;
  }

  public String toString() {
    return super.toString() + " CALL " + myCallee.num();
  }

  public Iterable<? extends Instruction> succ(CallEnvironment env) {
    getStack(env, myCallee).push(this);
    return Collections.singletonList(myCallee);
  }

  public Iterable<? extends Instruction> allSucc() {
    return Collections.singletonList(myCallee);
  }

  protected String getElementPresentation() {
    return "";
  }
}
