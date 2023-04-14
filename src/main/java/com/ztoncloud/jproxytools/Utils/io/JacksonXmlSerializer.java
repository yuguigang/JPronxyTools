package com.ztoncloud.jproxytools.Utils.io;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ztoncloud.jproxytools.exception.AppException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class JacksonXmlSerializer<T>  {

    protected final XmlMapper mapper;
    protected final TypeReference<T> typeRef;

    public JacksonXmlSerializer(XmlMapper mapper, TypeReference<T> typeRef) {
        this.mapper = Objects.requireNonNull(mapper, "mapper");
        this.typeRef = Objects.requireNonNull(typeRef, "typeRef");
    }


    /**
     * 序列化
     *
     * @param outputStream 输出流
     * @param value        价值
     */
    public void serialize(OutputStream outputStream, T value) {
        try {
            mapper.writeValue(outputStream, value);
        } catch (IOException e) {
            throw new AppException("OutputStream写出时出错！");
        }
    }


    /**
     * 反序列化
     *
     * @param inputStream 输入流
     * @return {@link T}
     */
    public T deserialize(InputStream inputStream) {
        try {
            return mapper.readValue(inputStream, typeRef);
        } catch (IOException e) {
            throw new AppException("InputStream读取时出错");
        }
    }
}
