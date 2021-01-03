package com.lg.xmlparse.utils;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PullHelper {

    static String TAG = PullHelper.class.getSimpleName();

    public PullHelper(){

    }




    public static <T> ArrayList<T> xmlToObject(InputStream xml , Class<T> clazz , String tagEntity){
        ArrayList<T> list = null;
        try{
        XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(xml,"UTF-8");
            Field[] fields = clazz.getDeclaredFields();
            int type = xmlPullParser.getEventType();
            String lastTag = "";
            T t = null;
            while (type!= XmlPullParser.END_DOCUMENT) {


                //3. add list
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        String START_DOCUMENTtagName = xmlPullParser.getName();
                        Log.e(TAG, "xmlToObject START_DOCUMENT: tagName =" +START_DOCUMENTtagName);

                        list = new ArrayList<T>();
                        break;

                    case XmlPullParser.START_TAG:

                        String tagName = xmlPullParser.getName();
                        Log.e(TAG, "xmlToObject START_TAG: tagName =" +tagName);


                        if (tagEntity.equals(tagName)) {
                            t = clazz.newInstance();
                            lastTag = tagEntity;

                            Log.e(TAG, "xmlToObject START_TAG:    xmlPullParser.getAttributeValue() =" +   xmlPullParser.getAttributeValue(0));

                        } else if (tagEntity.equals(lastTag)) {
                            String value = xmlPullParser.nextText();
                            String filedName = xmlPullParser.getName();
                            Log.e(TAG, "xmlToObject: filedName =" +filedName+" value = "+value);

                            for (Field field : fields) {
                                ReflectUtil.setFieldValue(t, field, filedName, value);
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = xmlPullParser.getName();
                        Log.e(TAG, "xmlToObject END_TAG: tagName =" +tagName);

                        if (tagEntity.equals(tagName)) {
                            list.add(t);
                            lastTag = "";
                        }
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        String tagName1 = xmlPullParser.getName();
                        Log.e(TAG, "xmlToObject END_DOCUMENT: tagName =" +tagName1);
                        break;

                }
                type = xmlPullParser.next();
            }
        }catch (Exception e){
                e.printStackTrace();
        }
            //4.end return list
        return list;
    }


        public static<T> ArrayList<T> attributeToObject(InputStream inputStream, Class<T> clazz, String tagName){
            if(TextUtils.isEmpty(tagName))return null;
            ArrayList<T> list = null;
            XmlPullParser xmlPullParser = Xml.newPullParser();
            try {
                xmlPullParser.setInput(inputStream, "utf-8");
                int type = xmlPullParser.getEventType();
                T t = null;
                while(type != XmlPullParser.END_DOCUMENT){
                    switch(type){
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<T>();
                            break;
                        case XmlPullParser.START_TAG:
                            if(tagName.equals(xmlPullParser.getName())){
                                Log.e(TAG, "attributeToObject: tagName = "+tagName );
                                t = clazz.newInstance();
                                Field[] fields = clazz.getDeclaredFields();
                                for(Field field : fields){
                                    String fieldName = field.getName();
                                    for(int index = 0;index < xmlPullParser.getAttributeCount();index++){
                                        Log.e(TAG, "attributeToObject: fileName = "+fieldName );
                                        Log.e(TAG, "attributeToObject: AttributeName = "+xmlPullParser.getAttributeName(index) );
                                        if(fieldName.equals(xmlPullParser.getAttributeName(index))){
                                            Log.e(TAG, "attributeToObject: AttributeValue "+xmlPullParser.getAttributeValue(index) );
                                            ReflectUtil.setFieldValue(t,field,fieldName,xmlPullParser.getAttributeValue(index));
                                        }
                                    }
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if(tagName.equals(xmlPullParser.getName())){
                                list.add(t);
                            }
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                    type = xmlPullParser.next();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return list;

        }

        /**
         * 获取Xml文件中的属性值
         * @param xml	xml文件字符串
         * @param tagName		标签名称
         * @param attributeName		属性名称
         * @return	返回获取的值，或者null
         */
        public static String getTagAttribute(InputStream inputStream, String tagName, String attributeName){
            if(TextUtils.isEmpty(tagName) || TextUtils.isEmpty(attributeName)){
                throw new IllegalArgumentException("请填写标签名称或属性名称");
            }
            XmlPullParser xmlPullParser = Xml.newPullParser();
            try {
                xmlPullParser.setInput(inputStream, "utf-8");
                int type = xmlPullParser.getEventType();
                while(type != XmlPullParser.END_DOCUMENT){
                    switch(type){
                        case XmlPullParser.START_TAG:
                            if(tagName.equals(xmlPullParser.getName())){
                                for(int i=0; i < xmlPullParser.getAttributeCount();i++){
                                    if(attributeName.equals(xmlPullParser.getAttributeName(i))){
                                        return xmlPullParser.getAttributeValue(i);
                                    }
                                }
                            }
                            break;
                    }
                    type = xmlPullParser.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    public static <T> void save(ArrayList<T> tList, OutputStream out,String attribute,String tagname) throws Exception {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(out, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag(null, tagname);

        for (T t : tList) {
            Log.e(TAG, "save: tag name = "+t.getClass().getSimpleName().toLowerCase() );
            serializer.startTag(null, t.getClass().getSimpleName().toLowerCase());
            Field[] fields = t.getClass().getDeclaredFields();

            //set attribute
            if(attribute!=null&&!attribute.equals("")){
                Object value = ReflectUtil.getFieldValue(t,attribute);
                serializer.attribute(null ,attribute, value+"");

            }

            //tag text
            for(Field field : fields){
                String fieldName = field.getName();

                Object value = ReflectUtil.getFieldValue(t,fieldName);

                Log.e(TAG, "save: fieldName = "+fieldName );
                Log.e(TAG, "save: test = "+value );
                if(!attribute.equals(fieldName)) {
                    serializer.startTag(null,fieldName);
                    serializer.text(String.valueOf(value));
                    serializer.endTag(null,fieldName);

                }

            }

            serializer.endTag(null,t.getClass().getSimpleName().toLowerCase());
        }

        serializer.endTag(null,tagname);
        serializer.endDocument();
        out.flush();
        out.close();
    }

}
