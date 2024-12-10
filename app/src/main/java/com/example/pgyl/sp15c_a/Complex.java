package com.example.pgyl.sp15c_a;

public class Complex {   //  TOUT est en radians, (xr,xi)) et éventuellement (yr,yi) -> f(x) ou f(x,y) -> dans (xr,xi))

    private Double xr;
    private Double xi;
    private Double yr;
    private Double yi;

    public Complex() {
        init();
    }

    private void init() {
        setX(0.0, 0.0);
        setY(0.0, 0.0);
    }

    public void close() {
        //  NOP
    }

    public void setX(Double a, Double b) {   //  a+bi
        xr = a;
        xi = b;
    }

    public void setY(Double a, Double b) {   //  a+bi
        yr = a;
        yi = b;
    }

    public Double getXRe() {
        return xr;
    }

    public Double getXIm() {
        return xi;
    }

    public void plus() {
        setX(yr + xr, yi + xi);   //  x = y + x
    }

    public void minus() {
        setX(yr - xr, yi - xi);   //  x = y - x
    }

    public void mult() {
        setX(yr * xr - yi * xi, yr * xi + yi * xr);   //  x = y * x
    }

    public void inv() {
        double tr = xr * xr + xi * xi;
        setX(xr / tr, -xi / tr);   //  x = 1/x
    }

    public void div() {
        inv();    //  x = 1/x
        mult();   //  x = y * 1/x cad y / x
    }

    public void sqr() {
        setX(xr * xr - xi * xi, xr * xi * 2);   //  x = x * x cad x^2
    }

    public void chs() {
        setX(-xr, -xi);   //  x = -x
    }

    public void multI() {   //  x = x * i
        setX(-xi, xr);
    }

    public void divI() {
        setX(xi, -xr);   //  x = x / i
    }

    public void multC(Double coeff) {
        setX(xr * coeff, xi * coeff);   //  x = x * coeff
    }

    public void divC(Double coeff) {
        setX(xr / coeff, xi / coeff);   //  x = x / coeff
    }

    public void abs() {
        setX(Math.sqrt(xr * xr + xi * xi), 0.0);   //  x = abs(x)
    }

    public void pol() {
        setX(Math.hypot(xr, xi), Math.atan2(xi, xr));   //  Résultat gardé en radians
    }

    public void rect() {   //  Suppose xi en radians
        setX(xr * Math.cos(xi), xi = xr * Math.sin(xi));
    }

    public void ln() {
        pol();
        xr = Math.log(xr);   //  Math.log est en fait ln
    }

    public void log10() {
        ln();
        divC(Math.log(10.0));
    }

    public void exp() {
        double tr = Math.exp(xr);
        setX(tr * Math.cos(xi), tr * Math.sin(xi));   //  x = e^x
    }

    public void exp10() {   //  10^x
        multC(Math.log(10));   //   x = x * ln(10)
        exp();   //  x = e^(x*ln(10)) cad 10^x
    }

    public void pow() {
        double tr = xr;
        double ti = xi;   //  t = x orig (exposant)
        setX(yr, yi);   //  x = base
        ln();      //  x = ln(base)
        setY(tr, ti);   //  y = x orig (exposant)
        mult();   //  y = exposant*ln(base)
        exp();   //  x = e^(exposant*ln(base)) cad base^exposant
    }

    public void sqrt() {
        setY(xr, xi);   //  y = x
        setX(0.5, 0.0);   //  x = 1/2
        pow();   //  x = x^(1/2) cad sqrt(x)
    }

    public void sin() {
        multI();   //  x = ix
        exp();   //   x = e^ix
        setY(xr, xi);   //  y = e^ix
        inv();   //  x = e^-ix
        minus();   //  x = e^ix-e^-ix
        divI();   //  x = (e^ix-e^-ix)/i
        divC(2.0);   //  x = (e^ix-e^-ix)/(2i)
    }

