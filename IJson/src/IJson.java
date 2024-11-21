import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
public class IJson implements Json{
	private String tmp;
	private String json;
	/**
	 * Create json object from input data
	 * @param in - InputStream for reading data
	 */
	public IJson(InputStream in){
		try {
			json = new String(in.readAllBytes());
		} catch (IOException e) {
			System.err.println("Cannot read from InputStream");
			e.printStackTrace();
			RuntimeException exc = new RuntimeException("Cannot read  from InputStream");
			exc.initCause(e);
			throw exc;
		}
		format();
		tmp = json;
	}
	/**
	 * Create json object from json string
	 * @param json - json string
	 */
	public IJson(String json){
		//System.out.println("Вход: "+json);
		this.json = json;
		format();
		//System.out.println("Выход: "+this.json);
		tmp = this.json;
	}
	@Override
	public Json get(String propertyName){
		tmp = json;
		int start = tmp.indexOf("\""+propertyName+"\"");
		if(start == -1){
			throw new IJsonException("No such property: "+propertyName ,IJsonException.NO_SUCH_PROPERTY,propertyName, this);
		}
		start+=propertyName.length()+3; //'"'(0, current) + propertyName + '"'(1) + ','(2) + '"'(3)
		int end = getCloseBracketIndex(start);
		if(tmp.charAt(start) == '\"')
			return new IJson(tmp.substring(start+1, end));
		return new IJson(tmp.substring(start, end+1));
	}
	@Override
	public Json get(int index){	
		int arg = index;
		tmp = json;
		deleteBracket('[');
		int start = 0;
		for(; start < tmp.length(); start++){
			if(index == 0) break;
			int close;
			close = getCloseBracketIndex(start);	
			if(close != -1){
				start = close;
				index--;
			}						
		}
		if(start != 0)
			start++;
		int end = -1;
		end = getCloseBracketIndex(start);
		if(end == -1){
			IJsonException exc = new IJsonException("Index "+arg+" out of bounds", IJsonException.NO_SUCH_ELEMENT, arg, this);
			throw exc;
		}
		if(tmp.charAt(start) == '\"'){
			if(end < tmp.length()-1 && tmp.charAt(end+1) == ':'){
				return new IJson(tmp.substring(1,tmp.length()-1));
			}
			return new IJson(tmp.substring(start+1, end));
		}
		return new IJson(tmp.substring(start, end+1));
	}
	@Override
	public Json get(){
		return get(0);
	}
	
	private void format(){
		switch(json.charAt(0)){
		case ' ','	','\n','\r','{' ,'[','\"'->{}
		default ->{return;}
		}
		char[] result = new char[json.length()];
		int currentChar = 0;
		{
			boolean needDelete = true;
			for(int i = 0; i < json.length(); i++){
				switch(json.charAt(i)){
				case '	', ' ', '\n', '\r' -> {
					if(!needDelete)
						result[currentChar++] = json.charAt(i);
				}
				case '\"' -> {
					if(i > 0 && i < json.length()-1){
						 if(json.charAt(i-1) == '\\'){
							needDelete=!needDelete;
						} else if(!needDelete && !isManage(json.charAt(i+1))){
							throw new IJsonException("Invalid json format at index "+i,IJsonException.INVALID_JSON_FORMAT,i, this);
						}
					}
					needDelete=!needDelete;
					result[currentChar++] = json.charAt(i);			
				}
				default -> {result[currentChar++] = json.charAt(i);}
				};
			}
			if(!needDelete)
				throw new IJsonException("Invalid json format at index "+(json.length()-1)+", missing '\"'",IJsonException.INVALID_JSON_FORMAT,json.length()-1, this);
		}	
		json = new String(result).trim();
	}
	private boolean isManage(char ch){
		return switch(ch){
		case '{','[',':',',','}',']','\n','\r' -> true;
		default -> false;
		};
	}
	private char getCloseBracket(char ch){
		return switch(ch){
		case '[' -> ']';
		case '{' -> '}';
		case '<' -> '>';
		default -> 'n';
		};
	}
	private boolean deleteBracket(char ch){
		if (tmp.charAt(0) != ch) return false;
		char close = getCloseBracket(tmp.charAt(0));	
		if(close == 'n') return false;
		if(tmp.charAt(tmp.length()-1) == close){
			tmp = tmp.substring(1, tmp.length()-1);
			return true;
		}		
		else return false;
	}
	private int getCloseBracketIndex(int index){
		if(index > tmp.length()) return -1;
		char open = tmp.charAt(index);
		if(open ==  '\"'){
			do
				index = tmp.indexOf('\"', index+1);
			while(tmp.charAt(index-1) == '\\');
			return index;	
		}
		if(open == '\\'){
			return tmp.indexOf('\\', index+1);
		}
			
		char close = getCloseBracket(open);
		if(close == 'n') return -1;
		int count = 0;
		for(int i = index+1; i < tmp.length(); i++){
			if(tmp.charAt(i) == open) count++;
			if(tmp.charAt(i) == close) count--;
			if(count == -1) return i;
		}
		return -1;
	}
	@Override
	public String toString(){
		if(json.startsWith("\\\"")){
			json = json.replaceAll("\\\\\"","\"");
		}
		return json;
	}
	public boolean equals(IJson json){
		return this.json.equals(json.toString());
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
	public Iterator<Json> iterator() {	
		return new iter<Json>(this);
	}
	@SuppressWarnings("hiding")
	private class iter<Json> implements Iterator<Json>{
		Json json;
		int num = 0;
		Json next = null;
		public iter(Json json){
			this.json = (Json) new IJson(json.toString());
		}
		@Override
		public boolean hasNext() {
			try{
				next = (Json) ((IJson) json).get(num);
				num++;
				return true;
			}catch(IJsonException e){
				if(e.getError() == IJsonException.NO_SUCH_ELEMENT)
					return false;
				else throw e;
			}
		}
		@Override
		public Json next() {
			return (Json) next;
		}
	}
}
