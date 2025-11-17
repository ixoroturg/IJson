package ixoroturg.json;

public class IJsonSetting {
  public static byte CHARACTERS_BEFORE_ERROR_INDEX = 16;
  public static byte CHARACTERS_AFTER_ERROR_INDEX = 16;
  public static boolean DECODE_UNICODE_SEQUENCE = true;
  public static int BUFFER_SIZE = 14;
  public static int STRING_BUILDER_BUFFER_SIZE = 16;
  public static byte PARSE_CONTEXT_COUNT = 8;

  public static byte FORMAT_CONTEXT_COUNT = 8;
  public static char FORMAT_INDENT_SYMBOL = '\t';
  public static byte FORMAT_INDENT_COUNT = 1;
  public static boolean NULL_STRING_AS_NULL_VALUE = true;
  public static boolean ESCAPE_CONTROL_CHARACTERS = false;
  public static boolean SHOW_INNER_DOUBLE_VALUE = false;
  // public static boolean PRIMITIVES_IN_ONE_ROW = true;

  static boolean isWhiteSpace(int ch){
    return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
  }
}