    public void cos() {
        multI();   //  x = ix
        exp();   //   x = e^ix
        setY(xr, xi);   //  y = e^ix
        inv();   //  x = e^-ix
        plus();   //  x = e^ix+e^-ix
        divC(2.0);
        ;   //  x = (e^ix+e^-ix)/2
    }

    public void tan() {
        double tr = xr;
        double ti = xi;   //  t = x orig
        sin();   //  x = sin(x)
        double ur = xr;
        double ui = xi;   //  u = sin(x)
        setX(tr, ti);  //  x = x orig
        cos();   //  x = cos(x)
        setY(ur, ui);   //  y = sin(x)
        div();   //  y = sin(x) / cos(x) cad tan(x)
    }

    public void asin() {
        double tr = xr;
        double ti = xi;   //  t = x orig
        sqr();   //  x = x^2
        chs();   //  x = -x^2
        xr = xr + 1;   //  x = 1-x^2
        sqrt();   //  x = sqrt(1-x^2)
        setY(xr, xi);   //  y = sqrt(1-x^2)
        setX(tr, ti);  //  x = x orig
        multI();   //  x = ix
        plus();   //  x = ix+sqrt(1-x^2)
        ln();   //  x = ln(ix+sqrt(1-x^2))
        divI();   //  x = -iln(ix+sqrt(1-x^2))   (-i=1/i)
    }

    public void acos() {
        asin();   //  x = asin(x)
        setY(Math.PI / 2, 0.0);   //  x = pi/2
        minus();   //  x = pi/2-asin(x)
    }

    public void atan() {
        multI();   //  x = ix
        double tr = xr;
        double ti = xi;   //  t = ix
        xr = xr + 1;   //  x = 1+ix
        ln();   //  x = ln(1+ix)
        setY(xr, xi);    //  y = ln(1+ix)
        setX(tr, ti);   //  x = ix
        chs();   //  x = -ix
        xr = xr + 1;   //  x = 1-ix
        ln();   //  x = ln(1-ix)
        minus();   //  x = ln(1+ix)-ln(1-ix)
        divI();   //  x = -i(ln(1+ix)-ln(1-ix))   (-i = 1/i)
        divC(2.0);    //  x = i(ln(1+ix)-ln(1-ix))/2
    }

    public void sinh() {
        exp();
        setY(xr, xi);   //  y = e^x
        inv();   //  x = e^-x
        minus();   //  x = e^x-e^-x
        divC(2.0);   //  x = (e^x-e^-x)/2
    }

    public void cosh() {
        exp();
        setY(xr, xi);   //  y = e^x
        inv();   //  x = e^-x
        plus();   //  x = e^x+e^-x
        divC(2.0);   //  x = (e^x+e^-x)/2
    }

    public void tanh() {
        double tr = xr;
        double ti = xi;   //  t = x orig
        sinh();   //  x = sin(x)
        double ur = xr;
        double ui = xi;   //  u = sinh(x)
        setX(tr, ti);   //  x = x orig
        cosh();   //  x = cosh(x)
        setY(ur, ui);  //  y = sinh(x)
        div();   //  y = sinh(x) / cosh(x) cad tanh(x)
    }

    public void asinh() {
        multI();   //  x = ix
        asin();   //  x = asin(ix)
        divI();   //  x = -iasin(ix)   (-i = 1/i)
    }

    public void acosh() {
        double tr = xr;
        double ti = xi;   //  t = x orig
        sqr();   //  x = x^2
        xr = xr - 1;   //  x = x^2-1
        sqrt();   //  x = sqrt(x^2-1)
        setY(tr, ti);   //  y = x orig
        plus();   //  x = x+sqrt(x^2-1)
        ln();   //  x = ln(x+sqrt(x^2-1))
    }

    public void atanh() {
        multI();   //  x = ix
        atan();   //  x = atan(ix)
        divI();   //  x = -iatan(ix)   (-i = 1/i)
    }

}
