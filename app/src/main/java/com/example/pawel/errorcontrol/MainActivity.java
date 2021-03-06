package com.example.pawel.errorcontrol;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

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
    TextView correctedBits;
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
        correctedBits = findViewById(R.id.corrected_bits);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                outputBits.setText("");
                correctedBits.setText("");
                decodedBits.setText("");
                bitsSend.setText("");
                redundant.setText("");
                detectedBits.setText("");
                bitsFixed.setText("");
                notDetected.setText("");
                outputBitsText.setText("");
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
                correctedBits.setText("");
                decodedBits.setText("");
                bitsSend.setText("");
                redundant.setText("");
                detectedBits.setText("");
                bitsFixed.setText("");
                notDetected.setText("");
                outputBitsText.setText("");

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
                if (str.length() == 0) {
                    str = "00000000";
                    inputBits.setText(str);
                }else if(transmitter!=parityTransmitter){
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
                correctedBits.setText(transmitter.codeToString());
                transmitter.decode();
                decodedBits.setText(transmitter.dataToString());
                bitsSend.setText(Integer.toString(transmitter.getDataBitsNumber()));
                redundant.setText(Integer.toString(transmitter.getControlBitsNumber()));
                int detected = transmitter.getDetectedErrorsNumber();
                detectedBits.setText(Integer.toString(detected));
                bitsFixed.setText(Integer.toString(transmitter.getFixedErrorsNumber()));
                colorFixedBits(transmitter.getBitTypes());
                notDetected.setText(Integer.toString(errors-detected));
            }
        });
    }
    private int countErrors()
    {
        String input = outputBitsText.getText().toString();
        String output = outputBits.getText().toString();
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

    public void colorFixedBits(int type[])
    {
        String colored = "";
        String str=correctedBits.getText().toString();
        if (str.length()==type.length)
        {


            correctedBits.setText("");
            int l = type.length;
            for (int i=0; i<l; i++)
            {
                String color="#000000";
                switch (type[i])
                {
                    case 0: color="#10930b"; break;
                    case 1: color="#920b0b"; break;
                    case 2: color="#db7f00"; break;
                    case 3: color="#00FFFF"; break;
                    case 4: color="#FF00FF"; break;
                    case 5: color="#f2ff00"; break;
                }
                colored += "<font color='"+ color +"'>"+str.charAt(i)+"</font>";
            }
            correctedBits.setText(Html.fromHtml(colored));
        }
    }
}
