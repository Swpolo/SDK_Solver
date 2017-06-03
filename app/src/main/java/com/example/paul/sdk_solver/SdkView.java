package com.example.paul.sdk_solver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SdkView extends View {
    private int focusedColumn;
    private int focusedRow;

    private int[][] cellValue = new int[9][9];
    private int[][] cellUserSet = new int[9][9];

    // visual
    private int gridSize;

    int cellSize = 100;
    int outBorderSize = 5;
    int inBorderSize = 3;

    Paint paint = new Paint();

    public SdkView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SdkView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context, attrs, 0);
    }

    public SdkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        gridSize = outBorderSize * 2 + inBorderSize * 8 + cellSize * 9;
        reset();
    }

    public void reset()
    {
        for(int c = 0; c < 9; c++){
            for(int r = 0; r < 9; r++){
                cellValue[c][r]     = 0;
                cellUserSet[c][r]   = 0;
            }
        }
        focusedColumn = 0;
        focusedRow = 0;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int curRow = 0;

        // DRAW 9x9 tiny cells
        paint.setColor(Color.LTGRAY);
        for(int i = outBorderSize + cellSize; i < gridSize - outBorderSize; i = i + cellSize + inBorderSize) {
            curRow = curRow + 1;
            if ((curRow == 3) || (curRow == 6)) {
                continue;
            }
            canvas.drawRect(outBorderSize, i, gridSize - outBorderSize, i + inBorderSize, paint);
            canvas.drawRect(i, outBorderSize, i + inBorderSize, gridSize - outBorderSize, paint);
        }

        // DRAW 3x3 blocks separator
        paint.setColor(Color.DKGRAY);

        curRow = 0;
        for(int i = outBorderSize + cellSize; i < gridSize - outBorderSize; i = i + cellSize + inBorderSize) {
            curRow = curRow + 1;
            if (!((curRow == 3) || (curRow == 6))) {
                continue;
            }
            canvas.drawRect(outBorderSize, i, gridSize - outBorderSize, i + inBorderSize, paint);
            canvas.drawRect(i, outBorderSize, i + inBorderSize, gridSize - outBorderSize, paint);
        }

        // DRAW grid border
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, outBorderSize, gridSize, paint);
        canvas.drawRect(0, 0, gridSize, outBorderSize, paint);
        canvas.drawRect(0, gridSize - outBorderSize, gridSize, gridSize, paint);
        canvas.drawRect(gridSize - outBorderSize, 0, gridSize, gridSize, paint);

        // DRAW current focused cell
        if ((focusedColumn != 0) && (focusedRow != 0)) {
            paint.setColor(Color.YELLOW);
            canvas.drawRect(getPosPxStart(focusedColumn), getPosPxStart(focusedRow), getPosPxEnd(focusedColumn), getPosPxEnd(focusedRow), paint);
        }

        // DRAW cells value
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellSize/2);
        for(int c = 0; c < 9; c++) {
            for(int r = 0; r < 9; r++) {
                paint.setColor(cellUserSet[c][r] == 0 ? Color.RED : Color.BLACK);
                if (cellValue[c][r] != 0) {
                    float posX = (float) getPosPxMiddle(c + 1);
                    float posY = (float) getPosPxMiddle(r + 1) + (cellSize / 4);
                    canvas.drawText(Integer.toString(cellValue[c][r]), posX, posY, paint);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(gridSize, gridSize);
    }

    public void focusedCell(int x, int y){
        focusedColumn = getPosNum(x);
        focusedRow = getPosNum(y);
    }

    private int getPosNum(int px) {
        int pos = 0;
        for(int i = outBorderSize; i < gridSize - outBorderSize; i = i + cellSize + inBorderSize) {
            if (px <= i) {
                break;
            }
            pos++;
        }
        return pos;
    }

    private int getPosPxStart(int num) {
        return (outBorderSize + ((num - 1) * inBorderSize) + cellSize * (num - 1));
    }

    private int getPosPxEnd(int num) {
        return (outBorderSize + ((num - 1) * inBorderSize) +  cellSize * num);
    }

    private int getPosPxMiddle(int num) {
        return ((getPosPxStart(num) + getPosPxEnd(num)) / 2);
    }

    public void setCell(int newValue) {
        if ((focusedColumn != 0) && (focusedRow != 0)) {
            cellValue[focusedColumn - 1][focusedRow - 1]    = newValue;
            cellUserSet[focusedColumn - 1][focusedRow - 1]  = 1;
        }
    }

    public void setGrid(int cells[][]){
        for (int c = 0; c < 9; c++) {
            for(int r = 0; r < 9; r++) {
                cellValue[c][r] = cells[c][r];
            }
        }
    }

    public void setCellUserSet(){
        for (int c = 0; c < 9; c++) {
            for (int r = 0; r < 9; r++) {
                cellUserSet[c][r] = ((cellValue[c][r] == 0) ? 0 : 1);
            }
        }
    }

    public int getCell(int column, int row) {
        return cellValue[column][row];
    }

    public int[][] getGrid() {
        return cellValue;
    }
}
