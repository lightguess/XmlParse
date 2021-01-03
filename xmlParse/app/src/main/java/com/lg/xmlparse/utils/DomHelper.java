package com.lg.xmlparse.utils;

import android.content.Context;
import android.util.Log;

import com.lg.xmlparse.contants.Contants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DomHelper {
   static String TAG = DomHelper.class.getSimpleName();
    public static <T>ArrayList<T> queryXML(Context context, InputStream is, Class<T> clazz){
        ArrayList<T> tArrayList = new ArrayList<T>();

        try{
            DocumentBuilderFactory dbFactory=  DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            Document doc = dbBuilder.parse(is);
            NodeList nodeList = doc.getElementsByTagName(clazz.getSimpleName().toLowerCase());
            for(int i = 0;i < nodeList.getLength();i++){
                Element mElement =(Element)nodeList.item(i);
                T t = clazz.newInstance();
                //set attribute
                Field field = clazz.getDeclaredField(Contants.attributeName);
                Log.e(TAG, "queryXML: attributevalue = "+mElement.getAttribute(Contants.attributeName) );
                ReflectUtil.setFieldValue(t, field, Contants.attributeName, mElement.getAttribute(Contants.attributeName));

                //set childNoList
                NodeList childNoList = mElement.getChildNodes();
                for(int j = 0;j<childNoList.getLength();j++){
                    Node childNode = childNoList.item(j);
                    if(childNode.getNodeType() == Node.ELEMENT_NODE){
                        Element childElement = (Element)childNode;
                        Field[] childField = clazz.getDeclaredFields();
                        for (Field mfield:childField){
                            Log.e(TAG, "mfield getName  = "+mfield.getName() );
                            Log.e(TAG, "mfield value = "+childElement.getFirstChild().getNodeValue() );

                            if(childElement.getNodeName().equals(mfield.getName())){
                                ReflectUtil.setFieldValue(t, mfield, mfield.getName(), childElement.getFirstChild().getNodeValue());

                            }
                        }

                    }
                }
                tArrayList.add(t);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return tArrayList;
    }


}
