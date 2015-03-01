/*
 * License (MIT)
 *
 * Copyright (c) 2014-2015 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.loader;

import javassist.*;

import java.util.Map;
import java.util.Set;

public class BytecodeDeobfuscator {
    public void deobfuscate(Set<CtClass> classesToEdit, Mappings mappings) {
        CodeConverter converter = new CodeConverter();

        // Loop through classes in mappings
        for (Map.Entry<String, CtClass> classEntry : mappings.getClasses().entrySet()) {
            // Add the methods and fields to the converter

            for (Map.Entry<String, CtMethod> methodEntry : mappings.getMethods().get(classEntry.getKey()).entrySet()) {
                // Rename the method to the new name and redirect the old name to the new method
                String oldName = methodEntry.getValue().getName();
                methodEntry.getValue().setName(methodEntry.getKey());

                try {
                    converter.redirectMethodCall(oldName, methodEntry.getValue());
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            }

            for (Map.Entry<String, CtField> fieldEntry : mappings.getFields().get(classEntry.getKey()).entrySet()) {
                // Redirect the field to the new name, but the field hasn't actually been renamed (that happens later)
                converter.redirectFieldAccess(fieldEntry.getValue(), fieldEntry.getValue().getDeclaringClass(), fieldEntry.getKey());
            }
        }

        // Apply the code converter to all classes, and replace class accesses
        for (CtClass clazz : classesToEdit) {
            try {
                // Replace class accesses, which can't be done with a converter
                // This will also implicitly rename the class itself
                for (Map.Entry<String, CtClass> classEntry : mappings.getClasses().entrySet()) {
                    clazz.replaceClassName(classEntry.getValue().getName(), classEntry.getKey());
                }

                // Apply code converter
                clazz.instrument(converter);
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }
}
