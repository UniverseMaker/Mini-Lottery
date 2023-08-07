package com.dspark.lottery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ActivityMemberEdit extends Fragment {
    static MainActivity ma;
    static ActivityMember am;
    View v;

    SimpleListAdapter adapter;
    ArrayList<SimpleListItem> mItems = new ArrayList<>();
    RecyclerView recyclerView;

    int id;
    String name;
    String memberlist;

    public ActivityMemberEdit() {
        // Required empty public constructor
    }

    public static ActivityMemberEdit newInstance(MainActivity _ma, ActivityMember _am, int id, String name, String memberlist) {
        ma = _ma;
        am = _am;
        ActivityMemberEdit fragment = new ActivityMemberEdit();
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("name", name);
        args.putString("memberlist", memberlist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
            name = getArguments().getString("name");
            memberlist = getArguments().getString("memberlist");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("명단관리");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_member_edit, container, false);
        v.findViewById(R.id.btnMemberNewAdd).setOnClickListener(btnMemberNewAdd_Clicked);
        v.findViewById(R.id.btnMemberNewSubmit).setOnClickListener(btnMemberNewSubmit_Clicked);
        //v.findViewById(R.id.btnMemberNewDelete).setOnClickListener(btnMemberNewDelete_Clicked);

        setRecyclerView();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        Button bt = (Button)getActivity().findViewById(R.id.btnMemberNewSubmit);
        if(id != -1) {
            mItems.clear();

            String[] memberlists = memberlist.split("/_/");
            for (int i = 0; i < memberlists.length; i++)
                mItems.add(new SimpleListItem(memberlists[i]));

            adapter.notifyDataSetChanged();
            //recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

            TextView tv = (TextView) getActivity().findViewById(R.id.txtMemberEditTitle);
            tv.setText(name);

            bt.setText("수정하기");
        }
        else {
            bt.setText("등록하기");
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    private void setRecyclerView(){
        //ActivityMainBinding mainBinding;

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_memberlist_edit);

        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        recyclerView.setHasFixedSize(true);

        // RecyclerView에 Adapter를 설정해줍니다.
        adapter = new SimpleListAdapter(mItems);
        recyclerView.setAdapter(adapter);

        // 다양한 LayoutManager 가 있습니다. 원하시는 방법을 선택해주세요.
        // 지그재그형의 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        // 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        // 가로 또는 세로 스크롤 목록 형식
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setItemClick(listMemberEditRecycler_Clicked);
    }

    View.OnClickListener btnMemberNewAdd_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText tv = (EditText)getActivity().findViewById(R.id.txtMemberEditName);
            mItems.add(new SimpleListItem(tv.getText().toString()));
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

            tv.setText("");
        }
    };

    View.OnClickListener btnMemberNewSubmit_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText etn = (EditText) getActivity().findViewById(R.id.txtMemberEditName);
            EditText et = (EditText) getActivity().findViewById(R.id.txtMemberEditTitle);
            String name = et.getText().toString();

            String member = "";
            for(int i = 0; i < mItems.size(); i++)
            {
                if(member.isEmpty() == false)
                    member += "/_/";

                member += mItems.get(i).getName();
            }

            if(name.isEmpty() == true || member.isEmpty() == true) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "추첨목록명과 추첨인명단은 꼭 입력해야합니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            //ma.deleteDatabase("MINISLOTTERY");
            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null , 1);
            if(id == -1)
                dbHelper.insertMember(name, member);
            else
                dbHelper.updateMember(String.valueOf(id), name, member);

            InputMethodManager imm = (InputMethodManager) ma.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etn.getWindowToken(), 0);

            //am.onStart();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    };

    SimpleListAdapter.ItemClick listMemberEditRecycler_Clicked = new SimpleListAdapter.ItemClick() {
        @Override
        public void onClick(View view, final int position) {
            //클릭시 실행될 함수 작성

            final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
            dlgAlert.setMessage("선택대상\n" + mItems.get(position).getName());
            dlgAlert.setTitle("원하는 동작을 선택하세요");
            dlgAlert.setNeutralButton("삭제",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            mItems.remove(position);
                            adapter.notifyDataSetChanged();
                            //recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

                        }
                    });
            dlgAlert.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            AlertDialog ad = dlgAlert.create();
                            ad.cancel();
                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();


        }
    };

    /*
    View.OnClickListener btnMemberNewDelete_Clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null , 1);

        }
    };
    */

}
