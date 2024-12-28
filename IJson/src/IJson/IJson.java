package IJson;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class IJson implements Json, Cloneable{
	private IJson parent;
	private Map<String, Json> map = new LinkedHashMap<String, Json>();
	private String json;
	private LinkedList<Json> jsonList = new LinkedList<Json>();
	public IJson(String json){
		this.json = json;
		proccess();
	}
	public IJson(){
		
	}
	public IJson(Json[] jsons) {
		this.json = "[";
		
		for(Json js: jsons) {		
			this.json += js.toString();
		}	
		this.json += "]";
	}
	public void setMap(Map<String, Json> map){
		this.map = map;
	}
	public IJson(InputStream in) throws IOException{
		BufferedInputStream reader = new  BufferedInputStream(in);
		json = new String(reader.readAllBytes());
		proccess();
	}
	private IJson(String json, IJson parent){
		this(json);
		this.parent = parent;	
	}
	@Override
	public IJson back(){
		return parent;
	}
	private void proccess(){
		format();
		if(json.startsWith("\""))
			return;
		if(!isOpenBracket(json.charAt(0)))
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
			
		}
	}
	private void format(){
		boolean needWrite = false;
		int changeIndex = 0;
		char[] formattedJson = new char[json.length()];
		int formattedJsonIndex = 0;
		for(int i = 0; i < json.length(); i++){
			char ch;
			if( (ch = json.charAt(i)) == '"' & isFunctionalQuote(i)){
				needWrite = !needWrite;
				changeIndex = i;
			}
				
			if(needWrite){
				formattedJson[formattedJsonIndex++] = ch;
			} else if(!isSpaceSymbol(ch))
				formattedJson[formattedJsonIndex++] = ch;
		}
		if(needWrite)
			throw new IJsonException("No close \" symbol at "+changeIndex);
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
				if(isFunctionalQuote(i))
					break;
				else {
					index = i;
				}
			}
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
		return map.values().iterator();
	}
	@Override
	public Json get() {
		return get("0");
	}

	@Override
	public Json get(int index) {
		return get("\r"+index);
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
	private String toStringResult = null;
	private void addToStringResult(String add){
		toStringResult+=add;
	}
	@Override
	public String toString(){	
		if(!jsonList.isEmpty()) {
			String result = "[";
			boolean first = true;
			for(Json js: jsonList){
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
	}
	
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
		IJson result = (IJson) map.get(key);
		if(result == null){
			if(((String) key).charAt(0) == '\r')
				key = ((String) key).substring(1);
			throw new IJsonException("No such property \""+key+"\"");
		}
			
		return map.get(key);
	}
	@Override
	public void add(Json json) {
		jsonList.add(json);
	}
	@Override
	public Json put(String key, Json value) {
		return map.put(key,value);
	}
	public Json put(String key, String value) {
		return map.put(key, new IJson(value));
	}
	@Override
	public Json remove(Object key) {
		return map.remove(key);
	}
	@Override
	public void putAll(Map<? extends String, ? extends Json> m) {
		map.putAll(m);
	}
	@Override
	public void clear() {
		map.clear();
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
}
