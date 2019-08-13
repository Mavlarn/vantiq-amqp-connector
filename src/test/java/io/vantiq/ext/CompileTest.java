package io.vantiq.ext;

import com.itranswarp.compiler.JavaStringCompiler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.FaceOther;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


public class CompileTest {

    private static final Logger LOG = LoggerFactory.getLogger(AMQPTest.class);

    String msg = "ClYSDQjjDxAIGAwgEygaMCUaFDQ0MDYwNTAzMDAxMzI2OTY0NjU2Ig4xOTIuMTY4LjEyLjE4MSoOMTkyLjE2OC4xMi4xODE17griQj1Q7bhBRQAAgD9IARgB" +
            "IAEoATD///////////8BOP///////////wFCFA0bL10+FcHK4T4dzczMPSVYOTQ+WktodHRwOi8vMTkyLjE2OC4xMi42MDo4MC9ncm91cDIvTTAwLzAwLzY1" +
            "L3dLZ01QbDFSVFAyQUNEb1FBQWFhWEdKX3pCbzc3Ni5qcGdaS2h0dHA6Ly8xOTIuMTY4LjEyLjYwOjgwL2dyb3VwMi9NMDAvMDAvNjUvd0tnTVBsMVJUUDJB" +
            "SUJmMUFBQWotUFk2QzhNNDUyLmpwZw==";

    @Test
    public void test1() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JavaStringCompiler compiler = new JavaStringCompiler();

        String path = this.getClass().getClassLoader().getResource("Face.java_str").getPath();
        String code = new String(Files.readAllBytes(Paths.get(path)));

        Map<String, byte[]> results = compiler.compile("Face.java", code);
        Class<?> clazz = compiler.loadClass("Face$FACE_DETECT_MESSAGE", results);
        LOG.debug("Compiled class:{}", clazz);


        FaceOther.FACE_DETECT_MESSAGE.Builder builder = FaceOther.FACE_DETECT_MESSAGE.newBuilder();
        FaceOther.FACE_DETECT_MESSAGE msg = builder.setAge(10).setColor(FaceOther.E_RACE_TYPE.RACE_TYPE_UIGUER).setBeard(FaceOther.E_BOOL.BOOL_NO).setGlass(FaceOther.E_BOOL.BOOL_NO)
                                                   .setMask(FaceOther.E_BOOL.BOOL_NO).setSex(FaceOther.E_SEX_TYPE.SEX_TYPE_MAN).setSimilarity(0.88f).setSourceUrl("uuu")
                                                   .build();
        byte[] result = msg.toByteArray();
        LOG.debug("result bytes: {}", new String(result));


        Method method = clazz.getMethod("parseFrom", byte[].class);
        LOG.debug("parseFrom method:{}", method);
        Object res = method.invoke(clazz, result);

        LOG.debug("newBuilder result:{}", res);
    }

    @Test
    public void testWithFile() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JavaStringCompiler compiler = new JavaStringCompiler();

        String path = this.getClass().getClassLoader().getResource("Face.java").getPath();
        String code = new String(Files.readAllBytes(Paths.get(path)));

        Map<String, byte[]> results = compiler.compile("Face.java", code);
        Class<?> clazz = compiler.loadClass("Face$FACE_DETECT_MESSAGE", results);
        LOG.debug("Compiled class:{}", clazz);


        String path2 = this.getClass().getClassLoader().getResource("face.txt").getPath();
        byte[] protoBytes = Files.readAllBytes(Paths.get(path2));
        Method method = clazz.getMethod("parseFrom", byte[].class);
        Object res = method.invoke(clazz, protoBytes);
        LOG.debug("Face$FACE_DETECT_MESSAGE result:{}", res);
    }

    @Test
    public void test2() throws ClassNotFoundException {
        Class faceClazz = Class.forName("proto.FaceOther");
        faceClazz.getMethods();

    }


    @Test
    public void test3() throws ClassNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class clazz = Class.forName("proto.FaceOther$FACE_DETECT_MESSAGE");
        Method method = clazz.getMethod("parseFrom", byte[].class);
        Object obj = method.invoke(clazz, msg.getBytes());
        LOG.debug("result object: {}", obj);


    }
}
