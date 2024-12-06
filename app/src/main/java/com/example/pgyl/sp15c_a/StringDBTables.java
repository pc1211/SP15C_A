package com.example.pgyl.sp15c_a;

import com.example.pgyl.pekislib_a.InputButtonsActivity;

import static com.example.pgyl.pekislib_a.Constants.REGEXP_SIX_CHARS;
import static com.example.pgyl.pekislib_a.Constants.REGEXP_SIX_CHARS_ERROR_MESSAGE;
import static com.example.pgyl.pekislib_a.StringDBTables.TABLE_IDS;

public class StringDBTables {

    public static final int DATA_VERSION = 84;   //   A augmenter dès que les données éventuelles dans la DB existante ne seront plus comptatibles après changements

    enum SP15C_TABLES {   // Les tables, rattachées à leurs champs de data
        STACK_REGS(Sp15cTableDataFields.StackRegs.class, "Stack Registers"),
        IM_STACK_REGS(Sp15cTableDataFields.ImStackRegs.class, "Im Stack Registers"),
        FLAGS(Sp15cTableDataFields.StackRegs.class, "Flags"),
        REGS(Sp15cTableDataFields.Regs.class, "Regs"),
        RET_STACK(Sp15cTableDataFields.RetStack.class, "Return Stack"),
        PROG_LINES(Sp15cTableDataFields.ProgLines.class, "ProgLines"),
        PARAMS(Sp15cTableDataFields.Params.class, "Params"),
        PALETTE_COLORS(Sp15cTableDataFields.paletteColors.class, "Color Palette");

        private int dataFieldsCount;
        private String description;

        SP15C_TABLES(Class<? extends Sp15cTableDataFields> sp15cTableDataFields, String description) {
            dataFieldsCount = sp15cTableDataFields.getEnumConstants().length;
            this.description = description;
        }

        public String DESCRIPTION() {
            return description;
        }

        public int INDEX() {
            return ordinal();
        }

        public int getDataFieldsCount() {
            return dataFieldsCount;
        }
    }

    private interface Sp15cTableDataFields {  //  Les champs de data, par table

        enum StackRegs implements Sp15cTableDataFields {
            VALUE;

            public int INDEX() {
                return ordinal() + 1;
            }   //  INDEX 0 pour identifiant utilisateur
        }

        enum ImStackRegs implements Sp15cTableDataFields {
            VALUE;

            public int INDEX() {
                return ordinal() + 1;
            }   //  INDEX 0 pour identifiant utilisateur
        }

        enum Flags implements Sp15cTableDataFields {
            VALUE;

            public int INDEX() {
                return ordinal() + 1;
            }   //  INDEX 0 pour identifiant utilisateur
        }

        enum Regs implements Sp15cTableDataFields {
            VALUE;

            public int INDEX() {
                return ordinal() + 1;
            }   //  INDEX 0 pour identifiant utilisateur
        }

        enum RetStack implements Sp15cTableDataFields {
            VALUE;

            public int INDEX() {
                return ordinal() + 1;
            }   //  INDEX 0 pour identifiant utilisateur
        }

        enum ProgLines implements Sp15cTableDataFields {
            OPCODE1, OPCODE2, OPCODE3;     //  Max 3 OpCodes par ligne

            public int INDEX() {
                return ordinal() + 1;
            }   //  INDEX 0 pour identifiant utilisateur
        }

        enum Params implements Sp15cTableDataFields {
            VALUE;

            public int INDEX() {
                return ordinal() + 1;
            }   //  INDEX 0 pour identifiant utilisateur
        }

        enum paletteColors implements Sp15cTableDataFields {   //  Les champs de data de la table PALETTE_COLORS
            DISP_LEFT_ON, DISP_LEFT_OFF, DISP_LEFT_BACK,
            DISP_RIGHT_ON, DISP_RIGHT_OFF, DISP_RIGHT_BACK,
            PANEL_TOP, PANEL_LOW,
            KEY_OUTLINE,
            KEY_TOP_FRONT, KEY_MID_FRONT, KEY_MID_BACK, KEY_LOW_FRONT, KEY_LOW_BACK,
            KEY_F_MID_FRONT, KEY_F_MID_BACK, KEY_F_LOW_BACK,
            KEY_G_MID_FRONT, KEY_G_MID_BACK, KEY_G_LOW_BACK,
            KEY_CLEAR_TOP_FRONT;

            public int INDEX() {
                return ordinal() + 1;
            }   //  INDEX 0 pour identifiant utilisateur
        }
    }

    public static int getSp15cTableDataFieldsCount(String tableName) {
        return SP15C_TABLES.valueOf(tableName).getDataFieldsCount();
    }

    public static int getSp15cTableIndex(String tableName) {
        return SP15C_TABLES.valueOf(tableName).INDEX();
    }

    public static String getSp15cTableDescription(String tableName) {
        return SP15C_TABLES.valueOf(tableName).DESCRIPTION();
    }

    public static String getPaletteColorsTableName() {
        return SP15C_TABLES.PALETTE_COLORS.toString();
    }

