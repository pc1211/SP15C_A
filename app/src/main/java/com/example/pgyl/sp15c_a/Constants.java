package com.example.pgyl.sp15c_a;


public class Constants {
    //region Constantes
    public enum SP15C_ACTIVITIES {
        MAIN;

        public int INDEX() {
            return ordinal();
        }
    }

    public static final int SP15C_ACTIVITIES_REQUEST_CODE_MULTIPLIER = 100;
    public static final int DOT_ASCII_CODE = 46;
}
