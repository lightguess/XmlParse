package com.lg.xmlparse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lg.xmlparse.bean.Person;
import com.lg.xmlparse.utils.DomHelper;
import com.lg.xmlparse.utils.PullHelper;
import com.lg.xmlparse.utils.ReflectUtil;
import com.lg.xmlparse.utils.SaxHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    ListView lv_xmllist;

    private ArrayAdapter<Person> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_xmllist =findViewById(R.id.lv_xmllist);
    }

    public void onClickXmlParse(View view) {
        try {
            InputStream inputStream = getAssets().open("pull_person.xml");

            ArrayList<Person> personList= PullHelper.xmlToObject(inputStream, Person.class,"person");
            updataList(personList);

           // String getAttribute = PullHelper.getTagAttribute(inputStream,"person","id");

//            Context context = getApplicationContext();
//            ArrayList<Person> newPersonList = new ArrayList<Person>();
//            newPersonList.add(new Person(21,"小明5",70));
//            newPersonList.add(new Person(31,"小红6",50));
//            newPersonList.add(new Person(11,"小花7",30));
//            File xmlFile = new File(context.getFilesDir(),"lg.xml");
//            FileOutputStream fos;
//             fos = new FileOutputStream(xmlFile);
//            PullHelper.save(newPersonList,fos,"id","persons");
//
//            Log.e(TAG, "onClickXmlParse:  save finished path = "+xmlFile.getAbsolutePath() );


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onClickSaxParse(View view) {
        try{
            //获取文件资源建立输入流对象
            InputStream is = getAssets().open("sax_person.xml");
            //①创建XML解析处理器
            SaxHelper ss = new SaxHelper(Person.class);
            //②得到SAX解析工厂
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //③创建SAX解析器
            SAXParser parser = factory.newSAXParser();
            //④将xml解析处理器分配给解析器,对文档进行解析,将事件发送给处理器
            parser.parse(is, ss);
            is.close();
            ArrayList<Person> personList= ss.getXmlArrayList();
            Log.e(TAG, "onClickXmlParse:  size = "+personList.size() );
            updataList(personList);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onClickDomParse(View view) {
        try{
            InputStream is = getAssets().open("dom_person.xml");
            ArrayList<Person> personList= DomHelper.queryXML(this,is,Person.class);
            Log.e(TAG, "onClickXmlParse:  size = "+personList.size() );
            updataList(personList);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void updataList(ArrayList<Person> personList){
        mAdapter = new ArrayAdapter<Person>(MainActivity.this,
                android.R.layout.simple_expandable_list_item_1, personList);
        lv_xmllist.setAdapter(mAdapter);
    }
}