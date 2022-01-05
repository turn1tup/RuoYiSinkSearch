package company;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class MyClassVistor extends ClassVisitor {
    private String resultFile ;

    public void Record(String data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(this.resultFile),true);
            fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public MyClassVistor(String file) {
        super(ASM6);
        this.resultFile = file;
    }



    public static Set<String> classNames = new HashSet<String>();

    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {

        className = name;

        int[] invalidAccesses = new int[]{
                ACC_ABSTRACT,
                ACC_PROTECTED,
                ACC_PRIVATE,
                ACC_INTERFACE
        };
        // 不能是抽象类

        for (int invalidAccess : invalidAccesses) {
            if (access>= invalidAccess && (access ^ invalidAccess) < access) {
                goodClass = false;
                break;
            }
        }
    }

    /**
     没有构造函数
     有构造函数，且存在public 无参的构造函数
     */
    boolean hasConstruct = false;
    boolean goodConstruct = false;
    boolean goodMethod = false;
    boolean goodClass = true;
    String className = "";
    //List<String> methodNames = new ArrayList<String>();
    Set<String> methodNames = new HashSet<>();
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//        if ( names.contains(name)) {
//            System.out.println("public"+name);
//        }
        String[] blackMethods = new String[]{
                "is",
                "set",
                "newUTF8",
                "<init>",
                "remove",
                "visit",
                "put",
                "newNameType",
                "newClass",
                "newField",
                "newPackage",
                "reserveName",
                "StringTo",
                "add",
                "getByteArray",
                "convert",
                "newHandle",
                "getProperty",
                "has",
                "getChar",
                "newModule",
        };
        if (name.equals("<init>")) {
            hasConstruct = true;
            if (access == ACC_PUBLIC) {
                if (descriptor.startsWith("()")) {
                    goodConstruct = true;
                }
            }
        }
//        else if(
//                name.startsWith("visit")
//                || name.startsWith("put")
//        ){
//
//        }
        else
        {
            boolean isBlack = false;
            for (String blackMethod : blackMethods) {
                if (name.startsWith(blackMethod)) {
                    isBlack = true;
                    break;
                }
            }
            //boolean Z  int I double D float F
            if (access == ACC_PUBLIC && !isBlack) {

                String params = descriptor.split("\\)")[0];
                //只关心有字符串输入的
                if (params.contains("Ljava/lang/String;")) {
                    String[] whites = new String[]{
                            "Ljava/lang/String;",
                            "F",
                            "I",
                            "Z",
                            "D",
                    };
                    for (String white : whites) {
                        params = params.replace(white, "");
                    }
                    if (params.equals("(")) {
                        goodMethod = true;
                        methodNames.add(name+" "+descriptor);
                    }
                }

            }
        }
        if (cv == null) {
            return null;
        }
        return cv.visitMethod(access,name,descriptor,signature,exceptions);
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
    }

    @Override
    public void visitEnd() {
        if (goodConstruct || !hasConstruct) {
            if (goodClass && goodMethod) {
                if (!className.contains("$")) {
                    for (String methodName : methodNames) {
                        Record(String.format("%30s method: %s",className,methodName));
                        classNames.add(className);
                    }
                }

            }
        }
        super.visitEnd();
    }
}
