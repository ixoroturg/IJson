package IJson;
import java.util.Map;

public interface Json extends Iterable<Json>, Map<String, Json>{
	/**
	 * return new Json with value of property
	 * @param propertyName - name  of property
	 * @return new Json with value of property
	 */
	//public Json get(String propertyName);
	/**
	 * return get(0);
	 * @return return new Json with value of 0th array element
	 */
	public Json get();
	/**
	 * return new Json with value of array element with index
	 * @param index - index of array value
	 * @return return new Json with value of array element
	 */	
	public Json get(int index);
	/**
	 * return parent of this Json
	 * @return parent of this Json
	 */
	public Json back();
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return boolean from json
	 */
	public boolean getBoolean(String propertyName);
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return float from json
	 */
	public float getFloat(String propertyName);
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return double from json
	 */
	public double getDouble(String propertyName);
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return byte from json
	 */	
	public byte getByte(String propertyName);
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return short from json
	 */
	public short getShort(String propertyName);
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return int from json
	 */
	public int getInt(String propertyName);
	/**
	 * get and parse
	 * @param propertyName - name  of property
	 * @return long from json
	 */
	public long getLong(String propertyName);
	
}
