package com.musicapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.musicapp.R;

import com.musicapp.aes.AESHelper;

/**
 * Created by PalseeTrivedi on 2/8/2017.
 */

public class AesDemo extends Activity {
    TextView tvDycript;
  //  String encryptedText="Rx7YWOI6i9hkTEs3Dl+nJw==";
  /*String encryptedText="AriwCjrFQD1eCVDqVW32pA==";*/

    String encryptedText="z3zYwQ36g9tZw7AM2e1zPXgD0IXsCK5kGw6V+qk3Gf3pKCeNlQkEg4xVlMc+5KFhYW2871oNo6sUHUInL0Z7vZkTwZUzCZ3l7lKohzmAQNwsflU988EvhcjDwtmQnveX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes_demo);
        tvDycript=(TextView) findViewById(R.id.tvDycript);
        try {
          /*  encryptedText=encryptedText.replace("+"," ");
           encryptedText=encryptedText.replace("="," ");
            encryptedText=encryptedText.replace("n"," ");*/
         //   String dycriptText= AESHelper.decrypt("musicapp2017",encryptedText);
           // String dycriptText= AESHelper.decrypt("musicapp20172016","YDUIqtrK1C0Epy9dNcilFQ==");
            String dycriptText= AESHelper.decrypt("musicapp2017",encryptedText);
            System.out.println("Dycripted data"+dycriptText);
            tvDycript.setText(dycriptText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
