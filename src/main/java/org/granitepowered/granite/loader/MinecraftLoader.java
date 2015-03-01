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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class MinecraftLoader {
    public static void createPool(File originalJarFile) {
        // Create a class pool and add the original jar file
        ClassPool pool = new ClassPool(true);
        try {
            pool.appendClassPath(originalJarFile.getPath());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        // Add the pool to Classes, so other classes can use it
        Classes.pool = pool;
    }

    public void load(File originalJarFile, File outputFile, Mappings mappings) {
        try {
            // Iterate through every file in the jar, and load it from the class pool into a set
            Set<CtClass> classes = new HashSet<>();

            JarFile jar = new JarFile(originalJarFile);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    classes.add(Classes.pool.get(entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6)));
                }
            }

            // Deobfuscate all the classes
            BytecodeDeobfuscator deobf = new BytecodeDeobfuscator();
            deobf.deobfuscate(classes, mappings);

            // TODO: Run modifier stuff here

            // Write all the classes back to a jar file
            if (outputFile.exists()) {
                FileUtils.forceDelete(outputFile);
            }

            JarOutputStream output = new JarOutputStream(new FileOutputStream(outputFile));
            for (CtClass clazz : classes) {
                ZipEntry entry = new ZipEntry(clazz.getName().replaceAll("\\.", "/") + ".class");
                output.putNextEntry(entry);

                clazz.toBytecode(new DataOutputStream(output));
            }

            output.close();

            // Add the jar to the system class loader using reflection
            URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(systemClassLoader, outputFile.toURI().toURL());

            // Mark that loading is done, so the classes can be loaded by the JVM
            Classes.loadingDone = true;
        } catch (NotFoundException | IOException | CannotCompileException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
