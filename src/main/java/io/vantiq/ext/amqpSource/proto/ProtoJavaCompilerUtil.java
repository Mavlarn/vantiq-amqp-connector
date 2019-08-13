package io.vantiq.ext.amqpSource.proto;

import com.itranswarp.compiler.JavaStringCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ProtoJavaCompilerUtil {

    private static JavaStringCompiler compiler = new JavaStringCompiler();

    private static final Logger LOG = LoggerFactory.getLogger(ProtoJavaCompilerUtil.class);

    public static Class compile(String protoName, String className) {

        String javaFileName = protoName + ".java";
        String path = ProtoJavaCompilerUtil.class.getClassLoader().getResource(javaFileName).getPath();

        try {
            String code = new String(Files.readAllBytes(Paths.get(path)));

            Map<String, byte[]> results = compiler.compile(javaFileName, code);
            Class<?> clazz = compiler.loadClass(className, results);
            LOG.debug("Compiled class:{}", clazz);
            return clazz;

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            LOG.error("invalid class name:" + className, e);
        }
        return null;
    }
}
