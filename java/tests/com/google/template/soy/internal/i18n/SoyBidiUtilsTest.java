/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.template.soy.internal.i18n;

import junit.framework.TestCase;


/**
 * Unit tests for SoyBidiUtils.
 *
 */
public class SoyBidiUtilsTest extends TestCase {


  public void testGetBidiGlobalDir() {
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir(null));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("en"));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("fr"));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("ru"));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("ja"));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("zh-CN"));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("fil"));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("az"));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("iw-Latn"));
    assertEquals(1, SoyBidiUtils.getBidiGlobalDir("zz-ZZ"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("qbi"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("en-US-psrtl"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("en-x-psrtl"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("iw"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("iw-IL"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("he"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("ar"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("ar-EG"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("fa"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("ur"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("az-Arab"));
    assertEquals(-1, SoyBidiUtils.getBidiGlobalDir("az-Arab-IR"));
  }


  public void testGetBidiFormatter() {
    assertEquals(1, SoyBidiUtils.getBidiFormatter(1).getContextDir().ord);
    assertEquals(-1, SoyBidiUtils.getBidiFormatter(-1).getContextDir().ord);
    assertTrue(SoyBidiUtils.getBidiFormatter(1) == SoyBidiUtils.getBidiFormatter(100));
    assertTrue(SoyBidiUtils.getBidiFormatter(-1) == SoyBidiUtils.getBidiFormatter(-5));
    assertTrue(SoyBidiUtils.getBidiFormatter(-1) != SoyBidiUtils.getBidiFormatter(1));
  }


  public void testDecodeBidiGlobalDirFromJsOptions() {
    assertNull(SoyBidiUtils.decodeBidiGlobalDirFromJsOptions(0, false));

    BidiGlobalDir bidiGlobalDir;

    bidiGlobalDir = SoyBidiUtils.decodeBidiGlobalDirFromJsOptions(1, false);
    assertTrue(bidiGlobalDir.isStaticValue());
    assertEquals(bidiGlobalDir.getStaticValue(), 1);

    bidiGlobalDir = SoyBidiUtils.decodeBidiGlobalDirFromJsOptions(-1, false);
    assertTrue(bidiGlobalDir.isStaticValue());
    assertEquals(bidiGlobalDir.getStaticValue(), -1);

    bidiGlobalDir = SoyBidiUtils.decodeBidiGlobalDirFromJsOptions(0, true);
    assertFalse(bidiGlobalDir.isStaticValue());
    assertEquals(bidiGlobalDir.getCodeSnippet(), "soy.$$IS_LOCALE_RTL?-1:1");
  }

  public void testDecodeBidiGlobalDirFromPyOptions() {
    assertNull(SoyBidiUtils.decodeBidiGlobalDirFromPyOptions(null));
    assertNull(SoyBidiUtils.decodeBidiGlobalDirFromPyOptions(""));

    BidiGlobalDir bidiGlobalDir;

    bidiGlobalDir = SoyBidiUtils.decodeBidiGlobalDirFromPyOptions("mod.is_rtl");
    assertFalse(bidiGlobalDir.isStaticValue());
    assertEquals(bidiGlobalDir.getCodeSnippet(), "-1 if external_bidi.is_rtl() else 1");

    bidiGlobalDir = SoyBidiUtils.decodeBidiGlobalDirFromPyOptions("package.mod.is_rtl");
    assertFalse(bidiGlobalDir.isStaticValue());
    assertEquals(bidiGlobalDir.getCodeSnippet(), "-1 if external_bidi.is_rtl() else 1");
  }

  public void testInvalidDecodeBidiGlobalDirFromPyOptions() {
    try {
      SoyBidiUtils.decodeBidiGlobalDirFromPyOptions("is_rtl");
      fail("bidiIsRtlFn without a module path did not except");
    } catch (IllegalArgumentException expected) {
    }

    try {
      SoyBidiUtils.decodeBidiGlobalDirFromPyOptions(".is_rtl");
      SoyBidiUtils.decodeBidiGlobalDirFromPyOptions("is_rtl.");
      fail("bidiIsRtlFn with invalid path did not except");
    } catch (IllegalArgumentException expected) {
    }
  }
}
