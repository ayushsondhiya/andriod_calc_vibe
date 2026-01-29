package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView display;
    private TextView history;
    private java.util.List<String> historyList;
    private StringBuilder input = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = findViewById(R.id.display);
        int[] ids = new int[]{
                R.id.btnSin, R.id.btnCos, R.id.btnTan, R.id.btnPow,
                R.id.btnC, R.id.btnOpen, R.id.btnClose, R.id.btnBack,
                R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDiv,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btnMul,
                R.id.btn1, R.id.btn2, R.id.btn3, R.id.btnMinus,
                R.id.btnNeg, R.id.btn0, R.id.btnDot, R.id.btnPlus,
                R.id.btnSqrt, R.id.btnLn, R.id.btnLog, R.id.btnPct,
                R.id.btnEq
        };
        for (int id : ids) findViewById(id).setOnClickListener(this);

        // history view
        history = findViewById(R.id.history);
        history.setText("");
        historyList = new java.util.ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSin: append("sin("); return;
            case R.id.btnCos: append("cos("); return;
            case R.id.btnTan: append("tan("); return;
            case R.id.btnPow: append("^"); return;
            case R.id.btnC: clear(); return;
            case R.id.btnBack: backspace(); return;
            case R.id.btnEq: evaluate(); return;
            case R.id.btnNeg: negate(); return;
            case R.id.btnPct: append("%"); return;
            case R.id.btnOpen: append("("); return;
            case R.id.btnClose: append(")"); return;
            case R.id.btnDiv: append("/"); return;
            case R.id.btnMul: append("*"); return;
            case R.id.btnMinus: append("-"); return;
            case R.id.btnPlus: append("+"); return;
            case R.id.btnDot: append("."); return;
            case R.id.btnSqrt: append("sqrt("); return;
            case R.id.btnLn: append("ln("); return;
            case R.id.btnLog: append("log("); return;
            default:
                Button b = (Button) v;
                append(b.getText().toString());
        }
    }

    private void append(String s) {
        input.append(s);
        display.setText(input.length() == 0 ? "0" : input.toString());
    }

    private void clear() {
        input.setLength(0);
        display.setText("0");
    }

    private void backspace() {
        if (input.length() > 0) {
            input.deleteCharAt(input.length() - 1);
            display.setText(input.length() == 0 ? "0" : input.toString());
        }
    }

    private void negate() {
        try {
            String expr = input.toString();
            if (expr.isEmpty()) return;
            int i = expr.length() - 1;
            while (i >= 0 && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.' || expr.charAt(i) == '%')) i--;
            String before = expr.substring(0, i+1);
            String number = expr.substring(i+1);
            if (number.isEmpty()) return;
            if (number.startsWith("-")) number = number.substring(1);
            else number = "(" + "-" + number + ")";
            input.setLength(0);
            input.append(before).append(number);
            display.setText(input.toString());
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    private void evaluate() {
        String expr = input.toString();
        try {
            BigDecimal res = CalculatorEval.evaluate(expr);
            String out = res.stripTrailingZeros().toPlainString();
            display.setText(out);

            // add to history
            historyList.add(expr + " = " + out);
            StringBuilder h = new StringBuilder();
            for (int i = Math.max(0, historyList.size()-10); i < historyList.size(); i++) {
                h.append(historyList.get(i)).append("\n");
            }
            history.setText(h.toString());

            input.setLength(0);
            input.append(out);
        } catch (Exception ex) {
            display.setText("Error");
            input.setLength(0);
        }
    }
}
