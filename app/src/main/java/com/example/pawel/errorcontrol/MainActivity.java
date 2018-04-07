package com.example.pawel.errorcontrol;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parity parityTransmitter = new Parity();
        final Hamming hammingTransmitter = new Hamming();
        Crc crcTransmitter = new Crc();
        //Crc32 crc32Transmitter = new Crc32();
        Parity parityReceiver = new Parity();
        Hamming hammingReceiver = new Hamming();
        Crc crcReceiver = new Crc();
        final DataBits dataBits = new DataBits();
        final CodeBase transmitter=parityTransmitter;
        CodeBase receiver=parityReceiver;

        Spinner dropdown = findViewById(R.id.methodSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.methods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        Spinner dropdown2 = findViewById(R.id.subMethodSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.submethods_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown2.setAdapter(adapter2);

        Button generate = findViewById(R.id.generate);
        Button disrupt = findViewById(R.id.disrupt);
        Button encode = findViewById(R.id.codeButton);
        final TextView outputBits = findViewById(R.id.outputBits);
        final TextView inputBits = findViewById(R.id.inputBits);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Podaj liczbę bitów");
                final String[] stringArray = new String[10];
                int n=8;
                for(int i=0; i<10; i++){
                    stringArray[i] = Integer.toString(n);
                    n+=8;
                }
                final NumberPicker picker = dialog.findViewById(R.id.numberPicker);
                picker.setMaxValue(stringArray.length-1);
                picker.setMinValue(0);
                picker.setDisplayedValues(stringArray);
                Button okButton = dialog.findViewById(R.id.ok_button);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataBits.generate((picker.getValue()+1)*8);
                        inputBits.setText(dataBits.toString());
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        disrupt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Podaj liczbę bitów");
                final NumberPicker picker = dialog.findViewById(R.id.numberPicker);
                picker.setMaxValue(20);
                picker.setMinValue(0);
                Button okButton = dialog.findViewById(R.id.ok_button);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        outputBits.setText(String.valueOf(picker.getValue()));
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = inputBits.getText().toString();
                if (str.length()==0)
                {
                    str = "00000000";
                    inputBits.setText(str);
                }
                else if (str.length()%8!=0)
                {
                    String temp = "";
                    int zeros = 8-str.length()%8;
                    for (int i=0; i<zeros; i++) temp+="0";
                    str = temp + str;
                    inputBits.setText(str);
                }

                transmitter.setData(str);
                transmitter.encode();
                outputBits.setText(transmitter.codeToString());
                transmitter.setCode(transmitter.codeToString());
                //receivedData.setText(transmitter.codeToString());
            }
        });
    }

}
