package IJson;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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
	private JsonType type;
	private LinkedList<Json> array = new LinkedList<Json>();
	
	public IJson(String json){
		//if(json.length() == 0)
			//throw new JsonInvalidFormatException("Given null string");
		this.json = json;
		proccess();
	}
	public IJson(){
		type = JsonType.object;
	}
	public IJson(JsonType type) {
		this.type = type;
	}
	public IJson(Json[] jsons) {
		type = JsonType.array;
		array.addAll(Arrays.asList(jsons));
		//System.out.println();
	}
	public void setMap(Map<String, Json> map){
		this.map = map;
	}
	private IJson(String json, IJson parent, int offset){
		this.json = json;
		this.offset = offset;
		this.parent = parent;
		//if(json.length() == 0)
			//throw new JsonInvalidFormatException("Unexcepted null string at position");
		//System.out.println(offset+ " "+this.json);
		proccess();
		//System.out.println("Cоздано: "+this.offset);
	}
	protected int getOffset() {
		return offset;
	}
	@Override
	public IJson back(){
		return parent;
	}
	public String getRAWFormattedJson() {
		return json;
	}
	private void proccess(){
		format();
		
		//System.out.println(offset+" "+json);
		//System.out.println(offset;
		
		if(type == JsonType.value || type == JsonType.string)
			return;
		if(type == JsonType.array) {
			int end = -1;
			for(int i = 1; i < json.length()-1; i++) {
				if(isOpenBracket(json.charAt(i))) {
					end = getCloseBracketIndex(i)+1;	
				} else {
					end = isValidValue(i, false);				
				}
				array.add(new IJson(json.substring(i,end), this, offset+i));
				i = end; 
				if( json.charAt(i) != ',' && !(i == json.length()-1 && json.charAt(i) == ']') ) {
					//System.out.println(json);
					//System.out.println(json.length()-1+" "+i);
					//System.out.println((json.charAt(i) == ']') +" " +(i != json.length()-1)+"");
					
					throw new JsonInvalidFormatException("Expect , but found "+ json.charAt(i) + " at position: "+(offset+i));
					
				}
			}
			return;
		}
			
		
		String key = null;
		Json value = null;
		char ch = 0;
		for(int i = 1; i < json.length()-1; i++) {
			
			//int index = findNextQuote(i);
			
			ch = json.charAt(i);
			//if()
			
			if(ch != '\"') {
				//System.out.println(json.substring(i-10));
				throw new JsonInvalidFormatException("Expect \" for property name, but found " + ch+" at position: "+(offset+i));				
			}
			int end = getCloseBracketIndex(i);
			key = json.substring(i+1, end);
			i = end + 1; // propertyName"<-here	:<-here+1 in "propertyName":"value"
			if(json.charAt(i) != ':')
				throw new JsonInvalidFormatException("Expect : but found "+json.charAt(i)+" at position: "+(offset+i));
			i++; // :<-here " or { or [ <-here+1 in "propertyName":"value"
			if(!isOpenBracket(json.charAt(i))) {
				end = isValidValue(i, false) + i;
				
				//System.out.println(json.substring(i));
				//System.out.println(end);
				//System.out.println("Создание value с offset: "+(offset+i));
				value = new IJson(json.substring(i,end), this, offset+i);
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
		
		/*if(!isOpenBracket(json.charAt(0)))
			return;
		int elementNumber = 0;
		for(int i = 1; i < json.length(); i++){
			String key;
			IJson value;
			if(isOpenBracket(json.charAt(i))){
				if(json.charAt(i) == '"'){
					key = json.substring(i+1, i = getCloseBracketIndex(i));
					i++;
					if(json.charAt(i) == ','){
						value = new IJson(key, this);
						key = "\r"+elementNumber+"";
						map.put(key, value);
						elementNumber++;
						continue;
					} else
						i++;
					// ":" symbol and next bracket;	
					value = new IJson(json.substring(i, (i = getCloseBracketIndex(i))+1), this);
					map.put(key, value);
					elementNumber++;
					i++;				
				} else {
					key = "\r"+elementNumber+"";
					value = new IJson(json.substring(i, i = getCloseBracketIndex(i)+1), this);
					map.put(key, value);
					elementNumber++;
				}
			}	
		}*/
	}
	
	
	/*private int findNextQuote(int i) {
		while(true){
			i = json.indexOf('"', i);
			if(i == -1)
				throw new JsonInvalidFormatException("Could not find close symbol for "+json.charAt(i)+" at posotion: "+(offset+i));
			if(isFunctionalQuote(i))
				break;
			else {
				i++;
			}
		}
		return i;
	}*/
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
	 * 
	 * @param from 
	 * @return index of next char after value or -1 if value is invalid
	 */
	private int isValidValue(int from, boolean onlyOneValue) {
		int i = 0;
		String str;
		if(onlyOneValue)
			str = json;
		else
			str = json.substring(from);
		//if(str.length() == 0)
			//throw new JsonInvalidFormatException("Empty strin");
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
					throw new JsonInvalidFormatException("Unexpected end of line");
			}
			return i;
		}
		
		
		boolean wasDot = false;
		boolean wasExp = false;
		char ch;
		for(i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if(ch == '-') {
				if(i != 0) {
					if (str.charAt(i-1) != 'e' || str.charAt(i-1) != 'E')
						throw new JsonInvalidFormatException("Unexpected - at position: "+(offset+from+i));
				}
				continue;
			}
			if(ch == '+') {
				if (i == 0 || str.charAt(i-1) != 'e' || str.charAt(i-1) != 'E')
					throw new JsonInvalidFormatException("Unexpected + at position: "+(offset+from+i));
				continue;
			}
			if(ch == '.') {
				if(!wasDot) {
					wasDot = true;
					continue;
				}
					
				else throw new JsonInvalidFormatException("Unexpected . at position: "+(offset+from+i));
			}
			if(ch == 'e' || ch == 'E') {
				if(!wasExp) {
					wasExp = true;
					continue;
				}
					
				else throw new JsonInvalidFormatException("Unexpected "+ch+" at position: "+(offset+from+i));
			}
			if(isCloseSymbol(ch)) {
				if(i == 0)
					throw  new JsonInvalidFormatException("Empty value at position: "+(offset+from));
				return i;
			}
				
			if(ch == '0') {
				if(i < 2) {
					switch(str.charAt(i+1)) {
						case '.','e','E' -> {continue;}
						default -> {throw new JsonInvalidFormatException("Unexpected 0 at position: "+(offset+from+i));}
					}
				}
			}
			// 0 was tested in previous case
			//'1'(49) - '0'(48) > 0 it's true, 0 and below will <=0
			//'9'(57) - '10'(58) < 0 it's true, above 9 will >= 0
			if(ch - '0' > 0 && ch - ('9'+1) < 0)
				continue;			
			//System.out.println(str.substring(i));
			//System.out.println(type);
			//System.out.println(offset);
			throw new JsonInvalidFormatException("Unexpected "+ch+" at position: "+(offset+from+i));
		}
		if(onlyOneValue)
			return 1; //need just return non-zero
		else
			throw new JsonInvalidFormatException("Unexpected end of line");
	}
	
	public static String formatString(String json) {
		json = json
			.replaceAll("\"", "\\\\\\\\\\\"")
			.replaceAll("\\\\", "\\\\\\\\")
			.replaceAll("/", "\\\\/")	
			.replaceAll("\b", "\\\\b")
			.replaceAll("\f", "\\\\f")
			.replaceAll("\n", "\\\\n")
			.replaceAll("\r", "\\\\r")
			.replaceAll("\t", "\\\\t");
			
		checkUnicodePoints(json, 0);
		return json;
	}
	private static void checkUnicodePoints(String str, int offset) {
		int i = 0;
		String hex = "";
		
		try {
			for(; (i = str.indexOf("\\u", i)) != -1;i++) {
				//System.out.println(str.substring(i));
				hex = str.substring(i+2, i+6);
				Integer.parseInt(hex,16);
				
			}
		}catch(NumberFormatException e)	{
			throw new JsonInvalidFormatException("Expected \\uXXXX, where XXXX is 0-9 digit numbers, but found \\u"+hex+" at position: "+(offset+i));
		}catch(StringIndexOutOfBoundsException e) {
			throw new JsonInvalidFormatException("Expected \\uXXXX, where XXXX is 0-9 digit numbers, but found "+str.substring(i)+" at position: "+(offset+i));
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
			isValidValue(0, true); //will throw exception is invalid
			return;
		}
		//System.out.println(json);
		
		//boolean needWrite = false;
		//int changeIndex = 0;
		char[] formattedJson = new char[json.length()];
		int formattedJsonIndex = 0;
		for(int i = 0; i < json.length(); i++){
			char ch = json.charAt(i);
			if(ch == '\"' && isFunctionalQuote(ch)) {
				//System.out.println("Найдена кавычка");
				int end = getCloseBracketIndex(i);
				for(int j = i; j < end+1; j++)
					formattedJson[formattedJsonIndex++] = json.charAt(j);
				i = end;
				continue;
			}
			//System.out.println("неНайдена кавычка");
			if(!isSpaceSymbol(ch))
				formattedJson[formattedJsonIndex++] = ch;
			/*if( (ch = json.charAt(i)) == '"' & isFunctionalQuote(i)){
				needWrite = !needWrite;
				changeIndex = i;
			}*/
				
			/*if(needWrite){
				formattedJson[formattedJsonIndex++] = ch;
			} else if(!isSpaceSymbol(ch))
				formattedJson[formattedJsonIndex++] = ch;*/
		}
		//System.out.println(json);
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
		if(openBracket == '"'){
			int i;
			while(true){
				i = json.indexOf('"', index+1);	
				if(i == -1)
					break;
				if(isFunctionalQuote(i))
					break;
				else {
					index = i;
				}
			}
			if(i == -1)
				throw new JsonInvalidFormatException("Could not find close symbol for "+json.charAt(i)+" at posotion: "+(offset+i));
			return i;
		}
		char closeBracket = getCloseBracket(openBracket);
		int bracketCount = 0;
		for(int i = index+1; i < json.length(); i++){
			if(json.charAt(i) == openBracket)
				bracketCount++;				
			if(json.charAt(i) == closeBracket)
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
		return Boolean.parseBoolean(get(propertyName).toString());
	}
	@Override
	public float getFloat(String propertyName) {
		return Float.parseFloat(get(propertyName).toString());
	}
	@Override
	public double getDouble(String propertyName) {
		return Double.parseDouble(get(propertyName).toString());
	}
	@Override
	public byte getByte(String propertyName) {
		return Byte.parseByte(get(propertyName).toString());
	}
	@Override
	public short getShort(String propertyName) {
		return Short.parseShort(get(propertyName).toString());
	}
	@Override
	public int getInt(String propertyName) {
		return Integer.parseInt(get(propertyName).toString());
	}
	@Override
	public long getLong(String propertyName) {
		return Long.parseLong(get(propertyName).toString());
	}
	@Override
	public String getString(String propertyName){
		String result = get(propertyName).toString();
		
		result = result
		
		.replaceAll("\\\\\"", "\"")
		.replaceAll("\\\\\\\\", "\\\\")
		.replaceAll("\\\\/", "/")	
		.replaceAll("\\\\b", "\b")
		.replaceAll("\\\\f", "\f")
		.replaceAll("\\\\n", "\n")
		.replaceAll("\\\\r", "\r")
		.replaceAll("\\\\t", "\t");
		
		//System.out.println(result);
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
			throw new JsonInvalidFormatException("Expected \\uXXXX, where XXXX is 0-9 digit numbers, but found \\u"+hex+" at posotion: "+(offset+i));
		}
			return result.substring(1, result.length()-1);
	}
	private String toStringResult = null;
	private void addToStringResult(String add){
		toStringResult+=add;
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
			return "["+result.substring(1)+"]";
		}
		String result = "";
		for(String key: map.keySet()) {
			result += ",\""+key+"\":"+map.get(key).toString();
		}
		return "{"+result.substring(1)+"}";
	}
	
	/*@Override
	public String toString(){	
		if(!array.isEmpty()) {
			String result = "[";
			boolean first = true;
			for(Json js: array){
				if(!first) 
					result +=",";
				result += js.toString();
				first = false;
				
			}
			result += "]";
			return result;
		}
		if(this.json == null)
			return "{"+guts()+"}";
		if(json.startsWith("\"")){
			return json.substring(1,json.length()-1).replaceAll("\\\\\\\"", "\"");
		}		
		if(!isOpenBracket(json.charAt(0))){
			return json.replaceAll("\\\\\\\"", "\"");
		}	
		char openBracket = json.charAt(0);
		
		return openBracket + guts() + getCloseBracket(openBracket);	
	}*/
	
	private String guts() {
		toStringResult = "";
		forEach((key, value) -> {
			String val = value.toString();
			if(!isOpenBracket(val.charAt(0)))
				val = "\"" + val +"\"";
			if(key.startsWith("\r")){
				addToStringResult(","+val);
				return;
			}
			addToStringResult(",\""+key+"\":"+val);
		});	
		return toStringResult.substring(1);
	}
	@Override
	public int size() {	
		return map.size();
	}
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	@Override
	public Json get(Object key) {
		if(type != JsonType.object) 
			throw new JsonIllegalTypeException("get() is able only for objects, this json is "+type);
		IJson result = (IJson) map.get(key);
		if(result == null){
			throw new JsonNoSuchPropertyException("Could not find a property with key \""+key+"\"");
		}		
		return map.get(key);
	}
	@Override
	public void remove(Json json) {
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("remove(Json) is able only for arrays, this json is "+type);
		array.remove(json);
	}
	@Override
	public boolean add(Json json) {
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("add() is able only for arrays, this json is "+type);
		return array.add(json);
	}
	/*@Override
	public void remove(String json) {
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("remove() is able only for arrays");
		array.remove(new IJson(json));
	}*/
	@Override
	public void add(String json) {
		if(type != JsonType.array)
			throw new JsonIllegalTypeException("add() is able only for arrays");
		array.add(new IJson(json));
	}
	@Override
	public Json put(String key, Json value) {
		return map.put(key,value);
	}
	public Json put(String key, String value) {
		return map.put(key, new IJson(value));
	}
	@Override
	public Json remove(String key) {
		if(type != JsonType.object)
			throw new JsonIllegalTypeException("remove(String) is able only for objects, this json is "+type);
		return map.remove(key);
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
		return map.remove(key);
	}
}
