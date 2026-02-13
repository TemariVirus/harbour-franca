package com.simpulator.engine;

import java.util.List;

/** Utilities for dealing with List<T>. */
class ListUtil {

    /** Swap the position of the 2 indices. */
    public static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /** Remove the element at index i in O(1) without reserving order of elements. */
    public static void swapRemove(List<?> list, int i) {
        swap(list, i, list.size() - 1);
        list.remove(list.size() - 1);
    }
}
