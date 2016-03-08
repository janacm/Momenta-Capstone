package com.momenta;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Helper class to build alert dialog since the dialog has a number of buttons
 *
 */
public class TimeDialogBuilder implements View.OnClickListener{

    private static final String POSITIVE = "POSITIVE";
    private static final String NEGATIVE = "NEGATIVE";
    private static final String NEUTRAL = "NEUTRAL";
    public String CLICKED = "";
    private String time;
    private AlertDialog.Builder builder;
    private View view;
    private String activity;

    public TimeDialogBuilder(LogFragment fragment, View v, String activity){
        time = "";
        view = v;
        this.activity = activity;

        TextView nameTextView = (TextView)v.findViewById(R.id.activity_name_textview);
        nameTextView.setText(activity);
        setUpButtons(v);
        setUpDialogButtons(fragment, v);
    }

    private void setUpDialogButtons(final LogFragment fragment, View v) {
        builder = new AlertDialog.Builder(fragment.getContext());
        builder.setView(v)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CLICKED = NEGATIVE;
                    }
                })
                .setNeutralButton(fragment.getContext().getString(R.string.dialog_more),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CLICKED = NEUTRAL;
                            }
                        })
                .setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Task task = new Task(activity);
                        task.setTimeInMinutes( timeInMinutes() );
                        DBHelper.getInstance(fragment.getContext()).insertTask(task);
                        fragment.updateView();
                        CLICKED = POSITIVE;
                    }
                });
    }

    private void setUpButtons(View v) {
        Button buttons[] = new Button[10];

        buttons[0] = (Button)v.findViewById(R.id.buttonOne);
        buttons[1] = (Button)v.findViewById(R.id.buttonTwo);
        buttons[2] = (Button)v.findViewById(R.id.buttonThree);
        buttons[3] = (Button)v.findViewById(R.id.buttonFour);
        buttons[4] = (Button)v.findViewById(R.id.buttonFive);
        buttons[5] = (Button)v.findViewById(R.id.buttonSix);
        buttons[6] = (Button)v.findViewById(R.id.buttonSeven);
        buttons[7] = (Button)v.findViewById(R.id.buttonEight);
        buttons[8] = (Button)v.findViewById(R.id.buttonNine);
        buttons[9] = (Button)v.findViewById(R.id.buttonZero);
        ImageButton backspace = (ImageButton)v.findViewById(R.id.buttonBackspace);

        for( int i=0; i<10; i++ ) {
            buttons[i].setOnClickListener(this);
        }
        backspace.setOnClickListener(this);
    }





    public AlertDialog getAlertDialog() {
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.buttonOne:
                buttonPressed("1");
                break;
            case R.id.buttonTwo:
                buttonPressed("2");
                break;
            case R.id.buttonThree:
                buttonPressed("3");
                break;
            case R.id.buttonFour:
                buttonPressed("4");
                break;
            case R.id.buttonFive:
                buttonPressed("5");
                break;
            case R.id.buttonSix:
                buttonPressed("6");
                break;
            case R.id.buttonSeven:
                buttonPressed("7");
                break;
            case R.id.buttonEight:
                buttonPressed("8");
                break;
            case R.id.buttonNine:
                buttonPressed("9");
                break;
            case R.id.buttonZero:
                buttonPressed("0");
                break;
            case R.id.buttonBackspace:
                backspacePressed();
                break;
        }
    }

    private void buttonPressed(String number) {
        if ( time.length() < 4) {
            if ( time.length()==0 && number.equals("0") ) {
                return;
            }
            time += number;
            updateTextView();
        }
    }

    private void backspacePressed() {
        if ( time.length() > 0 ) {
            time = time.substring(0, time.length()-1 );
            updateTextView();
        }
    }

    private void updateTextView() {
        TextView minute = (TextView)view.findViewById(R.id.activity_minute_textview);
        TextView hour = (TextView)view.findViewById(R.id.activity_hour_textview);
        String temp = "0000" + time;
        hour.setText( temp.substring( temp.length()-4, temp.length()-2 ) );
        minute.setText( temp.substring( temp.length()-2, temp.length() ) );
    }

    private int timeInMinutes() {
        String temp = "0000" + time;
        int minutes = Integer.valueOf( temp.substring( temp.length()-2, temp.length() ) );
        int hour = Integer.valueOf( temp.substring( temp.length()-4, temp.length()-2 ) );
        int result = (hour*60) + minutes;
        return result;
    }
}
