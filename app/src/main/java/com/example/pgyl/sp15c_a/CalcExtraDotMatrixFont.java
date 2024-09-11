package com.example.pgyl.sp15c_a;

import com.example.pgyl.pekislib_a.DotMatrixFont;
import com.example.pgyl.pekislib_a.DotMatrixSymbol;

public class CalcExtraDotMatrixFont extends DotMatrixFont {

    private DotMatrixFont defaultFont;

    public CalcExtraDotMatrixFont(DotMatrixFont defaultFont) {
        super();

        this.defaultFont = defaultFont;
        init();
    }

    private void init() {
        //  Caractères redéfinis pour l'affichage du temps ("." et ":") (plus fins que la fonte par défaut de DotMatrixDisplayView)
        final DotMatrixSymbol[] EXTRA_FONT_SYMBOLS = {
                new DotMatrixSymbol(44, ',', new int[][]{{1}}),   //  "," pour le point séparateur de milliers
                new DotMatrixSymbol(46, '.', new int[][]{{1}}),
                new DotMatrixSymbol(58, ':', new int[][]{{0}, {0}, {1}, {0}, {1}, {0}, {0}}),
                new DotMatrixSymbol(69, 'E', new int[][]{{1, 1, 1, 1, 1}, {1, 1, 0, 0, 0}, {1, 1, 0, 0, 0}, {1, 1, 1, 1, 0}, {1, 1, 0, 0, 0}, {1, 1, 0, 0, 0}, {1, 1, 1, 1, 1}}),
        };

        final int EXTRA_FONT_RIGHT_MARGIN = 1;

        setSymbols(EXTRA_FONT_SYMBOLS);
        setRightMargin(EXTRA_FONT_RIGHT_MARGIN);

        DotMatrixSymbol symbol = getSymbolByCode(46);   //  Dot "."
        symbol.setOverwrite(true);   //  Le "." surcharge le symbole précédent (en-dessous dans sa marge droite)
        symbol.setPosOffset(-symbol.getDimensions().width, defaultFont.getDimensions().height);

        symbol = getSymbolByCode(44);   //  ","" pour le point séparateur de milliers
        symbol.setOverwrite(true);   //  Surcharge le symbole précédent
        symbol.setPosOffset(-symbol.getDimensions().width, -1);   //  Dans sa marge supérieure droite
        symbol = null;
    }

}
