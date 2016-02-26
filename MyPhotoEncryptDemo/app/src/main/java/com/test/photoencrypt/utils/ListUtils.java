package com.test.photoencrypt.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * List Utils
 * 
 * @author fuweiwei 2015-9-2
 */
public class ListUtils {

    /** default join separator **/
    public static final String DEFAULT_JOIN_SEPARATOR = ",";

    private ListUtils() {
        throw new AssertionError();
    }

    /**
     * get size of list
     * 
     * <pre>
     * getSize(null)   =   0;
     * getSize({})     =   0;
     * getSize({1})    =   1;
     * </pre>
     * 
     * @param <V>
     * @param sourceList
     * @return if list is null or empty, return 0, else return {@link List#size()}.
     */
    public static <V> int getSize(List<V> sourceList) {
        return sourceList == null ? 0 : sourceList.size();
    }

    /**
     * is null or its size is 0
     * 
     * <pre>
     * isEmpty(null)   =   true;
     * isEmpty({})     =   true;
     * isEmpty({1})    =   false;
     * </pre>
     * 
     * @param <V>
     * @param sourceList
     * @return if list is null or its size is 0, return true, else return false.
     */
    public static <V> boolean isEmpty(List<V> sourceList) {
        return (sourceList == null || sourceList.size() == 0);
    }

