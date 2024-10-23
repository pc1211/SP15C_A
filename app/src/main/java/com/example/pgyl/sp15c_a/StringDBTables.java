package com.example.pgyl.sp15c_a;

public class StringDBTables {

    public static final int DATA_VERSION = 8;   //   A augmenter dès que les données éventuelles dans la DB existante ne seront plus comptatibles après changements

    enum SP15C_TABLES {   // Les tables, rattachées à leurs champs de data
        STACK_REGS(Sp15cTableDataFields.StackRegs.class, "Stack Registers"),

        ;

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

    //region STACK_REGS
    public static String getStackRegsTableName() {
        return SP15C_TABLES.STACK_REGS.toString();
    }

//endregion

}
