package com.applitools.utils;

public class Validate {
    public static boolean isAllNull(Object... vars) {
        for (Object var : vars)
            if (var != null) return false;
        return true;
    }

    public static boolean isExactlyOneNotNull(Object... vars) {
        boolean foundOne = false;
        for (Object var : vars)
            if (var != null)
                if (foundOne)
                    return false;
                else
                    foundOne = true;
        return foundOne;
    }

}
