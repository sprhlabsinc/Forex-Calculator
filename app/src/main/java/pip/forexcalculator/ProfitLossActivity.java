package pip.forexcalculator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

import pip.forexcalculator.app.CalcInfo;
import pip.forexcalculator.app.ForexCalc;

import static pip.forexcalculator.app.AppConfig.JPY_COST;

public class ProfitLossActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;

    private TextView[] title_txt = new TextView[6], profit_loss_title_txt = new TextView[6];
    private Switch[] setting_switch = new Switch[6];
    private TextView[] mark_txt = new TextView[6];

    private EditText[] entry_txt = new EditText[6], exit_txt = new EditText[6], lotsize_txt = new EditText[6], xtime_txt = new EditText[6];
    private EditText[] profit_loss_txt = new EditText[6], gross_profit_loss_txt = new EditText[3];
    private TextView[] amount_pip_txt = new TextView[6], one_pip_worth_txt = new TextView[6], total_pip_worth_txt = new TextView[6], leverage_txt = new TextView[6], leverage_title_txt = new TextView[6];
    private TextView[] entry_title_txt = new TextView[6], exit_title_txt = new TextView[6];
    private RelativeLayout[] entry_layout = new RelativeLayout[6], exit_layout = new RelativeLayout[6], lotsize_layout = new RelativeLayout[6], xtime_layout = new RelativeLayout[6];
    private RelativeLayout[] profit_layout = new RelativeLayout[6], gross_profit_loss_layout = new RelativeLayout[3];

    private Button[] long_but = new Button[6], short_but = new Button[6], delete_but = new Button[3];

    private ForexCalc forexCalc = new ForexCalc();
    private int mPosition = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        // Making notification bar transparent
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profitloss);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.profit_cell,
                R.layout.profit_cell,
                R.layout.profit_cell};

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                loadValues();
            }
        };
        handler.postDelayed(r, 100);
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onPageSelected(int position) {
            mPosition = position;

            loadValues();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }

        @Override
        public void onPageScrollStateChanged(int arg0) { }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        int pos1 = mPosition * 2;
        int pos2 = pos1 + 1;

        for (int i = pos1; i <= pos2; i ++) {
            if (v == long_but[i] || v == short_but[i]) {
                if (v == long_but[i]) {
                    forexCalc.calcInfos[i].islong = true;
                } else if (v == short_but[i]) {
                    forexCalc.calcInfos[i].islong = false;
                }
                if (forexCalc.gross_first && forexCalc.calcInfos[i].profitloss != 0 && forexCalc.calcInfos[i].entry != 0) {
                    forexCalc.calcExitFromEntryAndProfit(i);
                    if (forexCalc.calcInfos[i].isJYN)
                        exit_txt[i].setText(String.format("%.02f", forexCalc.calcInfos[i].exit));
                    else
                        exit_txt[i].setText(String.format("%.04f", forexCalc.calcInfos[i].exit));
                }
                forexCalc.buttonClicked(i);
                CalcInfo calcInfo = forexCalc.calcInfos[i];
                if (calcInfo.profitloss == 0)
                    profit_loss_txt[i].setText("");
                else {
                        profit_loss_txt[i].setText(String.format("%.02f", calcInfo.profitloss));
                }
                if (forexCalc.grossloss == 0)
                    gross_profit_loss_txt[mPosition].setText("");
                else
                    gross_profit_loss_txt[mPosition].setText(String.format("%.02f", forexCalc.grossloss));

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);

                calcWorth(i);
            } else if (v == entry_layout[i]) {
                entry_txt[i].requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(entry_txt[i], InputMethodManager.SHOW_FORCED);
            } else if (v == exit_layout[i]) {
                exit_txt[i].requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(exit_txt[i], InputMethodManager.SHOW_FORCED);
            } else if (v == lotsize_layout[i]) {
                lotsize_txt[i].requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(lotsize_txt[i], InputMethodManager.SHOW_FORCED);
            } else if (v == xtime_layout[i]) {
                xtime_txt[i].requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(xtime_txt[i], InputMethodManager.SHOW_FORCED);
            }
            else if (v == profit_layout[i]) {
                profit_loss_txt[i].requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(profit_loss_txt[i], InputMethodManager.SHOW_FORCED);
            }
        }
        if (v == gross_profit_loss_layout[mPosition]) {
            gross_profit_loss_txt[mPosition].requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(gross_profit_loss_txt[mPosition], InputMethodManager.SHOW_FORCED);
        }
        else if (v == delete_but[mPosition]) {

            forexCalc.deleteValues();
            loadValues();
            lotsize_txt[pos1].requestFocus();
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    0);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int pos1 = mPosition * 2;
        int pos2 = pos1 + 1;

        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

            for (int i = pos1; i <= pos2; i ++) {
                if (v == lotsize_txt[i]) {
                    forexCalc.calcExitFromEntryAndGross();
                    loadCalculates(pos1, "gross_profit");
                    loadCalculates(pos2, "gross_profit");
                    break;
                }
                if (v == entry_txt[i]) {
                    //forexCalc.calcProtfitFromEntryAndExit(i);
                    //loadCalculates(i, "entry");
                    break;
                }
                else if (v == exit_txt[i]) {
                    forexCalc.calcProtfitFromEntryAndExit(i);
                    loadCalculates(pos1, "exit");
                    loadCalculates(pos2, "exit");
                    break;
                }
                else if (v == profit_loss_txt[i]) {
                    forexCalc.calcExitFromEntryAndProfit(i);
                    loadCalculates(i, "profit");
                    break;
                }
            }
            if (v == gross_profit_loss_txt[mPosition]) {
                forexCalc.gross_first = true;
                forexCalc.calcExitFromEntryAndGross();
                loadCalculates(pos1, "gross_profit");
                loadCalculates(pos2, "gross_profit");
            }
            return true;
        }
        return false;
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);

            final int pos1 = position * 2;
            final int pos2 = pos1 + 1;

            mark_txt[pos1] = (TextView) view.findViewById(R.id.mark1_txt);
            mark_txt[pos2] = (TextView) view.findViewById(R.id.mark2_txt);

            setting_switch[pos1] = (Switch) view.findViewById(R.id.setting1_switch);
            setting_switch[pos2] = (Switch) view.findViewById(R.id.setting2_switch);

            title_txt[pos1] = (TextView) view.findViewById(R.id.title1_txt);
            title_txt[pos2] = (TextView) view.findViewById(R.id.title2_txt);

            profit_loss_title_txt[pos1] = (TextView) view.findViewById(R.id.profit_loss1_title_txt);
            profit_loss_title_txt[pos2] = (TextView) view.findViewById(R.id.profit_loss2_title_txt);

            entry_txt[pos1] = (EditText) view.findViewById(R.id.entry1_txt);
            exit_txt[pos1] = (EditText) view.findViewById(R.id.exit1_txt);
            lotsize_txt[pos1] = (EditText) view.findViewById(R.id.lotsize1_txt);
            xtime_txt[pos1] = (EditText) view.findViewById(R.id.xtime1_txt);
            profit_loss_txt[pos1] = (EditText) view.findViewById(R.id.profit_loss1_txt);
            gross_profit_loss_txt[position] = (EditText) view.findViewById(R.id.gross_profit_loss_txt);

            entry_txt[pos2] = (EditText) view.findViewById(R.id.entry2_txt);
            exit_txt[pos2] = (EditText) view.findViewById(R.id.exit2_txt);
            lotsize_txt[pos2] = (EditText) view.findViewById(R.id.lotsize2_txt);
            xtime_txt[pos2] = (EditText) view.findViewById(R.id.xtime2_txt);
            profit_loss_txt[pos2] = (EditText) view.findViewById(R.id.profit_loss2_txt);

            entry_txt[pos1].setOnKeyListener(ProfitLossActivity.this);
            exit_txt[pos1].setOnKeyListener(ProfitLossActivity.this);
            profit_loss_txt[pos1].setOnKeyListener(ProfitLossActivity.this);
            lotsize_txt[pos1].setOnKeyListener(ProfitLossActivity.this);
            gross_profit_loss_txt[position].setOnKeyListener(ProfitLossActivity.this);
            entry_txt[pos2].setOnKeyListener(ProfitLossActivity.this);
            exit_txt[pos2].setOnKeyListener(ProfitLossActivity.this);
            profit_loss_txt[pos2].setOnKeyListener(ProfitLossActivity.this);
            lotsize_txt[pos2].setOnKeyListener(ProfitLossActivity.this);

            entry_title_txt[pos1] = (TextView) view.findViewById(R.id.entry1_title_txt);
            exit_title_txt[pos1] = (TextView) view.findViewById(R.id.exit1_title_txt);
            entry_title_txt[pos2] = (TextView) view.findViewById(R.id.entry2_title_txt);
            exit_title_txt[pos2] = (TextView) view.findViewById(R.id.exit2_title_txt);

            amount_pip_txt[pos1] = (TextView) view.findViewById(R.id.amount_pip1_txt);
            one_pip_worth_txt[pos1] = (TextView) view.findViewById(R.id.one_pip_worth1_txt);
            total_pip_worth_txt[pos1] = (TextView) view.findViewById(R.id.total_pip_worth1_txt);
            leverage_txt[pos1] = (TextView) view.findViewById(R.id.leverage1_txt);
            leverage_title_txt[pos1] = (TextView) view.findViewById(R.id.leverage_title1_txt);

            amount_pip_txt[pos2] = (TextView) view.findViewById(R.id.amount_pip2_txt);
            one_pip_worth_txt[pos2] = (TextView) view.findViewById(R.id.one_pip_worth2_txt);
            total_pip_worth_txt[pos2] = (TextView) view.findViewById(R.id.total_pip_worth2_txt);
            leverage_txt[pos2] = (TextView) view.findViewById(R.id.leverage2_txt);
            leverage_title_txt[pos2] = (TextView) view.findViewById(R.id.leverage_title2_txt);

            entry_layout[pos1] = (RelativeLayout) view.findViewById(R.id.entry1_layout);
            exit_layout[pos1] = (RelativeLayout) view.findViewById(R.id.exit1_layout);
            lotsize_layout[pos1] = (RelativeLayout) view.findViewById(R.id.lotsize1_layout);
            xtime_layout[pos1] = (RelativeLayout) view.findViewById(R.id.xtime1_layout);
            profit_layout[pos1] = (RelativeLayout) view.findViewById(R.id.profit_loss1_layout);
            gross_profit_loss_layout[position] = (RelativeLayout) view.findViewById(R.id.gross_profit_loss_layout);

            entry_layout[pos1].setOnClickListener(ProfitLossActivity.this);
            exit_layout[pos1].setOnClickListener(ProfitLossActivity.this);
            lotsize_layout[pos1].setOnClickListener(ProfitLossActivity.this);
            xtime_layout[pos1].setOnClickListener(ProfitLossActivity.this);
            profit_layout[pos1].setOnClickListener(ProfitLossActivity.this);
            gross_profit_loss_layout[position].setOnClickListener(ProfitLossActivity.this);

            entry_layout[pos2] = (RelativeLayout) view.findViewById(R.id.entry2_layout);
            exit_layout[pos2] = (RelativeLayout) view.findViewById(R.id.exit2_layout);
            lotsize_layout[pos2] = (RelativeLayout) view.findViewById(R.id.lotsize2_layout);
            xtime_layout[pos2] = (RelativeLayout) view.findViewById(R.id.xtime2_layout);
            profit_layout[pos2] = (RelativeLayout) view.findViewById(R.id.profit_loss2_layout);

            entry_layout[pos2].setOnClickListener(ProfitLossActivity.this);
            exit_layout[pos2].setOnClickListener(ProfitLossActivity.this);
            lotsize_layout[pos2].setOnClickListener(ProfitLossActivity.this);
            xtime_layout[pos2].setOnClickListener(ProfitLossActivity.this);
            profit_layout[pos2].setOnClickListener(ProfitLossActivity.this);

            long_but[pos1] = (Button) view.findViewById(R.id.long1_but);
            short_but[pos1] = (Button) view.findViewById(R.id.short1_but);

            long_but[pos1].setOnClickListener(ProfitLossActivity.this);
            short_but[pos1].setOnClickListener(ProfitLossActivity.this);

            long_but[pos2] = (Button) view.findViewById(R.id.long2_but);
            short_but[pos2] = (Button) view.findViewById(R.id.short2_but);

            long_but[pos2].setOnClickListener(ProfitLossActivity.this);
            short_but[pos2].setOnClickListener(ProfitLossActivity.this);

            delete_but[position] = (Button) view.findViewById(R.id.delete_but);
            delete_but[position].setOnClickListener(ProfitLossActivity.this);

            setting_switch[pos1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    forexCalc.calcInfos[pos1].isJYN = !isChecked;
                    forexCalc.calcAmountBySetting();

                    if (forexCalc.calcInfos[pos1].isJYN) {
                        title_txt[pos1].setText("JPY pairs");
                        entry_title_txt[pos1].setText("Entry (1.33)");
                        exit_title_txt[pos1].setText("Exit (1.32)");
                    }
                    else {
                        title_txt[pos1].setText("Not for JPY pairs");
                        entry_title_txt[pos1].setText("Entry (1.3320)");
                        exit_title_txt[pos1].setText("Exit (1.3210)");
                    }
                    calcWorth(pos1);
                    CalcInfo calcInfo = forexCalc.calcInfos[pos1];
                    if (calcInfo.exit != 0) {
                        if (calcInfo.profitloss == 0)
                            profit_loss_txt[pos1].setText("");
                        else {
                                profit_loss_txt[pos1].setText(String.format("%.02f", calcInfo.profitloss));
                        }
                        if (forexCalc.grossloss == 0)
                            gross_profit_loss_txt[mPosition].setText("");
                        else
                            gross_profit_loss_txt[mPosition].setText(String.format("%.02f", forexCalc.grossloss));
                    }
                }
            });

            lotsize_txt[pos1].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                    forexCalc.calcInfos[pos1].lotsize = ss;
                    //calcWorth(pos1);
                }
            });

            entry_txt[pos1].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                    forexCalc.calcInfos[pos1].entry = ss;
                }
            });
            exit_txt[pos1].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                    forexCalc.calcInfos[pos1].exit = ss;
                    forexCalc.calcAmount();
                    calcWorth(pos1);
                }
            });
            xtime_txt[pos1].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    int ss = 0;
                    try {
                        ss = Integer.parseInt(s.toString());
                    } catch (Exception e) {}
                    forexCalc.calcInfos[pos1].leverage = ss;
                    calcWorth(pos1);
                }
            });

            profit_loss_txt[pos1].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                        forexCalc.calcInfos[pos1].profitloss = ss;
                    calcWorth(pos1);
                }
            });

            gross_profit_loss_txt[position].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                    forexCalc.grossloss = ss;
                    calcWorth(pos1);
                    calcWorth(pos2);
                }
            });
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            setting_switch[pos2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    forexCalc.calcInfos[pos2].isJYN = !isChecked;
                    forexCalc.calcAmountBySetting();
                    if (forexCalc.calcInfos[pos2].isJYN) {
                        title_txt[pos2].setText("JPY pairs");
                        entry_title_txt[pos2].setText("Entry (1.33)");
                        exit_title_txt[pos2].setText("Exit (1.32)");
                    }
                    else {
                        title_txt[pos2].setText("Not for JPY pairs");
                        entry_title_txt[pos2].setText("Entry (1.3320)");
                        exit_title_txt[pos2].setText("Exit (1.3210)");
                    }
                    calcWorth(pos2);
                    CalcInfo calcInfo = forexCalc.calcInfos[pos2];
                    if (calcInfo.exit != 0) {
                        if (calcInfo.profitloss == 0)
                            profit_loss_txt[pos2].setText("");
                        else {
                                profit_loss_txt[pos2].setText(String.format("%.02f", calcInfo.profitloss));
                        }
                        if (forexCalc.grossloss == 0)
                            gross_profit_loss_txt[mPosition].setText("");
                        else
                            gross_profit_loss_txt[mPosition].setText(String.format("%.02f", forexCalc.grossloss));
                    }
                }
            });

            lotsize_txt[pos2].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                    forexCalc.calcInfos[pos2].lotsize = ss;
                }
            });

            entry_txt[pos2].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                    forexCalc.calcInfos[pos2].entry = ss;
                }
            });
            exit_txt[pos2].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                    forexCalc.calcInfos[pos2].exit = ss;
                    forexCalc.calcAmount();
                    calcWorth(pos2);
                }
            });
            xtime_txt[pos2].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    int ss = 0;
                    try {
                        ss = Integer.parseInt(s.toString());
                    } catch (Exception e) {}
                    forexCalc.calcInfos[pos2].leverage = ss;
                    calcWorth(pos2);
                }
            });

            profit_loss_txt[pos2].addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    double ss = 0;
                    try {
                        ss = Double.parseDouble(s.toString());
                    } catch (Exception e) {}
                        forexCalc.calcInfos[pos2].profitloss = ss;
                    calcWorth(pos2);
                }
            });

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calcWorth(int pos) {

        CalcInfo calcInfo = forexCalc.calcInfos[pos];
        if (calcInfo.amount == 0 || calcInfo.exit == 0)
            amount_pip_txt[pos].setText("");
        else
            amount_pip_txt[pos].setText(String.format("%d", calcInfo.amount));

        double lotsize = calcInfo.lotsize;
        double entry = calcInfo.entry;
        int xtime = calcInfo.leverage;

        if (!forexCalc.calcInfos[pos].isJYN) {
            if (lotsize < 0) {
                one_pip_worth_txt[pos].setText(String.format("($%s)", doubleToStringNoDecimal(lotsize * 10 * -1)));
            } else {
                one_pip_worth_txt[pos].setText(String.format("$%s", doubleToStringNoDecimal(lotsize * 10)));
            }

            double total_worth = lotsize * 10 * (double) forexCalc.calcInfos[pos].amount;
            if (total_worth < 0) {
                total_pip_worth_txt[pos].setText(String.format("($%s)", doubleToStringNoDecimal(total_worth * -1)));
            } else {
                total_pip_worth_txt[pos].setText(String.format("$%s", doubleToStringNoDecimal(total_worth)));
            }

            double leverage = 0;
            if (xtime != 0)
                leverage = 100000 / (double)xtime * entry * lotsize;

            if (entry * lotsize < 0) {
                leverage_title_txt[pos].setText(String.format("%dx leverage", xtime));
                leverage_txt[pos].setText(String.format("($%s)", doubleToStringNoDecimal(leverage * -1)));
            } else {
                leverage_title_txt[pos].setText(String.format("%dx leverage", xtime));
                leverage_txt[pos].setText(String.format("$%s", doubleToStringNoDecimal(leverage)));
            }
        }
        else {
            if (lotsize < 0) {
                one_pip_worth_txt[pos].setText(String.format("($%s)", doubleToStringNoDecimal(lotsize * 10 * JPY_COST * -1)));
            } else {
                one_pip_worth_txt[pos].setText(String.format("$%s", doubleToStringNoDecimal(lotsize * 10 * JPY_COST)));
            }

            double total_worth = lotsize * 10 * (double) forexCalc.calcInfos[pos].amount * JPY_COST;
            if (total_worth < 0) {
                total_pip_worth_txt[pos].setText(String.format("($%s)", doubleToStringNoDecimal(total_worth * -1)));
            } else {
                total_pip_worth_txt[pos].setText(String.format("$%s", doubleToStringNoDecimal(total_worth)));
            }

            double leverage = 0;
            if (xtime != 0)
                leverage = 1000 / (double)xtime * entry * lotsize * JPY_COST;

            if (entry * lotsize < 0) {
                leverage_title_txt[pos].setText(String.format("%dx leverage", xtime));
                leverage_txt[pos].setText(String.format("($%s)", doubleToStringNoDecimal(leverage * -1)));
            } else {
                leverage_title_txt[pos].setText(String.format("%dx leverage", xtime));
                leverage_txt[pos].setText(String.format("$%s", doubleToStringNoDecimal(leverage)));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###,###,###.00");
        return formatter.format(d);
    }

    private void loadCalculates(int i, String item) {
        CalcInfo calcInfo = forexCalc.calcInfos[i];

//        if (!item.equals("entry")) {
//            if (calcInfo.exit == 0)
//                entry_txt[i].setText("");
//            else
//                entry_txt[i].setText(String.format("%.04f", calcInfo.entry));
//        }

        if (!item.equals("exit")) {
            if (calcInfo.exit == 0)
                exit_txt[i].setText("");
            else {
                if (calcInfo.isJYN)
                    exit_txt[i].setText(String.format("%.02f", calcInfo.exit));
                else
                    exit_txt[i].setText(String.format("%.04f", calcInfo.exit));
            }
        }

        if (!item.equals("profit")) {
            if (calcInfo.profitloss == 0)
                profit_loss_txt[i].setText("");
            else {
                    profit_loss_txt[i].setText(String.format("%.02f", calcInfo.profitloss));
            }
        }

        if (!item.equals("gross_profit")) {
            if (forexCalc.grossloss == 0)
                gross_profit_loss_txt[mPosition].setText("");
            else
                gross_profit_loss_txt[mPosition].setText(String.format("%.02f", forexCalc.grossloss));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadValues() {
        int pos1 = mPosition * 2;
        int pos2 = pos1 + 1;

        for (int i = pos1; i <= pos2; i ++) {
            mark_txt[i].setText(String.format("Trade %d", i + 1));
            profit_loss_title_txt[i].setText(String.format("Trade %d P/L", i + 1));

            CalcInfo calcInfo = forexCalc.calcInfos[i];

            if (calcInfo.isJYN) {
                if (calcInfo.entry == 0)
                    entry_txt[i].setText("");
                else
                    entry_txt[i].setText(String.format("%.02f", calcInfo.entry));

                if (calcInfo.exit == 0)
                    exit_txt[i].setText("");
                else
                    exit_txt[i].setText(String.format("%.02f", calcInfo.exit));
            }
            else {
                if (calcInfo.entry == 0)
                    entry_txt[i].setText("");
                else
                    entry_txt[i].setText(String.format("%.04f", calcInfo.entry));

                if (calcInfo.exit == 0)
                    exit_txt[i].setText("");
                else
                    exit_txt[i].setText(String.format("%.04f", calcInfo.exit));
            }

            if (calcInfo.lotsize == 0)
                lotsize_txt[i].setText("");
            else
                lotsize_txt[i].setText(String.format("%.04f", calcInfo.lotsize));

            if (calcInfo.leverage == 0)
                xtime_txt[i].setText("");
            else
                xtime_txt[i].setText(String.format("%d", calcInfo.leverage));

            if (calcInfo.profitloss == 0)
                profit_loss_txt[i].setText("");
            else {
                    profit_loss_txt[i].setText(String.format("%.02f", calcInfo.profitloss));
            }

            if (forexCalc.grossloss == 0)
                gross_profit_loss_txt[mPosition].setText("");
            else
                gross_profit_loss_txt[mPosition].setText(String.format("%.02f", forexCalc.grossloss));

            if (calcInfo.isJYN) {
                title_txt[i].setText("JPY pairs");
                setting_switch[i].setChecked(!calcInfo.isJYN);
            }
            else {
                title_txt[i].setText("Not for JPY pairs");
                setting_switch[i].setChecked(!calcInfo.isJYN);
            }
        }
        calcWorth(pos1);
        calcWorth(pos2);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                forexCalc.deleteValues();
                loadValues();
                int pos1 = mPosition * 2;
                entry_txt[pos1].requestFocus();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_to_right, R.anim.activity_right_to_left);
    }
}
