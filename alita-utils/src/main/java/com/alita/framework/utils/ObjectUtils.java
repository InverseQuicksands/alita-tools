package com.alita.framework.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * ObjectUtils
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-05-08 17:21:03
 */
public class ObjectUtils {


    /**
     * Determine whether the given array is empty:
     * i.e. {@code null} or of zero length.
     * @param array the array to check
     * @see #isEmpty(Object)
     */
    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }


    /**
     * Determine whether the given object is empty.
     * <p>This method supports the following object types.
     * <ul>
     * <li>{@code Optional}: considered empty if not {@link Optional#isPresent()}</li>
     * <li>{@code Array}: considered empty if its length is zero</li>
     * <li>{@link CharSequence}: considered empty if its length is zero</li>
     * <li>{@link Collection}: delegates to {@link Collection#isEmpty()}</li>
     * <li>{@link Map}: delegates to {@link Map#isEmpty()}</li>
     * </ul>
     * <p>If the given object is non-null and not one of the aforementioned
     * supported types, this method returns {@code false}.
     * @param obj the object to check
     * @return {@code true} if the object is {@code null} or <em>empty</em>
     * @since 4.2
     * @see Optional#isPresent()
     * @see ObjectUtils#isEmpty(Object[])
     * @see StringUtils#hasLength(CharSequence)
     * @see CollectionUtils#isEmpty(java.util.Collection)
     * @see CollectionUtils#isEmpty(java.util.Map)
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Optional<?> optional) {
            return !optional.isPresent();
        }
        if (obj instanceof CharSequence charSequence) {
            return charSequence.length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection<?> collection) {
            return collection.isEmpty();
        }
        if (obj instanceof Map<?, ?> map) {
            return map.isEmpty();
        }

        // else
        return false;
    }

}
