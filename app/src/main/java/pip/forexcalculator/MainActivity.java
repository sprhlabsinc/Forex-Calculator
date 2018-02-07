package pip.forexcalculator;

import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

import static pip.forexcalculator.app.AppConfig.JPY_COST;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener, View.OnFocusChangeListener {

    private Switch setting_switch;
    private EditText entry_txt, exit_txt, lotsize_txt, xtime_txt;
    private TextView amount_pip_txt, one_pip_worth_txt, total_pip_worth_txt, leverage_txt, leverage_title_txt, entry_title_txt, exit_title_txt;
    private Button long_but, short_but, delete_but;
    private TextView title_txt;

    private RelativeLayout entry_layout, exit_layout, lotsize_layout, xtime_layout;
    private ScrollView forex_scrollview;

    private int amount_pips = 0;
    private boolean isLongButClicked = false;
    private boolean isJYN = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("      Advanced Forex Calculator");

        setting_switch = (Switch) findViewById(R.id.setting_switch);
        entry_txt = (EditText) findViewById(R.id.entry_txt);
        exit_txt = (EditText) findViewById(R.id.exit_txt);
        lotsize_txt = (EditText) findViewById(R.id.lotsize_txt);
        xtime_txt = (EditText) findViewById(R.id.xtime_txt);

        entry_txt.setOnKeyListener(this);
        exit_txt.setOnKeyListener(this);
        lotsize_txt.setOnKeyListener(this);
        xtime_txt.setOnKeyListener(this);

        entry_txt.setOnFocusChangeListener(this);
        exit_txt.setOnFocusChangeListener(this);
        lotsize_txt.setOnFocusChangeListener(this);
        xtime_txt.setOnFocusChangeListener(this);

        entry_txt.setNextFocusDownId(entry_txt.getId());
        entry_txt.setNextFocusUpId(entry_txt.getId());
        exit_txt.setNextFocusDownId(exit_txt.getId());
        exit_txt.setNextFocusUpId(exit_txt.getId());
        lotsize_txt.setNextFocusDownId(lotsize_txt.getId());
        lotsize_txt.setNextFocusUpId(lotsize_txt.getId());
        xtime_txt.setNextFocusDownId(xtime_txt.getId());
        xtime_txt.setNextFocusUpId(xtime_txt.getId());

        xtime_txt.setText("100");

        amount_pip_txt = (TextView) findViewById(R.id.amount_pip_txt);
        one_pip_worth_txt = (TextView) findViewById(R.id.one_pip_worth_txt);
        total_pip_worth_txt = (TextView) findViewById(R.id.total_pip_worth_txt);
        leverage_title_txt = (TextView) findViewById(R.id.leverage_title_txt);
        leverage_txt = (TextView) findViewById(R.id.leverage_txt);

        entry_title_txt = (TextView) findViewById(R.id.entry_title_txt);
        exit_title_txt = (TextView) findViewById(R.id.exit_title_txt);

        title_txt = (TextView) findViewById(R.id.title_txt);

        entry_layout = (RelativeLayout) findViewById(R.id.entry_layout);
        exit_layout = (RelativeLayout) findViewById(R.id.exit_layout);
        lotsize_layout = (RelativeLayout) findViewById(R.id.lotsize_layout);
        xtime_layout = (RelativeLayout) findViewById(R.id.xtime_layout);

        entry_layout.setOnClickListener(this);
        exit_layout.setOnClickListener(this);
        lotsize_layout.setOnClickListener(this);
        xtime_layout.setOnClickListener(this);

        long_but = (Button) findViewById(R.id.long_but);
        short_but = (Button) findViewById(R.id.short_but);
        delete_but = (Button) findViewById(R.id.delete_but);

        long_but.setOnClickListener(this);
        short_but.setOnClickListener(this);
        delete_but.setOnClickListener(this);

        setting_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isJYN = !isChecked;

                if (isJYN) {
                    title_txt.setText("JPY pairs");
                    entry_title_txt.setText("Entry (1.33)");
                    exit_title_txt.setText("Exit (1.32)");
                }
                else {
                    title_txt.setText("Not for JPY pairs");
                    entry_title_txt.setText("Entry (1.3320)");
                    exit_title_txt.setText("Exit (1.3210)");
                }
                calcPip();
                calcWorth();
            }
        });

        lotsize_txt.addTextChangedListener(new TextWatcher() {

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

                //calcWorth();
            }
        });

        entry_txt.addTextChangedListener(new TextWatcher() {

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

                //calcPip();
                //calcWorth();
            }
        });
        exit_txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                calcPip();
            }
        });
        xtime_txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                calcWorth();
            }
        });

        forex_scrollview = (ScrollView) findViewById(R.id.forex_scrollview);
        calcWorth();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calcWorth() {

        double lotsize = 0;
        try {
            lotsize = Double.parseDouble(lotsize_txt.getText().toString());
        } catch (Exception e) {}

        double entry = 0;
        try {
            entry = Double.parseDouble(entry_txt.getText().toString());
        } catch (Exception e) {}

        int xtime = 0;
        try {
            xtime = Integer.parseInt(xtime_txt.getText().toString());
        } catch (Exception e) {}

        if (!isJYN) {
            if (lotsize < 0) {
                one_pip_worth_txt.setText(String.format("($%s)", doubleToStringNoDecimal(lotsize * 10 * -1)));
            } else {
                one_pip_worth_txt.setText(String.format("$%s", doubleToStringNoDecimal(lotsize * 10)));
            }

            double total_worth = lotsize * 10 * (double) amount_pips;
            if (total_worth < 0) {
                total_pip_worth_txt.setText(String.format("($%s)", doubleToStringNoDecimal(total_worth * -1)));
            } else {
                total_pip_worth_txt.setText(String.format("$%s", doubleToStringNoDecimal(total_worth)));
            }

            double leverage = 0;
            if (xtime != 0)
             leverage = 100000 / (double)xtime * entry * lotsize;

            if (entry * lotsize < 0) {
                leverage_title_txt.setText(String.format("%dx leverage", xtime));
                leverage_txt.setText(String.format("($%s)", doubleToStringNoDecimal(leverage * -1)));
            } else {
                leverage_title_txt.setText(String.format("%dx leverage", xtime));
                leverage_txt.setText(String.format("$%s", doubleToStringNoDecimal(leverage)));
            }
        }
        else {
            if (lotsize < 0) {
                one_pip_worth_txt.setText(String.format("($%s)", doubleToStringNoDecimal(lotsize * 10 * JPY_COST * -1)));
            } else {
                one_pip_worth_txt.setText(String.format("$%s", doubleToStringNoDecimal(lotsize * 10 * JPY_COST)));
            }

            double total_worth = lotsize * 10 * (double) amount_pips * JPY_COST;
            if (total_worth < 0) {
                total_pip_worth_txt.setText(String.format("($%s)", doubleToStringNoDecimal(total_worth * -1)));
            } else {
                total_pip_worth_txt.setText(String.format("$%s", doubleToStringNoDecimal(total_worth)));
            }

            double leverage = 0;
            if (xtime != 0)
                leverage = 1000 / (double)xtime * entry * lotsize * JPY_COST;

            if (entry * lotsize < 0) {
                leverage_title_txt.setText(String.format("%dx leverage", xtime));
                leverage_txt.setText(String.format("($%s)", doubleToStringNoDecimal(leverage * -1)));
            } else {
                leverage_title_txt.setText(String.format("%dx leverage", xtime));
                leverage_txt.setText(String.format("$%s", doubleToStringNoDecimal(leverage)));
            }
        }
    }

    private void calcPip() {
        double entry = 0;
        try {
            entry = Double.parseDouble(entry_txt.getText().toString());
        } catch (Exception e) {}
        double exit = 0;
        try {
            exit = Double.parseDouble(exit_txt.getText().toString());
        } catch (Exception e) {}

        double result = entry - exit;
        if (entry < exit) {
            result = exit - entry;
        }
        amount_pips = (int)(result * 10000 + 0.5d);
        if (isJYN) {
            amount_pips = (int)(result * 100 + 0.5d);
        }
        if (isLongButClicked) {
            if (entry > exit) {
                amount_pips *= -1;
            }
        }
        else {
            if (entry < exit) {
                amount_pips *= -1;
            }
        }
        if (amount_pips == 0 || exit == 0)
            amount_pip_txt.setText(String.format(""));
        else
            amount_pip_txt.setText(String.format("%d", amount_pips));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter .applyPattern("#,###,###,###.00");
        return formatter.format(d);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        if (v == delete_but) {

            lotsize_txt.setText("");
            entry_txt.setText("");
            exit_txt.setText("");
            xtime_txt.setText("100");

            lotsize_txt.requestFocus();
            short_but.callOnClick();
        }
        else if (v == long_but || v == short_but) {
            if (v == long_but) {
                isLongButClicked = true;
            } else if (v == short_but) {
                isLongButClicked = false;
            }

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    0);

            calcPip();
            calcWorth();
        }
        else if (v == entry_layout) {
            entry_txt.requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(entry_txt, InputMethodManager.SHOW_FORCED);
        }
        else if (v == exit_layout) {
            exit_txt.requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(exit_txt, InputMethodManager.SHOW_FORCED);
        }
        else if (v == lotsize_layout) {
            lotsize_txt.requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(lotsize_txt, InputMethodManager.SHOW_FORCED);
        }
        else if (v == xtime_layout) {
            xtime_txt.requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(xtime_txt, InputMethodManager.SHOW_FORCED);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

            // Perform action on key press
            if (v == exit_txt && !entry_txt.getText().toString().equals("") &&
                    !exit_txt.getText().toString().equals("") &&
                    !lotsize_txt.getText().toString().equals("")
                    //&& !xtime_txt.getText().toString().equals("")
                    ) {
                if (isLongButClicked)
                    long_but.callOnClick();
                else
                    short_but.callOnClick();
            }
            else if (lotsize_txt.getText().toString().equals("")) {
                lotsize_txt.requestFocus();
            }
            else if (entry_txt.getText().toString().equals("")) {
                entry_txt.requestFocus();
            }
            else if (exit_txt.getText().toString().equals("")) {
                exit_txt.requestFocus();
            }
            else if (xtime_txt.getText().toString().equals("")) {
                xtime_txt.requestFocus();
            }
            return true;
        }

        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            if (v == lotsize_txt || v == xtime_txt) {
                forex_scrollview.post(new Runnable() {
                    public void run() {
                        //forex_scrollview.scrollTo(0, forex_scrollview.getBottom());
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_advance:

                Intent intent = new Intent(MainActivity.this, ProfitLossActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.advance, menu);
        return true;
    }
}
