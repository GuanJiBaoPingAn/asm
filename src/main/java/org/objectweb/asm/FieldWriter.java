/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.objectweb.asm;

/**
 * {@link FieldVisitor} 的实现类，用于生成Java 域的字节码
 * field_info {
 *  u2 access_flags;
 *  u2 name_index;
 *  u2 descriptor_index;
 *  u2 attributes_count;
 *  attribute_info attributes[attributes_count];
 * }
 * An {@link FieldVisitor} that generates Java fields in bytecode form.
 * 
 * @author Eric Bruneton
 */
final class FieldWriter extends FieldVisitor {

    /**
     * The class writer to which this field must be added.
     */
    private final ClassWriter cw;

    /**
     * 该域的访问标识
     * Access flags of this field.
     */
    private final int access;

    /**
     * 该域名称在常量池中的索引
     * The index of the constant pool item that contains the name of this
     * method.
     */
    private final int name;

    /**
     * 该域描述符在常量池中索引
     * The index of the constant pool item that contains the descriptor of this
     * field.
     */
    private final int desc;

    /**
     * 该域签名在常量池中的索引
     * The index of the constant pool item that contains the signature of this
     * field.
     */
    private int signature;

    /**
     * 该域值在常量池中的索引
     * The index of the constant pool item that contains the constant value of
     * this field.
     */
    private int value;

    /**
     * 该域上运行时可见的注解
     * The runtime visible annotations of this field. May be <tt>null</tt>.
     */
    private AnnotationWriter anns;

    /**
     * 该域上运行时不可见的注解
     * The runtime invisible annotations of this field. May be <tt>null</tt>.
     */
    private AnnotationWriter ianns;

    /**
     * 该域上运行时可见的类型注解
     * The runtime visible type annotations of this field. May be <tt>null</tt>.
     */
    private AnnotationWriter tanns;

    /**
     * 该域上运行时不可见的类型注解
     * The runtime invisible type annotations of this field. May be
     * <tt>null</tt>.
     */
    private AnnotationWriter itanns;

    /**
     * 该域的非标准属性
     * The non standard attributes of this field. May be <tt>null</tt>.
     */
    private Attribute attrs;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new {@link FieldWriter}.
     * 
     * @param cw
     *            the class writer to which this field must be added.
     * @param access
     *            the field's access flags (see {@link Opcodes}).
     * @param name
     *            the field's name.
     * @param desc
     *            the field's descriptor (see {@link Type}).
     * @param signature
     *            the field's signature. May be <tt>null</tt>.
     * @param value
     *            the field's constant value. May be <tt>null</tt>.
     */
    FieldWriter(final ClassWriter cw, final int access, final String name,
            final String desc, final String signature, final Object value) {
        super(Opcodes.ASM5);
        if (cw.firstField == null) {
            cw.firstField = this;
        } else {
            cw.lastField.fv = this;
        }
        cw.lastField = this;
        this.cw = cw;
        this.access = access;
        this.name = cw.newUTF8(name);
        this.desc = cw.newUTF8(desc);
        if (ClassReader.SIGNATURES && signature != null) {
            this.signature = cw.newUTF8(signature);
        }
        if (value != null) {
            this.value = cw.newConstItem(value).index;
        }
    }

