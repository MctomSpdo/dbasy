package com.example.dbasy.database.mysql;

import com.example.dbasy.database.DBUI;
import javafx.scene.image.Image;

import java.util.regex.Pattern;

public class MySQLUI extends DBUI {
    public static Image icon = null;

    @Override
    public Image getIcon() {
        if(icon == null) {
            icon = new Image(String.valueOf(MySQLUI.class.getResource("icon-24.png")));
        }
        return icon;
    }

    //<editor-fold desc="Highlighting pattern">
    private static final String[] KEYWORDS = new String[] {
            "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "BACKUP", "BETWEEN", "BY", "CASE", "CHECK", "COLUMN",
            "CONSTRAINT", "CREATE", "DATABASE", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "EXEC", "EXISTS",
            "FOREIGN", "FROM", "FULL", "GROUP", "HAVING", "IN", "INDEX", "INNER", "INSERT", "INTO", "IS", "JOIN",
            "KEY", "LEFT", "LIKE", "LIMIT", "NOT", "NULL", "OR", "ORDER", "OUTER", "PRIMARY", "PROCEDURE", "REPLACE",
            "RIGHT", "ROWNUM", "SELECT", "SET", "TABLE", "TOP", "TRUNCATE", "UNION", "UNIQUE", "UPDATE", "VALUES",
            "VIEW", "WHERE"
    };

    private static final String[] FUNCTIONS = new String[] {
            //string functions:
            "ASCII", "CHAR_LENGTH", "CHARACTER_LENGTH", "CONCAT", "CONCAT_WS", "FIELD", "FIND_IN_SET", "FORMAT",
            "INSERT", "INSTR", "LCASE", "LEFT", "LENGTH", "LOCATE", "LOWER", "LPAD", "LTRIM", "MID", "POSITION",
            "REPEAT", "REPLACE", "REVERSE", "RIGHT", "RPAD", "RTRIM", "SPACE", "STRCMP", "SUBSTR", "SUBSTRING",
            "SUBSTRING_INDEX", "TRIM", "UCASE", "UPPER",
            //numeric functions:
            "ABS", "ACOS", "ASIN", "ATAN", "ATAN2", "AVG", "CEIL", "CEILING", "COS", "COT", "COUNT", "DEGREES", "DIV",
            "EXP", "FLOOR", "GREATEST", "LEAST", "LN", "LOG", "LOG10", "LOG2", "MAX", "MIN", "MOD", "PI", "POW",
            "POWER", "RADIANS", "RAND", "ROUND", "SIGN", "SIN", "SQRT", "SUM", "TAN", "TRUNCATE",
            //date functions:
            "ADDDATE", "ADDTIME", "CURDATE", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURTIME", "DATE",
            "DATEDIFF", "DATE_ADD", "DATE_FORMAT", "DATE_SUB", "DAY", "DAYNAME", "DAYOFMONTH", "DAYOFWEEK",
            "DAYOFYEAR", "EXTRACT", "FROM_DAYS", "HOUR", "LAST_DAY", "LOCALTIME", "LOCALTIMESTAMP", "MAKEDATE",
            "MAKETIME", "MICROSECOND", "MINUTE", "MONTH", "MONTHNAME", "NOW", "PERIOD_ADD", "PERIOD_DIFF", "QUARTER",
            "SECOND", "SEC_TO_TIME", "STR_TO_DATE", "SUBDATE", "SUBTIME", "SYSDATE", "TIME", "TIME_FORMAT",
            "TIME_TO_SEC", "TIMEDIFF", "TIMESTAMP", "TO_DAYS", "WEEK", "WEEKDAY", "WEEKOFYEAR", "YEAR", "YEARWEEK",
            //advanced functions:
            "BIN", "BINARY", "CASE", "CAST", "COALESCE", "CONNECTION_ID", "CONV", "CONVERT", "CURRENT_USER", "DATABASE",
            "IF", "IFNULL", "ISNULL", "LAST_INSERT_ID", "NULLIF", "SESSION_USER", "SYSTEM_USER", "USER", "VERSION"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String FUNCTION_PATTERN = "\\b(" + String.join("|", FUNCTIONS) + ")\\b";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\(|\\)";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public String[] getKeywords() {
        return KEYWORDS;
    }

    @Override
    public String[] getFunctions() {
        return FUNCTIONS;
    }
    //</editor-fold>
}
