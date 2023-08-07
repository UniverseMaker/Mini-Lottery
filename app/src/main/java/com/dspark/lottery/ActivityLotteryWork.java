package com.dspark.lottery;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityLotteryWork extends Fragment {
    static MainActivity ma;
    View v;

    RecyclerView.Adapter adapter;
    ArrayList<LotteryWorkRecyclerItem> mItems = new ArrayList<>();
    RecyclerView recyclerView;

    int firstStart = 0;

    private static final int SEND_THREAD_INFOMATION = 0;
    private static final int SEND_THREAD_STOP_MESSAGE = 1;

    Timer mainTimer;
    public static ArrayList<String> userList = new ArrayList<String>();
    public static ArrayList<String> wimList = new ArrayList<String>();
    DetailLotteryTask dlt;

    int maxWims = 0;
    int remainWims = 0;
    int currentValue = 0;
    String lastLog;

    TextView Viewer;
    String name;
    public static ArrayList<String> memberlist = new ArrayList<String>();
    public static ArrayList<String> memberlistClone = new ArrayList<String>();
    public static ArrayList<String> prizelist = new ArrayList<String>();
    public static ArrayList<String> prizelistClone = new ArrayList<String>();


    public ActivityLotteryWork() {
        // Required empty public constructor
    }

    public static ActivityLotteryWork newInstance(MainActivity _ma, String name, ArrayList<String> memberlist, ArrayList<String> prizelist) {
        ma = _ma;
        ActivityLotteryWork fragment = new ActivityLotteryWork();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putStringArrayList("memberlist", memberlist);
        args.putStringArrayList("prizelist", prizelist);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
            memberlist = getArguments().getStringArrayList("memberlist");
            prizelist = getArguments().getStringArrayList("prizelist");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("추첨시작");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_lotterywork, container, false);

        Viewer = (TextView)v.findViewById(R.id.txtDetailLotteryViewer);

        //((Button)v.findViewById(R.id.btnLotteryWorkGetWim)).setOnClickListener(this);
        v.findViewById(R.id.btnLotteryWorkGetWim).setOnClickListener(btnLotteryWorkGetWim_Clicked);
        v.findViewById(R.id.btnLotteryWorkGetWimRapid).setOnClickListener(btnLotteryWorkGetWimRapid_Clicked);
        v.findViewById(R.id.btnLotteryWorkRecord).setOnClickListener(btnLotteryWorkRecord_Clicked);

        //TextView infol = (TextView)getActivity().findViewById(R.id.txtDetailLotteryInfol);
        //infol.setText("마음의 준비가 되면 당첨자확인을 누르세요");
        //infol.setBackgroundColor(Color.parseColor("#FFFF99"));

        setRecyclerView();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(firstStart == 0) {
            TextView infol = (TextView) getActivity().findViewById(R.id.txtDetailLotteryInfol);
            infol.setText("마음의 준비가 되면 당첨자확인을 누르세요");
            infol.setBackgroundColor(Color.parseColor("#FFFF99"));

            StartWork();
        }

        firstStart++;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    private void setRecyclerView(){
        //ActivityMainBinding mainBinding;

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        recyclerView.setHasFixedSize(true);

        // RecyclerView에 Adapter를 설정해줍니다.
        adapter = new LotteryWorkRecyclerAdapter(mItems);
        recyclerView.setAdapter(adapter);

        // 다양한 LayoutManager 가 있습니다. 원하시는 방법을 선택해주세요.
        // 지그재그형의 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        // 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        // 가로 또는 세로 스크롤 목록 형식
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    /*
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLotteryWorkGetWim :
                mItems.add(new LotteryWorkRecyclerItem("안나", "1위"));
                adapter.notifyDataSetChanged();
                break ;
        }
    }
    */

    public void StartWork(){
        try{
            mainTimer.cancel();
        }catch(Exception e){

        }

        memberlistClone.clear();
        for(int i = 0; i < memberlist.size(); i++)
            memberlistClone.add(memberlist.get(i));

        prizelistClone.clear();
        for(int i = 0; i < prizelist.size(); i++)
            prizelistClone.add(prizelist.get(i));

        mainTimer = new Timer();
        lastLog = "";

        maxWims = prizelist.size();
        remainWims = prizelist.size();
        //Wims.clear();


        dlt = new DetailLotteryTask(memberlistClone.size());
        mainTimer.schedule(dlt, 0, 50);
    }

    View.OnClickListener btnLotteryWorkGetWim_Clicked2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mItems.add(new LotteryWorkRecyclerItem("안나", "1위 - " + "상품없음"));
            adapter.notifyDataSetChanged();


        }
    };

    View.OnClickListener btnLotteryWorkGetWimRapid_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            TextView infol = (TextView)getActivity().findViewById(R.id.txtDetailLotteryInfol);

            while(remainWims > 0) {
                remainWims--;

                try {
                    int selWim = new Random().nextInt(memberlistClone.size());
                    String nowWim = memberlistClone.get(selWim);

                    memberlistClone.remove(selWim);
                    try {
                        dlt.setMaxNum(memberlistClone.size());
                    } catch (Exception e) {

                    }

                    int seqWim = maxWims - remainWims;
                    //Wims.add(nowWim + " (" + seqWim + "위)");
                    int prize = new Random().nextInt(prizelistClone.size());
                    String prizeName = prizelistClone.get(prize);
                    prizelistClone.remove(prize);
                    mItems.add(new LotteryWorkRecyclerItem(nowWim, seqWim + "위" + prizeName));
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

                    infol.setText(String.valueOf(seqWim) + "번째 당첨자 " + nowWim + "\n" + String.valueOf(remainWims) + "명 남았습니다");
                    infol.setBackgroundColor(Color.parseColor("#99FF99"));
                } catch (Exception e){

                }
            }

            ViewResult();
        }
    };

    View.OnClickListener btnLotteryWorkGetWim_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView infol = (TextView)getActivity().findViewById(R.id.txtDetailLotteryInfol);

            if(remainWims > 0){
                remainWims--;

                try {
                    int selWim = currentValue;
                    String nowWim = memberlistClone.get(selWim);

                    memberlistClone.remove(selWim);
                    try {
                        dlt.setMaxNum(memberlistClone.size());
                    } catch (Exception e) {

                    }

                    int seqWim = maxWims - remainWims;
                    //Wims.add(nowWim + " (" + seqWim + "위)");
                    int prize = new Random().nextInt(prizelistClone.size());
                    String prizeName = prizelistClone.get(prize);
                    prizelistClone.remove(prize);
                    mItems.add(new LotteryWorkRecyclerItem(nowWim, seqWim + "위" + prizeName));
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

                    infol.setText(String.valueOf(seqWim) + "번째 당첨자 " + nowWim + "\n" + String.valueOf(remainWims) + "명 남았습니다");
                    infol.setBackgroundColor(Color.parseColor("#99FF99"));
                } catch (Exception e){

                }

            }




            ViewResult();

        }
    };

    public void ViewResult(){
        if(remainWims <= 0) {
            mainTimer.cancel();
            Viewer.setVisibility(View.GONE);
            TextView Viewer2 = (TextView) getActivity().findViewById(R.id.txtDetailLotteryViewer2);
            Viewer2.setVisibility(View.VISIBLE);
            Viewer2.setText("추 첨 결 과");
            Viewer2.setTextSize(50);

            TextView infol = (TextView)getActivity().findViewById(R.id.txtDetailLotteryInfol);
            infol.setText("총 " + memberlist.size() + "명중 " + maxWims + "명 당첨\n미당첨자 " + String.valueOf(memberlist.size() - maxWims) + "명");
            infol.setBackgroundColor(Color.parseColor("#FFFFFF"));

            Button gw1 = (Button)getActivity().findViewById(R.id.btnLotteryWorkGetWim);
            Button gw2 = (Button)getActivity().findViewById(R.id.btnLotteryWorkGetWimRapid);
            Button gwrecord = (Button)getActivity().findViewById(R.id.btnLotteryWorkRecord);
            gw1.setVisibility(View.GONE);
            gw2.setVisibility(View.GONE);
            gwrecord.setVisibility(View.VISIBLE);

        }
    }

    View.OnClickListener btnLotteryWorkRecord_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FinalStage();
        }
    };

    public void FinalStage(){
        if(remainWims <= 0){
            try {
                try {
                    mainTimer.cancel();
                } catch (Exception e) {

                }

                Viewer.setText("추첨완료");

                TextView infol = (TextView)getActivity().findViewById(R.id.txtDetailLotteryInfol);
                infol.setText("추첨완료! 축하합니다!");
                infol.setBackgroundColor(Color.parseColor("#99FF99"));


                String loserlist = "";
                for (int i = 0; i < memberlistClone.size(); i++) {
                    if (loserlist.isEmpty() == false)
                        loserlist += "/_/";

                    loserlist += memberlistClone.get(i);
                }

                //TextView tagv = (TextView)getActivity().findViewById(R.id.txtDetailLotteryTag);
                //String tag = tagv.getText().toString();

                // 현재 시간을 msec으로 구한다.
                long now = System.currentTimeMillis();
                // 현재 시간을 저장 한다.
                Date date = new Date(now);
                // 시간 포맷으로 만든다.
                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String strNow = sdfNow.format(date);

                lastLog += "추첨 기록\n";
                if (name.isEmpty() == false) {
                    lastLog += "추첨 제목: " + name + "\n";
                } else {
                    lastLog += "추첨 제목: " + strNow + "\n";
                    name = strNow;
                }

                lastLog += "추첨 시각: " + strNow + "\n\n";
                lastLog += "추첨 결과: \n";

                for (int i = 0; i < mItems.size(); i++) {
                    lastLog += mItems.get(i).getName() + " (" + mItems.get(i).getNumber() + ")\n";
                }

                DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null, 1);
                dbHelper.insertRecord(name, lastLog + "//_//" + loserlist, strNow);

                ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityLotteryResult.newInstance(ma, lastLog + "//_//" + loserlist, "")).addToBackStack(null).commit();

                /*
                Message msg = alerthandler.obtainMessage();
                AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
                ad.setCancelable(false);
                ad.setTitle("당첨 결과");
                ad.setMessage(lastLog + "\n당첨번호는 추첨기록에 저장되었습니다.");
                ad.setButton(AlertDialog.BUTTON_NEUTRAL, "확인", msg);
                ad.show();


                String[] wimsClone = Wims.toArray(new String[Wims.size()]);
                Intent intent = new Intent(getActivity(), ActivityListView.class);
                intent.putExtra("list", wimsClone);
                startActivity(intent);

                //FragmentMain.setData(lastLog);

                sdfNow = new SimpleDateFormat("yyyy/MM/dd");
                strNow = sdfNow.format(date);

                SharedPreferences pref = getActivity().getSharedPreferences("MINILOTTERY", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();
                ed.putInt("totalLottery", (pref.getInt("totalLottery", 0)+1));
                ed.putString("lastLottery", strNow);
                ed.putString("Log", "<data>" + lastLog + "</data>" + pref.getString("Log", ""));
                ed.commit();
                */
            } catch (Exception e) {

            }
        }
    }

    private class DetailLotteryTask extends TimerTask {
        //TextView Viewer;
        Random random = new Random();
        int MaxNum = 0;
        public DetailLotteryTask(int _MaxNum){
            //Viewer = _Viewer;
            MaxNum = _MaxNum;
        }

        public void setMaxNum(int _MaxNum){
            MaxNum = _MaxNum;
        }

        public void run() {
            try {
                //counter++;
                //Viewer.setText(String.valueOf(counter));

                // 메시지 얻어오기
                Message msg = handler.obtainMessage();

                // 메시지 ID 설정
                msg.what = SEND_THREAD_INFOMATION;

                // 메시지 정보 설정 (int 형식)
                msg.arg1 = random.nextInt(MaxNum);

                handler.sendMessage(msg);
            }catch (Exception e) {

            }
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            currentValue = msg.arg1;
            if(currentValue < memberlistClone.size()){
                try{
                    if(remainWims >= 0)
                        Viewer.setText(memberlistClone.get(msg.arg1));
                }catch(Exception e){
                    try{
                        if(remainWims >= 0)
                            Viewer.setText(memberlistClone.get(0));
                    }catch(Exception e2){

                    }
                }

            }
        }
    };
}
