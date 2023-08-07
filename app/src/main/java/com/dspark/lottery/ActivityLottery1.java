package com.dspark.lottery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ActivityLottery1 extends Fragment implements View.OnClickListener{
    static MainActivity ma;
    View v;

    public ActivityLottery1() {
        // Required empty public constructor
    }

    public static ActivityLottery1 newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivityLottery1 fragment = new ActivityLottery1();
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
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("빠른추첨");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_lottery1, container, false);
        ((Button)v.findViewById(R.id.btnLottery1Start)).setOnClickListener(this);

        return v ;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLottery1Start :
                EditText et = (EditText)getActivity().findViewById(R.id.txtLottery1Description);
                String name = et.getText().toString();

                et = (EditText)getActivity().findViewById(R.id.txtLottery1Member);
                String member = et.getText().toString();

                et = (EditText)getActivity().findViewById(R.id.txtLottery1Wim);
                String wims = et.getText().toString();

                if(member.isEmpty() == true || wims.isEmpty() == true) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "추첨대상인원과 당첨인원은 꼭 입력해야합니다.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                }

                if(wims.isEmpty() != true && Pattern.matches("(^[0-9]*$)", wims) == false)
                {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "당첨인원은 숫자만 입력할 수 있습니다.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                try
                {
                    Integer.parseInt(member);
                    Integer.parseInt(wims);
                }
                catch (Exception e){
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "추첨대상과 당첨인원은 최대 21억명을 넘을 수 없습니다.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                ArrayList<String> memberlist = new ArrayList<>();
                for(int i = 0; i < Integer.parseInt(member); i++)
                    memberlist.add(String.valueOf(i+1));

                ArrayList<String> prizelist = new ArrayList<>();
                for(int i = 0; i < Integer.parseInt(wims); i++)
                    prizelist.add("상품없음");

                if(prizelist.size() == 0){
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
                    dlgAlert.setTitle("확인해주세요!");
                    dlgAlert.setMessage("당첨자는 최소 1명이상이어야 합니다!\n현재 추첨대상: " + memberlist.size() + "명\n현재 당첨대상: " + prizelist.size() + "명");
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

                if(memberlist.size() < prizelist.size()){
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
                    dlgAlert.setTitle("확인해주세요!");
                    dlgAlert.setMessage("추첨대상의 인원수보다 당첨인원수가 더 적어야만 합니다!\n현재 추첨대상: " + memberlist.size() + "명\n현재 당첨대상: " + prizelist.size() + "명");
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

                try {
                    ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityLotteryWork.newInstance(ma, name, memberlist, prizelist)).addToBackStack(null).commit();
                }catch (Exception e){

                }
                break ;
        }
    }

    /*
    View.OnClickListener btnLottery1Start = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            FragmentTransaction transaction = ma.getSupportFragmentManager().beginTransaction();

            transaction.add(R.id.container, ActivityLotteryWork.newInstance(ma, "",""));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };

    public void btnLottery1Start_Cliecked(View v) {
        FragmentTransaction transaction = ma.getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.container, ActivityLotteryWork.newInstance(ma, "",""));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    class BtnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //TextView textView1 = (TextView) findViewById(R.id.textView1);
            switch (view.getId()) {
                case R.id.btnLottery1Start :
                    ma.getSupportFragmentManager().beginTransaction().add(R.id.container, ActivityLotteryWork.newInstance(ma, "","")).addToBackStack(null).commit();
                    break ;
            }
        }
    }
    */

}
