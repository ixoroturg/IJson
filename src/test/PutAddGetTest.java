package test;

import ixoroturg.json.*;

public class PutAddGetTest{
  public static void test(){
    Json js = null;
    System.out.println("\nPut test:\n");

    IJsonSetting.KEY_DELIMETER = '.';
    IJsonSetting.PARENT_CHARACTER = 'p';
    IJsonSetting.USE_ARRAY_SYNTAX = true;
    try{
      js = IJson.ofObject()
        .put("byte",(byte)120)
        .put("short",(short)30300)
        .put("int",3242321)
        .put("long",43243243232141l)
        .put("float", 23.49f)
        .put("double",432.3334522)
        .put("boolean_true", true)
        .put("boolean_false",false)
        .put("some_string","just a simple string")
        .put("some_null_string", (String)null)
        .put("some_null_json",(Json)null)
          .putGoArray("array_test")
          .add((byte)77)
          .add((short)22333)
          .add(321432)
          .add(32121321l)
          .add(43.25f)
          .add(3124.574321)
          .add(true)
          .add(false)
          .add((Json)null)
          .add((String)null)
          .add("some string in array")
        .back();
      System.out.println(js.toStringFormat());

      byte b = js.getByte("byte");
      short s = js.getShort("short");
      int I = js.getInt("int");
      long l = js.getLong("long");
      float f = js.getFloat("float");
      double d = js.getDouble("double");
      String str = js.getString("some_string");
      Json json = js.get("array_test");
      boolean act = js.getBoolean("boolean_true");

      System.out.println("\ngetType(): byte,short,int,long,float,double,string,json,booleans");
      System.out.println(b);
      System.out.println(s);
      System.out.println(I);
      System.out.println(l);
      System.out.println(f);
      System.out.println(d);
      System.out.println(str);
      System.out.println(json.toStringFormat());
      System.out.println(act);
      System.out.println(js.getBoolean("boolean_false"));


      System.out.println("\nДобавление через длинные ключи");
      String base = "test_long_key";
      js
        .putObject(base)
        .put(base + ".some_byte",(byte)7)
        .put(base + ".some_short", (short)2313)
        .putObject(base+".some_object")
        .put(base + ".some_object.name", "ixoroturg")
        .put(base + ".some_object.age", 23)
        .put(base + ".some_object.something", (String)null)
        .putArray(base+".some_object.some_array");
    System.out.println("добавили массив:"+js.toStringFormat());
      js
        .go(base + ".some_object.some_array");
    System.out.println("Текущий массив:"+js.toStringFormat());
      js .back(0)
        .addArray(base+ ".some_object.some_array");
    System.out.println("Текущий массив 2:"+js.toStringFormat());
      js
        .add(base + ".some_object.some_array[0]",1)
        .add(base + ".some_object.some_array[0]",2)
        .addAll(base + ".some_object.some_array[0]",new int[]{3,4,5})
        .add(base+".some_object.some_array[0]",new int[]{6,7,8,9})
        .add(base + ".some_object.some_array", new String[]{"one", "two", "three"})
        .addAll(base + ".some_object.some_array",new String[]{"four","five"});
      
      System.out.println(js.toStringFormat());

    }catch(Exception e){
      if(js != null)
        System.out.println("Ошибка, текущий: "+js.toStringFormat());
      TestException t = new TestException("Ошибка в PutTest");
      t.initCause(e);
      throw t;
    }
  }
}
