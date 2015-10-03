package org.powlab.jeye;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TestMaker {

    private static final String ACCEPT = "Usage";
    private static final String PACKAGE = "usages";
    private static final String TEST_FORMAT = "  class %sTest extends DecompileTestClass(classOf[%s]) {}\n";

    public static void main(String[] args) throws IOException {
        String path = args[0];
        File tests = new File(path);
        File packageFile = new File(tests, PACKAGE);
        packageFile.mkdirs();
        File[] files = tests.listFiles();

        StringBuilder report = new StringBuilder();
        report.append("package org.powlab.jeye.tests").append("\n").append("\n");
        report.append("import org.powlab.jeye.tests.").append(PACKAGE).append("._").append("\n");
        report.append("import org.powlab.jeye.decompile._").append("\n").append("\n");
        report.append("package ").append(PACKAGE).append(" {").append("\n");
        for (File file : files) {
            String name = file.getName();
            if (name.endsWith(".java") && name.startsWith(ACCEPT)) {
                String classname = name.substring(0, name.length() - ".java".length());
                report.append("\n").append("  @org.junit.Ignore").append("\n");
                report.append(String.format(TEST_FORMAT, classname, classname));
                File newTest = new File(packageFile, name);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Files.copy(file.toPath(), baos);
                String testContent = baos.toString();
                String newTestContent = testContent.replace("package org.powlab.jeye.tests;", "package org.powlab.jeye.tests." + PACKAGE + ";");
                Files.copy(new ByteArrayInputStream(newTestContent.getBytes()), newTest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                file.delete();
            }
        }
        report.append("}");
        System.out.println(report);

        String outPath = args[1];
        File outFile = new File (outPath, ("" + PACKAGE.charAt(0)).toUpperCase() + PACKAGE.substring(1) + "Tests.scala");
        Files.copy(new ByteArrayInputStream(report.toString().getBytes()), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println(outFile);
    }

}
