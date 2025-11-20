package ixoroturg.json;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IJsonSetting {
  /**
   * How many characrets show before error position<br>
   * Default: 16
   */
  public static byte CHARACTERS_BEFORE_ERROR_INDEX = 16;
  /**
   * How many characters show after error position<br>
   * Default: 16
   */
  public static byte CHARACTERS_AFTER_ERROR_INDEX = 16;
  /**
   * Decode \\uxxxx into char<br>
   * Default: false
   */
  public static boolean DECODE_UNICODE_SEQUENCE = false;
  static byte BUFFER_SIZE = 12;
  /** Initial size of inner buffer of StringBuilder instances inside the parser.<br>
   * It is use for numbers and String parsing, so buffer size must be like the longest String in your json.
   * If Buffer size is overflow, StringBuilder will grow it as normal StringBuilder and safe new size along all runtime.
   * So you could set this setting before any parse begin.<br>
   * Default: 16
   */
  public static int STRING_BUILDER_BUFFER_SIZE = 16;
  /**
   * How many parsing proccess can hold in the memory.<br>
   * Parser use char buffer and StringBuilder across one parsing proccess. To avoid memory leak parser use pool of parser contexts.
   * This is the size of this pool. If there is more parsing proccess than can hold the pool, then pool size grows by to times and never going back.
   * So you should set this before any parsing proccess begin.<br>
   * Default: 8
   */
  static byte PARSE_CONTEXT_COUNT = 8;

  /**
   * How many format proccess can hold in the memory.<br>
   * There is using a pool of format context to create format output. This is the size of this pool.
   * Default: 8
   */
  static byte FORMAT_CONTEXT_COUNT = 8;
  /**
   * Then create formatted output, which symbol should be use for indent.<br>
   * Default: \t (tab, 0x0009)
   */
  public static char FORMAT_INDENT_SYMBOL = '\t';
  /**
   * Then create formatted output, how many indent symbols should be paste for one format depth.<br>
   * Default: 1
   */
  public static byte FORMAT_INDENT_COUNT = 1;
  /**
   * If find an empty string (someString.isBlank() == true) then interpretate this as NULL, otherwise use this string.<br>
   * Default: false
   */
  public static boolean NULL_STRING_AS_NULL_VALUE = false;
  /**
   * Control characters (\t, \f, \b, \n, \r) is not allowed and it will be throw an exception if found.
   * Set this to TRUE to escape them with slash and their character as www.json.org specific<br>
   * Default: false
   */
  public static boolean ESCAPE_CONTROL_CHARACTERS = false;
  /** Then create an output, there is will be String.valueOf(double_value) for numbers.<br>
   * If this setting is FALSE, then output be with string representaion of double which was getted while parsing proccess
   * or the same String.valueOf(double_value).<br>
   * Default: true
   */
  public static boolean SHOW_INNER_DOUBLE_VALUE = true;

  /**
   * Then the format output create, the control characters will be escaped.<br>
   * If this setting is true, then control characters will write into output.<br>
   * Default: false
   */
  public static boolean FORMAT_DIRECT_WRITE_CONTROL_CHARACTER = false;

  /**
   * If TRUE then parser use Double.parseDouble() method.<br>
   * Then FALSE then parser use custom double parse algorithm. It is more faster, but can do mistake near 1e-16 position.
   * So very sensetivity data should parse with FALSE. But if you parse Integer value less then 2^53 then it is safety.
   * Unsafety only last bits of mantiss.<br>
   * This setting works only if IJsonSetting.USE_LAZY_NUMBER_PARSER = false, but it's true by default.<br>
   * Default: false
   */
  public static boolean USE_FAST_NUMBER_PARSE = false;

  /**
   * If TRUE then number parsing will be defer until the first get() method<br>
   * In these case parser use Double.parseDouble()<br>
   * Default: true
   */
  public static boolean USE_LAZY_NUMBER_PARSER = true;
  
  
  /**
   * This character using as splitter for keys<br>
   * <p>Example: users.theme.color;</p>
   * <p>If there is a 0x0000 then it is disabled</p>
   * Default: 0x0000
   */
  public static char KEY_DELIMETER = 0;
  /**
   * These character is using as a parent ref, then with backslash.
   * <p>Example: \p.\p.anotherChild.child</p>
   * If there is 0x0000 then it is disabled
   * Default: 0x0000
   */
  public static char PARENT_CHARACTER = 0;
  /**
   * If TRUE then property someProp[3] will interpreted as thied element of array with name someProp
   * Default: false
   */
  public static boolean USE_ARRAY_SYNTAX = false;
  
  /**
   * This map will be use as map.clone() for JsonObject representation.<br>
   * Signature: Map(String,IJsonEntry)<br>
   * Default: java.util.TreeMap
   */
  static Map<String, IJsonEntry> MAP_INSTANCE = new TreeMap<String,IJsonEntry>();
  /**
   * This list will be use as list.clone() for JsonArray representaion.<br>
   * Signature: List(IJsonEntry)<br>
   * Default: java.util.LinkedList
   */
  static List<IJsonEntry> LIST_INSTANCE = new LinkedList<IJsonEntry>();

  // public static boolean PRIMITIVES_IN_ONE_ROW = true;

  static boolean isWhiteSpace(int ch){
    return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
  }
  
  /**
   * Set the buffer for IO operations<br>
   * Parser will read 2^power chars by one IO call. Actually buffer size will be 2^(power+1).
   * So you need to set this value to your's amount of CPU cache memory.<br>
   * This operation is thread safety. Changes will apply only at next parsing process.<br>
   * Default: 12, this is 2^13 = 8KiB of memmory by one parsing proccess.
   */
  public static void setBufferSize(int power){
	  
    BUFFER_SIZE = (byte)power;
    for(int i = 0; i < IJsonParseContext.ctx.length; i++){
      if(IJsonParseContext.ctx[i] != null)
        IJsonParseContext.ctx[i].updateBuffer = true;
    }
  }
  /**
   * Get the buffer for IO operations<br>
   * Parser read 2^power chars by one IO call. Actually buffer size is 2^(power+1).
   * So you need to set this value to your's amount of CPU cache memory.<br>
   * Default: 12, this is 2^13 = 8KiB of memmory by one parsing proccess.
   */
  public static byte getBufferSize(){
    return BUFFER_SIZE;
  }
}
