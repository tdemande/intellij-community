/*
 * Copyright 2000-2011 JetBrains s.r.o.
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
package com.intellij.util.containers;

import java.util.Map;

@SuppressWarnings("ClassNameSameAsAncestorName")
public class HashMap<K, V> extends java.util.HashMap<K, V> {
  public HashMap(int i, float v) {
    super(i, v);
  }

  public HashMap(int i) {
    super(i);
  }

  public HashMap() { }

  public <K1 extends K, V1 extends V> HashMap(Map<K1, V1> map) {
    super(map);
  }

  public void clear() {
    if (size() == 0) return; // optimization
    super.clear();
  }
}
