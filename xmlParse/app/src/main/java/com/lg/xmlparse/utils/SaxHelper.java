package com.lg.xmlparse.utils;

import android.util.Log;

import com.lg.xmlparse.contants.Contants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;

//DefaultHandler
//id 为Attributes

public class SaxHelper<T> extends DefaultHandler {
    String TAG = SaxHelper.class.getSimpleName();

    public   T t;
    private ArrayList<T> tArrayList;
    private String tagName= null;
    Class<T> clazz;
    Field[] fields;
   public SaxHelper(Class<T> mClazz){
       clazz=mClazz;
      fields = clazz.getDeclaredFields();

    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this.tArrayList = new ArrayList<T>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        Log.e(TAG, "startElement : localName "+localName );
        Log.e(TAG, "startElement : (clazz.getSimpleName() "+clazz.getSimpleName().toLowerCase()) ;

        if(localName.equals(clazz.getSimpleName().toLowerCase())){
            Log.e(TAG, "startElement: newInstance" );
                try {
                    this.t = clazz.newInstance();
                     fields = clazz.getDeclaredFields();

                    for (Field field : fields) {
                        if(field.getName().equals(Contants.attributeName)){
                            ReflectUtil.setFieldValue(this.t, field, Contants.attributeName, attributes.getValue(Contants.attributeName));

                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        this.tagName = localName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        try {
            if(this.tagName != null) {
                String data = new String(ch,start, length);
                Log.e(TAG, "characters:  data = "+ data );
                Log.e(TAG, "characters:  tagname = "+ this.tagName );

               // fields = clazz.getDeclaredFields();
                if(data!=null){
                    for (Field field : fields) {
                        Log.e(TAG, "characters:  field.getName() = "+ field.getName());

                        if(this.tagName.equals(field.getName())){
                            ReflectUtil.setFieldValue(this.t, field, field.getName(), data);

                        }
                    }
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if(localName.equals(clazz.getSimpleName().toLowerCase())){
            this.tArrayList.add(t);
            t=null;
        }
        this.tagName = null;

    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        Log.i("SAX", "endDocument");

    }

    //获取persons集合
    public ArrayList<T> getXmlArrayList() {
        return this.tArrayList;
    }

}
