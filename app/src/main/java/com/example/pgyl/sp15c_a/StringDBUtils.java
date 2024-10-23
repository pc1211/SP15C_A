package com.example.pgyl.sp15c_a;

import com.example.pgyl.pekislib_a.StringDB;

import static com.example.pgyl.pekislib_a.StringDB.TABLE_DATA_INDEX;
import static com.example.pgyl.pekislib_a.StringDB.TABLE_ID_INDEX;
import static com.example.pgyl.sp15c_a.StringDBTables.getSp15cTableDataFieldsCount;
import static com.example.pgyl.sp15c_a.StringDBTables.getStackRegsTableName;

public class StringDBUtils {

    //region TABLES
    public static void createSp15cTableIfNotExists(StringDB stringDB, String tableName) {
        stringDB.createTableIfNotExists(tableName, 1 + getSp15cTableDataFieldsCount(tableName));   //  Champ ID + Données;
    }

    public static void initializeTableStackRegs(StringDB stringDB) {
        //  stringDB.insertOrReplaceRows(getStackRegsTableName(), getSp15cStackRegsInits());
    }
    //endregion

    public static String[][] getDBStackRegs(StringDB stringDB) {
        return stringDB.selectRows(getStackRegsTableName(), null);
    }

    public static void saveDBStackRegs(StringDB stringDB, String[][] values) {
        stringDB.deleteRows(getStackRegsTableName(), null);
        stringDB.insertOrReplaceRows(getStackRegsTableName(), values);
    }

    public static double[] stackRegRowsToStackRegs(String[][] stackRegsRows) {
        double[] res = new double[8];
        if (stackRegsRows != null) {
            int n = stackRegsRows.length;
            for (int i = 0; i <= (n - 1); i = i + 1) {
                res[Integer.parseInt(stackRegsRows[i][TABLE_ID_INDEX])] = Double.parseDouble(stackRegsRows[i][TABLE_DATA_INDEX]);
            }
        }
        return res;
    }

    public static String[][] stackRegsToStackRegsRows(double[] stackRegs) {
        String[][] res = new String[8][2];   //  2: champ ID + champ VALUE
        int n = stackRegs.length;
        for (int i = 0; i <= (n - 1); i = i + 1) {
            res[i][TABLE_ID_INDEX] = String.valueOf(i);
            res[i][TABLE_DATA_INDEX] = String.valueOf(stackRegs[i]);
        }
        return res;
    }

}
