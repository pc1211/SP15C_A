package com.example.pgyl.sp15c_a;

import com.example.pgyl.sp15c_a.Executor.OPS;

public class PairOp {
    OPS op1;
    OPS op2;

    PairOp(OPS op1, OPS op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PairOp)) return false;
        PairOp pairOp = (PairOp) obj;
        return ((op1.equals(pairOp.op1)) && (op2.equals(pairOp.op2)));
    }

    @Override
    public int hashCode() {
        return (op1.SYMBOL() + op2.SYMBOL()).hashCode();
    }
}