    // ------------------------------------------------------------------------
    // Implementation of the FieldVisitor abstract class
    // ------------------------------------------------------------------------

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
            final boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv, 2);
        if (visible) {
            aw.next = anns;
            anns = aw;
        } else {
            aw.next = ianns;
            ianns = aw;
        }
        return aw;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef,
            final TypePath typePath, final String desc, final boolean visible) {
        if (!ClassReader.ANNOTATIONS) {
            return null;
        }
        ByteVector bv = new ByteVector();
        // write target_type and target_info
        AnnotationWriter.putTarget(typeRef, typePath, bv);
        // write type, and reserve space for values count
        bv.putShort(cw.newUTF8(desc)).putShort(0);
        AnnotationWriter aw = new AnnotationWriter(cw, true, bv, bv,
                bv.length - 2);
        if (visible) {
            aw.next = tanns;
            tanns = aw;
        } else {
            aw.next = itanns;
            itanns = aw;
        }
        return aw;
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        attr.next = attrs;
        attrs = attr;
    }

    @Override
    public void visitEnd() {
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * 返回该域的大小
     * Returns the size of this field.
     * 
     * @return the size of this field.
     */
    int getSize() {
        int size = 8;
        if (value != 0) {
            cw.newUTF8("ConstantValue");
            // jvms 4.7.2
            size += 8;
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5 || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                cw.newUTF8("Synthetic");
                // jvms 4.7.8
                size += 6;
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            cw.newUTF8("Deprecated");
            // jvms 4.7.15
            size += 6;
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            cw.newUTF8("Signature");
            // jvms 4.7.9
            size += 8;
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            cw.newUTF8("RuntimeVisibleAnnotations");
            // jvms 4.7.16
            size += 8 + anns.getSize();
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            cw.newUTF8("RuntimeInvisibleAnnotations");
            // jvms 4.7.17
            size += 8 + ianns.getSize();
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            cw.newUTF8("RuntimeVisibleTypeAnnotations");
            // 4.7.20
            size += 8 + tanns.getSize();
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            cw.newUTF8("RuntimeInvisibleTypeAnnotations");
            // 4.7.21
            size += 8 + itanns.getSize();
        }
        if (attrs != null) {
            size += attrs.getSize(cw, null, 0, -1, -1);
        }
        return size;
    }

    /**
     * 将域写入给定的字节数组
     * Puts the content of this field into the given byte vector.
     * 
     * @param out
     *            where the content of this field must be put.
     */
    void put(final ByteVector out) {
        final int FACTOR = ClassWriter.TO_ACC_SYNTHETIC;
        int mask = Opcodes.ACC_DEPRECATED | ClassWriter.ACC_SYNTHETIC_ATTRIBUTE
                | ((access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) / FACTOR);
        out.putShort(access & ~mask).putShort(name).putShort(desc);
        int attributeCount = 0;
        if (value != 0) {
            ++attributeCount;
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                ++attributeCount;
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            ++attributeCount;
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            ++attributeCount;
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            ++attributeCount;
        }
        if (attrs != null) {
            attributeCount += attrs.getCount();
        }
        out.putShort(attributeCount);
        if (value != 0) {
            /**
             * jvms8 4.7.2
             * ConstantValue_attribute {
             *  u2 attribute_name_index;
             *  u4 attribute_length;
             *  u2 constantvalue_index;
             * }
             */
            out.putShort(cw.newUTF8("ConstantValue"));
            out.putInt(2).putShort(value);
        }
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            /**
             * jvms8 4.7.8
             * Synthetic_attribute {
             *  u2 attribute_name_index;
             *  u4 attribute_length;
             * }
             */
            if ((cw.version & 0xFFFF) < Opcodes.V1_5
                    || (access & ClassWriter.ACC_SYNTHETIC_ATTRIBUTE) != 0) {
                out.putShort(cw.newUTF8("Synthetic")).putInt(0);
            }
        }
        if ((access & Opcodes.ACC_DEPRECATED) != 0) {
            /**
             * jvms8 4.7.15
             * Deprecated_attribute {
             *  u2 attribute_name_index;
             *  u4 attribute_length;
             * }
             */
            out.putShort(cw.newUTF8("Deprecated")).putInt(0);
        }
        if (ClassReader.SIGNATURES && signature != 0) {
            /**
             * jvms8 4.7.9
             * Signature_attribute {
             *  u2 attribute_name_index;
             *  u4 attribute_length;
             *  u2 signature_index;
             * }
             */
            out.putShort(cw.newUTF8("Signature"));
            out.putInt(2).putShort(signature);
        }
        if (ClassReader.ANNOTATIONS && anns != null) {
            /**
             * jvms8 4.7.16
             * RuntimeVisibleAnnotations_attribute {
             *  u2 attribute_name_index;
             *  u4 attribute_length;
             *  u2 num_annotations;
             *  annotation annotations[num_annotations];
             * }
             */
            out.putShort(cw.newUTF8("RuntimeVisibleAnnotations"));
            anns.put(out);
        }
        if (ClassReader.ANNOTATIONS && ianns != null) {
            /**
             * jvms 4.7.17
             * RuntimeInvisibleAnnotations_attribute {
             *  u2 attribute_name_index;
             *  u4 attribute_length;
             *  u2 num_annotations;
             *  annotation annotations[num_annotations];
             * }
             */
            out.putShort(cw.newUTF8("RuntimeInvisibleAnnotations"));
            ianns.put(out);
        }
        if (ClassReader.ANNOTATIONS && tanns != null) {
            /**
             * jvms8 4.7.20
             * RuntimeVisibleTypeAnnotations_attribute {
             *  u2 attribute_name_index;
             *  u4 attribute_length;
             *  u2 num_annotations;
             *  type_annotation annotations[num_annotations];
             * }
             */
            out.putShort(cw.newUTF8("RuntimeVisibleTypeAnnotations"));
            tanns.put(out);
        }
        if (ClassReader.ANNOTATIONS && itanns != null) {
            /**
             * 4.7.21
             * RuntimeInvisibleTypeAnnotations_attribute {
             *  u2 attribute_name_index;
             *  u4 attribute_length;
             *  u2 num_annotations;
             *  type_annotation annotations[num_annotations];
             * }
             */
            out.putShort(cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
            itanns.put(out);
        }
        if (attrs != null) {
            attrs.put(cw, null, 0, -1, -1, out);
        }
    }
}
