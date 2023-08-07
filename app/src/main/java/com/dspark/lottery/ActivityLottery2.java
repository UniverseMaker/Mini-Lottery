package com.dspark.lottery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ActivityLottery2 extends Fragment {
    static MainActivity ma;
    View v;

    Button bmember;
    Button bprize;
    TextView twim;

    String member_name = "";
    String member_list = "";

    String prize_name = "";
    String prize_list = "";

    String mParam1 = "";
    String mParam2 = "";

    public ActivityLottery2() {
        // Required empty public constructor
    }

    public static ActivityLottery2 newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivityLottery2 fragment = new ActivityLottery2();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("이름추첨");

        try {
            //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
            v = inflater.inflate(R.layout.content_lottery2, container, false);
            v.findViewById(R.id.btnLottery2Start).setOnClickListener(btnLottery2Start_Clicked);
            v.findViewById(R.id.btnLottery2GetMemberList).setOnClickListener(btnLottery2GetMemberList_Clicked);
            v.findViewById(R.id.btnLottery2GetPrizeList).setOnClickListener(btnLottery2GetPrizeList_Clicked);
            v.findViewById(R.id.txtLottery2Wim).setOnFocusChangeListener(txtLottery2Wim_Focused);
            ma.activityLottery2 = this;
        }catch (Exception e){
            ma.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        bmember = (Button)getActivity().findViewById(R.id.btnLottery2GetMemberList);
        bprize = (Button)getActivity().findViewById(R.id.btnLottery2GetPrizeList);
        twim = (TextView)getActivity().findViewById(R.id.txtLottery2Wim);

        if(mParam1 == "result"){
            String[] pset = mParam2.split("//_//");
            member_name = pset[0];
            member_list = pset[1];
            bmember.setText("목록선택됨\n<미당첨자 재추첨>");

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    public void callbackMember(String id, String name, String data){
        member_name = name;
        member_list = data;
        bmember.setText("목록선택됨\n<" + name + ">");
    }

    public void callbackPrize(String id, String name, String data){
        prize_name = name;
        prize_list = data;
        bprize.setText("목록선택됨\n<" + name + ">");
    }

    View.OnClickListener btnLottery2GetMemberList_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ma.getSupportFragmentManager().beginTransaction().add(R.id.container, ActivityMember.newInstance(ma,"lottery2", "")).addToBackStack(null).commit();

        }
    };

    View.OnClickListener btnLottery2GetPrizeList_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            twim.setText("");
            ma.getSupportFragmentManager().beginTransaction().add(R.id.container, ActivityPrize.newInstance(ma,"lottery2", "")).addToBackStack(null).commit();

        }
    };

    View.OnFocusChangeListener txtLottery2Wim_Focused = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                prize_list = "";
                prize_name = "";
                bprize.setText("목록해제됨");
            }
        }
    };


    View.OnClickListener btnLottery2Start_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et = (EditText)getActivity().findViewById(R.id.txtLottery2Description);
            String name = et.getText().toString();

            et = (EditText)getActivity().findViewById(R.id.txtLottery2Wim);
            String wims = et.getText().toString();

            if(member_name.isEmpty() == true || !(wims.isEmpty() == true ^ prize_name.isEmpty() == true)) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "추첨대상명단과 당첨인원(경품목록)은 꼭 입력해야합니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            if(wims.isEmpty() != true && Pattern.matches("(^[0-9]*$)", wims) == false)
            {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "당첨인원은 숫자만 입력할 수 있습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            try
            {
                //Integer.parseInt(member);
                if(prize_name.isEmpty() == true)
                    Integer.parseInt(wims);
            }
            catch (Exception e){
                Snackbar.make(getActivity().findViewById(android.R.id.content), "추첨대상과 당첨인원은 최대 21억명을 넘을 수 없습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            /*
            String[] members;
            if(member_list.indexOf("/_/") != -1)
                members = member_list.split("/_/");
            else
                members = new String[]{member_list};
            */

            ArrayList<String> memberlist = new ArrayList<>();
            ArrayList<String> prizelist = new ArrayList<>();

            String[] members = member_list.split("/_/");
            for(int i = 0; i < members.length; i++)
                memberlist.add(members[i]);

            String[] prizes = null;
            if(wims.isEmpty() == false) {
                for (int i = 0; i < Integer.parseInt(wims); i++)
                    prizelist.add("");
            }
            else{
                prizes = prize_list.split("/_/");
                for(int i = 0; i < prizes.length; i++)
                    prizelist.add(" - " + prizes[i]);
            }

            if(prizelist.size() == 0){
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
                dlgAlert.setTitle("확인해주세요!");
                dlgAlert.setMessage("당첨자는 최소 1명이상이어야 합니다!\n현재 추첨대상: " + memberlist.size() + "명\n현재 당첨대상: 0명");
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
        }
    };

}
