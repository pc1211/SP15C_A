package com.example.pgyl.sp15c_a;

import com.example.pgyl.pekislib_a.StringDB;

import java.util.ArrayList;

import static com.example.pgyl.pekislib_a.StringDB.TABLE_DATA_INDEX;
import static com.example.pgyl.pekislib_a.StringDB.TABLE_ID_INDEX;
import static com.example.pgyl.sp15c_a.StringDBTables.getSp15cTableDataFieldsCount;

public class StringDBUtils {

    //region TABLES
    public static void createSp15cTableIfNotExists(StringDB stringDB, String tableName) {
        stringDB.createTableIfNotExists(tableName, 1 + getSp15cTableDataFieldsCount(tableName));   //  Champ ID + Données;
    }

    public static void initializeTableStackRegs(StringDB stringDB) {
        //  stringDB.insertOrReplaceRows(getStackRegsTableName(), getSp15cStackRegsInits());
    }
    //endregion

    public static String[][] loadRowsFromDB(StringDB stringDB, String tableName) {
        return stringDB.selectRows(tableName, null);
    }

    public static void saveRowsToDB(StringDB stringDB, String tableName, String[][] values) {
        stringDB.deleteAndInsertRows(tableName, null, values);
    }

    public static double[] rowsToDoubleArray(String[][] arrRows, int arrLength) {
        double[] res = null;
        if (arrLength > 0) {
            res = new double[arrLength];   //  Par défaut
            if (arrRows != null) {
                int n = arrRows.length;
                res = new double[n];
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res[Integer.parseInt(arrRows[i][TABLE_ID_INDEX])] = Double.parseDouble(arrRows[i][TABLE_DATA_INDEX]);
                }
            }
        }
        return res;
    }

    public static String[][] doubleArrayToRows(double[] arr) {
        String[][] res = null;
        if (arr != null) {
            int n = arr.length;
            if (n > 0) {
                res = new String[n][2];   //  2: champ ID + champ VALUE
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res[i][TABLE_ID_INDEX] = String.valueOf(i);
                    res[i][TABLE_DATA_INDEX] = String.valueOf(arr[i]);
                }
            }
        }
        return res;
    }

    public static boolean[] rowsToBooleanArray(String[][] arrRows, int arrLength) {
        boolean[] res = null;
        if (arrLength > 0) {
            res = new boolean[arrLength];   //  Par défaut
            if (arrRows != null) {
                int n = arrRows.length;
                res = new boolean[n];
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res[Integer.parseInt(arrRows[i][TABLE_ID_INDEX])] = (arrRows[i][TABLE_DATA_INDEX].equals("1"));
                }
            }
        }
        return res;
    }

    public static String[][] booleanArrayToRows(boolean[] arr) {
        String[][] res = null;
        if (arr != null) {
            int n = arr.length;
            if (n > 0) {
                res = new String[n][2];   //  2: champ ID + champ VALUE
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res[i][TABLE_ID_INDEX] = String.valueOf(i);
                    res[i][TABLE_DATA_INDEX] = (arr[i] ? "1" : "0");
                }
            }
        }
        return res;
    }

    public static int[] rowsToIntArray(String[][] arrRows, int arrLength) {
        int[] res = null;
        if (arrLength > 0) {
            res = new int[arrLength];    //  Par défaut
            if (arrRows != null) {
                int n = arrRows.length;
                res = new int[n];   //  On s'adapte à la réalité
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res[Integer.parseInt(arrRows[i][TABLE_ID_INDEX])] = Integer.parseInt(arrRows[i][TABLE_DATA_INDEX]);
                }
            }
        }
        return res;
    }

    public static String[][] intArrayToRows(int[] arr) {
        String[][] res = null;
        if (arr != null) {
            int n = arr.length;
            res = new String[n][2];   //  2: champ ID + champ VALUE
            if (n > 0) {
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res[i][TABLE_ID_INDEX] = String.valueOf(i);
                    res[i][TABLE_DATA_INDEX] = String.valueOf(arr[i]);
                }
            }
        }
        return res;
    }

    public static double[] doubleListToArray(ArrayList<Double> arrayList) {
        double[] res = null;
        if (arrayList != null) {
            int n = arrayList.size();
            if (n > 0) {
                res = new double[n];
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res[i] = arrayList.get(i);
                }
            }
        }
        return res;
    }

    public static ArrayList<Double> doubleArrayToList(double[] arr) {
        ArrayList<Double> res = null;
        if (arr != null) {
            int n = arr.length;
            if (n > 0) {
                res = new ArrayList<Double>();
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res.add(arr[i]);
                }
            }
        }
        return res;
    }

    public static int[] intListToArray(ArrayList<Integer> arrayList) {
        int[] res = null;
        if (arrayList != null) {
            int n = arrayList.size();
            if (n > 0) {
                res = new int[n];
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res[i] = arrayList.get(i);
                }
            }
        }
        return res;
    }

    public static ArrayList<Integer> intArrayToList(int[] arr) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        if (arr != null) {
            int n = arr.length;
            if (n > 0) {
                for (int i = 0; i <= (n - 1); i = i + 1) {
                    res.add(arr[i]);
                }
            }
        }
        return res;
    }

}
