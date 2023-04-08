package com.example.pgyl.swtimer_a;

import com.example.pgyl.pekislib_a.TimeDateUtils.TIME_UNITS;

public class Constants {
    //region Constantes
    public enum SWTIMER_ACTIVITIES {
        MAIN, CT_DISPLAY, CT_DISPLAY_COLORS, CT_DISPLAY_DOT_MATRIX_DISPLAY;

        public int INDEX() {
            return ordinal();
        }
    }

    public static final TIME_UNITS APP_TIME_UNIT_PRECISION = TIME_UNITS.TS;  //  Précision souhaitée dans l'affichage du temps
    public static final int SWTIMER_ACTIVITIES_REQUEST_CODE_MULTIPLIER = 100;
    public static final int DOT_ASCII_CODE = 46;
}
