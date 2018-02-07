package pip.forexcalculator.app;

import java.io.Serializable;

/**
 * Created by b on 1/13/2017.
 */

public class CalcInfo {
    public double entry;
    public double exit;
    public double lotsize;
    public int leverage;
    public double profitloss;
    public boolean islong;
    public boolean isJYN;
    public int amount;

    public CalcInfo() {
        entry = 0;
        exit = 0;
        lotsize = 0;
        leverage = 100;
        profitloss = 0;
        amount = 0;

        islong = false;
        isJYN = false;
    }
}