    /**
     * compare two list
     * 
     * <pre>
     * isEquals(null, null) = true;
     * isEquals(new ArrayList&lt;String&gt;(), null) = false;
     * isEquals(null, new ArrayList&lt;String&gt;()) = false;
     * isEquals(new ArrayList&lt;String&gt;(), new ArrayList&lt;String&gt;()) = true;
     * </pre>
     * 
     * @param <V>
     * @param actual
     * @param expected
     * @return
     */
    public static <V> boolean isEquals(ArrayList<V> actual, ArrayList<V> expected) {
        if (actual == null) {
            return expected == null;
        }
        if (expected == null) {
            return false;
        }
        if (actual.size() != expected.size()) {
            return false;
        }

        for (int i = 0; i < actual.size(); i++) {
            if (!ObjectUtils.isEquals(actual.get(i), expected.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * join list to string, separator is ","
     * 
     * <pre>
     * join(null)      =   "";
     * join({})        =   "";
     * join({a,b})     =   "a,b";
     * </pre>
     * 
     * @param list
     * @return join list to string, separator is ",". if list is empty, return ""
     */
    public static String join(List<String> list) {
        return join(list, DEFAULT_JOIN_SEPARATOR);
    }

    /**
     * join list to string
     * 
     * <pre>
     * join(null, '#')     =   "";
     * join({}, '#')       =   "";
     * join({a,b,c}, ' ')  =   "abc";
     * join({a,b,c}, '#')  =   "a#b#c";
     * </pre>
     * 
     * @param list
     * @param separator
     * @return join list to string. if list is empty, return ""
     */
    public static String join(List<String> list, char separator) {
        return join(list, new String(new char[] {separator}));
    }

    /**
     * join list to string. if separator is null, use {@link #DEFAULT_JOIN_SEPARATOR}
     * 
     * <pre>
     * join(null, "#")     =   "";
     * join({}, "#$")      =   "";
     * join({a,b,c}, null) =   "a,b,c";
     * join({a,b,c}, "")   =   "abc";
     * join({a,b,c}, "#")  =   "a#b#c";
     * join({a,b,c}, "#$") =   "a#$b#$c";
     * </pre>
     * 
     * @param list
     * @param separator
     * @return join list to string with separator. if list is empty, return ""
     */
    public static String join(List<String> list, String separator) {
        return list == null ? "" : TextUtils.join(separator, list);
    }

    /**
     * add distinct entry to list
     * 
     * @param <V>
     * @param sourceList
     * @param entry
     * @return if entry already exist in sourceList, return false, else add it and return true.
     */
    public static <V> boolean addDistinctEntry(List<V> sourceList, V entry) {
        return (sourceList != null && !sourceList.contains(entry)) ? sourceList.add(entry) : false;
    }

    /**
     * add all distinct entry to list1 from list2
     * 
     * @param <V>
     * @param sourceList
     * @param entryList
     * @return the count of entries be added
     */
    public static <V> int addDistinctList(List<V> sourceList, List<V> entryList) {
        if (sourceList == null || isEmpty(entryList)) {
            return 0;
        }

        int sourceCount = sourceList.size();
        for (V entry : entryList) {
            if (!sourceList.contains(entry)) {
                sourceList.add(entry);
            }
        }
        return sourceList.size() - sourceCount;
    }

    /**
     * remove duplicate entries in list
     * 
     * @param <V>
     * @param sourceList
     * @return the count of entries be removed
     */
    public static <V> int distinctList(List<V> sourceList) {
        if (isEmpty(sourceList)) {
            return 0;
        }

        int sourceCount = sourceList.size();
        int sourceListSize = sourceList.size();
        for (int i = 0; i < sourceListSize; i++) {
            for (int j = (i + 1); j < sourceListSize; j++) {
                if (sourceList.get(i).equals(sourceList.get(j))) {
                    sourceList.remove(j);
                    sourceListSize = sourceList.size();
                    j--;
                }
            }
        }
        return sourceCount - sourceList.size();
    }

    /**
     * add not null entry to list
     * 
     * @param sourceList
     * @param value
     * @return <ul>
     *         <li>if sourceList is null, return false</li>
     *         <li>if value is null, return false</li>
     *         <li>return {@link List#add(Object)}</li>
     *         </ul>
     */
    public static <V> boolean addListNotNullValue(List<V> sourceList, V value) {
        return (sourceList != null && value != null) ? sourceList.add(value) : false;
    }


    /**
     * invert list
     * 
     * @param <V>
     * @param sourceList
     * @return
     */
    public static <V> List<V> invertList(List<V> sourceList) {
        if (isEmpty(sourceList)) {
            return sourceList;
        }

        List<V> invertList = new ArrayList<V>(sourceList.size());
        for (int i = sourceList.size() - 1; i >= 0; i--) {
            invertList.add(sourceList.get(i));
        }
        return invertList;
    }

    /**
     * String Utils
     *
     * @author fuweiwei 2015-9-2
     */
    public static class StringUtils {

        private StringUtils() {
            throw new AssertionError();
        }

        /**
         * is null or its length is 0 or it is made by space
         *
         * <pre>
         * isBlank(null) = true;
         * isBlank(&quot;&quot;) = true;
         * isBlank(&quot;  &quot;) = true;
         * isBlank(&quot;a&quot;) = false;
         * isBlank(&quot;a &quot;) = false;
         * isBlank(&quot; a&quot;) = false;
         * isBlank(&quot;a b&quot;) = false;
         * </pre>
         *
         * @param str
         * @return if string is null or its size is 0 or it is made by space, return true, else return false.
         */
        public static boolean isBlank(String str) {
            return (str == null || str.trim().length() == 0);
        }

        /**
         * is null or its length is 0
         *
         * <pre>
         * isEmpty(null) = true;
         * isEmpty(&quot;&quot;) = true;
         * isEmpty(&quot;  &quot;) = false;
         * </pre>
         *
         * @param str
         * @return if string is null or its size is 0, return true, else return false.
         */
        public static boolean isEmpty(CharSequence str) {
            return (str == null || str.length() == 0);
        }

        /**
         * compare two string
         *
         * @param actual
         * @param expected
         * @return
         * @see ObjectUtils#isEquals(Object, Object)
         */
        public static boolean isEquals(String actual, String expected) {
            return ObjectUtils.isEquals(actual, expected);
        }

        /**
         * get length of CharSequence
         *
         * <pre>
         * length(null) = 0;
         * length(\"\") = 0;
         * length(\"abc\") = 3;
         * </pre>
         *
         * @param str
         * @return if str is null or empty, return 0, else return {@link CharSequence#length()}.
         */
        public static int length(CharSequence str) {
            return str == null ? 0 : str.length();
        }

        /**
         * null Object to empty string
         *
         * <pre>
         * nullStrToEmpty(null) = &quot;&quot;;
         * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
         * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
         * </pre>
         *
         * @param str
         * @return
         */
        public static String nullStrToEmpty(Object str) {
            return (str == null ? "" : (str instanceof String ? (String)str : str.toString()));
        }

        /**
         * capitalize first letter
         *
         * <pre>
         * capitalizeFirstLetter(null)     =   null;
         * capitalizeFirstLetter("")       =   "";
         * capitalizeFirstLetter("2ab")    =   "2ab"
         * capitalizeFirstLetter("a")      =   "A"
         * capitalizeFirstLetter("ab")     =   "Ab"
         * capitalizeFirstLetter("Abc")    =   "Abc"
         * </pre>
         *
         * @param str
         * @return
         */
        public static String capitalizeFirstLetter(String str) {
            if (isEmpty(str)) {
                return str;
            }

            char c = str.charAt(0);
            return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str : new StringBuilder(str.length())
            .append(Character.toUpperCase(c)).append(str.substring(1)).toString();
        }

        /**
         * encoded in utf-8
         *
         * <pre>
         * utf8Encode(null)        =   null
         * utf8Encode("")          =   "";
         * utf8Encode("aa")        =   "aa";
         * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
         * </pre>
         *
         * @param str
         * @return
         * @throws UnsupportedEncodingException if an error occurs
         */
        public static String utf8Encode(String str) {
            if (!isEmpty(str) && str.getBytes().length != str.length()) {
                try {
                    return URLEncoder.encode(str, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
                }
            }
            return str;
        }

        /**
         * encoded in utf-8, if exception, return defultReturn
         *
         * @param str
         * @param defultReturn
         * @return
         */
        public static String utf8Encode(String str, String defultReturn) {
            if (!isEmpty(str) && str.getBytes().length != str.length()) {
                try {
                    return URLEncoder.encode(str, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return defultReturn;
                }
            }
            return str;
        }

        /**
         * get innerHtml from href
         *
         * <pre>
         * getHrefInnerHtml(null)                                  = ""
         * getHrefInnerHtml("")                                    = ""
         * getHrefInnerHtml("mp3")                                 = "mp3";
         * getHrefInnerHtml("&lt;a innerHtml&lt;/a&gt;")                    = "&lt;a innerHtml&lt;/a&gt;";
         * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
         * getHrefInnerHtml("&lt;a&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
         * getHrefInnerHtml("&lt;a href="baidu.com"&gt;innerHtml&lt;/a&gt;")               = "innerHtml";
         * getHrefInnerHtml("&lt;a href="baidu.com" title="baidu"&gt;innerHtml&lt;/a&gt;") = "innerHtml";
         * getHrefInnerHtml("   &lt;a&gt;innerHtml&lt;/a&gt;  ")                           = "innerHtml";
         * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                      = "innerHtml";
         * getHrefInnerHtml("jack&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                  = "innerHtml";
         * getHrefInnerHtml("&lt;a&gt;innerHtml1&lt;/a&gt;&lt;a&gt;innerHtml2&lt;/a&gt;")        = "innerHtml2";
         * </pre>
         *
         * @param href
         * @return <ul>
         *         <li>if href is null, return ""</li>
         *         <li>if not match regx, return source</li>
         *         <li>return the last string that match regx</li>
         *         </ul>
         */
        public static String getHrefInnerHtml(String href) {
            if (isEmpty(href)) {
                return "";
            }

            String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
            Pattern hrefPattern = Pattern.compile(hrefReg, Pattern.CASE_INSENSITIVE);
            Matcher hrefMatcher = hrefPattern.matcher(href);
            if (hrefMatcher.matches()) {
                return hrefMatcher.group(1);
            }
            return href;
        }

        /**
         * process special char in html
         *
         * <pre>
         * htmlEscapeCharsToString(null) = null;
         * htmlEscapeCharsToString("") = "";
         * htmlEscapeCharsToString("mp3") = "mp3";
         * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
         * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
         * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
         * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
         * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
         * </pre>
         *
         * @param source
         * @return
         */
        public static String htmlEscapeCharsToString(String source) {
            return StringUtils.isEmpty(source) ? source : source.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                    .replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
        }

        /**
         * transform half width char to full width char
         *
         * <pre>
         * fullWidthToHalfWidth(null) = null;
         * fullWidthToHalfWidth("") = "";
         * fullWidthToHalfWidth(new String(new char[] {12288})) = " ";
         * fullWidthToHalfWidth("！＂＃＄％＆) = "!\"#$%&";
         * </pre>
         *
         * @param s
         * @return
         */
        public static String fullWidthToHalfWidth(String s) {
            if (isEmpty(s)) {
                return s;
            }

            char[] source = s.toCharArray();
            for (int i = 0; i < source.length; i++) {
                if (source[i] == 12288) {
                    source[i] = ' ';
                    // } else if (source[i] == 12290) {
                    // source[i] = '.';
                } else if (source[i] >= 65281 && source[i] <= 65374) {
                    source[i] = (char)(source[i] - 65248);
                } else {
                    source[i] = source[i];
                }
            }
            return new String(source);
        }

        /**
         * transform full width char to half width char
         *
         * <pre>
         * halfWidthToFullWidth(null) = null;
         * halfWidthToFullWidth("") = "";
         * halfWidthToFullWidth(" ") = new String(new char[] {12288});
         * halfWidthToFullWidth("!\"#$%&) = "！＂＃＄％＆";
         * </pre>
         *
         * @param s
         * @return
         */
        public static String halfWidthToFullWidth(String s) {
            if (isEmpty(s)) {
                return s;
            }

            char[] source = s.toCharArray();
            for (int i = 0; i < source.length; i++) {
                if (source[i] == ' ') {
                    source[i] = (char)12288;
                    // } else if (source[i] == '.') {
                    // source[i] = (char)12290;
                } else if (source[i] >= 33 && source[i] <= 126) {
                    source[i] = (char)(source[i] + 65248);
                } else {
                    source[i] = source[i];
                }
            }
            return new String(source);
        }
    }
}
