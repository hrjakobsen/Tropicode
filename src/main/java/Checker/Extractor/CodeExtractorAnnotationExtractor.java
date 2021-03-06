/*
 *     Copyright (C) 2021.  Mathias Jakobsen <m.jakobsen.1@research.gla.ac.uk>
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

import Checker.Typestate;
import JVM.JvmClass;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public class CodeExtractorAnnotationExtractor extends AnnotationVisitor {
    private final String descriptor;
    private final boolean visible;
    private final JvmClass klass;
    private boolean isProtocol = false;


    public CodeExtractorAnnotationExtractor(AnnotationVisitor annotationVisitor, String descriptor, boolean visible, JvmClass klass) {
        super(Opcodes.ASM8, annotationVisitor);
        this.klass = klass;
        this.descriptor = descriptor;
        this.visible = visible;
        if (descriptor.equals("LAnnotations/Protocol;")) {
            isProtocol = true;
        }
    }


    @Override
    public void visit(String name, Object value) {
        if (isProtocol && name.equals("value")) {
            klass.setProtocol(Typestate.fromString(value.toString()));
        }
        super.visit(name, value);
    }
}
