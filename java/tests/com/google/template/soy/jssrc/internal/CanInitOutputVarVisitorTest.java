/*
 * Copyright 2015 Google Inc.
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

package com.google.template.soy.jssrc.internal;

import static com.google.common.truth.Truth.assertThat;

import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.SoyJsSrcOptions.CodeStyle;
import com.google.template.soy.soytree.SoyNode;

import junit.framework.TestCase;

/**
 * Unit tests for CanInitOutputVarVisitor.
 *
 */
public class CanInitOutputVarVisitorTest extends TestCase {


  private static SoyJsSrcOptions jsSrcOptions;


  @Override protected void setUp() {
    jsSrcOptions = new SoyJsSrcOptions();
  }


  public void testSameValueAsIsComputableAsJsExprsVisitor() {

    runTestHelper("Blah blah.", true);

    runTestHelper("{msg desc=\"\"}Blah{/msg}", true, 0);  // GoogMsgDefNode

    runTestHelper("{msg desc=\"\"}Blah{/msg}", true, 1);  // GoogMsgRefNode

    runTestHelper("{msg desc=\"\"}<a href=\"{$url}\">Click here</a>{/msg}",
                  true, 0, 0, 0);  // MsgHtmlTagNode

    runTestHelper("{msg desc=\"\"}<a href=\"{$url}\">Click here</a>{/msg}",
                  true, 0, 0, 2);  // MsgHtmlTagNode

    runTestHelper("{msg desc=\"\"}<span id=\"{for $i in range(3)}{$i}{/for}\">{/msg}",
                  true, 0, 0, 0);  // MsgHtmlTagNode

    runTestHelper("{$boo.foo}", true);

    runTestHelper("{xid selected-option}", true);

    runTestHelper("{css selected-option}", true);

    runTestHelper("{switch $boo}{case 0}Blah{case 1}Bleh{default}Bluh{/switch}", true);

    runTestHelper("{foreach $boo in $booze}{$boo}{/foreach}", true);

    runTestHelper("{for $i in range(4)}{$i + 1}{/for}", true);

    runTestHelper("{if $boo}Blah{elseif $foo}Bleh{else}Bluh{/if}", true);

    runTestHelper("{if $goo}{foreach $moo in $moose}{$moo}{/foreach}{/if}", true);

    // Note: Default code style is 'stringbuilder'.
    runTestHelper("{call name=\".foo\" data=\"all\" /}", true);

    jsSrcOptions.setCodeStyle(CodeStyle.CONCAT);
    runTestHelper("{call name=\".foo\" data=\"all\" /}", true);

    runTestHelper("{call name=\".foo\" data=\"$boo\"}{param key=\"goo\" value=\"$moo\" /}{/call}",
                  true);

    runTestHelper("{call name=\".foo\" data=\"$boo\"}{param key=\"goo\"}Blah{/param}{/call}",
                  true);
  }


  public void testNotSameValueAsIsComputableAsJsExprsVisitor() {

    jsSrcOptions.setCodeStyle(CodeStyle.CONCAT);
    runTestHelper("{call name=\".foo\" data=\"$boo\"}" +
                  "{param key=\"goo\"}{foreach $moo in $moose}{$moo}{/foreach}{/param}" +
                  "{/call}",
                  false);
  }


  private static void runTestHelper(
      String soyNodeCode, boolean isSameValueAsIsComputableAsJsExprsVisitor) {
    runTestHelper(soyNodeCode, isSameValueAsIsComputableAsJsExprsVisitor, 0);
  }


  /**
   * @param indicesToNode Series of indices for walking down to the node we want to test.
   */
  private static void runTestHelper(
      String soyCode, boolean isSameValueAsIsComputableAsJsExprsVisitor, int... indicesToNode) {

    SoyNode node = JsSrcTestUtils.parseSoyCodeAndGetNode(soyCode, indicesToNode).getParseTree();

    IsComputableAsJsExprsVisitor icajev = new IsComputableAsJsExprsVisitor(jsSrcOptions);
    CanInitOutputVarVisitor ciovv = new CanInitOutputVarVisitor(jsSrcOptions, icajev);
    assertThat(ciovv.exec(node) == icajev.exec(node))
        .isEqualTo(isSameValueAsIsComputableAsJsExprsVisitor);
  }

}
