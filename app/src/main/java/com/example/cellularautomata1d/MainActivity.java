package com.example.cellularautomata1d;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    int[] cells;
    int cellWidth = 20;
    int cellHeight = 20;
    int ruleValue;
    int[] ruleSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        LinearLayout mainLayout = new LinearLayout(this);
        LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(mainLayoutParams);
        mainLayout.setBackgroundColor(Color.BLACK);


        int total = displayMetrics.widthPixels / cellWidth;
        cells = new int[total];
        cells[((total +  1) / 2) - 1] = 1;


        // Create the EditText
        EditText ruleEdit = new EditText(this);
        ruleEdit.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        // Create the parent layout (LinearLayout)
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(ruleEdit);

        new AlertDialog.Builder(this)
                .setTitle("Enter a Rule Set")
                .setView(linearLayout)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    ruleValue = Integer.parseInt(ruleEdit.getText().toString());
                    Toast.makeText(this, "Now Loading Rule " + ruleValue + "... Please wait for it to load.", Toast.LENGTH_SHORT).show();
                    ruleSet = decimalToBinaryArray(ruleValue);
                    new Thread(() -> createTree(displayMetrics, mainLayout)).start();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .create()
                .show();

        setContentView(mainLayout);
    }
    private void createTree(DisplayMetrics displayMetrics, LinearLayout mainLayout) {
        for (int z = 0; z < (displayMetrics.heightPixels / cellHeight); z++) {
            LinearLayout generation = new LinearLayout(this);
            LinearLayout.LayoutParams generationParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            if (z > 0) {generationParams.topMargin = cellWidth * z; generationParams.setMarginStart(cellWidth * -cells.length);}
            generation.setLayoutParams(generationParams);
            createCells(generation);
            cells = getNextCells();
            mainLayout.addView(generation);
        }
    }

    @NonNull
    private int[] getNextCells() {
        int[] nextCells = new int[cells.length];
        nextCells[0] = cells[0];
        nextCells[cells.length - 1] = cells[cells.length - 1];
        for (int i = 1; i < cells.length - 1; i++) {
            int leftCell = cells[i - 1];
            int rightCell = cells[i + 1];
            int currentCell = cells[i];
            int newState = calculateState(leftCell, currentCell, rightCell);
            nextCells[i] = newState;
        }
        return nextCells;
    }

    private void createCells(LinearLayout generation) {
        for (int cell : cells) {
            LinearLayout box = new LinearLayout(this);
            LinearLayout.LayoutParams boxParameters = new LinearLayout.LayoutParams(cellWidth, cellHeight);
            box.setLayoutParams(boxParameters);
            box.setBackgroundColor(Color.BLACK);
            if (cell == 1) {
                box.setBackgroundColor(Color.rgb(ruleValue,
                        255 - ruleValue,
                        255 - ruleValue+ 10));
            }
            generation.addView(box);
        }
    }
    private int calculateState(int leftCell, int currentCell, int rightCell) {
        String neighborhood =  "" + leftCell + currentCell + rightCell;
        int value = 7 - Integer.parseInt(neighborhood, 2);
        return ruleSet[value];
    }
    private static int[] decimalToBinaryArray(int decimalNumber) {
        int[] binaryArray = new int[8]; // Assuming a 32-bit integer
        for (int i = 7; i >= 0; i--) {
            binaryArray[i] = (decimalNumber & 1);
            decimalNumber >>= 1;
        }
        return binaryArray;
    }
}