package io.vantiq.ext;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.itranswarp.compiler.JavaStringCompiler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proto.FaceOther;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ProtoBufTest {

    private static final Logger LOG = LoggerFactory.getLogger(AMQPTest.class);
    String msg = "ClYSDQjjDxAIGAwgEygaMCUaFDQ0MDYwNTAzMDAxMzI2OTY0NjU2Ig4xOTIuMTY4LjEyLjE4MSoOMTkyLjE2OC4xMi4xODE17griQj1Q7bhBRQAAgD9IARgB" +
            "IAEoATD///////////8BOP///////////wFCFA0bL10+FcHK4T4dzczMPSVYOTQ+WktodHRwOi8vMTkyLjE2OC4xMi42MDo4MC9ncm91cDIvTTAwLzAwLzY1" +
            "L3dLZ01QbDFSVFAyQUNEb1FBQWFhWEdKX3pCbzc3Ni5qcGdaS2h0dHA6Ly8xOTIuMTY4LjEyLjYwOjgwL2dyb3VwMi9NMDAvMDAvNjUvd0tnTVBsMVJUUDJB" +
            "SUJmMUFBQWotUFk2QzhNNDUyLmpwZw==";

//    @Test
//    public void test1() throws ClassNotFoundException, IOException {
//        ObjectMapper mapper = new ProtobufMapper();
//        JavaStringCompiler compiler = new JavaStringCompiler();
//
//        String pathJava = StringCompilation.class.getClassLoader().getResource("Face.java").getPath();
//        String code = new String(Files.readAllBytes(Paths.get(pathJava)));
//
//        Map<String, byte[]> results = compiler.compile("Face.java", code);
//        Class<?> clazz = compiler.loadClass("Face$FACE_DETECT_MESSAGE", results);
////        Class clazz = Class.forName("Face$FACE_DETECT_MESSAGE");
//
//        String path = StringCompilation.class.getClassLoader().getResource("face.proto").getPath();
//        String proto = new String(Files.readAllBytes(Paths.get(path)));
//        ProtobufSchema schema = ProtobufSchemaLoader.std.parse(proto);
//
//
//        String path2 = StringCompilation.class.getClassLoader().getResource("face.txt").getPath();
//        byte[] protoBytes = Files.readAllBytes(Paths.get(path2));
//
//        Object obj = mapper.readerFor(clazz).with(schema).readValue(protoBytes);
//        LOG.debug("result: {}", obj);
//    }

    @Test
    public void test1() throws ClassNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JavaStringCompiler compiler = new JavaStringCompiler();

        String pathJava = this.getClass().getClassLoader().getResource("Face.java").getPath();
        String code = new String(Files.readAllBytes(Paths.get(pathJava)));

        Map<String, byte[]> results = compiler.compile("Face.java", code);
        Class<?> clazz = compiler.loadClass("Face$FACE_DETECT_MESSAGE", results);
//        Class clazz = Class.forName("Face$FACE_DETECT_MESSAGE");

        String path = this.getClass().getClassLoader().getResource("face.proto").getPath();
        String proto = new String(Files.readAllBytes(Paths.get(path)));


        JsonFormat jsonFormat = new JsonFormat();

        String path2 = this.getClass().getClassLoader().getResource("face.txt").getPath();
        byte[] protoBytes = Files.readAllBytes(Paths.get(path2));
        Method method = clazz.getMethod("parseFrom", byte[].class);
        Object objData = method.invoke(clazz, protoBytes);
        String asJson = jsonFormat.printToString((Message) objData);
        LOG.debug("result json string: {}", asJson);

        Method methodBuilder = clazz.getMethod("newBuilder", null);
        Object builder = methodBuilder.invoke(clazz, null);
        jsonFormat.merge(new ByteArrayInputStream(asJson.getBytes()), (Message.Builder) builder);
        Object objBack = ((Message.Builder) builder).build();

        LOG.debug("result: {}", objBack);
    }

//    @Test
//    public void test2() throws ClassNotFoundException, IOException {
//        ObjectMapper mapper = new ProtobufMapper();
//
////        Class clazz = Class.forName("Face$FACE_DETECT_MESSAGE");
//
//        String protobuf_str = "message Employee {\n"
//                +" required string name = 1;\n"
//                +" required int32 age = 2;\n"
//                +" repeated string emails = 3;\n"
//                +" optional Employee boss = 4;\n"
//                +"}\n";
//        ProtobufSchema schema = ProtobufSchemaLoader.std.parse(protobuf_str);
//
//        Employee emp = new Employee();
//        emp.age = 18;
//        emp.boss = null;
//        emp.emails = new String[] {"ok@t.cn"};
//        emp.name = "mav";
//        byte[] result = mapper.writer(schema).writeValueAsBytes(emp);
//        LOG.debug("protobuf data: {}", result);
//
//        Object obj = mapper.readerFor(Employee.class).with(schema).readValue(result);
//        LOG.debug("result object: {}", obj);
//    }

    @Test
    public void test3() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class clazz = Class.forName("proto.FaceOther$FACE_DETECT_MESSAGE");
        Method method = clazz.getMethod("parseFrom", byte[].class);
        Object obj = method.invoke(clazz, msg.getBytes());
        LOG.debug("result object: {}", obj);
    }

    @Test
    public void test4() throws InvalidProtocolBufferException {
        FaceOther.FACE_DETECT_MESSAGE.Builder builder = FaceOther.FACE_DETECT_MESSAGE.newBuilder();
        FaceOther.FACE_DETECT_MESSAGE msg = builder.setAge(10).setColor(FaceOther.E_RACE_TYPE.RACE_TYPE_UIGUER).setBeard(FaceOther.E_BOOL.BOOL_NO).setGlass(FaceOther.E_BOOL.BOOL_NO)
                                                   .setMask(FaceOther.E_BOOL.BOOL_NO).setSex(FaceOther.E_SEX_TYPE.SEX_TYPE_MAN).setSimilarity(0.88f).setSourceUrl("uuu")
                                                   .build();
        byte[] result = msg.toByteArray();
        LOG.debug("result bytes: {}", new String(result));

        Object obj = FaceOther.FACE_DETECT_MESSAGE.parseFrom(result);
        LOG.debug("result object: {}", obj);
    }
}
