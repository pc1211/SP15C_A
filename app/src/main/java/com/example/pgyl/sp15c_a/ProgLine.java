package com.example.pgyl.sp15c_a;

import static com.example.pgyl.sp15c_a.Alu.OPS;

public class ProgLine {

    private final int MAX_OPS = 5;

    private OPS[] ops;

    public ProgLine() {
        ops = new OPS[MAX_OPS];
        for (int i = 0; i <= (ops.length - 1); i = i + 1) {
            ops[i] = null;
        }
    }

    public OPS[] getOps() {
        return ops;
    }

    public int getOpsSize() {
        return ops.length;
    }

    public void setOp(int index, OPS op) {
        ops[index] = op;
    }

    public OPS getOp(int index) {
        return ops[index];
    }
}
