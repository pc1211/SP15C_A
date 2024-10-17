package com.example.pgyl.sp15c_a;

import com.example.pgyl.sp15c_a.Alu.OPS;

public class ProgLine {

    public enum LINE_OPS {   //  A laisser dans cet ordre (naturel): cf progLineToString()
        BASE, A4OP, DOT, A09, AE, I, DIM, INDI, RAND, SIGMA_PLUS, CHS, GHOST1, GHOST2;   //  (Sauf A4OP, A09, AE, GHOST1, GHOST2) Doivent porter le même nom que les OPS auxquels ils font référence (cf Main prepareMultiOpsProgLine())

        public int INDEX() {
            return ordinal();
        }
    }

    public static LINE_OPS getLineOpByOp(OPS op) {
        LINE_OPS res = null;
        try {
            res = LINE_OPS.valueOf(op.toString());
        } catch (IllegalArgumentException iae) {
            //  NOP
        }
        return res;
    }

    public OPS[] ops;
    public int destProgLineNumber;
    public String symbol;

    public ProgLine() {
        ops = new OPS[LINE_OPS.values().length];
        destProgLineNumber = 0;
        symbol = "";
    }
}
