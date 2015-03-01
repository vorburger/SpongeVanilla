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
import javassist.bytecode.Descriptor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Mappings {
    private Map<String, CtClass> classes;
    private Map<String, Map<String, CtMethod>> methods;
    private Map<String, Map<String, CtField>> fields;

    public Mappings() {
        classes = new HashMap<>();
        methods = new HashMap<>();
        fields = new HashMap<>();
    }

    // NOTE
    // Make sure to add ALL classes before starting to add the methods and fields

    public void addClassMapping(String obf, String deobf) {
        // Add a class mapping
        try {
            classes.put(deobf, Classes.pool.get(obf));
            methods.put(deobf, new HashMap<String, CtMethod>());
            fields.put(deobf, new HashMap<String, CtField>());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addMethodMapping(String deobfClass, String obfMethod, String deobfMethod) {
        CtClass clazz = classes.get(deobfClass);

        // Break the obfuscated method into bits
        // (obfuscated method: a(int,boolean,java.lang.Object):void)
        String obfName = obfMethod.split("\\(")[0];
        String obfSig = obfMethod.split("\\(")[1].split("\\)")[0];

        // There's really no point in the return type
        // Since it's not possible to have two methods with the same return type in Java
        // However, Mojang's obfuscator may catch onto that, so we're keeping it here in case that happens
        String obfReturn = obfMethod.split(":")[1];

        // Split the signature into classes, and re-obfuscate deobfuscated names if possible
        String[] paramNames = obfSig.split(",");
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];

            if (classes.containsKey(paramName)) {
                paramNames[i] = classes.get(paramName).getName();
            }
        }

        // Re-obfuscate the return type
        if (classes.containsKey(obfReturn)) {
            obfReturn = classes.get(obfReturn).getName();
        }

        obfSig = StringUtils.join(paramNames, ",");

        // Find a method that matches exactly
        for (CtMethod method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(obfName)) {
                String prettyDescriptor = Descriptor.toString(method.getSignature());

                if (prettyDescriptor.equals("(" + obfSig + ")")) {
                    try {
                        if (method.getReturnType().getName().equals(obfReturn)) {
                            // Match!

                            methods.get(deobfClass).put(deobfMethod, method);
                        }
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void addFieldMapping(String deobfClass, String obfField, String deobfField) {
        CtClass clazz = classes.get(deobfClass);

        String obfName = obfField.split(":")[0];
        String obfType = obfField.split(":")[1];

        // Re-obfuscate the type
        if (classes.containsKey(obfType)) {
            obfType = classes.get(obfType).getName();
        }

        for (CtField field : clazz.getDeclaredFields()) {
            if (field.getName().equals(obfName)) {
                try {
                    if (field.getType().getName().equals(obfType)) {
                        // Match!

                        fields.get(deobfClass).put(deobfField, field);
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, CtClass> getClasses() {
        return classes;
    }

    public Map<String, Map<String, CtMethod>> getMethods() {
        return methods;
    }

    public Map<String, Map<String, CtField>> getFields() {
        return fields;
    }
}
