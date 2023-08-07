package com.dspark.lottery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActivityLotteryResult extends Fragment {
    static MainActivity ma;
    View v;

    String mParam1;
    String mParam2;

    String logdata = "";
    String loserlist = "";

    public ActivityLotteryResult() {
        // Required empty public constructor
    }

    public static ActivityLotteryResult newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivityLotteryResult fragment = new ActivityLotteryResult();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
            mParam2 = getArguments().getString("param2");

            String[] dataset = mParam1.split("//_//",-1);
            logdata = dataset[0];
            loserlist = dataset[1];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("결과보고서");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_lotteryresult, container, false);
        v.findViewById(R.id.btnShareResult).setOnClickListener(btnShareResult_Clicked);
        v.findViewById(R.id.btnLotteryForLoser).setOnClickListener(btnLotteryForLoser_Clicked);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();


        TextView tv = (TextView)getActivity().findViewById(R.id.txtLotteryResult1);
        tv.setText(logdata);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    View.OnClickListener btnShareResult_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "미니의 추첨기");
            intent.putExtra(Intent.EXTRA_TEXT, logdata += "\nhttps://goo.gl/mnA8qW");

            Intent chooser = Intent.createChooser(intent, "공유하기");
            startActivity(chooser);
        }
    };

    View.OnClickListener btnLotteryForLoser_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(loserlist.isEmpty() == true) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
                dlgAlert.setTitle("확인해주세요!");
                dlgAlert.setMessage("미당첨자가 없습니다!");
                dlgAlert.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return;
            }
            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityLottery2.newInstance(ma, "result", "재추첨//_//" + loserlist)).addToBackStack(null).commit();

        }
    };



}
