package company;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class main {
    public static void main(String[] args) throws IOException {
        //通过 mvn 命令复制依赖jar
        String path = "jdk-libs";
        path = "D:\\turn1tup\\program_java\\RuoYi-4.7.1_test\\ruoyi-src-jar";
        String resultFile = "ruoyi-public-class_tmp2.txt";

        for (String jarFileName : getFiles(path)) {
            try {
                JarFile jarFile = new JarFile(jarFileName);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (!jarEntry.getName().endsWith(".class")) {
                        continue;
                    }
                    MyClassVistor classVisitor = new MyClassVistor(resultFile);
                   // classVisitor.setResultFile(resultFile);
                    ClassReader classReader = new ClassReader(jarFile.getInputStream(jarEntry));
                    classReader.accept(classVisitor, 0);
                }

            } catch (Exception e) {
                System.out.println("error: "+jarFileName);
            }
        }

    }

    private static Set<String> getFiles(String file){
        Set<String> files = new HashSet<String>();
        File[] fs = new File(file).listFiles();
        for(File f:fs){
            if(f.isDirectory())
                files.addAll(getFiles(f.getPath()));
            if(f.isFile())
                files.add(f.getPath());
        }
        return files;
    }
}
