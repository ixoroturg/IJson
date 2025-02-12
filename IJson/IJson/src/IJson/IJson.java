package IJson;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class IJson implements Json, Cloneable, Iterable<Json>{
	private IJson parent;
	private int offset = 0;
	private Map<String, Json> map = new LinkedHashMap<String, Json>();
	private String json;
	private JsonType type = null;
	private LinkedList<Json> array = new LinkedList<Json>();
	
	public IJson(String json){
		this.json = json;
		proccess();
	}
	public IJson(){}
	public IJson(JsonType type) {
		this.type = type;
	}
	public IJson(Json[] jsons) {
		type = JsonType.array;
		array.addAll(Arrays.asList(jsons));
	}
	public void setMap(Map<String, Json> map){
		this.map = map;
	}
	private IJson(String json, IJson parent, int offset){
		this.json = json;
		this.offset = offset;
		this.offset = 0;
		this.parent = parent;
		proccess();
	}
	protected int getOffset() {
		return offset;
	}
	@Override
	public IJson back(){
		if(parent == null)
			throw new JsonNoParentException("This json has no parent",this,-1);
		return parent;
	}
	public String getRAWJson() {
		return json;
	}
	private void proccess(){
		try {
			format();
			if(type == JsonType.value || type == JsonType.string)
				return;
			
			if(type == JsonType.array) {
				int end = -1;
				for(int i = 1; i < json.length()-1; i++) {
					if(isOpenBracket(json.charAt(i))) {
						end = getCloseBracketIndex(i)+1;	
					} else {			
						end = isValidValue(i, false) + i;	
					}
					try {
						array.add(new IJson(json.substring(i,end), this, offset+i));
					}catch(Exception e) {
						JsonException e2 = new JsonException("Parse error\nIf cause is \"StringIndexOutOfBoundsException\" you propably get mistake in brakets at position: ",this,(offset+i));
						e2.initCause(e);
						throw e2;
					}
					i = end; 
					if( json.charAt(i) != ',' && !(i == json.length()-1 && json.charAt(i) == ']') ) {
						throw new JsonInvalidFormatException("Expect , but found "+ json.charAt(i) + " at position: ", this,(offset+i));	
					}
				}
				return;
			}
	
			String key = null;
			Json value = null;
			char ch = 0;
			for(int i = 1; i < json.length()-1; i++) {
				ch = json.charAt(i);
				if(ch != '\"') {
					throw new JsonInvalidFormatException("Expect \" for property name, but found " + ch+" at position: ",this , (offset+i));				
				}
				int end = getCloseBracketIndex(i);
				key = json.substring(i+1, end);
				i = end + 1; // propertyName"<-here	:<-here+1 in "propertyName":"value"
				if(json.charAt(i) != ':')
					throw new JsonInvalidFormatException("Expect : but found "+json.charAt(i)+" at position: ", this, (offset+i));
				i++; // :<-here " or { or [ <-here+1 in "propertyName":"value"
				if(!isOpenBracket(json.charAt(i))) {
					end = isValidValue(i, false) + i;		
					try {
						value = new IJson(json.substring(i,end), this, offset+i);
					}catch(Exception e) {
						JsonException e2 = new JsonException("Parse error\nIf cause is \"StringIndexOutOfBoundsException\" you propably get mistake in brakets at position: ",this,(offset+i));
						e2.initCause(e);
						throw e2;
					}	
					map.put(key, value);
					key = null;
					value = null;
					i = end; // value, [<- here]
					continue;
				}
				end = getCloseBracketIndex(i);
				value = new IJson(json.substring(i,end+1), this, offset+i);
				map.put(key, value);
				key = null;
				value = null;
				i = end + 1; // "..." or {...} or [...] [<- here] , [<- here+1] "[<- here+1 because "for"] propertyName			
			}
		}catch(Exception e) {
			if(e instanceof JsonParseException e2) {
				JsonException cause = (JsonException) e2.getCause();
				var e3 = new JsonParseException("Parse error at position: ",cause.getJson(), cause.getIndex());
				e3.initCause(cause);
				throw e3;
			}
			else {	
				var e2 =  new JsonParseException("Parse error at position: ",this,-1);//.initCause(e);
				e2.initCause(e);
				throw e2;
			}
		}
	}
	public void writeTo(OutputStream out) throws IOException {
		var writer = new BufferedOutputStream(out);
		writer.write(toString().getBytes());
		writer.flush();
		writer.close();
	}
	public Json readFrom(InputStream in) throws IOException{
		var reader = new BufferedInputStream(in);
		json = new String(reader.readAllBytes());
		reader.close();
		if(json.length() == 0)
			throw new JsonReadedNothingException("Readed null string from given InputStream");
		proccess();
		return this;
	}
	/**
	 * @param from 
	 * @return index of next char after value or -1 if value is invalid
	 */
	private int isValidValue(int from, boolean onlyOneValue) {
		int i = 0;
		String str = json.substring(from).trim();
		if(str.startsWith("true"))
			i = 4;
		if(str.startsWith("false"))
			i = 5;
		if(str.startsWith("null"))
			i = 4;
		if(i != 0) {
			if(i == str.length()) {
				if(onlyOneValue)
					return i;
				else 
					throw new JsonInvalidFormatException("Unexpected end of line at position: ",this,(offset+from+i));
			}
			return i;
		}	
		boolean wasDot = false;
		boolean wasExp = false;
		char ch;
		for(i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			boolean isDigit = (ch - '0' >= 0) && (ch - '9' <= 0);
			switch(ch) {
			case '-' -> {
				if(i != 0 && !(str.charAt(i-1) != 'e' ^ str.charAt(i-1) != 'E'))
					throw new JsonInvalidFormatException("Unexpected - at position: ",this,(offset+from+i));
				}
			case '0' -> {
				if(i == 0 && str.length() > 1 && !(str.charAt(i + 1) != '.' ^ !(str.charAt(i + 1) != 'e' ^ str.charAt(i + 1) != 'E')) )
					throw new JsonInvalidFormatException("Unexpected 0 at position: ",this,(offset+from+i));
				}
			case 'e' , 'E' -> {
				if(wasExp)
					throw new JsonInvalidFormatException("Unexpected "+ch+" at position: ",this,(offset+from+i));
				wasExp = true;
				}
			case '.' -> {
				if(wasDot)
					throw new JsonInvalidFormatException("Unexpected . at position: ",this,(offset+from+i));
				wasDot = true;
				}
			case '+' -> {
				if(str.length() < 2 || i == 0 || !(str.charAt(i-1) != 'e' ^ str.charAt(i-1) != 'E'))
					throw new JsonInvalidFormatException("Unexpected + at position: ",this,(offset+from+i));
			}
			default -> {
					if(isDigit) 
						continue;					
					if(!isCloseSymbol(ch))
						throw new JsonInvalidFormatException("Unexpected "+ch+" at position: ",this,(offset+from+i));
					if(i == 0)
						throw  new JsonInvalidFormatException("Empty value at position: ",this,(offset+from));
					return i;										
				}
			}	
		}
		
		if(onlyOneValue)
			return 1; //need just return non-zero
		else
			throw new JsonInvalidFormatException("Unexpected end of line at position: ",this,(offset+from+i));
	}
	public String formatString(String json) {
		json = json
			.replaceAll("\"", "\\\\\\\\\\\"")
			.replaceAll("\\\\", "\\\\\\\\")
			.replaceAll("/", "\\\\/")	
			.replaceAll("\b", "\\\\b")
			.replaceAll("\f", "\\\\f")
			.replaceAll("\n", "\\\\n")
			.replaceAll("\r", "\\\\r")
			.replaceAll("\t", "\\\\t")	
				;
		checkUnicodePoints(json, 0);
		return json;
	}
	private void checkUnicodePoints(String str, int offset) {
		int i = 0;
		String hex = "";
		
		try {
			for(; (i = str.indexOf("\\u", i)) != -1;i++) {
				hex = str.substring(i+2, i+6);
				Integer.parseInt(hex,16);
				
			}
		}catch(NumberFormatException e)	{
			throw new JsonInvalidFormatException("Expected \\uXXXX, where XXXX is 0-9 digit numbers, but found \\u"+hex+" at position: ",this,(offset+i));
		}catch(StringIndexOutOfBoundsException e) {
			throw new JsonInvalidFormatException("Expected \\uXXXX, where XXXX is 0-9 digit numbers, but found "+str.substring(i)+" at position: ",this,(offset+i));
		}
	}
	private boolean isCloseSymbol(char ch) {
		return switch(ch) {
		case '\"','}',']',',' -> true;
		default -> false;
		};
	}
	private void format(){
		json = json.trim();
		if(json.startsWith("{")) {
			type = JsonType.object;
		} else if (json.startsWith("[")) {
			type = JsonType.array;
		} else if (json.startsWith("\"")){
			type = JsonType.string;
			json = json.substring(1, json.length()-1);
			checkUnicodePoints(json, offset+1);
			return;
		} else {
			type = JsonType.value;
			try {
				isValidValue(0, true); //will throw exception is invalid
			}catch(JsonInvalidFormatException | StringIndexOutOfBoundsException e) {
				type = JsonType.string;
				checkUnicodePoints(json, offset+1); 
			}
				return;
		}
		char[] formattedJson = new char[(int)(json.length()*1.1)];
		int formattedJsonIndex = 0;
		
		
		
		for(int i = 0; i < json.length(); i++){
			char ch = json.charAt(i);
	try {
		
			if(ch == '\"' && isFunctionalQuote(i)) {
				
				int end = getCloseBracketIndex(i);
				for(int j = i; j <= end; j++) {
					char ch2 = json.charAt(j);
					switch(ch2) {
					case '{', '[', '}', ']' -> {
							if(j != 0 && json.charAt(j - 1) != '\\')
							formattedJson[formattedJsonIndex++] = '\\';
						}
					}
					formattedJson[formattedJsonIndex++] = ch2;
				}
				i = end;
				continue;
			}
	}catch(Exception e) {
		System.err.println("Ошибка в форматировании "+ ch+" "+i);
		e.printStackTrace();
		try {
			wait(100);
		} catch (InterruptedException e1) {}
		return;
	}
			if(!isSpaceSymbol(ch))
				formattedJson[formattedJsonIndex++] = ch;
		}
		json = new String(formattedJson).trim();
	}
	
	private boolean isFunctionalQuote(int index){
		if(index == 0)
			return true;
		return json.charAt(index-1) != '\\';
	}
	private boolean isOpenBracket(char bracket){
		return switch(bracket){
		case '[' -> true;
		case '{' -> true;
		case '"' -> true;
		default -> false;
		};
	}
	private char getCloseBracket(char bracket){
		return switch(bracket){
		case '[' -> ']';
		case '{' -> '}';
		case '"' -> '"';
		default -> (char)-1;
		};
	}
	private boolean isSpaceSymbol(char symbol){
		return switch(symbol){
		case ' ',	//spacebar
		'	',		//tab
		'\r',
		'\n' -> true;
		default -> false;
		};
	}
	private int getCloseBracketIndex(int index){
		char openBracket = json.charAt(index);
		if(openBracket == '\"'){
			int i;
			while(true){
				i = json.indexOf('\"', index+1);	
				if(i == -1)
					break;
				if(isFunctionalQuote(i))
					break;
				else {
					index = i;
				}
			}
			if(i == -1) {
				throw new JsonInvalidFormatException("Could not find close symbol for "+json.charAt(index)+" at posotion: ",this,(offset+index));		
			}
				return i;
		}
		char closeBracket = getCloseBracket(openBracket);
		int bracketCount = 0;
		for(int i = index+1; i < json.length(); i++){
			if(json.charAt(i) == openBracket && i != 0 && json.charAt(i - 1) != '\\')
				bracketCount++;				
			if(json.charAt(i) == closeBracket  && i != 0 && json.charAt(i - 1) != '\\')
				bracketCount--;
			if(bracketCount == -1)
				return i;
		}
		return -1;
	}
	@Override
	public Iterator<Json> iterator() {
		return array.iterator();
	}
	@Override
	public Json get() {
		return get(0);
	}

	@Override
	public Json get(int index) {
		return array.get(index);
	}
	@Override
	public boolean getBoolean(String propertyName) {
		boolean result = false;
		String prop = null;
		try {
		 prop = getString(propertyName);
		}catch(JsonWrongTypeException e) {
			throw new JsonWrongTypeException("This is not a boolean",get(propertyName),-1);
		}
		result = prop.equals("true") ? true : false;
		if(!result && !prop.equals("false"))
			throw new JsonWrongTypeException("This is not a boolean",this,-1);
		return result;
	}
	@Override
	public float getFloat(String propertyName) {
		float result = 0;
		try {
			result = Float.parseFloat(get(propertyName).toString());
		} catch (NumberFormatException e){
			throw new JsonWrongTypeException("This is not a float",this,-1);
		}
		return result;
	}
	@Override
	public double getDouble(String propertyName) {
		double result = 0;
		try {
			result = Double.parseDouble(get(propertyName).toString());
		} catch (NumberFormatException e){
			throw new JsonWrongTypeException("This is not a Double",this,-1);
		}
		return result;
	}
	@Override
	public byte getByte(String propertyName) {
		byte result = 0;
		try {
			result = Byte.parseByte(get(propertyName).toString());
		} catch (NumberFormatException e){
			throw new JsonWrongTypeException("This is not a byte",this,-1);
		}
		return result;
	}
	@Override
	public short getShort(String propertyName) {
		short result = 0;
		try {
			result = Short.parseShort(get(propertyName).toString());
		} catch (NumberFormatException e){
			throw new JsonWrongTypeException("This is not a short",this,-1);
		}
		return result;
	}
	@Override
	public int getInt(String propertyName) {
		int result = 0;
		try {
			result = Integer.parseInt(get(propertyName).toString());
		} catch (NumberFormatException e){
			throw new JsonWrongTypeException("This is not an int",this,-1);
		}
		return result;
	}
	@Override
	public long getLong(String propertyName) {
		long result = 0;
		try {
			result = Long.parseLong(get(propertyName).toString());
		} catch (NumberFormatException e){
			throw new JsonWrongTypeException("This is not a long",this,-1);
		}
		return result;
	}
	@Override
	public String getString(String propertyName){
		IJson tmp = (IJson)get(propertyName);
		if(tmp.type == JsonType.value) {
			return tmp.json;
		}
		if(tmp.type == JsonType.object || tmp.type == JsonType.array) {
			throw new JsonWrongTypeException("This is not a string",tmp,-1);
		}
		String result = tmp.toString();
		for(int i = 0; i < result.length(); i++) {
			char ch = result.charAt(i);
			if(ch == '{' || ch == '[' || ch == '}' || ch == ']')
				if(i != 0 && result.charAt(i-1) == '\\')
					result = result.substring(0,i-1) + result.substring(i);
					
		}
		
		result = result.translateEscapes();
		
		int i = 0;
		String hex = "";
		try {
			for(; (i = result.indexOf("\\u", i)) != -1;) {
				hex = result.substring(i+2, i+6);
				char ch = (char)Integer.parseInt(hex,16);
				result = result.substring(0, i)+ch+result.substring(i+6);
				i=0;
			}
		}catch(NumberFormatException e)	{
			throw new JsonInvalidFormatException("Expected \\uXXXX, where XXXX is 0-9 digit numbers, but found \\u"+hex+" at posotion: ",this,(offset+i));
		}
			return result.substring(1, result.length()-1);
	}
	@Override
	public String toString() {
		if(type == JsonType.value)
			return json;
		if(type == JsonType.string) {
			return "\""+json+"\"";
		}
		if(type == JsonType.array) {
			String result = "";
			for(Json js: array) {
				result += ","+js.toString();
			}
			if(result.length() == 0)
				return "[]";
			return "["+result.substring(1)+"]";
		}
		String result = "";
		for(String key: map.keySet()) {
			result += ",\""+key+"\":"+map.get(key).toString();
		}
		if(result.length() == 0)
			return "{}";
		return "{"+result.substring(1)+"}";
	}
	@Override
	public int size() {	
		return map.size() + array.size();
	}
	@Override
	public boolean isEmpty() {
		return map.isEmpty() && array.isEmpty();
	}
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value) || array.contains(value);
	}
	@Override
	public Json get(Object key) {
		if(type != JsonType.object) 
			throw new JsonIllegalTypeException("get() is able only for objects, this json is "+type ,this,-1);
		IJson result = (IJson) map.get(key);
		if(result == null){
			throw new JsonNoSuchPropertyException("Could not find a property with key \""+key+"\"",this,-1);
		}		
		return result;
	}
	@Override
	public Json remove(Json json) {
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("remove(Json) is able only for arrays, this json is "+type,this,-1);
		array.remove(json);
		return this;
	}
	@Override
	public Json add(Json json) {
		if(type == null)
			type = JsonType.array;
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("add() is able only for arrays, this json is "+type,this,-1);
		array.add(json);
		return this;
	}
	@Override
	public Json add(String json) {
		if(json == null)
			json = "null";
		if(type == null)
			type = JsonType.array;
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("add() is able only for arrays",this,-1);
		array.add(new IJson(json));
		return this;
	}
	
	@Override
	public Json put(String key, Json value) {
		if(type == null)
			type = JsonType.object;
		map.put(key,value);
		return this;
	}
	public Json put(String key, String value) {
		if(value == null)
			value = "null";
		if(type == null)
			type = JsonType.object;
		int offset = 0;
		if(json != null)
			offset = json.length()-2;
		map.put(key, new IJson(value, this, offset));
		return this;
	}
	@Override
	public Json remove(String key) {
		if(type != JsonType.object)
			throw new JsonIllegalTypeException("remove(String) is able only for objects, this json is "+type,this,-1);
		map.remove(key);
		return this;
	}
	@Override
	public void putAll(Map<? extends String, ? extends Json> m) {
		map.putAll(m);
	}
	@Override
	public void clear() {
		map.clear();
		array.clear();
	}
	@Override
	public Set<String> keySet() {
		return map.keySet();
	}
	@Override
	public Collection<Json> values() {
		return map.values();
	}
	@Override
	public Set<Entry<String, Json>> entrySet() {
		return map.entrySet();		
	}
	@Override
	public Json remove(Object key) {
		map.remove(key);
		return this;
	}
	@Override
	public Json put(String key, boolean value) {
		map.put(key, new IJson(String.valueOf(value)));
		return this;
	}
	@Override
	public Json put(String key, long value) {
		map.put(key, new IJson(String.valueOf(value)));
		return this;
	}
	@Override
	public Json put(String key, double value) {
		map.put(key, new IJson(String.valueOf(value)));
		return this;
	}
	@Override
	public Json add(boolean value) {
		array.add(new IJson(String.valueOf(value)));
		return this;
	}
	@Override
	public Json add(long value) {
		array.add(new IJson(String.valueOf(value)));
		return this;
	}
	@Override
	public Json add(double value) {
		array.add(new IJson(String.valueOf(value)));
		return this;
	}
}
