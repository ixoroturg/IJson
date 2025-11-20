package ixoroturg.json;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

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
    public Json put(String key, String value) {
      return null;
    }

    @Override
    public Json add(String value) {
        return null;
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
    public boolean has(String key) throws UnsupportedOperationException {
    	if(currentJson instanceof IJsonObject) {
    		try {
    			return privateGet(new StringReader(key), key.length(), currentJson) != null;
    		} catch(JsonNoSuchPropertyException | JsonNoParentException e ) {
    			return false;
    		}
    	}
    	throw new UnsupportedOperationException("has(String) support only for object");
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
    public Json get(String key) throws JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException {
    	if(currentJson instanceof IJsonObject obj) {
    		IJsonEntry test = privateGet(new StringReader(key), key.length(), currentJson);
        	if(test == null)
        		throw new JsonNoSuchPropertyException("Property " +key+ " not found");
        	currentJson = test;
        	return this;
    	}
    	throw new UnsupportedOperationException("get(String) is allowed only for object");
    }
    
    @Override
    public Json get(int key) throws JsonNoSuchPropertyException {
    	if(currentJson instanceof IJsonArray arr) {
    		if(arr.list.size() > key)
    			throw new JsonNoSuchPropertyException("There is no element with index: "+key);
    		currentJson = arr.list.get(key);
    		return this;
    	}
    	throw new UnsupportedOperationException("get(int) is allowed only for array");
    }
    
    /**
     * @param reader - StringReader for String;
     * @param length - String length;
     * @param entry - recurse accumulator, currentJson by default;
     * 
     */
    private IJsonEntry privateGet(Reader reader, int length, IJsonEntry entry) throws JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException{
    		if(length == 0)
    			return entry;
    		try {
        		reader.mark(length);
//        		int newLenght = ;
        		for(int i = 0; true ; i++) {
        			char ch = (char)reader.read();
        			if(ch == '[' && IJsonSetting.USE_ARRAY_SYNTAX && i == 0) {
        				StringBuilder builder = new StringBuilder();
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
        							throw new JsonNoSuchPropertyException("Expected integer number, but found: "+"[]");
        						if(entry instanceof IJsonArray arr) {
        							entry = arr.list.get(parse);
        							length -= i;
        							length -= 3;
        							reader.skip(1);
        							break;
        						} else
        							throw new JsonNoSuchPropertyException("Expected integer number, but found: "+builder.toString());
        						
        					} else {
        						throw new JsonNoSuchPropertyException("Expected integer number, but found: "+builder.toString());
        					}
        						
        				}
        				return privateGet(reader,length,entry);
        			} else 
        			if(i == length - 1 || (IJsonSetting.KEY_DELIMETER != 0 && ch == IJsonSetting.KEY_DELIMETER) || (ch == '[' && IJsonSetting.USE_ARRAY_SYNTAX )) {
        				reader.reset();
        				char[] buf = new char[i];
        				length -= i;
        				reader.read(buf);
        				String newKey = new String(buf);
//        				entry = obj.map.get(newKey);
        				if(entry instanceof IJsonObject obj) {
        					entry = obj.map.get(newKey);
        					length--;
//        					reader.skip(1);
//        					break;
        				} else
        					throw new JsonNoSuchPropertyException("Property " +newKey+ " not found");
//        				if(entry == null)
//        					throw new JsonNoSuchPropertyException("Property " +newKey+ " not found");
        				return privateGet(reader,length,entry);
        			}
        		}
//        		if()
        	}catch(IOException e) {}
    	
    	throw new JsonNoSuchPropertyException("Unexpected error");
    }
//    private IJsonEntry privateMap(String key, IJsonEntry entry) throws JsonNoSuchPropertyException{
//    	if(entry instanceof IJsonObject obj) {
//    		entry = obj.map.get(key);
//    		if(entry == null)
//    			throw new JsonNoSuchPropertyException("Property " +key+ " not found");
//    	}
//    	throw new JsonNoSuchPropertyException("Property " +key+ " not found");
//    }
//    private IJsonEntry privateArray(int key, IJsonEntry entry) throws JsonNoSuchPropertyException{
//    	if(entry instanceof IJsonArray arr) {
//    		entry = arr.list.get(key);
//    		if(entry == null)
//    			throw new JsonNoSuchPropertyException("Property " +key+ " not found");
//    	}
//    	throw new JsonNoSuchPropertyException("Property " +key+ " not found");
//    }
    
    private IJsonEntry privateGet2(String key) throws JsonNoSuchPropertyException, JsonNoParentException, UnsupportedOperationException{
    	if(currentJson instanceof IJsonObject obj) {
    		IJsonEntry test = null;
    		if(IJsonSetting.KEY_DELIMETER == 0) {
    			test = obj.map.get(key);
    			if(test == null)
    				throw new JsonNoSuchPropertyException("Property " + key+ " not found");
    			return test;
    		}
        		int start = 0;
        		for(int i = 0; i < key.length(); i++) {
        			char ch = key.charAt(i);
        			if(ch == IJsonSetting.KEY_DELIMETER || i == key.length()-1) {
        				String getKey = key.substring(start,i);
        				if(getKey.length() == 2) {
        					if(getKey.charAt(0) == '\\' && IJsonSetting.PARENT_CHARACTER != 0 && getKey.charAt(1) == IJsonSetting.PARENT_CHARACTER) {
        						test = test.parent;
        						if(test == null)
        							throw new JsonNoParentException("This json has no parent");
        					} else
        						test = obj.map.get(getKey);
        						
        				} else {
        					if(getKey.charAt(getKey.length()-1) == ']') {
        						for(int j = getKey.length()-1; j > 0; j--) {
        							if(getKey.charAt(j) == '[') {
        								int parse = 0;
        								for(int k = j+1; k < getKey.length()-1; k++) {
        									ch = getKey.charAt(k);
        									if(ch >= '0' && ch <= '9')        										
        										parse = parse * 10 + ch - '0';
        									else
        										throw new JsonNoSuchPropertyException("Expected integer number, but found: "+getKey.substring(j+1,getKey.length()-1));
        								}
        								
        							}
        						}
        					} else {
        						test = obj.map.get(getKey);
        					}
        				}
        					
        				if(test == null)
							throw new JsonNoSuchPropertyException("Property " + getKey+ " not found");
        				start = i+1;
        			}
        			
        		}
        	return test;
    	}
    	throw new UnsupportedOperationException("get(String) support only for objects");
    }
}
