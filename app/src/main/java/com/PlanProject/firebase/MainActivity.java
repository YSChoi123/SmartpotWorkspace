package com.PlanProject.firebase;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {


    int LedMode , WaterMode, WindMode;    // 모드 상태 -> 0: 수동, 1: 자동
    int LedState;                         // LED 수동조작 상태 -> 0: off, 1: on

    Button CtrBtn_led;      // 수동 조작 버튼

    TextView text_led, text_water, text_wind;
    FirebaseDatabase database;
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 레이아웃 객체 생성
        CtrBtn_led = (Button) findViewById(R.id.button5);
        text_led = (TextView) findViewById(R.id.text_LedMode);
        text_water = (TextView) findViewById(R.id.text_WaterMode);
        text_wind = (TextView) findViewById(R.id.text_WindMode);

        LedMode = 1;
        WaterMode =1;
        WindMode =1;
        LedState = 0;

        firebaseInit();
    }


    public void firebaseInit(){
        // 파이어베이스 데이터베이스 인스턴스를 가져와 루트 위치 ref(참조) 얻기
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();

        // DB root 하위 위치에서 발생하는 이벤트 리스너 등록
        rootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

                Log.d("Main", "RDB-root 리스너: "+snapshot.getValue());
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.getValue(String.class).equals("change"))      // auto_mode 하위 항목들 값이 "change"로 변경되었다면 팝업 알림
                        Toast.makeText(getApplicationContext(), "변경 완료", Toast.LENGTH_SHORT).show();
                    else if(ds.getValue(String.class).equals("finish")) // work 하위 항목들 값이 "finish"로 변경되었다면 팝업 알림
                        Toast.makeText(getApplicationContext(), "작업 완료", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved( DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }


    // 파이어베이스 DB 업데이트 - 원하는 key 항목에 데이터 쓰기
    public void setDataFirebase(String func, String key, String value){
        // func-key(지정 키) 항목의 값을 value 값(String)으로 넣기
        rootRef.child(func).child(key).setValue(value);

    }

    // 자동 모드 관련 버튼 클릭 시 mode에 따라 상태 변경
    public int onClickModeButton(TextView stateText, int mode, String key){
        if(mode == 0){ // 해당 모드를 자동으로 변경
            setDataFirebase("auto_mode", key, "on");
            stateText.setText("ON");
            return 1;
        }
        else{           // 해당 모드를 수동으로 변경
            setDataFirebase("auto_mode", key, "off");
            stateText.setText("OFF");
            return 0;
        }

    }

    // LED 조작 버튼 클릭 시 상태 변경
    public void onClickLEDControlButton(){
        if(LedState == 0) { // led 켜기
            setDataFirebase("work", "led", "on");
            CtrBtn_led.setText("led 켬");
            LedState = 1;
        }
        else{               // led 끄기
            setDataFirebase("work", "led", "off");
            CtrBtn_led.setText("led 끔");
            LedState = 0;
        }


    }

    // 버튼 클릭 리스너
    public void MyonClick(View view){
        switch(view.getId()){
            case R.id.button:
                LedMode = onClickModeButton(text_led, LedMode, "led");
                break;
            case R.id.button2:
                WaterMode = onClickModeButton(text_water, WaterMode, "water");
                break;
            case R.id.button3:
               WindMode = onClickModeButton(text_wind, WindMode, "wind");
                break;
            case R.id.button4:
                setDataFirebase("work","water","request");
                break;
            case R.id.button5:
                onClickLEDControlButton();
                break;
        }
    }





}