/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
 *
 *     Tropicode is a Java bytecode analyser used to verify object protocols.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package Checker.Extractor;

import JVM.JvmClass;
import JVM.JvmMethod;
import JVM.JvmValue;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@Log4j2
public class CodeExtractorClassVisitor extends ClassVisitor {

    JvmClass klass = new JvmClass();
    Set<String> classDependencies = new HashSet<>();

    public CodeExtractorClassVisitor() {
        super(Opcodes.ASM8);
    }

    public CodeExtractorClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM8, cv);
    }

    public JvmClass getJvmClass() {
        return klass;
    }

    public Set<String> getClassDependencies() {
        return classDependencies;
    }

    @Override
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces) {
        if (superName != null) {
            classDependencies.add(superName);
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(
            int access, String name, String descriptor, String signature, String[] exceptions) {
        JvmMethod m = new JvmMethod(access, name, descriptor, signature);
        klass.getMethods().put(name + descriptor, m);
        return new CodeExtractorMethodVisitor(
                super.visitMethod(access, name, descriptor, signature, exceptions),
                m,
                classDependencies);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return new CodeExtractorAnnotationExtractor(
                super.visitAnnotation(desc, visible), desc, visible, klass);
    }

    @Override
    public FieldVisitor visitField(
            int access, String name, String descriptor, String signature, Object value) {
        boolean isStatic = (access & Opcodes.ACC_STATIC) > 0;
        if (isStatic) {
            if (descriptor.charAt(0) == 'L') {
                klass.getStaticFields().put(name, JvmValue.UNKNOWN_REFERENCE);
            } else {
                klass.getStaticFields().put(name, JvmValue.UNKNOWN);
            }
        } else {
            klass.getFields().add(name);
        }
        return super.visitField(access, name, descriptor, signature, value);
    }
}
