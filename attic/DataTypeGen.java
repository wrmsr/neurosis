/*
Vertical / Horizontal
Factory Interface
Immut / Mut
Setters return this, new value, old value
Annotation propagation
Mixins
*/

package morgoth.spike;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import jdk.nashorn.internal.ir.ReturnNode;
import morgoth.common.HexUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;


public class DataTypeGen {

    public static class ClassPathHacker {

        private static final Class[] parameters = new Class[]{URL.class};

        public static void addFile(String s) throws IOException {
            File f = new File(s);
            addFile(f);
        }

        public static void addFile(File f) throws IOException {
            addURL(f.toURL());
        }

        public static void addURL(URL u) throws IOException {

            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class sysclass = URLClassLoader.class;

            try {
                Method method = sysclass.getDeclaredMethod("addURL", parameters);
                method.setAccessible(true);
                method.invoke(sysloader, new Object[]{u});
            } catch (Throwable t) {
                t.printStackTrace();
                throw new IOException("Error, could not add URL to system classloader");
            }
        }
    }

    public interface Point {

        public float x();
        public float x(float x);

        public float y();
        public Point y(float y);
    }

    public class SimplePointImpl implements Point {

        private float x;
        private float y;

        public SimplePointImpl(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public float x() {
            return x;
        }

        @Override
        public float x(float x) {
            return this.x = x;
        }

        @Override
        public float y() {
            return y;
        }

        @Override
        public Point y(float y) {
            this.y = y;
            return this;
        }
    }

    public static void main(String[] args) throws Exception {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(7,
                ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                "asm1/Notifier",    // class name
                "java/lang/Object", // super class
                null,               // interfaces
                null);   // source file

        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT,
                "notify",                // method name
                "(Ljava/lang/String;)V", // method descriptor
                null,                    // exceptions
                null);                   // method attributes

        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT,
                "addListener",           // method name
                "(Lasm1/Listener;)V",    // method descriptor
                null,                    // exceptions
                null);                   // method attributes

        cw.visitEnd();

        byte[] bytecode = cw.toByteArray();
        System.out.println(HexUtils.hexdump(bytecode));

        ClassNode cn = new ClassNode();
        cn.version = V1_7;
        cn.access = ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE;
        cn.name = "pkg/Comparable";
        cn.superName = "java/lang/Object";
        // cn.interfaces.add("pkg/Mesurable");
        cn.fields.add(new FieldNode(
                ACC_PUBLIC + ACC_FINAL + ACC_STATIC,
                "LESS", "I", null, new Integer(-1)));
        cn.fields.add(new FieldNode(
                ACC_PUBLIC + ACC_FINAL + ACC_STATIC,
                "EQUAL", "I", null, new Integer(0)));
        cn.fields.add(new FieldNode(
                ACC_PUBLIC + ACC_FINAL + ACC_STATIC,
                "GREATER", "I", null, new Integer(1)));
        cn.methods.add(new MethodNode(
                ACC_PUBLIC + ACC_ABSTRACT,
                "compareTo", "(Ljava/lang/Object;)I", null, null));

        cw = new ClassWriter(0);
        cn.accept(cw);
        bytecode = cw.toByteArray();
        System.out.println(HexUtils.hexdump(bytecode));

        cn = new ClassNode();
        cn.version = V1_6;
        cn.access = ACC_PUBLIC;
        cn.name = "pkg/SimplePointImpl";
        cn.superName = "java/lang/Object";
        // cn.interfaces = ImmutableList.of("morgoth/spike/DataTypeGen$Point");

        cn.fields.add(new FieldNode(
                ACC_PUBLIC,
                "x", "F", "F", new Float(0)));

        MethodNode mn = new MethodNode(ACC_PUBLIC, "x", "()F", null, null);
        cn.methods.add(mn);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new FieldInsnNode(GETFIELD, "pkg/SimplePointImpl", "x", "F"));
        il.add(new InsnNode(FRETURN));
        mn.maxStack = 1;
        mn.maxLocals = 1;

        mn = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
        cn.methods.add(mn);
        il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
        il.add(new VarInsnNode(ALOAD, 0));

        /*
        LabelNode fzs = new LabelNode();
        il.add(fzs);
        LocalVariableNode fz = new LocalVariableNode("fz", "F", "F", fzs, null, 2);
        mn.localVariables.add(fz);
        il.add(new VarInsnNode(FLOAD, 2));
        LabelNode fze = new LabelNode();
        il.add(fze);
        fz.end = fze;
        */


        il.add(new FieldInsnNode(PUTFIELD, "pkg/SimplePointImpl", "x", "F"));
        il.add(new InsnNode(RETURN));
        mn.maxStack = 2;
        mn.maxLocals = 3;


        cw = new ClassWriter(0);
        cn.accept(cw);
        bytecode = cw.toByteArray();
        System.out.println(HexUtils.hexdump(bytecode));

        File tempDir = Files.createTempDir();
        // tempDir.deleteOnExit();
        System.out.println(tempDir);
        File pkg = new File(tempDir, "pkg");
        pkg.mkdir();
        File cls = new File(pkg, "SimplePointImpl.class");
        ClassPathHacker.addFile(tempDir.getPath());

        try (FileOutputStream fos = new FileOutputStream(cls)) {
            fos.write(bytecode);
        }

        Class pi = Class.forName("pkg.SimplePointImpl");
        System.out.println(pi);
        Object p = pi.newInstance();
        System.out.println(p.getClass().getDeclaredMethod("x").invoke(p));
    }
}
