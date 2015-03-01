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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javassist.*;
import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.granitepowered.granite.Granite;

import java.io.IOException;
import java.util.Map;

public class DeobfuscatorTransformer implements IClassTransformer, IClassNameTransformer {
    private static CodeConverter converter;
    private static BiMap<String, String> classMap;

    private static boolean loaded;

    @Override
    public String unmapClassName(String name) {
        if (loaded) {
            if (classMap.inverse().containsKey(name)) {
                return classMap.inverse().get(name);
            } else {
                return name;
            }
        } else {
            return name;
        }
    }

    @Override
    public String remapClassName(String name) {
        if (loaded) {
            if (classMap.containsKey(name)) {
                return classMap.get(name);
            } else {
                return name;
            }
        } else {
            return name;
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (loaded) {
            // Read a CtClass from the byte array
            ByteArrayClassPath path = new ByteArrayClassPath(name, basicClass);
            ClassPool pool = new ClassPool();
            pool.appendClassPath(path);

            try {
                CtClass clazz = pool.get(name);

                // Fix all references (and implicitly rename this class too)
                for (Map.Entry<String, String> classEntry : classMap.entrySet()) {
                    clazz.replaceClassName(classEntry.getKey(), classEntry.getValue());
                }

                // Run the CodeConverter
                clazz.instrument(converter);

                // Return the class file
                return clazz.toBytecode();
            } catch (NotFoundException | CannotCompileException | IOException e) {
                e.printStackTrace();
            }
            return new byte[]{};
        } else {
            return basicClass;
        }
    }

    public static void init(GraniteLoader loader) {
        if (!loaded) {
            loaded = true;

            // Mappings will be loaded when Granite starts up, BUT
            // transform() will NOT be called before this happens
            Mappings mappings = loader.mappings;

            // Set up CodeConverter and the classMap
            converter = new CodeConverter();
            classMap = HashBiMap.create();

            // For every class
            for (Map.Entry<String, CtClass> classEntry : mappings.getClasses().entrySet()) {
                // Add it to the class map
                // This is a String, so it doesn't break if the CtClass is renamed

                classMap.put(classEntry.getValue().getName(), classEntry.getKey());

                // Fill the CodeConverter with all methods and fields
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
                    // Redirect the field to the new name and rename it
                    converter.redirectFieldAccess(fieldEntry.getValue(), fieldEntry.getValue().getDeclaringClass(), fieldEntry.getKey());
                    fieldEntry.getValue().setName(fieldEntry.getKey());
                }
            }
        }
    }
}