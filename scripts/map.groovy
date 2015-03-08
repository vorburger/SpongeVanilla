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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import javassist.ClassPool
import javassist.bytecode.Descriptor
import javassist.bytecode.SignatureAttribute

JsonElement json = new Gson().fromJson(new FileReader(new File("../run/mappings.json")), JsonElement.class);

ClassPool.getDefault().appendClassPath("../run/minecraft_server.1.8.3.jar")

classes = json.getAsJsonObject().getAsJsonObject("classes")
classes.entrySet().forEach {
    ctClass = ClassPool.default.get(it.value.asJsonObject.getAsJsonPrimitive("name").getAsString())

    JsonObject methods = it.value.asJsonObject.getAsJsonObject("methods")
    if (methods != null) {
        if (methods.entrySet().empty) classes.remove("methods")
        new HashSet(methods.entrySet()).forEach {
            sig = it.key
            name = it.value

            oname = sig.split("\\(")[0]
            sig = "(" + sig.split("\\(")[1]

            pp = Descriptor.toString(sig)
            ret = Descriptor.toString(sig.split("\\)")[1])

            if (pp.length() > 2) {
                ppnop = pp.split("\\(")[1].split("\\)")[0]
                pms = ppnop.split(",")

                pms.clone().eachWithIndex { pm, idx ->
                    classes.entrySet().forEach {
                        naem = it.value.asJsonObject.getAsJsonPrimitive("name").getAsString()
                        if (pm.equals(naem)) {
                            pms[idx] = it.key
                        }
                    }
                }

                pp = "(" + pms.join(",") + ")"
            }
            classes.entrySet().forEach {
                naem = it.value.asJsonObject.getAsJsonPrimitive("name").getAsString()
                if (ret.equals(naem)) {
                    ret = it.key
                }
            }
            pp = oname + pp + ":" + ret
            methods.remove(it.key)
            methods.add(pp, it.value)
        }
    }

    JsonObject fields = it.value.asJsonObject.getAsJsonObject("fields")
    if (fields != null) {
        if (fields.entrySet().empty) classes.remove("fields")
        new HashSet(fields.entrySet()).forEach {
            obf = it.key
            name = it.value

            cf = ctClass.getDeclaredField(obf)

            t = cf.getType().getName()
            classes.entrySet().forEach {
                naem = it.value.asJsonObject.getAsJsonPrimitive("name").getAsString()
                if (t.equals(naem)) {
                    t = it.key
                }
            }

            fields.remove(it.key)
            fields.add(obf + ":" + t, it.value)
        }
    }
}

println new GsonBuilder().setPrettyPrinting().create().toJson(json)