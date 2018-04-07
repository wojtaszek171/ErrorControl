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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Parity parityTransmitter = new Parity();
    Hamming hammingTransmitter = new Hamming();
    Crc crcTransmitter = new Crc();
    Parity parityReceiver = new Parity();
    Hamming hammingReceiver = new Hamming();
    Crc crcReceiver = new Crc();
    DataBits dataBits = new DataBits();
    CodeBase transmitter =parityTransmitter;
    CodeBase receiver = parityReceiver;
    Button generate;
    Button disrupt;
    Button encode;
    Button decode;
     EditText outputBits;
     EditText inputBits;
     TextView outputBitsText;
     TextView decodedBits;
     TextView detectedBits;
     TextView notDetected;
     TextView bitsFixed;
     TextView bitsSend;
    TextView redundant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner dropdown = findViewById(R.id.methodSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.methods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        final Spinner dropdown2 = findViewById(R.id.subMethodSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.submethods_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown2.setAdapter(adapter2);

         generate = findViewById(R.id.generate);
         disrupt = findViewById(R.id.disrupt);
         encode = findViewById(R.id.codeButton);
         decode = findViewById(R.id.decodeButton);
        outputBits = findViewById(R.id.outputBits);
        inputBits = findViewById(R.id.inputBits);
        decodedBits = findViewById(R.id.outputDecodedBits);
        detectedBits = findViewById(R.id.detected_text);
        notDetected = findViewById(R.id.not_detected_text);
        bitsFixed = findViewById(R.id.bits_fixed);
        bitsSend = findViewById(R.id.bits_send);
        redundant = findViewById(R.id.redundant_bits);
        outputBitsText = findViewById(R.id.outputBitsText);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                outputBits.setText("");
                switch (i){
                    case 0:
                        transmitter = parityTransmitter;
                        receiver = parityReceiver;
                        dropdown2.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        transmitter = hammingTransmitter;
                        receiver = hammingReceiver;
                        dropdown2.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        transmitter = crcTransmitter;
                        receiver = crcReceiver;
                        dropdown2.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                outputBits.setText("");
                switch (i){
                    case 0:
                        crcTransmitter.setKey(Crc.CRC16);
                        crcReceiver.setKey(Crc.CRC16);
                        break;
                    case 1:
                        crcTransmitter.setKey(Crc.CRC32);
                        crcReceiver.setKey(Crc.CRC32);
                        break;
                    case 2:
                        crcTransmitter.setKey(Crc.SDLC);
                        crcReceiver.setKey(Crc.SDLC);
                        break;
                    case 3:
                        crcTransmitter.setKey(Crc.SDLC_REVERSE);
                        crcReceiver.setKey(Crc.SDLC_REVERSE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Podaj liczbę bitów");
                ((TextView) dialog.findViewById(R.id.dialog_text)).setText("Liczba bitów do wygenerowania");
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
                        transmitter.interfere(Integer.parseInt(String.valueOf(picker.getValue())));
                        outputBits.setText(transmitter.codeToString());
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
                outputBitsText.setText(transmitter.codeToString());
                transmitter.setCode(transmitter.codeToString());
                //receivedData.setText(transmitter.codeToString());
            }
        });
        decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transmitter.setCode(outputBits.getText().toString());
                int errors=countErrors();
                transmitter.fix();
                //correctedData.setText(transmitter[0].codeToString());
                //colorFixedBits(transmitter[0].getBitTypes());
                transmitter.decode();
                decodedBits.setText(transmitter.dataToString());
                bitsSend.setText(Integer.toString(transmitter.getDataBitsNumber()));
                redundant.setText(Integer.toString(transmitter.getControlBitsNumber()));
                int detected = transmitter.getDetectedErrorsNumber();
                detectedBits.setText(Integer.toString(detected));
                bitsFixed.setText(Integer.toString(transmitter.getFixedErrorsNumber()));
                notDetected.setText(Integer.toString(errors-detected));
            }
        });
    }
    private int countErrors()
    {
        String input = inputBits.getText().toString();
        String output = decodedBits.getText().toString();
        if (input.length()!=output.length()) return -1;
        else
        {
            int errors=0;
            int l=input.length();
            for (int i=0; i<l; i++)
            {
                if (input.charAt(i)!=output.charAt(i)) errors++;
            }
            return errors;
        }
    }

//    public void colorFixedBits(int type[])
//    {
//        String str=correctedData.getText();
//        if (str.length()==type.length)
//        {
//            StyledDocument doc = correctedData.getStyledDocument();
//            Style style = correctedData.addStyle("colours", null);
//
//            correctedData.setText("");
//            int l = type.length;
//            for (int i=0; i<l; i++)
//            {
//                Color colour=Color.black;
//                switch (type[i])
//                {
//                    case 0: colour=Color.green; break;
//                    case 1: colour=Color.red; break;
//                    case 2: colour=Color.orange; break;
//                    case 3: colour=Color.cyan; break;
//                    case 4: colour=Color.magenta; break;
//                    case 5: colour=Color.yellow; break;
//                }
//                StyleConstants.setForeground(style, colour);
//                try { doc.insertString(doc.getLength(), ""+str.charAt(i), style); }
//                catch (BadLocationException e){}
//            }
//        }
//    }
}
