    package com.sau.utils;

    public class CurrentHolderUtils {
        private static final ThreadLocal<Integer> CURRENT_LOCAL = new ThreadLocal<>();

        public static void setCurrentId(Integer id) {
            CURRENT_LOCAL.set(id);
        }

        public static Integer getCurrentId() {
            return CURRENT_LOCAL.get();
        }

        public static void remove() {
            CURRENT_LOCAL.remove();
        }
    }