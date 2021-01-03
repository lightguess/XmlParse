# XmlParse
Xml 解析

> 使用了泛型进行优化扩展，使用时候传入对应的xml的Java类进行转换成对应的对象列表，兼容所有的xml格式文本。

### 1.XmlPullParser

- 优点：读取方便、内存损耗小

- 缺点：需要知道节点名称（使用泛型优化）

**原理：**

> 基于事件驱动，根据XmlPullParser返回不同的标签进行获取xml数据。

---

### 2.Sax
​
优点：内存占用少。
​
缺点：无法修改xml内容，每次都要按顺序读取。
​
>原理：基于事件驱动，扫描文件的开始和结束，继承DefaultHandler类在对应的方法进行处理
​
---

### 3.Dom

优点：检索修改效率高

缺点：损耗内存大，每次都会把xml全部扫描

>原理：文档驱动，整个文档树保存在内存中，以便进行操作。

---
### 4.使用

```
//pull使用
ArrayList<Person> personList= PullHelper.xmlToObject(inputStream, Person.class,"person");

//sax使用
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

//dom使用
InputStream is = getAssets().open("dom_person.xml");
ArrayList<Person> personList= DomHelper.queryXML(this,is,Person.class);
```
