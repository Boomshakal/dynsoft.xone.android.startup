package dynsoft.xone.android.helper;

import java.util.ArrayList;

public class ExpressionHelper {

	public static String[] SplitStringToArray(String source, char escape, char seperator, boolean onlyFirst)
    {
        //用seperator指定的分隔符将source拆分成数组，
        //如果seperator前有奇数个escape(转义字符),则认为
        //seperator是拆分结果的一部分，不能作为分隔符

        if (source == null) return null;
        if (source.length() == 0) return null;

        ArrayList<String> list = new ArrayList<String>();
        int escapeCount = 0;
        int startPosition = 0;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == escape) {
                escapeCount++;
            } else {
                if (c == seperator) {
                    if (escapeCount % 2 == 0) {
                        String str = source.substring(startPosition, i);
                        list.add(str);
                        startPosition = i + 1;
                        if (onlyFirst) break;
                    }
                }
                escapeCount = 0;
            }
        }

        if (startPosition <= source.length()) {
            String str = source.substring(startPosition, source.length());
            list.add(RemoveEscapeChar(str, escape, new char[] { seperator }));
        }

        return list.toArray(new String[list.size()]);
    }

    public static String RemoveEscapeChar(String source, char escape, char[] specials)
    {
        if (source != null && source.length() == 0)
            return source;

        source = source.replace(new String(new char[] { escape, escape }), String.valueOf(escape));
        if (specials != null && specials.length > 0) {
            for (char c : specials) {
                source = source.replace(new String(new char[] { escape, c }), String.valueOf(c));
            }
        }

        return source;
    }
    

    public static String InsertEscapeChar(String source, char escape, char[] specials)
    {
        if (source == null || source.length() == 0)
            return source;

        source = source.replace(String.valueOf(escape), new String(new char[]{escape, escape}));
        if (specials != null && specials.length > 0) {
            for (char c : specials) {
                source = source.replace(String.valueOf(c), new String(new char[] { escape, c }));
            }
        }

        return source;
    }

    public static String[] FindExpressionArray(String source, char escape, char starter, char ender)
    {
        return FindExpressionArray(source, escape, starter, ender, false);
    }

    public static String[] FindExpressionArray(String source, char escape, char starter, char ender, boolean withOuter)
    {
        if (source == null || source.length() == 0) return null;

        ArrayList<String> list = new ArrayList<String>();
        int starterPos = -1;
        int starterCount = 0;
        int escapeCount = 0;
        ArrayList<Character> result = new ArrayList<Character>();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == escape) {
                escapeCount++;
                if (escapeCount % 2 == 0) {

                    if (starterCount == 1) {
                        result.add(escape);
                    } else {
                        result.add(escape);
                        result.add(escape);
                    }
                }
            } else {
                if (c == starter) {
                    if (escapeCount % 2 == 0) {
                        if (starterCount == 0) {
                            starterPos = i;
                        }
                        starterCount++;
                    } else {
                        if (starterCount > 1) {
                            result.add(escape);
                        }
                    }
                    result.add(starter);
                } else if (c == ender) {
                    if (escapeCount % 2 != 0) {
                        if (starterCount > 1) {
                            result.add(escape);
                        }
                    }
                    result.add(ender);
                    if (escapeCount % 2 == 0) {
                        if (starterPos > -1) {
                            if (starterCount > 1) {
                                starterCount--;
                            } else {
                                String str = CharacterArrayToString(result);
                                if (withOuter == false) {
                                    str = str.substring(1, str.length() - 1);
                                }
                                list.add(str);
                                result.clear();
                                starterCount = 0;
                                starterPos = -1;
                            }
                        }
                    }
                } else {
                    if (starterCount > 0) {
                        result.add(c);
                    }
                }

                escapeCount = 0;
            }
        }

        return list.toArray(new String[0]);
    }
    
    public static String CharacterArrayToString(ArrayList<Character> array)
    {
        StringBuilder sb = new StringBuilder();
        for (Character c : array) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean IsExpression(String source, char escape, char starter, char ender)
    {
        if (source == null || source.length() == 0) return false;
        
        String[] exprs = FindExpressionArray(source, escape, starter, ender, true);
        if (exprs.length > 0) {
            return exprs[0] == source;
        }
        return false;
    }

    public static String RemoveExpressionOuter(String expression, char starter, char ender)
    {
        if (expression == null || expression.length() == 0) return expression;
        if (expression.charAt(0) == starter && expression.charAt(expression.length() - 1) == ender) {
            return expression.substring(1, expression.length() - 1);
        }
        return expression;
    }
}
