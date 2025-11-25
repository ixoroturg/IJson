package ixoroturg.json;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class IJson implements Json {
    IJsonEntry currentJson = null;
    private long parseTime = -1;
    private IJson(IJsonEntry json){
      currentJson = json;
    }

    public static IJson ofObject(){
      return new IJson(new IJsonObject());
    }
    public static IJson ofArray(){
      return new IJson(new IJsonArray());
    }
//    public static IJson ofString(String value) {
//    	IJsonString str = new IJsonString(value);
//    	return new IJson(str);
//    }
    public static Json ofInnerRepresentation(IJsonEntry entry) {
    	return new IJson(entry);
    }
    public static IJson of(String json) throws JsonParseException {
      StringReader reader = new StringReader(json);
      return of(reader);
    }
    public static IJson of(InputStream input) throws JsonParseException {
      InputStreamReader reader = new InputStreamReader(input);
      return of(reader);
    }
    public static IJson of(Reader reader) throws JsonParseException{
      IJsonParseContext ctx = IJsonParseContext.openContext(reader);
      IJson result = of(ctx);
      return result;
    }
    @Override
    public long getParseTime(){
      return parseTime;
    }
    private static IJson of(IJsonParseContext ctx) throws JsonParseException{
      for(; ctx.pointer < ctx.buffer.length; ctx.pointer++, ctx.index++, ctx.column++){
        char ch = ctx.buffer[ctx.pointer];
        if(IJsonUtil.isWhiteSpace(ch)){
          if(ch == '\n'){
            ctx.row++;
            ctx.column=-1;
          }
          continue;
        }
        if(ch == -1){
          return null;
        }
        switch(ch){
          case '{' -> {
            return createEntry(new IJsonObject(), ctx);
          }
          case '[' -> {
            return createEntry(new IJsonArray(), ctx);
          }
          case '\"' -> {
            return createEntry(new IJsonString(), ctx);
          }
          case 't', 'f' -> {
            return createEntry(new IJsonBoolean(), ctx);
          }
          case 'n' -> {
            if(!IJsonUtil.testNull(ctx))
              throw new JsonParseException("Expected null", ctx);
            return null;
          }
          default -> {
            return createEntry(new IJsonNumber(), ctx);
          }
        }
      }
      ctx.read();
      return of(ctx);
    }
    private static IJson createEntry(IJsonEntry entry, IJsonParseContext ctx) throws JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidNumberException, JsonInvalidStringException, JsonInvalidBooleanException{
      entry.parse(ctx);
      IJson result = new IJson(entry);
      result.parseTime = ctx.close();
      return result;
    }

    @Override
    public int buffSize() {
      return currentJson.buffSize();
    }

    @Override
    public int buffSizeFormat() {
      return currentJson.buffSizeFormat();
    }

    @Override
    public Json parse(String json) throws JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException{
      currentJson = IJson.of(json).currentJson;
      return this;
    }

    @Override
    public Json parse(InputStream stream) throws IOException, JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException {
      currentJson = IJson.of(stream).currentJson;
      return this;
    }

    @Override
    public Json parse(Reader reader) throws IOException, JsonParseException, JsonInvalidArrayException, JsonInvalidObjectException, JsonInvalidStringException, JsonInvalidNumberException, JsonInvalidBooleanException{
      currentJson = IJson.of(reader).currentJson;
      return this;
    }

    @Override
    public String toString(){
      return currentJson.toString();
    }
    @Override
    public String toStringFormat() {
      return currentJson.toFormatedString();
    }

    @Override
    public void writeTo(OutputStream stream) throws IOException{
      OutputStreamWriter writer = new OutputStreamWriter(stream);
      writeTo(writer);
    }

    @Override
    public void writeTo(Writer writer) throws IOException{
      IJsonFormatContext ctx = IJsonFormatContext.openContext(writer);
      ctx.format = false;
      currentJson.toString(ctx);
      ctx.close();
    }

    @Override
    public void writeToFormat(OutputStream stream) throws IOException{
      OutputStreamWriter writer = new OutputStreamWriter(stream);
      writeToFormat(writer);
    }

    @Override
    public void writeToFormat(Writer writer) throws IOException{
      IJsonFormatContext ctx = IJsonFormatContext.openContext(writer);
      ctx.format = true;
      currentJson.toString(ctx);
      ctx.close();
    }

    @Override
    public String getPropertyName() throws JsonNoParentException{
        if(currentJson.paramName == null)
          throw new JsonNoParentException("This json has no parent");
        return currentJson.paramName;
    }

    @Override
    public String getPropertyNameOr(String value) {
      return currentJson.paramName;
    }

    @Override
    public Json back() throws JsonNoParentException {
      if(currentJson.parent == null)
          throw new JsonNoParentException("This json has no parent");
      currentJson = currentJson.parent;
      return this;
    }

    @Override
    public Json back(int depth) throws JsonNoParentException{
      if(depth == 0){
        while(currentJson.parent != null){
          currentJson = currentJson.parent;
        }
      } else {
        for(int i = 0; i < depth; i++){
          if(currentJson.parent == null)
            throw new JsonNoParentException("This json has no parent");
          currentJson = currentJson.parent;
        }
      }
      return this;
    }

    @Override
    public boolean has(String key) throws UnsupportedOperationException, JsonParseException {
    	if(currentJson instanceof IJsonObject) {
    		try {
    			return privateGet(new StringReader(key), key.length(), currentJson) != null;
    		} catch(JsonNoSuchPropertyException | JsonNoParentException e) {
    			return false;
    		}
    	}
    	throw new UnsupportedOperationException("has(String) support only for object");
    }
    @Override
    public boolean has(int key) throws UnsupportedOperationException {
      if(currentJson instanceof IJsonArray arr){
        return arr.list.size() > key;
      }
      throw new UnsupportedOperationException("has(int) si allowed only for array");
    }

    @Override
    public int size() throws UnsupportedOperationException {
      if(currentJson instanceof IJsonObject obj){
        return obj.map.size();
      }
      if(currentJson instanceof IJsonArray arr){
        return arr.list.size();
      }
      throw new UnsupportedOperationException("size() is not allowed for non array or object");
    }
    @Override
    public Json go(String key) throws JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException, JsonParseException {
    		IJsonEntry test = privateGet(new StringReader(key), key.length(), currentJson);
        	if(test == null)
        		throw new JsonNoSuchPropertyException("Property " +key+ " not found");
        	currentJson = test;
        	return this;
    }
    
    @Override
    public Json go(int key) throws JsonNoSuchPropertyException, UnsupportedOperationException {
    	if(currentJson instanceof IJsonArray arr) {
    		if(arr.list.size() < key)
    			throw new JsonNoSuchPropertyException("There is no element with index: "+key);
    		currentJson = arr.list.get(key);
    		return this;
    	}
    	throw new UnsupportedOperationException("get(int) is allowed only for array");
    }
    
    @Override
    public Json get(String key) throws JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException, JsonParseException {
    		IJsonEntry test = privateGet(new StringReader(key), key.length(), currentJson);
        	if(test == null)
        		throw new JsonNoSuchPropertyException("Property " +key+ " not found");
          return new IJson(test);
    }
    
    @Override
    public Json get(int key) throws JsonNoSuchPropertyException, UnsupportedOperationException {
    	if(currentJson instanceof IJsonArray arr) {
    		if(arr.list.size() < key)
    			throw new JsonNoSuchPropertyException("There is no element with index: "+key);
        return new IJson(arr.list.get(key));
    	}
    	throw new UnsupportedOperationException("get(int) is allowed only for array");
    }
    /**
     * @param reader - StringReader for String;
     * @param length - String length;
     * @param entry - recurse accumulator, currentJson by default;
     * 
     */
    private IJsonEntry privateGet(Reader reader, int length, IJsonEntry entry) throws JsonParseException, JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException{
   	// System.out.println("Получена длина: "+length);	
      // System.out.println("Принято, длина: "+length);
    		if(length == 0)		
    			return entry;
    		try {
      // System.out.println("Принято, длина: "+length);
      // // System.out.println(reader.toString());
      // reader.mark(length);
      // char[] testBuf = new char[length];
      // reader.read(testBuf);
      // System.out.println(new String(testBuf));
      // reader.reset();

          reader.mark(length);
          boolean wasSlash = false;
          boolean wasParent = false;
          for(int i = 0; i < length; i++) {
        	  char ch = (char)reader.read();
//        	  if(i == 0 && ch == IJsonSetting.KEY_DELIMETER)
//        		  continue;
	          if(ch == '[' && IJsonSetting.USE_ARRAY_SYNTAX && i == 0) {
	        	  StringBuilder builder = new StringBuilder(); 
	            if(entry instanceof IJsonArray arr){
	              int parse = -1;
	              for(i = 0; i < length; i++) {
	                ch = (char)reader.read();
	                builder.append(ch);
	                if(ch >= '0' && ch <= '9' && parse < Integer.MAX_VALUE) {
	                  if(parse == -1)
	                    parse = 0;
	                  parse = parse * 10 + ch - '0';
	                } else if (ch == ']'){
	                  if(parse == -1)
	                    throw new JsonParseException("Expected integer number, but found: "+"[]");
	                  if(parse > arr.list.size())
	                    return null;
	                  entry = arr.list.get(parse);
	                  length -= i;
	                  length--;
	                  reader.skip(1);
	                  if(length != 0)
	                	  length--;
//	                  ch = (char)reader.read();
//	                  if(ch != (char)65535 && ch != 0)
//	                	  length--;
	                  break;
	                  
	                } else {
	                  throw new JsonParseException("Expected integer number, but found: "+builder.toString());
	                }
	              }
	              return privateGet(reader,length,entry);
	            }
	              // throw new JsonNoSuchPropertyException("Expected integer number, but found: "+builder.toString());
	              throw new JsonNoSuchPropertyException("Found: "+builder.toString() + " but this json is not array");
	          } else {
	           
//	            for(int i = 0; true ; i++) {
//	              char ch = (char)reader.read();
//	            System.out.println("Символ: "+ch+"  slash = "+wasSlash);
	              if(ch == '\\'){
//	            	  System.out.println("Был слеш");
	                wasSlash = !wasSlash;
	                continue;
	              }
	              if(wasSlash && ch == IJsonSetting.PARENT_CHARACTER){
//	            	  System.out.println("Был спец символ");
	                wasParent = true;
	                if(length > 2)
	                	ch = (char)reader.read();
//	                continue;
	              }
	              if(wasSlash && wasParent){
	                if(ch != IJsonSetting.KEY_DELIMETER && length > 2)
	                  throw new JsonParseException("After \\ and IJsonSetting.PARENT_CHARACTER expected . but found: "+ch);
	                else{
	                  entry = entry.parent;
	                  if(entry == null)
	                    return null;
	                  if(length > 2)
	                	  length--;
	                    // throw new JsonNoParentException("This json has no parent");
	                  return privateGet(reader, length - 2, entry);
	                }
	              }
	            } 
	            if(i == length - 1 || (ch == IJsonSetting.KEY_DELIMETER) || (ch == '[' && IJsonSetting.USE_ARRAY_SYNTAX )) {
//	            	System.out.println("Найден конец");
	            	String newKey = null;
	            	
	              if(entry instanceof IJsonObject obj) {
	            	  
	              reader.reset();
	              if(i == length - 1) {
	            	  length++;
	            	  i++;
	              }
//	              System.out.println("Текущий: "+entry);
	              char[] buf = new char[i];
//	              System.out.println("Длина буфера: "+i);
	              length -= i;
	              reader.read(buf);
//	              System.out.println("Буфер: "+ Arrays.toString(buf));
	              newKey = new String(buf);
//	              System.out.println("Ключ мапы: "+newKey);
//	              System.out.println("текущая мапа: "+obj.map);
	                entry = obj.map.get(newKey);
//	               System.out.println("Из мапы: "+entry);
	                length--;
	                if(entry == null){
                    if(length != 0)
                      throw new JsonNoSuchPropertyException("Property not found "+ newKey);
	                  return null;
                  }
	                if(ch != '[')
	                	reader.skip(1);
	                return privateGet(reader,length,entry);
	              } else
	                throw new JsonNoSuchPropertyException("Property " +newKey+ " not found");
//	            }
	          }
    		}
        }catch(IOException e) {
        	e.printStackTrace();
        }
    		
    	throw new JsonParseException("Unexpected error");
    }

    private final byte OBJECT = 0, ARRAY = 1;
    private innerEntry returnBeforeLastEntry(String key, byte type) throws JsonNoParentException, JsonParseException,
	UnsupportedOperationException, JsonNoSuchPropertyException {
    	if(type == ARRAY) {
        // int newLength = key.length();
        // if(type == BEFORE_ARRAY && IJsonSetting.USE_ARRAY_SYNTAX){
        //   if(key.charAt(key.length()-1) == ']')
        //     for(int i = key.length()-2; i <= 0; i--){
        //       if(key.charAt(i) == '['){
        //         newLength = i - 1;
        //         break;
        //       }
        //     }
        // }
    		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
        // System.out.println(key+" : "+entry);
    		if(! (entry instanceof IJsonArray))
    			throw new UnsupportedOperationException("add() and getArray() methods is allowed only for array");
    		return new innerEntry(entry,0);
    	}
    	
    	int newLength = key.length();
		if(IJsonSetting.KEY_DELIMETER != 0) {
			for(int i = newLength-1; i >= 0; i--) {
				if(key.charAt(i) == IJsonSetting.KEY_DELIMETER || i == 0) {
          newLength = i;
					// newLength = i - 1;
					//      if(newLength < 0)
            // newLength = 0;
					break;
				}
			}	
		} else
      newLength = 0;
    // System.out.println("ключ от свойства: "+key.substring(newLength) + " | его длина: "+ newLength + "\nполный ключ: "+key);
		IJsonEntry entry = privateGet(new StringReader(key),newLength, currentJson);
    // System.out.println("Получено: "+ entry);
    // System.out.println("длина: "+newLength+" "+entry+" , current: "+currentJson);
		if( !(entry instanceof IJsonObject))
			throw new UnsupportedOperationException("put() and get() methods is allowed only for object");
		
//		switch(type) {
//			case OBJECT -> {
//				if( !(entry instanceof IJsonObject))
//					throw new UnsupportedOperationException("put() and get() methods is allowed only for object");
//			}
//			case ARRAY -> {
//				if( !(entry instanceof IJsonArray))
//					
//			}
//		}
		return new innerEntry(entry, newLength == 0 ? 0 : newLength+1);
    }
    @Override
    public IJsonEntry getInnerRepresentation() {
    	return currentJson;
    }
    
    private record innerEntry(IJsonEntry entry, int start) {};
    // search:put(key, value)
	@Override
	public Json put(String key, byte value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return put(key, (double)value);
	}

	@Override
	public Json put(String key, short value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return put(key, (double)value);
	}

	@Override
	public Json put(String key, int value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return put(key, (double)value);
	}

	@Override
	public Json put(String key, long value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return put(key, (double)value);
	}

	@Override
	public Json put(String key, float value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return put(key, (double)value);
	}

	@Override
	public Json put(String key, double value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		innerEntry b = returnBeforeLastEntry(key,OBJECT);
		IJsonObject entry = (IJsonObject) b.entry;
		IJsonEntry val = new IJsonNumber(value);

		val.parent = entry;
		val.paramName = key.substring(b.start);
		entry.map.put(val.paramName, val);
		return this;
	}

	@Override
	public Json put(String key, boolean value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		innerEntry b = returnBeforeLastEntry(key,OBJECT);
		IJsonObject entry = (IJsonObject) b.entry;
		IJsonEntry val =  new IJsonBoolean(value);
		val.parent = entry;
		val.paramName = key.substring(b.start);
		entry.map.put(val.paramName, val);
		return this;
	}

	@Override
	public Json put(String key, String value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		innerEntry b = returnBeforeLastEntry(key, OBJECT);
		IJsonObject entry = (IJsonObject) b.entry;
		IJsonEntry val = new IJsonString(value);
		val.parent = entry;
		val.paramName = key.substring(b.start);
		entry.map.put(val.paramName, val);
		return this;
	}

	@Override
	public Json put(String key, Json value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
        if(value == null)
          return put(key,(IJsonEntry)null);
		return put(key,value.getInnerRepresentation());
	}
	
	private Json put(String key, IJsonEntry value)throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
        // System.out.println("Ключ: " + key);
		innerEntry b = returnBeforeLastEntry(key, OBJECT);
		IJsonObject entry = (IJsonObject) b.entry;
    if(value != null){
      value.parent = entry;
      value.paramName = key.substring(b.start);
    }
    // System.out.println("Получен: "+ entry + "\nего ключ: " +(value != null ? value.paramName : key.substring(b.start)) );
    // System.out.println("Дальше");
    System.out.println(entry.map);
		entry.map.put(value != null ? value.paramName : key.substring(b.start), value);
    // System.out.println("Мапа 2: "+entry.map);
		return this;
	}
	// search:add(value)
	@Override
	public Json add(byte value) throws UnsupportedOperationException {
		return add((double)value);
	}

	@Override
	public Json add(short value) throws UnsupportedOperationException {
		return add((double)value);
	}

	@Override
	public Json add(int value) throws UnsupportedOperationException {
		return add((double)value);
	}

	@Override
	public Json add(long value) throws UnsupportedOperationException {
		return add((double)value);
	}

	@Override
	public Json add(float value) throws UnsupportedOperationException {
		return add((double)value);
	}

	@Override
	public Json add(double value) throws UnsupportedOperationException {
		if(currentJson instanceof IJsonArray arr) {
			IJsonEntry val = new IJsonNumber(value);
			val.parent = arr;
			arr.list.add(val);
		} else
			throw new UnsupportedOperationException("add() is allowed only for array");
		return this;
	}

	@Override
	public Json add(boolean value) throws UnsupportedOperationException {
		if(currentJson instanceof IJsonArray arr) {
			IJsonEntry val = new IJsonBoolean(value);
			val.parent = arr;
			arr.list.add(val);
		} else
			throw new UnsupportedOperationException("add() is allowed only for array");
		return this;
	}

	@Override
	public Json add(String value) throws UnsupportedOperationException {
		if(currentJson instanceof IJsonArray arr) {
			IJsonEntry val = new IJsonString(value);
			val.parent = arr;
			arr.list.add(val);
		} else
			throw new UnsupportedOperationException("add() is allowed only for array");
		return this;
	}

	@Override
	public Json add(Json value) throws UnsupportedOperationException {
    if(value == null)
      return add((IJsonEntry)null);
		return add(value.getInnerRepresentation());
	}
	private Json add(IJsonEntry value) throws UnsupportedOperationException {
		if(currentJson instanceof IJsonArray arr) {
      if(value != null)
        value.parent = arr;
			arr.list.add(value);
		} else
			throw new UnsupportedOperationException("add() is allowed only for array");
		return this;
	}

	// search:add(key, value)
	@Override
	public Json add(String key, byte value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return add(key, (double)value);
	}

	@Override
	public Json add(String key, short value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return add(key, (double)value);
	}

	@Override
	public Json add(String key, int value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return add(key, (double)value);
	}

	@Override
	public Json add(String key, long value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return add(key, (double)value);
	}

	@Override
	public Json add(String key, float value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return add(key, (double)value);
	}

	@Override
	public Json add(String key, double value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		innerEntry b = returnBeforeLastEntry(key, ARRAY);
		IJsonArray entry = (IJsonArray) b.entry;
		IJsonEntry val = new IJsonNumber(value);
		val.parent = entry;
		entry.list.add(val);
		return this;
	}

	@Override
	public Json add(String key, boolean value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		innerEntry b = returnBeforeLastEntry(key, ARRAY);
		IJsonArray entry = (IJsonArray) b.entry;
		IJsonEntry val = new IJsonBoolean(value);
		val.parent = entry;
		entry.list.add(val);
		return this;
	}

	@Override
	public Json add(String key, String value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		innerEntry b = returnBeforeLastEntry(key, ARRAY);
		IJsonArray entry = (IJsonArray) b.entry;
		IJsonEntry val = new IJsonString(value);
		val.parent = entry;
		entry.list.add(val);
		return this;
	}

	@Override
	public Json add(String key, Json value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		return add(key, value.getInnerRepresentation());
	}

	private Json add(String key, IJsonEntry value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
        // System.out.println("Передача: "+key);
		innerEntry b = returnBeforeLastEntry(key, ARRAY);
    // if( !(b instanceof IJsonArray ) || b == null){
    //   b = returnBeforeLastEntry(key,BEFORE_ARRAY)
    // }
		IJsonArray entry = (IJsonArray) b.entry;
		entry.list.add(value);
		value.parent = entry;
		return this;
	}

	// search:put(key, value[])
	@Override
	public Json put(String key, byte[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}

	@Override
	public Json put(String key, short[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}

	@Override
	public Json put(String key, int[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}

	@Override
	public Json put(String key, long[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}

	@Override
	public Json put(String key, float[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}

	@Override
	public Json put(String key, double[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}

	@Override
	public Json put(String key, boolean[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonBoolean(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}

	@Override
	public Json put(String key, String[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonString(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}

	@Override
	public Json put(String key, Json[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = value[i].getInnerRepresentation();
			val.parent = arr;
			arr.list.add(val);
		}
		return put(key, arr);
	}
	
	// search:add(value[])
	@Override
	public Json add(byte[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}

	@Override
	public Json add(short[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}

	@Override
	public Json add(int[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}

	@Override
	public Json add(long[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}

	@Override
	public Json add(float[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}

	@Override
	public Json add(double[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}

	@Override
	public Json add(boolean[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonBoolean(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}

	@Override
	public Json add(String[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonString(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}

	@Override
	public Json add(Json[] value) throws UnsupportedOperationException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = value[i].getInnerRepresentation();
			val.parent = arr;
			arr.list.add(val);
		}
		return add(arr);
	}
	// search:addAll(key, value[])
		@Override
		public Json addAll(String key, byte[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonNumber(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			return this;
		}

		@Override
		public Json addAll(String key, short[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
			for(int i = 0; i < value.length; i++) {
				IJsonEntry val = new IJsonNumber(value[i]);
				val.parent = arr;
				arr.list.add(val);
			}
			return this;
		}

		@Override
		public Json addAll(String key, int[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
			for(int i = 0; i < value.length; i++) {
				IJsonEntry val = new IJsonNumber(value[i]);
				val.parent = arr;
				arr.list.add(val);
			}
			return this;
		}

		@Override
		public Json addAll(String key, long[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
			for(int i = 0; i < value.length; i++) {
				IJsonEntry val = new IJsonNumber(value[i]);
				val.parent = arr;
				arr.list.add(val);
			}
			return this;
		}

		@Override
		public Json addAll(String key, float[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
			for(int i = 0; i < value.length; i++) {
				IJsonEntry val = new IJsonNumber(value[i]);
				val.parent = arr;
				arr.list.add(val);
			}
			return this;
		}

		@Override
		public Json addAll(String key, double[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
			for(int i = 0; i < value.length; i++) {
				IJsonEntry val = new IJsonNumber(value[i]);
				val.parent = arr;
				arr.list.add(val);
			}
			return this;
		}

		@Override
		public Json addAll(String key, boolean[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
			for(int i = 0; i < value.length; i++) {
				IJsonEntry val = new IJsonBoolean(value[i]);
				val.parent = arr;
				arr.list.add(val);
			}
			return this;
		}

		@Override
		public Json addAll(String key, String[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
			for(int i = 0; i < value.length; i++) {
				IJsonEntry val = new IJsonString(value[i]);
				val.parent = arr;
				arr.list.add(val);
			}
			return this;
		}

		@Override
		public Json addAll(String key, Json[] value) throws JsonNoParentException, JsonParseException,
				UnsupportedOperationException, JsonNoSuchPropertyException {
			IJsonArray arr = (IJsonArray) returnBeforeLastEntry(key, ARRAY).entry;
			for(int i = 0; i < value.length; i++) {
				IJsonEntry val = value[i].getInnerRepresentation();
				val.parent = arr;
				arr.list.add(val);
			}
			return this;
		}
	// search:add(key, value[])
	@Override
	public Json add(String key, byte[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}

	@Override
	public Json add(String key, short[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}

	@Override
	public Json add(String key, int[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}

	@Override
	public Json add(String key, long[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}

	@Override
	public Json add(String key, float[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}

	@Override
	public Json add(String key, double[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonNumber(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}

	@Override
	public Json add(String key, boolean[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonBoolean(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}

	@Override
	public Json add(String key, String[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = new IJsonString(value[i]);
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}

	@Override
	public Json add(String key, Json[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJsonArray arr = new IJsonArray();
		for(int i = 0; i < value.length; i++) {
			IJsonEntry val = value[i].getInnerRepresentation();
			val.parent = arr;
			arr.list.add(val);
		}
		return add(key, arr);
	}
	// search:addAll(value[])
		@Override
		public Json addAll(byte[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonNumber(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}

		@Override
		public Json addAll(short[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonNumber(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}

		@Override
		public Json addAll(int[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonNumber(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}

		@Override
		public Json addAll(long[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonNumber(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}

		@Override
		public Json addAll(float[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonNumber(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}

		@Override
		public Json addAll(double[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonNumber(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}

		@Override
		public Json addAll(boolean[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonBoolean(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}

		@Override
		public Json addAll(String[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = new IJsonString(value[i]);
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}

		@Override
		public Json addAll(Json[] value) throws UnsupportedOperationException {
			if(currentJson instanceof IJsonArray arr) {
				for(int i = 0; i < value.length; i++) {
					IJsonEntry val = value[i].getInnerRepresentation();
					val.parent = arr;
					arr.list.add(val);
				}
			} else
				throw new UnsupportedOperationException("addAll() is allowed only for array");
			return this;
		}
	// search:getType()
	@Override
	public byte getByte() throws UnsupportedOperationException {
		return (byte)getDouble();
	}

	@Override
	public short getShort() throws UnsupportedOperationException {
		return (short)getDouble();
	}

	@Override
	public int getInt() throws UnsupportedOperationException {
		return (int)getDouble();
	}

	@Override
	public long getLong() throws UnsupportedOperationException {
		return (long)getDouble();
	}

	@Override
	public float getFloat() throws UnsupportedOperationException {
		return (float)getDouble();
	}

	@Override
	public double getDouble() throws UnsupportedOperationException {
		if(currentJson instanceof IJsonNumber num) {
			if(Double.isNaN(num.value)) {
				num.value = Double.parseDouble(num.strValue);
			}
			return num.value;
		} else
			throw new UnsupportedOperationException("getNumberType() is allowed only for number");
	}

	@Override
	public boolean getBoolean() throws UnsupportedOperationException {
		if(currentJson instanceof IJsonBoolean bool) {
			return bool.value;
		} else
			throw new UnsupportedOperationException("getBoolean() is allowed only for boolean");
	}

	@Override
	public String getString() throws UnsupportedOperationException {
		if(currentJson instanceof IJsonString str) {
			return str.value;
		} else
			throw new UnsupportedOperationException("getString() is allowed only for string");
	}
	// search:getType(key)
	@Override
	public byte getByte(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,
			UnsupportedOperationException {
		return (byte)getDouble(key);
	}

	@Override
	public short getShort(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,
			UnsupportedOperationException {
		return (short)getDouble(key);
	}

	@Override
	public int getInt(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,
			UnsupportedOperationException {
		return (int)getDouble(key);
	}

	@Override
	public long getLong(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,
			UnsupportedOperationException {
		return (long)getDouble(key);
	}

	@Override
	public float getFloat(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,
			UnsupportedOperationException {
		return (float)getDouble(key);
	}

	@Override
	public double getDouble(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,
			UnsupportedOperationException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry instanceof IJsonNumber num) {
			if(Double.isNaN(num.value)) {
				num.value = Double.parseDouble(num.strValue);
			}
			return num.value;
		} else
			throw new UnsupportedOperationException("getNumberType(key) is allowed only for number");
	}

	@Override
	public boolean getBoolean(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry instanceof IJsonBoolean bool) {
			return bool.value;
		} else
			throw new UnsupportedOperationException("getBoolean(key) is allowed only for boolean");
	}

	@Override
	public String getString(String key) throws JsonNoParentException, JsonParseException, JsonNoSuchPropertyException,
			UnsupportedOperationException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry instanceof IJsonString str) {
			return str.value;
		} else
			throw new UnsupportedOperationException("getString(key) is allowed only for string");
	}
	// search:getTypeOr(key, value)
	
	@Override
	public byte getByteOr(String key, byte value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		return (byte)getDoubleOr(key, value);
	}

	@Override
	public short getShortOr(String key, short value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		return (short)getDoubleOr(key, value);
	}

	@Override
	public int getIntOr(String key, int value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		return (int)getDoubleOr(key, value);
	}

	@Override
	public long getLongOr(String key, long value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		return (long)getDoubleOr(key, value);
	}

	@Override
	public float getFloatOr(String key, float value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		return (float)getDoubleOr(key, value);
	}

	@Override
	public double getDoubleOr(String key, double value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonNumber num) {
			return num.value;
		} else
			throw new UnsupportedOperationException("getNumberType(key) is allowed only for number");
	}

	@Override
	public boolean getBooleanOr(String key, boolean value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonBoolean bool) {
			return bool.value;
		} else
			throw new UnsupportedOperationException("getBoolean(key) is allowed only for boolean");
	}

	@Override
	public String getStringOr(String key, String value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonString str) {
			return str.value;
		} else
			throw new UnsupportedOperationException("getString(key) is allowed only for string");
	}
	// search:getTypeArray()
	@Override
	public byte[] getByteArray() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			byte[] result = new byte[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (byte)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public short[] getShortArray() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			short[] result = new short[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (short)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public int[] getIntArray() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			int[] result = new int[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (int)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public long[] getLongArray() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			long[] result = new long[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (long)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public float[] getFloatArray() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			float[] result = new float[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (float)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public double[] getDoubleArray() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			double[] result = new double[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = ((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public boolean[] getBooleanArray() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			boolean[] result = new boolean[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = ((IJsonBoolean)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getBooleanArray() is allowed only for array");
	}

	@Override
	public String[] getStringArray() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			String[] result = new String[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = ((IJsonString)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getStringArray() is allowed only for array");
	}
	// search:getTypeArray(key)
	@Override
	public byte[] getByteArray(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonArray arr = (IJsonArray)returnBeforeLastEntry(key, ARRAY).entry;
		byte[] result = new byte[arr.list.size()];
		try {
			for(int i = 0; i < result.length; i++) {
				result[i] = (byte)((IJsonNumber)arr.list.get(i)).value;
			}
		}catch(ClassCastException e) {
			JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
			t.initCause(e);
			throw t;
		}
		return result;
	}

	@Override
	public short[] getShortArray(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonArray arr = (IJsonArray)returnBeforeLastEntry(key, ARRAY).entry;
		short[] result = new short[arr.list.size()];
		try {
			for(int i = 0; i < result.length; i++) {
				result[i] = (short)((IJsonNumber)arr.list.get(i)).value;
			}
		}catch(ClassCastException e) {
			JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
			t.initCause(e);
			throw t;
		}
		return result;
	}

	@Override
	public int[] getIntArray(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonArray arr = (IJsonArray)returnBeforeLastEntry(key, ARRAY).entry;
		int[] result = new int[arr.list.size()];
		try {
			for(int i = 0; i < result.length; i++) {
				result[i] = (int)((IJsonNumber)arr.list.get(i)).value;
			}
		}catch(ClassCastException e) {
			JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
			t.initCause(e);
			throw t;
		}
		return result;
	}

	@Override
	public long[] getLongArray(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonArray arr = (IJsonArray)returnBeforeLastEntry(key, ARRAY).entry;
		long[] result = new long[arr.list.size()];
		try {
			for(int i = 0; i < result.length; i++) {
				result[i] = (long)((IJsonNumber)arr.list.get(i)).value;
			}
		}catch(ClassCastException e) {
			JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
			t.initCause(e);
			throw t;
		}
		return result;
	}

	@Override
	public float[] getFloatArray(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonArray arr = (IJsonArray)returnBeforeLastEntry(key, ARRAY).entry;
		float[] result = new float[arr.list.size()];
		try {
			for(int i = 0; i < result.length; i++) {
				result[i] = (float)((IJsonNumber)arr.list.get(i)).value;
			}
		}catch(ClassCastException e) {
			JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
			t.initCause(e);
			throw t;
		}
		return result;
	}

	@Override
	public double[] getDoubleArray(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonArray arr = (IJsonArray)returnBeforeLastEntry(key, ARRAY).entry;
		double[] result = new double[arr.list.size()];
		try {
			for(int i = 0; i < result.length; i++) {
				result[i] = ((IJsonNumber)arr.list.get(i)).value;
			}
		}catch(ClassCastException e) {
			JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
			t.initCause(e);
			throw t;
		}
		return result;
	}

	@Override
	public boolean[] getBooleanArray(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonArray arr = (IJsonArray)returnBeforeLastEntry(key, ARRAY).entry;
		boolean[] result = new boolean[arr.list.size()];
		try {
			for(int i = 0; i < result.length; i++) {
				result[i] = ((IJsonBoolean)arr.list.get(i)).value;
			}
		}catch(ClassCastException e) {
			JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
			t.initCause(e);
			throw t;
		}
		return result;
	}

	@Override
	public String[] getStringArray(String key) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonArray arr = (IJsonArray)returnBeforeLastEntry(key, ARRAY).entry;
		String[] result = new String[arr.list.size()];
		try {
			for(int i = 0; i < result.length; i++) {
				result[i] = ((IJsonString)arr.list.get(i)).value;
			}
		}catch(ClassCastException e) {
			JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
			t.initCause(e);
			throw t;
		}
		return result;
	}
	// search:getTypeArrayOr(key, value)
	@Override
	public byte[] getByteArrayOr(String key, byte[] value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonArray arr) {
			byte[] result = new byte[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (byte)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public short[] getShortArrayOr(String key, short[] value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonArray arr) {
			short[] result = new short[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (short)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public int[] getIntArrayOr(String key, int[] value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonArray arr) {
			int[] result = new int[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (int)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public long[] getLongArrayOr(String key, long[] value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonArray arr) {
			long[] result = new long[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (long)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public float[] getFloatArrayOr(String key, float[] value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonArray arr) {
			float[] result = new float[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (float)((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumberTypeArray() is allowed only for array");
	}

	@Override
	public double[] getDoubleArrayOr(String key, double[] value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonArray arr) {
			double[] result = new double[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = ((IJsonNumber)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getNumbetTypeArray() is allowed only for array");
	}

	@Override
	public boolean[] getBooleanArrayOr(String key, boolean[] value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonArray arr) {
			boolean[] result = new boolean[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = (boolean)((IJsonBoolean)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getBooleanArray() is allowed only for array");
	}

	@Override
	public String[] getStringArrayOr(String key, String[] value) throws JsonNoParentException, JsonParseException,
	JsonNoSuchPropertyException, UnsupportedOperationException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return value;
		if(entry instanceof IJsonArray arr) {
			String[] result = new String[arr.list.size()];
			try {
				for(int i = 0; i < result.length; i++) {
					result[i] = ((IJsonString)arr.list.get(i)).value;
				}
			}catch(ClassCastException e) {
				JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
				t.initCause(e);
				throw t;
			}
			return result;
		} else
			throw new UnsupportedOperationException("getStringArray() is allowed only for array");
	}
	// search:getStream()
	@Override
	public IntStream getIntStream() throws UnsupportedOperationException, JsonIllegalTypeException {
		return Arrays.stream(getIntArray());
	}

	@Override
	public LongStream getLongStream() throws UnsupportedOperationException, JsonIllegalTypeException {
		return Arrays.stream(getLongArray());
	}

	@Override
	public DoubleStream getDoubleStream() throws UnsupportedOperationException, JsonIllegalTypeException {
		return Arrays.stream(getDoubleArray());
	}

	@Override
	public Stream<String> getStringStream() throws UnsupportedOperationException, JsonIllegalTypeException {
		return Arrays.<String>stream(getStringArray());
	}

	@Override
	public Stream<Json> getJsonStream() throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {	
			Json[] result = new Json[arr.list.size()];
			for(int i = 0; i < result.length; i++) {
				result[i] = new IJson(arr.list.get(i));
			}
			return Arrays.<Json>stream(result);
		} else
			throw new UnsupportedOperationException("getJsonStream() is allowed only for array");
	}

	// search:getStream(key)
	@Override
	public IntStream getIntStream(String key) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return get(key).getIntStream();
	}

	@Override
	public LongStream getLongStream(String key) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return get(key).getLongStream();
	}

	@Override
	public DoubleStream getDoubleStream(String key) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return get(key).getDoubleStream();
	}

	@Override
	public Stream<String> getStringStream(String key) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return get(key).getStringStream();
	}

	// search:getStreamOr(key)

	@Override
	public IntStream getIntStreamOr(String key, int[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return Arrays.stream(getIntArrayOr(key, value));
	}

	@Override
	public LongStream getLongStreamOr(String key, long[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return Arrays.stream(getLongArrayOr(key, value));
	}

	@Override
	public DoubleStream getDoubleStreamOr(String key, double[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return Arrays.stream(getDoubleArrayOr(key, value));
	}

	@Override
	public Stream<String> getStringStreamOr(String key, String[] value) throws JsonNoParentException,
			JsonParseException, UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return Arrays.<String>stream(getStringArrayOr(key, value));
	}

	@Override
	public Stream<Json> getJsonStreamOr(String key, Json[] value) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		IJsonEntry entry = privateGet(new StringReader(key), key.length(), currentJson);
		if(entry == null)
			return Arrays.<Json>stream(value);
		if(entry instanceof IJsonArray arr) {	
			Json[] result = new Json[arr.list.size()];
			for(int i = 0; i < result.length; i++) {
				result[i] = new IJson(arr.list.get(i));
			}
			return Arrays.<Json>stream(result);
		} else
			throw new UnsupportedOperationException("getJsonStream() is allowed only for array");
	}

	// search:puAddSpecial()
	@Override
	public Json putObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		put(key, new IJsonObject());
		return this;
	}

	@Override
	public Json putGetObject(String key) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJson js = IJson.ofObject();
		put(key,js);
		return js;
	}

	@Override
	public Json putGoObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		IJsonObject js = new IJsonObject();
		put(key, js);
		currentJson = js;
		return this;
	}

	@Override
	public Json putArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		return put(key, new IJsonArray());
	}

	@Override
	public Json putGetArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		IJson js = IJson.ofArray();
		put(key,js);
		return js;
	}

	@Override
	public Json putGoArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		IJsonArray js = new IJsonArray();
		put(key, js);
		currentJson = js;
		return this;
	}

	@Override
	public Json addObject() throws UnsupportedOperationException {
		return add(new IJsonObject());
	}

	@Override
	public Json addGetObject() throws UnsupportedOperationException {
		IJson js = IJson.ofObject();
		add(js);
		return js;
	}

	@Override
	public Json addGoObject() throws UnsupportedOperationException {
		IJsonObject js = new IJsonObject();
		add(js);
		currentJson = js;
		return this;
	}

	@Override
	public Json addArray() throws UnsupportedOperationException {
		return add(new IJsonArray());
	}

	@Override
	public Json addGetArray() throws UnsupportedOperationException {
		IJson js = IJson.ofArray();
		add(js);
		return js;
	}

	@Override
	public Json addGoArray() throws UnsupportedOperationException {
		IJsonArray js = new IJsonArray();
		add(js);
		currentJson = js;
		return this;
	}

	@Override
	public Json addObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		return add(key, new IJsonObject());
	}

	@Override
	public Json addGetObject(String key) throws JsonNoParentException, JsonParseException,
			UnsupportedOperationException, JsonNoSuchPropertyException {
		IJson js = IJson.ofObject();
		add(key, js);
		return js;
	}

	@Override
	public Json addGoObject(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		IJsonObject js = new IJsonObject();
		add(key, js);
		currentJson = js;
		return this;
	}

	@Override
	public Json addArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		return add(key,new IJsonArray());
	}

	@Override
	public Json addGetArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		IJson js = IJson.ofArray();
		add(key, js);
		return js;
	}

	@Override
	public Json addGoArray(String key) throws JsonNoParentException, JsonParseException, UnsupportedOperationException,
			JsonNoSuchPropertyException {
		IJsonArray js = new IJsonArray();
		add(key, js);
		currentJson = js;
		return this;
	}

	// search:getType(int index)
	@Override
	public byte getByte(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return (byte)getDouble(index);
	}

	@Override
	public short getShort(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return (short)getDouble(index);
	}

	@Override
	public int getInt(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return (int)getDouble(index);
	}

	@Override
	public long getLong(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return (long)getDouble(index);
	}

	@Override
	public float getFloat(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		return (float)getDouble(index);
	}

	@Override
	public double getDouble(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonNumber num) {
				return num.value;
			} else
				throw new JsonIllegalTypeException("getNumberType(int index) is allowed only for array and number value");
		}	else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public boolean getBoolean(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonBoolean bool) {
				return bool.value;
			} else
				throw new JsonIllegalTypeException("getBoolean(int index) is allowed only for array and boolean value");
		}	else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public String getString(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonString str) {
				return str.value;
			} else
				throw new JsonIllegalTypeException("getString(int index) is allowed only for array and string value");
		}	else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}
	// search:getTypeArray(int index)
	@Override
	public byte[] getByteArray(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonArray arr2) {
				byte[] result = new byte[arr2.list.size()];
				try {
					for(int i = 0; i < result.length; i++) {
						result[i] = (byte)((IJsonNumber)arr2.list.get(i)).value;
					}
					return result;
				}catch(ClassCastException e) {
					JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
					t.initCause(e);
					throw t;
				}
			} else
				throw new JsonIllegalTypeException("getNumberType(int index) is allowed only for array");	
		} else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public short[] getShortArray(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonArray arr2) {
				short[] result = new short[arr2.list.size()];
				try {
					for(int i = 0; i < result.length; i++) {
						result[i] = (short)((IJsonNumber)arr2.list.get(i)).value;
					}
					return result;
				}catch(ClassCastException e) {
					JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
					t.initCause(e);
					throw t;
				}
			} else
				throw new JsonIllegalTypeException("getNumberType(int index) is allowed only for array");	
		} else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public int[] getIntArray(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonArray arr2) {
				int[] result = new int[arr2.list.size()];
				try {
					for(int i = 0; i < result.length; i++) {
						result[i] = (int)((IJsonNumber)arr2.list.get(i)).value;
					}
					return result;
				}catch(ClassCastException e) {
					JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
					t.initCause(e);
					throw t;
				}
			} else
				throw new JsonIllegalTypeException("getNumberType(int index) is allowed only for array");	
		} else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public long[] getLongArray(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonArray arr2) {
				long[] result = new long[arr2.list.size()];
				try {
					for(int i = 0; i < result.length; i++) {
						result[i] = (long)((IJsonNumber)arr2.list.get(i)).value;
					}
					return result;
				}catch(ClassCastException e) {
					JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
					t.initCause(e);
					throw t;
				}
			} else
				throw new JsonIllegalTypeException("getNumberType(int index) is allowed only for array");	
		} else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public float[] getFloatArray(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonArray arr2) {
				float[] result = new float[arr2.list.size()];
				try {
					for(int i = 0; i < result.length; i++) {
						result[i] = (float)((IJsonNumber)arr2.list.get(i)).value;
					}
					return result;
				}catch(ClassCastException e) {
					JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
					t.initCause(e);
					throw t;
				}
			} else
				throw new JsonIllegalTypeException("getNumberType(int index) is allowed only for array");	
		} else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public double[] getDoubleArray(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonArray arr2) {
				double[] result = new double[arr2.list.size()];
				try {
					for(int i = 0; i < result.length; i++) {
						result[i] = ((IJsonNumber)arr2.list.get(i)).value;
					}
					return result;
				}catch(ClassCastException e) {
					JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
					t.initCause(e);
					throw t;
				}
			} else
				throw new JsonIllegalTypeException("getNumberType(int index) is allowed only for array");	
		} else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public boolean[] getBooleanArray(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonArray arr2) {
				boolean[] result = new boolean[arr2.list.size()];
				try {
					for(int i = 0; i < result.length; i++) {
						result[i] = ((IJsonBoolean)arr2.list.get(i)).value;
					}
					return result;
				}catch(ClassCastException e) {
					JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
					t.initCause(e);
					throw t;
				}
			} else
				throw new JsonIllegalTypeException("getBoolean(int index) is allowed only for array");	
		} else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public String[] getStringArray(int index) throws UnsupportedOperationException, JsonNoSuchPropertyException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+arr.list.size());
			IJsonEntry entry = arr.list.get(index);
			if(entry instanceof IJsonArray arr2) {
				String[] result = new String[arr2.list.size()];
				try {
					for(int i = 0; i < result.length; i++) {
						result[i] = ((IJsonString)arr2.list.get(i)).value;
					}
					return result;
				}catch(ClassCastException e) {
					JsonIllegalTypeException t = new JsonIllegalTypeException("Unexpected value");
					t.initCause(e);
					throw t;
				}
			} else
				throw new JsonIllegalTypeException("getString(int index) is allowed only for array");	
		} else
			throw new UnsupportedOperationException("getType(int index) is allowed only for array");
	}

	@Override
	public IntStream getIntStream(int index)
			throws UnsupportedOperationException, JsonIllegalTypeException, JsonNoSuchPropertyException {
		return Arrays.stream(getIntArray(index));
	}

	@Override
	public LongStream getLongStream(int index)
			throws UnsupportedOperationException, JsonIllegalTypeException, JsonNoSuchPropertyException {
		return Arrays.stream(getLongArray(index));
	}

	@Override
	public DoubleStream getDoubleStream(int index)
			throws UnsupportedOperationException, JsonIllegalTypeException, JsonNoSuchPropertyException {
		return Arrays.stream(getDoubleArray(index));
	}

	@Override
	public Stream<String> getStringStream(int index)
			throws UnsupportedOperationException, JsonIllegalTypeException, JsonNoSuchPropertyException {
		return Arrays.<String>stream(getStringArray(index));
	}

	@Override
	public Stream<Json> getJsonStream(int index)
			throws UnsupportedOperationException, JsonIllegalTypeException, JsonNoSuchPropertyException {
		if(currentJson instanceof IJsonArray test) {
			if(test.list.size() <= index)
				throw new JsonNoSuchPropertyException("Index "+index+ " out of bounds for size: "+test.list.size());
			IJsonEntry entry = test.list.get(index);
			if(entry instanceof IJsonArray arr) {	
				Json[] result = new Json[arr.list.size()];
				for(int i = 0; i < result.length; i++) {
					result[i] = new IJson(arr.list.get(i));
				}
				return Arrays.<Json>stream(result);
			} else
				throw new UnsupportedOperationException("getJsonStream(int index) is allowed only for array");
		}else
			throw new UnsupportedOperationException("getJsonStream(int index) is allowed only for array");
		
	}
	// search:getTypeStreamOr(int index, value)
	@Override
	public IntStream getIntStreamOr(int index, int[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return Arrays.stream(value);
			else
			try {
				return getIntStream(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeStreamOr(int index, value[]) methods");
				return null;
			}
				
		} else
			throw new UnsupportedOperationException("getTypeStreamOr(int index, value[]) is allowed only for array");
	}

	@Override
	public LongStream getLongStreamOr(int index, long[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return Arrays.stream(value);
			else
			try {
				return getLongStream(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeStreamOr(int index, value[]) methods");
				return null;
			}
				
		} else
			throw new UnsupportedOperationException("getTypeStreamOr(int index, value[]) is allowed only for array");
	}

	@Override
	public DoubleStream getDoubleStreamOr(int index, double[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return Arrays.stream(value);
			else
			try {
				return getDoubleStream(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeStreamOr(int index, value[]) methods");
				return null;
			}
				
		} else
			throw new UnsupportedOperationException("getTypeStreamOr(int index, value[]) is allowed only for array");
	}

	@Override
	public Stream<String> getStringStreamOr(int index, String[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return Arrays.stream(value);
			else
			try {
				return getStringStream(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeStreamOr(int index, value[]) methods");
				return null;
			}
				
		} else
			throw new UnsupportedOperationException("getTypeStreamOr(int index, value[]) is allowed only for array");
	}

	@Override
	public Stream<Json> getJsonStreamOr(int index, Json[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return Arrays.stream(value);
			else
			try {
				return getJsonStream(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeStreamOr(int index, value[]) methods");
				return null;
			}
				
		} else
			throw new UnsupportedOperationException("getTypeStreamOr(int index, value[]) is allowed only for array");
	}
	// search:getTypeOr(int index, value)
	@Override
	public byte getByteOr(int index, byte value) throws UnsupportedOperationException, JsonIllegalTypeException {
		return (byte)getDoubleOr(index, value);
	}

	@Override
	public short getShortOr(int index, short value) throws UnsupportedOperationException, JsonIllegalTypeException {
		return (short)getDoubleOr(index, value);
	}

	@Override
	public int getIntOr(int index, int value) throws UnsupportedOperationException, JsonIllegalTypeException {
		return (int)getDoubleOr(index, value);
	}

	@Override
	public long getLongOr(int index, long value) throws UnsupportedOperationException, JsonIllegalTypeException {
		return (long)getDoubleOr(index, value);
	}

	@Override
	public float getFloatOr(int index, float value) throws UnsupportedOperationException, JsonIllegalTypeException {
		return (float)getDoubleOr(index, value);
	}

	@Override
	public double getDoubleOr(int index, double value) throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getDouble(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getType(int index, value) methods");
				return Double.NaN;
			}
		} else
			throw new UnsupportedOperationException("getTypeOr(int index, value) is allowed only for array");
	}

	@Override
	public boolean getBooleanOr(int index, boolean value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getBoolean(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getType(int index, value) methods");
				return false;
			}
		} else
			throw new UnsupportedOperationException("getTypeOr(int index, value) is allowed only for array");
	}

	@Override
	public String getStringOr(int index, String value) throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getString(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getType(int index, value) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeOr(int index, value) is allowed only for array");
	}
	// search:getTypeArrayOr(int index, value[])
	@Override
	public byte[] getByteArrayOr(int index, byte[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getByteArray(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeArray(int index, value[]) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeArrayOr(int index, value[]) is allowed only for array");
	}

	@Override
	public short[] getShortArrayOr(int index, short[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getShortArray(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeArray(int index, value[]) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeArrayOr(int index, value[]) is allowed only for array");
	}

	@Override
	public int[] getIntArrayOr(int index, int[] value) throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getIntArray(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeArray(int index, value[]) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeArrayOr(int index, value[]) is allowed only for array");
	}

	@Override
	public long[] getLongArrayOr(int index, long[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getLongArray(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeArray(int index, value[]) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeArrayOr(int index, value[]) is allowed only for array");
	}

	@Override
	public float[] getFloatArrayOr(int index, float[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getFloatArray(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeArray(int index, value[]) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeArrayOr(int index, value[]) is allowed only for array");
	}

	@Override
	public double[] getDoubleArrayOr(int index, double[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getDoubleArray(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeArray(int index, value[]) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeArrayOr(int index, value[]) is allowed only for array");
	}

	@Override
	public boolean[] getBooleanArrayOr(int index, boolean[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getBooleanArray(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeArray(int index, value[]) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeArrayOr(int index, value[]) is allowed only for array");
	}

	@Override
	public String[] getStringArrayOr(int index, String[] value)
			throws UnsupportedOperationException, JsonIllegalTypeException {
		if(currentJson instanceof IJsonArray arr) {
			if(arr.list.size() <= index)
				return value;
			try {
				return getStringArray(index);
			}catch(JsonNoSuchPropertyException e) {
				System.err.println("Unexpected error on getTypeArray(int index, value[]) methods");
				return null;
			}
		} else
			throw new UnsupportedOperationException("getTypeArrayOr(int index, value[]) is allowed only for array");
	}
}
