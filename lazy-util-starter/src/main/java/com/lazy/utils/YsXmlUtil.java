package com.lazy.utils;

import com.lazy.exception.BizException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author ：cy
 * @description：xml
 * @date ：2022/1/6 20:33
 */
public class YsXmlUtil {

    /**
     * xml转换成JavaBean
     * @param xml
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T converyToJavaBean(String xml, Class<T> c) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            throw new BizException(String.format("xml转%s失败,error:%s", c.getName(), e.getMessage()));
        }
        return t;
    }

    /**
     * JavaBean转换成xml
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
            throw new BizException(obj + "转xml失败,error:" + e.getMessage());
        }

        return result;
    }
}