    public static int getPaletteColorDisp1OnIndex() {
        return Sp15cTableDataFields.paletteColors.DISP_LEFT_ON.INDEX();
    }

    public static int getPaletteColorDisp1OffIndex() {
        return Sp15cTableDataFields.paletteColors.DISP_LEFT_OFF.INDEX();
    }

    public static int getPaletteColorDisp1BackIndex() {
        return Sp15cTableDataFields.paletteColors.DISP_LEFT_BACK.INDEX();
    }

    public static int getPaletteColorDisp2OnIndex() {
        return Sp15cTableDataFields.paletteColors.DISP_RIGHT_ON.INDEX();
    }

    public static int getPaletteColorDisp2OffIndex() {
        return Sp15cTableDataFields.paletteColors.DISP_RIGHT_OFF.INDEX();
    }

    public static int getPaletteColorDisp2BackIndex() {
        return Sp15cTableDataFields.paletteColors.DISP_RIGHT_BACK.INDEX();
    }

    public static int getPaletteColorPanelTopIndex() {
        return Sp15cTableDataFields.paletteColors.PANEL_TOP.INDEX();
    }

    public static int getPaletteColorPanelLowIndex() {
        return Sp15cTableDataFields.paletteColors.PANEL_LOW.INDEX();
    }

    public static int getPaletteColorKeyOutlineIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_OUTLINE.INDEX();
    }

    public static int getPaletteColorKeyTopFrontIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_TOP_FRONT.INDEX();
    }

    public static int getPaletteColorKeyMidFrontIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_MID_FRONT.INDEX();
    }

    public static int getPaletteColorKeyMidBackIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_MID_BACK.INDEX();
    }

    public static int getPaletteColorKeyLowFrontIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_LOW_FRONT.INDEX();
    }

    public static int getPaletteColorKeyLowBackIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_LOW_BACK.INDEX();
    }

    public static int getPaletteColorKeyFMidFrontIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_F_MID_FRONT.INDEX();
    }

    public static int getPaletteColorKeyFMidBackIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_F_MID_BACK.INDEX();
    }

    public static int getPaletteColorKeyFLowBackIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_F_LOW_BACK.INDEX();
    }

    public static int getPaletteColorKeyGMidFrontIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_G_MID_FRONT.INDEX();
    }

    public static int getPaletteColorKeyGMidBackIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_G_MID_BACK.INDEX();
    }

    public static int getPaletteColorKeyGLowBackIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_G_LOW_BACK.INDEX();
    }

    public static int getPaletteColorKeyClearTopFrontIndex() {
        return Sp15cTableDataFields.paletteColors.KEY_CLEAR_TOP_FRONT.INDEX();
    }

    public static String[][] getPaletteColorsInits() {
        final String[][] PALETTE_COLORS_INITS = {
                {TABLE_IDS.LABEL.toString(),
                        "Display Left On", "Display Left Off", "Display Left Back",
                        "Display Right On", "Display Right Off", "Display Right Back",
                        "Panel Top", "Panel Low",
                        "Key Outline",
                        "Key Top Front", "Key Mid Front", "Key Mid Back", "Key Low Front", "Key Low Back",
                        "Key F Mid Front", "Key F Mid Back", "Key F Low Back",
                        "Key G Mid Front", "Key G Mid Back", "Key G Low Back",
                        "Key Clear Top Front"},
                {TABLE_IDS.KEYBOARD.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString(), InputButtonsActivity.KEYBOARDS.HEX.toString()},
                {TABLE_IDS.REGEXP.toString(), REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS, REGEXP_SIX_CHARS},
                {TABLE_IDS.REGEXP_ERROR_MESSAGE.toString(), REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE, REGEXP_SIX_CHARS_ERROR_MESSAGE},
                {TABLE_IDS.DEFAULT.toString(),
                        "000000", "BFAF00", "D9C700",
                        "000000", "BFAF00", "D9C700",
                        "A0A0A0", "BFBFBF",
                        "FF0000",
                        "000000", "FFFFFF", "606060", "A1BBFF", "404040",
                        "000000", "EEBD34", "8E711F",
                        "000000", "A1BBFF", "607099",
                        "B80000"}
        };
        return PALETTE_COLORS_INITS;
    }

    public static String getStackRegsTableName() {
        return SP15C_TABLES.STACK_REGS.toString();
    }

    public static String getImStackRegsTableName() {
        return SP15C_TABLES.IM_STACK_REGS.toString();
    }

    public static String getFlagsTableName() {
        return SP15C_TABLES.FLAGS.toString();
    }

    public static String getRegsTableName() {
        return SP15C_TABLES.REGS.toString();
    }

    public static String getRetStackTableName() {
        return SP15C_TABLES.RET_STACK.toString();
    }

    public static String getProgLinesTableName() {
        return SP15C_TABLES.PROG_LINES.toString();
    }

    public static String getParamsTableName() {
        return SP15C_TABLES.PARAMS.toString();
    }

}
