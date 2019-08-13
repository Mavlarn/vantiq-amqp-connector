package io.vantiq.ext.amqpSource.proto;

import com.itranswarp.compiler.JavaStringCompiler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ProtoJavaCompilerUtil {

    private static JavaStringCompiler compiler = new JavaStringCompiler();

    private static final Logger LOG = LoggerFactory.getLogger(ProtoJavaCompilerUtil.class);

    public static Class compile(String protoName, String className, String homeDir) {

        String javaFileName = protoName + ".java";
        try {
            InputStream in = ProtoJavaCompilerUtil.class.getClassLoader().getResourceAsStream(javaFileName);
            String code;
            if (in != null) {
                code = IOUtils.toString(in, "utf-8");
            } else {
                code = IOUtils.toString(new FileReader(homeDir + File.separator + javaFileName));
            }
            LOG.trace("Compile java code string:{}", code);

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
