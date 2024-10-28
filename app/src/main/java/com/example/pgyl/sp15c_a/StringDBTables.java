package com.example.pgyl.sp15c_a;

public class StringDBTables {

    public static final int DATA_VERSION = 53;   //   A augmenter dès que les données éventuelles dans la DB existante ne seront plus comptatibles après changements

    enum SP15C_TABLES {   // Les tables, rattachées à leurs champs de data
        STACK_REGS(Sp15cTableDataFields.StackRegs.class, "Stack Registers"),
        FLAGS(Sp15cTableDataFields.StackRegs.class, "Flags"),
        REGS(Sp15cTableDataFields.Regs.class, "Regs"),
        RET_STACK(Sp15cTableDataFields.RetStack.class, "RetStack"),
        PROG_LINES(Sp15cTableDataFields.ProgLines.class, "ProgLines"),
        PARAMS(Sp15cTableDataFields.Params.class, "Params");

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
            VALUE1, VALUE2, VALUE3;     //  3 OpCodes max par ligne

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

    public static String getStackRegsTableName() {
        return SP15C_TABLES.STACK_REGS.toString();
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
