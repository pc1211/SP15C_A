package com.example.pgyl.sp15c_a;

import static com.example.pgyl.sp15c_a.Alu.OPERATIONS;

public class ProgLine {

    private final int MAX_OPS = 5;
    private int number;
    private OPERATIONS[] ops;

    public ProgLine() {
        ops = new OPERATIONS[MAX_OPS];
        for (int i = 0; i <= (ops.length - 1); i = i + 1) {
            ops[i] = null;
        }
    }

    public OPERATIONS[] getOps() {
        return ops;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public int getOpsSize() {
        return ops.length;
    }

    public void setOp(int index, OPERATIONS op) {
        ops[index] = op;
    }

    public OPERATIONS getOp(int index) {
        return ops[index];
    }
}
