package com.Myapp.transliter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

        private Spinner FromSpinner,toSpinner;
        private TextInputEditText SourceEdt;
        private ImageView micIV;
        private MaterialButton translateBtn;
        private TextView translatedTV;
        String[] fromlanguages={"from","English"};
        String[] tolanguages={"To","Afrikaans","Albanian","Arabic","Belarusian","Bengali","Bulgarian","Catalan","Chinese (Simplified)","Chinese (Traditional)", "Croatian","Czech", "Danish", "Dutch","English","Esperanto","Estonian","Finnish","French","Galician","Georgian","German","Greek","Gujarati", "Haitian","Hindi","Hungarian","Icelandic","Indonesian","Irish","Italian","Japanese","Kannada","Korean","Latvian", "Lithuanian","Macedonian","Malay","Maltese","Marathi","Norwegian (Bokmål)","Persian","Polis","Portuguese","Romania","Russian","Slova","Slovenian","Spanish","Swahili","Swedish","Tamil","Telugu","Thai","Turkish","Ukrainian","Urdu","Vietnamese","Welsh","Zulu"};
        private static final int REQUEST_PERMISSION_CODE =1;
        int languageCode, fromlanguageCode, tolanguageCode = 0;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            FromSpinner = findViewById(R.id.idFromSpinner);
            toSpinner = findViewById(R.id.idToSpinner);
            SourceEdt = findViewById(R.id.idEdtSource);
            micIV = findViewById(R.id.idIVMic);
            translateBtn = findViewById(R.id.idBtnTranslate);
            translatedTV = findViewById(R.id.idTVTranslatedTV);
            FromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override

                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    fromlanguageCode = getLanguageCode(fromlanguages[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromlanguages);
            fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            FromSpinner.setAdapter(fromAdapter);

            toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tolanguageCode = getLanguageCode(tolanguages[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, tolanguages);
            toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            toSpinner.setAdapter(toAdapter);
            translateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    translatedTV.setText("");
                    if (SourceEdt.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Enter your text here to translate", Toast.LENGTH_SHORT).show();
                    } else if (fromlanguageCode == 0) {
                        Toast.makeText(MainActivity.this, "Select your source language", Toast.LENGTH_SHORT).show();
                    } else if (tolanguageCode == 0) {
                        Toast.makeText(MainActivity.this, "Select which language you need to translate", Toast.LENGTH_SHORT).show();
                    } else {
                        translateText(fromlanguageCode, tolanguageCode, SourceEdt.getText().toString());
                    }
                }

            });

         micIV.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                    Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                     i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                     i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                     i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text");
                     try {
                         startActivityForResult(i, REQUEST_PERMISSION_CODE);
                     } catch (Exception e) {
                         e.printStackTrace();
                         Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
             });
         }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                     if(requestCode==REQUEST_PERMISSION_CODE){
                         if(resultCode == RESULT_OK && data!=null)
                         {
                             ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                             SourceEdt.setText(result.get(0));

                         }
                     }
            }
                 private void translateText(int fromlanguageCode,int tolanguageCode, String source){
                     translatedTV.setText("downloading modal.....");
                     FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                             .setSourceLanguage(fromlanguageCode)
                             .setTargetLanguage(tolanguageCode)
                             .build();
                     FirebaseTranslator translator=FirebaseNaturalLanguage.getInstance().getTranslator(options);
                     FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();
                     translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {
                             translatedTV.setText("your text is translating.....");
                             translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                                 @Override
                                 public void onSuccess(String s) {
                                     translatedTV.setText(s);
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e){
                                     Toast.makeText(MainActivity.this, "Fail to translate:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                 }
                             });
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(MainActivity.this, "Fail to download language model:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
        public int getLanguageCode(String language) {
            int languageCode = 0;
            switch (language) {
                case "Afrikaans":
                    languageCode = FirebaseTranslateLanguage.AF;
                    break;
                case "Albanian":
                    languageCode = FirebaseTranslateLanguage.SQ;
                    break;
                case "Arabic":
                    languageCode = FirebaseTranslateLanguage.AR;
                    break;
                case "Belarusian":
                    languageCode = FirebaseTranslateLanguage.BE;
                    break;
                case "Bengali":
                    languageCode = FirebaseTranslateLanguage.BN;
                    break;
                case "Bulgarian":
                    languageCode = FirebaseTranslateLanguage.BG;
                    break;
                case "Catalan":
                    languageCode = FirebaseTranslateLanguage.CA;
                    break;
                case "Chinese (Simplified)":
                    languageCode = FirebaseTranslateLanguage.ZH;
                    break;
                case "Chinese (Traditional)":
                    languageCode = FirebaseTranslateLanguage.ZH;
                    break;
                case "Croatian":
                    languageCode = FirebaseTranslateLanguage.HR;
                    break;
                case "Czech":
                    languageCode = FirebaseTranslateLanguage.CS;
                    break;
                case "Danish":
                    languageCode = FirebaseTranslateLanguage.DA;
                    break;
                case "Dutch":
                    languageCode = FirebaseTranslateLanguage.NL;
                    break;
                case "English":
                    languageCode = FirebaseTranslateLanguage.EN;
                    break;
                case "Esperanto":
                    languageCode = FirebaseTranslateLanguage.EO;
                    break;
                case "Estonian":
                    languageCode = FirebaseTranslateLanguage.ET;
                    break;
                case "Finnish":
                    languageCode = FirebaseTranslateLanguage.FI;
                    break;
                case "French":
                    languageCode = FirebaseTranslateLanguage.FR;
                    break;
                case "Galician":
                    languageCode = FirebaseTranslateLanguage.GL;
                    break;
                case "Georgian":
                    languageCode = FirebaseTranslateLanguage.KA;
                    break;
                case "German":
                    languageCode = FirebaseTranslateLanguage.DE;
                    break;
                case "Greek":
                    languageCode = FirebaseTranslateLanguage.EL;
                    break;
                case "Gujarati":
                    languageCode = FirebaseTranslateLanguage.GU;
                    break;
                case "Haitian":
                    languageCode = FirebaseTranslateLanguage.HT;
                    break;
                case "Hindi":
                    languageCode = FirebaseTranslateLanguage.HI;
                    break;
                case "Hungarian":
                    languageCode = FirebaseTranslateLanguage.HU;
                    break;
                case "Icelandic":
                    languageCode = FirebaseTranslateLanguage.IS;
                    break;
                case "Indonesian":
                    languageCode = FirebaseTranslateLanguage.ID;
                    break;
                case "Irish":
                    languageCode = FirebaseTranslateLanguage.GA;
                    break;
                case "Italian":
                    languageCode = FirebaseTranslateLanguage.IT;
                    break;
                case "Japanese":
                    languageCode = FirebaseTranslateLanguage.JA;
                    break;
                case "Kannada":
                    languageCode = FirebaseTranslateLanguage.KN;
                    break;
                case "Korean":
                    languageCode = FirebaseTranslateLanguage.KO;
                    break;
                case "Latvian":
                    languageCode = FirebaseTranslateLanguage.LV;
                    break;
                case "Lithuanian":
                    languageCode = FirebaseTranslateLanguage.LT;
                    break;
                case "Macedonian":
                    languageCode = FirebaseTranslateLanguage.MK;
                    break;
                case "Malay":
                    languageCode = FirebaseTranslateLanguage.MS;
                    break;
                case "Maltese":
                    languageCode = FirebaseTranslateLanguage.MT;
                    break;
                case "Marathi":
                    languageCode = FirebaseTranslateLanguage.MR;
                    break;
                case "Norwegian (Bokmål)":
                    languageCode = FirebaseTranslateLanguage.NO;
                    break;
                case "Persian":
                    languageCode = FirebaseTranslateLanguage.FA;
                    break;
                case "Polis":
                    languageCode = FirebaseTranslateLanguage.PL;
                    break;
                case "Portuguese":
                    languageCode = FirebaseTranslateLanguage.PT;
                    break;
                case "Romania":
                    languageCode = FirebaseTranslateLanguage.RO;
                    break;
                case "Russian":
                    languageCode = FirebaseTranslateLanguage.RU;
                    break;

                case "Slova":
                    languageCode = FirebaseTranslateLanguage.SK;
                    break;

                case "Slovenian":
                    languageCode = FirebaseTranslateLanguage.SL;
                    break;

                case "Spanish":
                    languageCode = FirebaseTranslateLanguage.ES;
                    break;

                case "Swahili":
                    languageCode = FirebaseTranslateLanguage.SW;
                    break;

                case "Swedish":
                    languageCode = FirebaseTranslateLanguage.SV;
                    break;


                case "Tamil":
                    languageCode = FirebaseTranslateLanguage.TA;
                    break;


                case "Telugu":
                    languageCode = FirebaseTranslateLanguage.TE;
                    break;

                case "Thai":
                    languageCode = FirebaseTranslateLanguage.TH;
                    break;

                case "Turkish":
                    languageCode = FirebaseTranslateLanguage.TR;
                    break;

                case "Ukrainian":
                    languageCode = FirebaseTranslateLanguage.UK;
                    break;

                case "Urdu":
                    languageCode = FirebaseTranslateLanguage.UR;
                    break;


                case "Vietnamese":
                    languageCode = FirebaseTranslateLanguage.VI;
                    break;

                case "Welsh":
                    languageCode = FirebaseTranslateLanguage.CY;
                    break;

                case "Zulu":
                    languageCode = FirebaseTranslateLanguage.ZH;
                    break;
                default:
                    languageCode = 0;


            }

            return languageCode;
        }
}
