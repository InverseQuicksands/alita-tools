package com.alita.framework.utils;


/**
 * <p>Operations on {@link java.lang.String} that are
 * {@code null} safe.</p>
 *
 * <ul>
 *  <li><b>IsEmpty/IsBlank</b>
 *      - checks if a String contains text</li>
 *  <li><b>Trim/Strip</b>
 *      - removes leading and trailing whitespace</li>
 *  <li><b>Equals/Compare</b>
 *      - compares two strings in a null-safe manner</li>
 *  <li><b>startsWith</b>
 *      - check if a String starts with a prefix in a null-safe manner</li>
 *  <li><b>endsWith</b>
 *      - check if a String ends with a suffix in a null-safe manner</li>
 *  <li><b>IndexOf/LastIndexOf/Contains</b>
 *      - null-safe index-of checks
 *  <li><b>IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut</b>
 *      - index-of any of a set of Strings</li>
 *  <li><b>ContainsOnly/ContainsNone/ContainsAny</b>
 *      - checks if String contains only/none/any of these characters</li>
 *  <li><b>Substring/Left/Right/Mid</b>
 *      - null-safe substring extractions</li>
 *  <li><b>SubstringBefore/SubstringAfter/SubstringBetween</b>
 *      - substring extraction relative to other strings</li>
 *  <li><b>Split/Join</b>
 *      - splits a String into an array of substrings and vice versa</li>
 *  <li><b>Remove/Delete</b>
 *      - removes part of a String</li>
 *  <li><b>Replace/Overlay</b>
 *      - Searches a String and replaces one String with another</li>
 *  <li><b>Chomp/Chop</b>
 *      - removes the last part of a String</li>
 *  <li><b>AppendIfMissing</b>
 *      - appends a suffix to the end of the String if not present</li>
 *  <li><b>PrependIfMissing</b>
 *      - prepends a prefix to the start of the String if not present</li>
 *  <li><b>LeftPad/RightPad/Center/Repeat</b>
 *      - pads a String</li>
 *  <li><b>UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize</b>
 *      - changes the case of a String</li>
 *  <li><b>CountMatches</b>
 *      - counts the number of occurrences of one String in another</li>
 *  <li><b>IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable</b>
 *      - checks the characters in a String</li>
 *  <li><b>DefaultString</b>
 *      - protects against a null input String</li>
 *  <li><b>Rotate</b>
 *      - rotate (circular shift) a String</li>
 *  <li><b>Reverse/ReverseDelimited</b>
 *      - reverses a String</li>
 *  <li><b>Abbreviate</b>
 *      - abbreviates a string using ellipses or another given String</li>
 *  <li><b>Difference</b>
 *      - compares Strings and reports on their differences</li>
 *  <li><b>LevenshteinDistance</b>
 *      - the number of changes needed to change one String into another</li>
 * </ul>
 *
 * <p>The {@code StringUtils} class defines certain words related to
 * String handling.</p>
 *
 * <ul>
 *  <li>null - {@code null}</li>
 *  <li>empty - a zero-length string ({@code ""})</li>
 *  <li>space - the space character ({@code ' '}, char 32)</li>
 *  <li>whitespace - the characters defined by {@link Character#isWhitespace(char)}</li>
 *  <li>trim - the characters &lt;= 32 as in {@link String#trim()}</li>
 * </ul>
 *
 * <p>{@code StringUtils} handles {@code null} input Strings quietly.
 * That is to say that a {@code null} input will return {@code null}.
 * Where a {@code boolean} or {@code int} is being returned
 * details vary by method.</p>
 *
 * <p>A side effect of the {@code null} handling is that a
 * {@code NullPointerException} should be considered a bug in
 * {@code StringUtils}.</p>
 *
 * <p>Methods in this class include sample code in their Javadoc comments to explain their operation.
 * The symbol {@code *} is used to indicate any input including {@code null}.</p>
 *
 * <p>#ThreadSafe#</p>
 * @see java.lang.String
 * @since 1.0
 */
public class StringUtils {

   public static boolean isNotBlank(final CharSequence cs) {
      return !isBlank(cs);
   }

   public static boolean isBlank(final CharSequence cs) {
      final int strLen = length(cs);
      if (strLen == 0) {
         return true;
      }
      for (int i = 0; i < strLen; i++) {
         if (!Character.isWhitespace(cs.charAt(i))) {
            return false;
         }
      }
      return true;
   }

   public static int length(final CharSequence cs) {
      return cs == null ? 0 : cs.length();
   }

   /**
    * Check that the given {@code CharSequence} is neither {@code null} nor
    * of length 0.
    * <p>Note: this method returns {@code true} for a {@code CharSequence}
    * that purely consists of whitespace.
    * <p><pre class="code">
    * StringUtils.hasLength(null) = false
    * StringUtils.hasLength("") = false
    * StringUtils.hasLength(" ") = true
    * StringUtils.hasLength("Hello") = true
    * </pre>
    * @param str the {@code CharSequence} to check (may be {@code null})
    * @return {@code true} if the {@code CharSequence} is not {@code null} and has length
    * @see #hasLength(String)
    * @see #hasText(CharSequence)
    */
   public static boolean hasLength(CharSequence str) {
      return (str != null && str.length() > 0);
   }

   /**
    * Check that the given {@code String} is neither {@code null} nor of length 0.
    * <p>Note: this method returns {@code true} for a {@code String} that
    * purely consists of whitespace.
    * @param str the {@code String} to check (may be {@code null})
    * @return {@code true} if the {@code String} is not {@code null} and has length
    * @see #hasLength(CharSequence)
    * @see #hasText(String)
    */
   public static boolean hasLength(String str) {
      return (str != null && !str.isEmpty());
   }


   /**
    * Check whether the given {@code CharSequence} contains actual <em>text</em>.
    * <p>More specifically, this method returns {@code true} if the
    * {@code CharSequence} is not {@code null}, its length is greater than
    * 0, and it contains at least one non-whitespace character.
    * <p><pre class="code">
    * StringUtils.hasText(null) = false
    * StringUtils.hasText("") = false
    * StringUtils.hasText(" ") = false
    * StringUtils.hasText("12345") = true
    * StringUtils.hasText(" 12345 ") = true
    * </pre>
    * @param str the {@code CharSequence} to check (may be {@code null})
    * @return {@code true} if the {@code CharSequence} is not {@code null},
    * its length is greater than 0, and it does not contain whitespace only
    * @see #hasText(String)
    * @see #hasLength(CharSequence)
    * @see Character#isWhitespace
    */
   public static boolean hasText(CharSequence str) {
      return (str != null && str.length() > 0 && containsText(str));
   }

   /**
    * Check whether the given {@code String} contains actual <em>text</em>.
    * <p>More specifically, this method returns {@code true} if the
    * {@code String} is not {@code null}, its length is greater than 0,
    * and it contains at least one non-whitespace character.
    * @param str the {@code String} to check (may be {@code null})
    * @return {@code true} if the {@code String} is not {@code null}, its
    * length is greater than 0, and it does not contain whitespace only
    * @see #hasText(CharSequence)
    * @see #hasLength(String)
    * @see Character#isWhitespace
    */
   public static boolean hasText(String str) {
      return (str != null && !str.isEmpty() && containsText(str));
   }

   private static boolean containsText(CharSequence str) {
      int strLen = str.length();
      for (int i = 0; i < strLen; i++) {
         if (!Character.isWhitespace(str.charAt(i))) {
            return true;
         }
      }
      return false;
   }
}
