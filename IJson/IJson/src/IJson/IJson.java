package IJson;
import java.io.*;
import java.util.*;
import java.util.stream.*;
public class IJson implements Json, Cloneable, Iterable<Json>{
	IJson parent;
	private int offset = 0;
	private Map<String, Json> map = new LinkedHashMap<String, Json>();
	private String json;
	private JsonType type = null;
	private LinkedList<Json> array = new LinkedList<Json>();
	private List<String> separator = new ArrayList<String>(List.of(".","/"));
	private String PropertyName = null;
	
	private IJson(String json, int offset){
		this.json = json;
		proccess();
	}
	
	public IJson(Map<String,String> map) {
		type = JsonType.object;
		map.forEach((key,value) -> {
			Json val = new IJson(value,0);
			this.map.put(key, val);
			((IJson)val).setParent(this);
		});
	}
	
	public IJson(byte json) {
		type = JsonType.value;
		this.json = String.valueOf(json);
		proccess();
	}
	public IJson(short json) {
		type = JsonType.value;
		this.json = String.valueOf(json);
		proccess();
	}
	public IJson(int json) {
		type = JsonType.value;
		this.json = String.valueOf(json);
		proccess();
	}
	public IJson(long json) {
		type = JsonType.value;
		this.json = String.valueOf(json);
		proccess();
	}
	public IJson(float json) {
		type = JsonType.value;
		this.json = String.valueOf(json);
		proccess();
	}
	public IJson(double json) {
		type = JsonType.value;
		this.json = String.valueOf(json);
		proccess();
	}
	public IJson(boolean json) {
		type = JsonType.value;
		this.json = String.valueOf(json);
		proccess();
	}
	public IJson(char json) {
		type = JsonType.value;
		this.json = String.valueOf(json);
		proccess();
	}
	
	
	public IJson(byte[] array) {
		type = JsonType.array;
		add(array);
	}
	public IJson(short[] array) {
		type = JsonType.array;
		for(short value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
	}
	public IJson(int[] array) {
		type = JsonType.array;
		for(int value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
	}
	public IJson(long[] array) {
		type = JsonType.array;
		for(long value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
	}
	public IJson(float[] array) {
		type = JsonType.array;
		for(float value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
	}
	public IJson(double[] array) {
		type = JsonType.array;
		for(double value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
	}
	public IJson(boolean[] array) {
		type = JsonType.array;
		for(boolean value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
	}
	public IJson(char[] array) {
		type = JsonType.array;
		for(char value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
	}
	public <T> IJson(T[] array) {
		type = JsonType.array;
		for(T value: array) {
			Json val = new IJson(String.valueOf(value));
			this.array.add(val);
			((IJson)val).setParent(this);
		}
	}
	public <T> IJson(T object) {
		type = JsonType.value;
		Json val = new IJson(object.toString());
		this.array.add(val);
		((IJson)val).setParent(this);
	}
	
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
	protected int getOffset() {
		return offset;
	}
	@Override
	public Json back(int level) {
		Json result = this;
		if(level == 0) {
			while(((IJson)result).parent != null)
				result =  result.back();
			return result;
		}
		while(level-- > 0)
			result = result.back();
		return result;
	}
	@Override
	public Json back(){
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
						Json js = new IJson(json.substring(i,end), offset+i);
						array.add(js);
						((IJson)js).setParent(this);
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
						value = new IJson(json.substring(i,end), offset+i);
						
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
				value = new IJson(json.substring(i,end+1), offset+i);
				map.put(key, value);
				((IJson)value).setParent(this);
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
				if( (i != 0 && !(str.charAt(i-1) != 'e' ^ str.charAt(i-1) != 'E')) || i == str.length()-1 || !( (str.charAt(i+1) - '0' >= 0) && (str.charAt(i+1)  - '9' <= 0)) )
					throw new JsonInvalidFormatException("Unexpected - at position: ",this,(offset+from+i));
				
				}
			case '0' -> {
				if(i == 0 && str.length() > 1 && !(str.charAt(i + 1) != '.' ^ !(str.charAt(i + 1) != 'e' ^ str.charAt(i + 1) != 'E')) )
					throw new JsonInvalidFormatException("Unexpected 0 at position: ",this,(offset+from+i));
				}
			case 'e' , 'E' -> {
				if(wasExp || i == 0)
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
		if(json == null) {
			json = "null";
			type = JsonType.value;
			return;
		}
			
			
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
				isValidValue(0, true); //will throw exception if is invalid
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
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("this json is not array",this,-1);
		return array.iterator();
	}
	@Override
	public Json get() {
		return get(0);
	}
	private IJson setParent(IJson parent) {
		this.parent =  parent;
		String name = null;
		if(parent.type == JsonType.object) {
			for(String key: parent.map.keySet()){
				if(parent.map.get(key).equals(this)) {
					name = key;
					break;
				}
			}
		}
		if(parent.type == JsonType.array) {
			for(int i = 0; i < parent.array.size(); i++) {
				if(parent.array.get(i).equals(this)) {
					name = i+"";
					break;
				}
			}
		}
		PropertyName = name;
		return this;
	}
	@Override
	public Json get(int index) {
		try {
			if(type == JsonType.array)
				return array.get(index);
		}catch(IndexOutOfBoundsException e) {
			throw new JsonArrayIndexOutOfBoundsException("index: "+index+", size: "+array.size(),this,-1);
		}
		if(type == JsonType.object) {
			var iter = map.keySet().iterator();
			int i = index;
			String key = null;
			while(i-- >= 0)
				if(!iter.hasNext())
					throw new JsonArrayIndexOutOfBoundsException("no such index "+index,this,-1);
				else 
					key = iter.next();
			return map.get(key);
		}
		throw new JsonIllegalTypeException("this json is not array or object",this,-1);
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
				if(js == null)
					result += ",null";
				else
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
		if(type == null)
			return true;
		if(type == JsonType.array)
			return array.isEmpty();
		if(type == JsonType.object)
			return map.isEmpty();
		throw new JsonIllegalTypeException("this json is not array or object",this,-1);
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
		patternCheck:if(key instanceof String pattern) {
			String sep = null;
			for(String s: separator) {
				if(pattern.indexOf(s) != -1) {
					sep = s;
					break;
				}
			}
			if(sep == null)
				break patternCheck;
			if(sep.equals("."))
				sep = "\\"+sep;
			String[] arr = pattern.split(sep);
			Json result = this;
			for(String k: arr) {
				if(!result.containsKey(k)) {
					try {
						int keyIndex = Integer.parseInt(k);
						result = result.get(keyIndex);
						continue;
					}catch(NumberFormatException e) {
						System.err.println(result);
						throw new JsonNoSuchPropertyException("no such property or array index \""+k+"\"",result,-1);
					}
				}
				result = result.get(k);
			}
			return result;
		}
		
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
		
		if(json == null)
			json = new IJson("null");
		array.add(json);
		((IJson)json).setParent(this);
		return this;
	}
	@Override
	public Json add(String json) {
		return add(new IJson(json));
	}
	
	private record PutObj(String key, Json json) {};
	
	
	private PutObj superPut(String key) {

		pattern:if(key instanceof String pattern) {
			String sep = null;
			for(String s: separator) {
				if(pattern.indexOf(s) != -1) {
					sep = s;
					break;
				}
			}
			if(sep == null)
				break pattern;
			if(sep.equals("."))
				sep = "\\"+sep;
			String[] arr = pattern.split(sep);
			Json result = this;
			for(int i = 0; i < arr.length - 1; i++) {
				String k = arr[i];
				if(!result.containsKey(k)) {
					try {
						int keyIndex = Integer.parseInt(k);
						result = result.get(keyIndex);
						continue;
					}catch(NumberFormatException e) {;
						throw new JsonInvalidFormatException("no such property or array index \""+k+"\"",result,-1);
					}
				}
				result = result.get(k);
			}
			return new PutObj(arr[arr.length-1],result);
		}
		return null;
	}
	
	@Override
	public Json put(String key, Json value) {
		PutObj js;
		if( (js = superPut(key) ) != null) {
			js.json.put(js.key,value);
			return this;
		}
		
		if(type == null)
			type = JsonType.object;
		if(type != JsonType.object)
			throw new JsonIllegalTypeException("put() is able only for objects, this json type is "+type,this,-1);
		
		if(value == null)
			value = new IJson("null");
		map.put(key, value);
		((IJson)value).setParent(this);
		return this;
	}
	public Json put(String key, String value) {
		return put(key, new IJson(value));
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
//		if(type == JsonType.array)
//			return array.
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
		return put(key, new IJson(value));
	}
	@Override
	public Json put(String key, long value) {
		return put(key, new IJson(value));
	}
	@Override
	public Json put(String key, double value) {
		return put(key, new IJson(value));
	}
	@Override
	public Json add(boolean value) {
		return add(new IJson(value));
	}
	@Override
	public Json add(long value) {
		return add(new IJson(value));
	}
	@Override
	public Json add(double value) {
		return add(new IJson(value));
	}
	@Override
	public boolean equals(Object com) {
		
		IJson js = (IJson)com;
		
		if(type != js.type)
			return false;
		
		if(type == null) {
			return true;
		}
		
		return switch(type) {
			case JsonType.object -> {
				var key1 = map.keySet();
				var key2 = js.map.keySet();
				if(key1.size() != key2.size())
					yield false;
				
				var iter1 = key1.iterator();
				var iter2 = key2.iterator();
				while(iter1.hasNext()) 
					if(!iter1.next().equals(iter2.next()))
						yield false;
				
				iter1 = key1.iterator();
				iter2 = key2.iterator();
				while(iter1.hasNext())
					if(! map.get(iter1.next()).equals(js.map.get(iter2.next())) )
						yield false;		
				yield true;
			}
			case JsonType.array -> {
				
				if(array.size() != js.array.size())
					yield false;
				
				for(int i = 0; i < this.array.size(); i++)					
					if(!array.contains(js.array.get(i)))
						yield false;
				
				yield true;
			}
			case JsonType.string, JsonType.value -> json.equals(js.json);
		};
	}
	@Override
	public Json add(Json[] array) {
		if(type == null)
			type = JsonType.array;
		for(Json js: array){
			this.array.add(js);
			if(js instanceof IJson j)
				j.setParent(this);
		}
		return this;
	}
	@Override
	public Json add(String[] array) {
		if(type == null)
			type = JsonType.array;
		for(String js: array){
			Json val = new IJson(js,offset);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	
	@Override
	public <T> Json add(T[] array) {
		if(type == null)
			type = JsonType.array;
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("add() is able only for arrays, this json is "+type,this,-1);
		for(T value: array) {
			Json val = new IJson(String.valueOf(value),offset);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	
	private void checkArray() {
		if(type == null)
			type = JsonType.array;
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("add() is able only for arrays, this json is "+type,this,-1);
	}
	private void checkStream() {
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("getAnyStream() is able only for arrays, this json is "+type,this,-1);
	}
	@Override
	public Json add(byte[] array) {
		if(array == null)
			return add((IJson)null);
		checkArray();
		for(byte value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	@Override
	public Json add(short[] array) {
		checkArray();
		if(array == null)
			return add((IJson)null);
		for(short value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	@Override
	public Json add(int[] array) {
		if(array == null)
			return add((IJson)null);
		checkArray();
		for(int value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	@Override
	public Json add(long[] array) {
		if(array == null)
			return add((IJson)null);
		checkArray();
		for(long value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	@Override
	public Json add(float[] array) {
		if(array == null)
			return add((IJson)null);
		checkArray();
		for(float value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	@Override
	public Json add(double[] array) {
		if(array == null)
			return add((IJson)null);
		checkArray();
		for(double value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	@Override
	public Json add(char[] array) {
		if(array == null)
			return add((IJson)null);
		checkArray();
		for(char value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	@Override
	public Json add(boolean[] array) {
		if(array == null)
			return add((IJson)null);
		checkArray();
		for(boolean value: array) {
			Json val = new IJson(value);
			this.array.add(val);
			((IJson)val).setParent(this);
		}
		return this;
	}
	
	@Override
	public byte[] getByteArray() {
		checkArray();
		List<Json> list = array.stream().filter(js -> !js.equals("null")).toList();
		byte[] result = new byte[list.size()];
		int i = 0;
		for(Json value: list) {
			result[i++] = Byte.parseByte(value.toString());
		}
		return result;
	}
	@Override
	public short[] getShortArray() {
		checkArray();
		List<Json> list = array.stream().filter(js -> !js.equals("null")).toList();
		short[] result = new short[list.size()];
		int i = 0;
		for(Json value: list) {
			result[i++] = Short.parseShort(value.toString());
		}
		return result;
	}
	@Override
	public int[] getIntArray() {
		checkArray();
		List<Json> list = array.stream().filter(js -> !js.equals("null")).toList();
		int[] result = new int[list.size()];
		int i = 0;
		for(Json value: list) {
			result[i++] = Integer.parseInt(value.toString());
		}
		return result;
	}
	@Override
	public long[] getLongArray() {
		checkArray();
		List<Json> list = array.stream().filter(js -> !js.equals("null")).toList();
		long[] result = new long[list.size()];
		int i = 0;
		for(Json value: list) {
			result[i++] = Long.parseLong(value.toString());
		}
		return result;
	}
	@Override
	public float[] getFloatArray() {
		checkArray();
		List<Json> list = array.stream().filter(js -> !js.equals("null")).toList();
		float[] result = new float[list.size()];
		int i = 0;
		for(Json value: list) {
			result[i++] = Float.parseFloat(value.toString());
		}
		return result;
	}
	@Override
	public double[] getDoubleArray() {
		checkArray();
		List<Json> list = array.stream().filter(js -> !js.equals("null")).toList();
		double[] result = new double[list.size()];
		int i = 0;
		for(Json value: list) {
			result[i++] = Double.parseDouble(value.toString());
		}
		return result;
	}
	@Override
	public boolean[] getBooleanArray() {
		checkArray();
		List<Json> list = array.stream().filter(js -> !js.equals("null")).toList();
		boolean[] result = new boolean[list.size()];
		int i = 0;
		for(Json value: list) {
			result[i++] = Boolean.parseBoolean(value.toString());
		}
		return result;
	}
	@Override
	public String[] getStringArray() {
		checkArray();
		String[] result = new String[array.size()];
		int i = 0;
		for(Json value: array) {
			result[i] = value.toString();
			if(result[i].startsWith("\"")) {
				result[i] = result[i].substring(1, result[i].length()-1);
			}
			i++;
		}
		return result;
	}
	@Override
	public Json[] getJsonArray() {
		checkArray();
		Json[] result = new Json[array.size()];
		int i = 0;
		for(Json value: array) {
			result[i++] = value;
		}
		return result;
	}
	
	@Override
	public IntStream getIntStream() {
		checkStream();
		return Arrays.stream(getIntArray());
	}
	@Override
	public LongStream getLongStream() {
		checkStream();
		return Arrays.stream(getLongArray());
	}
	@Override
	public DoubleStream getDoubleStream() {
		checkStream();
		return Arrays.stream(getDoubleArray());
	}
	@Override
	public Stream<String> getStringStream() {
		checkStream();
		return Arrays.stream(getStringArray());
	}
	@Override
	public Stream<Json> getJsonStream() {
		checkStream();
		return Arrays.stream(getJsonArray());
	}
	@Override
	public byte[] toBytes() {
		return toString().getBytes();
	}
	@Override
	public Json put(String key, byte[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, short[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, int[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, long[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, float[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, double[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, char[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, boolean[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, String[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public Json put(String key, Json[] array) {
		return put(key, new IJson(array));
	}
	@Override
	public byte[] getByteArray(String key) {
		return get(key).getByteArray();
	}
	@Override
	public short[] getShortArray(String key) {
		return get(key).getShortArray();
	}
	@Override
	public int[] getIntArray(String key) {
		return get(key).getIntArray();
	}
	@Override
	public long[] getLongArray(String key) {
		return get(key).getLongArray();
	}
	@Override
	public float[] getFloatArray(String key) {
		return get(key).getFloatArray();
	}
	@Override
	public double[] getDoubleArray(String key) {
		return get(key).getDoubleArray();
	}
	@Override
	public boolean[] getBooleanArray(String key) {
		return get(key).getBooleanArray();
	}
	@Override
	public String[] getStringArray(String key) {
		return get(key).getStringArray();
	}
	@Override
	public Json[] getJsonArray(String key) {
		return get(key).getJsonArray();
	}
	
	@Override
	public Json add(String key, byte[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, short[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, int[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, long[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, float[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, double[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, char[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, boolean[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public <T> Json add(String key, T[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, String[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, Json[] array) {
		get(key).add(array);
		return this;
	}
	@Override
	public Json add(String key, Json json) {
		get(key).add(json);
		return this;
	}
	@Override
	public Json add(String key, boolean value) {
		get(key).add(value);
		return this;
	}
	@Override
	public Json add(String key, long value) {
		get(key).add(value);
		return this;
	}
	@Override
	public Json add(String key, double value) {
		get(key).add(value);
		return this;
	}
	@Override
	public Json add(String key, String value) {
		get(key).add(value);
		return this;
	}
	@Override
	public Json parseHttpRequest(String request) {
		if(type == null)
			type = JsonType.object;
		if(type != JsonType.object)
			throw new JsonIllegalTypeException("this json is not object and you will lose all previous data, use parseHttpRequest(request, true) to force this action",this,-1);
		if(request == null || request.indexOf("=") == -1)
			return this;
		String[] entries = request.trim().split("&");
		for(String entry: entries) {
			String key = entry.split("=")[0];
			Json value = new IJson(entry.split("=")[1],0);
			map.put(key, value);
			((IJson)value).setParent(this);
		}
		return this;
	}
	@Override
	public Json parseHttpRequestForce(String request) {
		setType(JsonType.object);
			return parseHttpRequest(request);
	}
	@Override
	public Json setType(JsonType type) {
		this.type = type; 
		return this;
	}
	@Override
	public Json putValue(String key, String value) {		
		String tmp = json;
		json = value;
		isValidValue(0,true);
		json = tmp;
		return put(key,value);
	}
	@Override
	public String getPropertyName() {
		if(PropertyName == null)
			throw new JsonNoParentException("this json has no parent to get it property name",this,-1);
		return PropertyName;
	}
	@Override
	public String getPropertyNameOrNull() {
		return PropertyName;
	}
	@Override
	public IntStream getIntStream(String key) {
		return get(key).getIntStream();
	}
	@Override
	public LongStream getLongStream(String key) {
		return get(key).getLongStream();
	}
	@Override
	public DoubleStream getDoubleStream(String key) {
		return get(key).getDoubleStream();
	}
	@Override
	public Stream<String> getStringStream(String key) {
		return get(key).getStringStream();
	}
	@Override
	public Stream<Json> getJsonStream(String key) {
		return get(key).getJsonStream();
	}
	@Override
	public boolean equals(String json) {
		return equals(new IJson(json));
	}
	@Override
	public Json putString(String key, String value) {
		return put(key,new IJson("\""+value+"\""));
	}
}
