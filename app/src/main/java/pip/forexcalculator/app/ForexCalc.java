package pip.forexcalculator.app;

import java.io.Serializable;
import static pip.forexcalculator.app.AppConfig.JPY_COST;

/**
 * Created by b on 1/13/2017.
 */

public class ForexCalc implements Serializable{

    public double grossloss;
    public boolean gross_first;
    public CalcInfo[] calcInfos;

    public ForexCalc() {

        gross_first = false;
        grossloss = 0;
        calcInfos = new CalcInfo[6];
        for (int i = 0; i < 6; i ++) {
            calcInfos[i] = new CalcInfo();
        }
    }

    public void deleteValues() {
        for (int i = 0; i < 6; i ++) {
            CalcInfo calcInfo = calcInfos[i];
            calcInfo.entry = 0;
            calcInfo.exit = 0;
            calcInfo.lotsize = 0;
            calcInfo.leverage = 100;
            calcInfo.profitloss = 0;
            calcInfo.amount = 0;

            calcInfo.islong = false;
            calcInfo.isJYN = false;
        }
        gross_first = false;
        grossloss = 0;
    }

    public void calcProtfitFromEntryAndExit(int pos) {
        CalcInfo calcInfo = calcInfos[pos];

        double entry = calcInfo.entry;
        double exit = calcInfo.exit;

        if (entry == 0 || exit == 0) { return; }

        double result = entry - exit;
        if (entry < exit) {
            result = exit - entry;
        }
        int amount = (int)(result * 10000 + 0.5d);
        if (calcInfo.isJYN) {
            amount = (int)(result * 100 * JPY_COST + 0.5d);
        }
        calcInfo.profitloss = calcInfo.lotsize * 10 * (double)amount;
        if (calcInfo.islong) {
            if (entry > exit) {
                calcInfo.profitloss *= -1;
            }
        }
        else {
            if (entry < exit) {
                calcInfo.profitloss *= -1;
            }
        }
        if (gross_first) {
            double gross = grossloss;
            int k = 0;
            for (int i = 0; i < 6; i++) {
                calcInfo = calcInfos[i];
                if (calcInfo.lotsize != 0 ) {
                    if (calcInfo.entry != 0 && calcInfo.exit != 0) {
                        gross -= calcInfo.profitloss;
                    }
                    else {
                        k ++;
                    }
                }
            }
            for (int i = 0; i < 6; i ++) {
                calcInfo = calcInfos[i];
                if (calcInfo.lotsize != 0 && (calcInfo.entry == 0 || calcInfo.exit == 0) ) {
                    calcInfo.profitloss = gross / k;
                }
            }
        }
        grossloss = 0;
        for (int i = 0; i < 6; i++) {
            calcInfo = calcInfos[i];
            grossloss += calcInfo.profitloss;
        }
    }

    public void calcExitFromEntryAndProfit(int pos) {
        CalcInfo calcInfo = calcInfos[pos];
        if (calcInfo.lotsize != 0 && calcInfo.entry != 0) {
//            if (calcInfo.isJYN)
//                calcInfo.exit = calcInfo.profitloss * 1.25 / calcInfo.lotsize / 100000 + calcInfo.entry;
//            else
            if (calcInfo.islong) {
                if (calcInfo.isJYN)
                    calcInfo.exit = calcInfo.profitloss / calcInfo.lotsize / 1000 / JPY_COST + calcInfo.entry;
                else
                    calcInfo.exit = calcInfo.profitloss / calcInfo.lotsize / 100000 + calcInfo.entry;
            }
            else {
                if (calcInfo.isJYN)
                    calcInfo.exit = -calcInfo.profitloss / calcInfo.lotsize / 1000 / JPY_COST + calcInfo.entry;
                else
                    calcInfo.exit = -calcInfo.profitloss / calcInfo.lotsize / 100000 + calcInfo.entry;
            }
        }

        grossloss = 0;
        for (int i = 0; i < 6; i ++) {
            calcInfo = calcInfos[i];
            grossloss += calcInfo.profitloss;
        }
        calcAmount();
    }

    public void calcExitFromEntryAndGross() {
        int ncount = 0;
        double c = grossloss;
        for (int i = 0; i < 6; i ++) {
            CalcInfo calcInfo = calcInfos[i];
            if (calcInfo.lotsize != 0) {
                if (calcInfo.profitloss != 0 && calcInfo.entry != 0 && calcInfo.exit != 0) {
                    c -= calcInfo.profitloss;
                }
                else {
                    ncount ++;
                }
            }
        }
        c /= ncount;
        for (int i = 0; i < 6; i ++) {
            CalcInfo calcInfo = calcInfos[i];
            if (calcInfo.lotsize != 0) {
                if (calcInfo.profitloss == 0 || calcInfo.entry == 0 || calcInfo.exit == 0)
                    calcInfo.profitloss = c;
            }
        }
        for (int i = 0; i < 6; i ++) {
            CalcInfo calcInfo = calcInfos[i];
            if (calcInfo.lotsize != 0 && calcInfo.entry != 0) {
//                if (calcInfo.isJYN)
//                    calcInfo.exit = calcInfo.profitloss * 1.25 / calcInfo.lotsize / 100000 + calcInfo.entry;
//                else
                if (calcInfo.islong) {
                    if (calcInfo.isJYN)
                        calcInfo.exit = calcInfo.profitloss / calcInfo.lotsize / 1000 / JPY_COST + calcInfo.entry;
                    else
                        calcInfo.exit = calcInfo.profitloss / calcInfo.lotsize / 100000 + calcInfo.entry;
                }
                else {
                    if (calcInfo.isJYN)
                        calcInfo.exit = -calcInfo.profitloss / calcInfo.lotsize / 1000 / JPY_COST + calcInfo.entry;
                    else
                        calcInfo.exit = -calcInfo.profitloss / calcInfo.lotsize / 100000 + calcInfo.entry;
                }
            }
        }
        calcAmount();
    }

    public void calcAmount() {
        for (int i = 0; i < 6; i ++) {
            calcAmountPerTrade(i);
            //calcProtfitFromEntryAndExit(i);
        }
    }

    public void buttonClicked(int i) {
        calcAmountPerTrade(i);
        calcProtfitFromEntryAndExit(i);
    }

    public void calcAmountBySetting() {
        for (int i = 0; i < 6; i ++) {
            calcAmountPerTrade(i);
            calcProtfitFromEntryAndExit(i);
        }
    }

    private void calcAmountPerTrade(int i) {
        CalcInfo calcInfo = calcInfos[i];

        double entry = calcInfo.entry;
        double exit = calcInfo.exit;

        double result = entry - exit;
        if (entry < exit) {
            result = exit - entry;
        }
        calcInfo.amount = (int)(result * 10000 + 0.5d);
        if (calcInfo.isJYN) {
            calcInfo.amount = (int)(result * 100 + 0.5d);
        }
        if (calcInfo.islong) {
            if (entry > exit) {
                calcInfo.amount *= -1;
            }
        }
        else {
            if (entry < exit) {
                calcInfo.amount *= -1;
            }
        }
    }
}
