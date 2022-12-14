package com.example.androidstudiobookworks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidstudiobookworks.greendao.UserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,CaetAdapter.RefreshPriceInterface{

    private LinearLayout top_bar;
    private ListView listview;
    private CheckBox all_chekbox;
    private TextView price;
    private TextView delete;
    private TextView tv_go_to_pay;

    private List<User> goodsList;
    private UserDao userDao;
    private List<HashMap<String,String>> listmap=new ArrayList<>();
    private CaetAdapter adapter;

    private double totalPrice = 0.00;
    private int totalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        top_bar = (LinearLayout) findViewById(R.id.top_bar);
        listview = (ListView) findViewById(R.id.listview);
        all_chekbox = (CheckBox) findViewById(R.id.all_chekbox);
        price = (TextView) findViewById(R.id.tv_total_price);
        delete = (TextView) findViewById(R.id.tv_delete);
        tv_go_to_pay = (TextView) findViewById(R.id.tv_go_to_pay);

        all_chekbox.setOnClickListener(this);
        delete.setOnClickListener(this);
        tv_go_to_pay.setOnClickListener(this);

        initDate();
        adapter = new CaetAdapter(MainActivity.this, listmap);
        listview.setAdapter(adapter);
        adapter.setRefreshPriceInterface(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_chekbox:
                AllTheSelected();
                break;
            case R.id.tv_delete:
                checkDelete(adapter.getPitchOnMap());
                break;
            case R.id.tv_go_to_pay:
                if(totalCount<=0){
                    Toast.makeText(this,"???????????????????????????~",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"????????????",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**
     * ??????
     */
    private void initDate() {
        //????????????
        goodsList = new ArrayList<>();
        //?????????
        userDao = Myapplication.getInstances().getDaoSession().getUserDao();
        userDao.deleteAll();
        //?????????
        for (int i = 0; i < 10; i++) {
            //????????????????????????
            User user = new User((long) i,
                    "??????????????????" + (i + 1) + "?????????",
                    (i + 20) + "???",
                    "10",
                    "10");
            userDao.insert(user);
        }
        //???????????????????????????????????????
        goodsList=userDao.loadAll();
        //???????????????????????????HashMap?????????
        for(int i=0;i<goodsList.size();i++){
            HashMap<String,String> map=new HashMap<>();
            map.put("id",goodsList.get(i).getId()+"");
            map.put("name",goodsList.get(i).getName());
            map.put("type",(goodsList.get(i).getType()));
            map.put("price",goodsList.get(i).getPrice()+"");
            map.put("count",goodsList.get(i).getCount()+"");
            listmap.add(map);
        }
    }

    @Override
    public void refreshPrice(HashMap<String, Integer> pitchOnMap) {
        priceControl(pitchOnMap);
    }

    /**
     * ????????????????????????
     */
    private void priceControl(Map<String, Integer> pitchOnMap){
        totalCount = 0;
        totalPrice = 0.00;
        for(int i=0;i<listmap.size();i++){
            if(pitchOnMap.get(listmap.get(i).get("id"))==1){
                totalCount=totalCount+Integer.valueOf(listmap.get(i).get("count"));
                double goodsPrice=Integer.valueOf(listmap.get(i).get("count"))*Double.valueOf(listmap.get(i).get("price"));
                totalPrice=totalPrice+goodsPrice;
            }
        }
        price.setText(" ?? "+totalPrice);
        tv_go_to_pay.setText("??????("+totalCount+")");
    }

    /**
     * ?????? ????????????????????????
     * @param map
     */
    private void checkDelete(Map<String,Integer> map){
        List<HashMap<String,String>> waitDeleteList=new ArrayList<>();
        Map<String,Integer> waitDeleteMap =new HashMap<>();
        for(int i=0;i<listmap.size();i++){
            if(map.get(listmap.get(i).get("id"))==1){
                waitDeleteList.add(listmap.get(i));
                waitDeleteMap.put(listmap.get(i).get("id"),map.get(listmap.get(i).get("id")));
            }
        }
        listmap.removeAll(waitDeleteList);
        map.remove(waitDeleteMap);
        priceControl(map);
        adapter.notifyDataSetChanged();
    }
    /**
     *???????????????
     */
    private void AllTheSelected(){
        HashMap<String,Integer> map=adapter.getPitchOnMap();
        boolean isCheck=false;
        boolean isUnCheck=false;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();

            if(Integer.valueOf(entry.getValue().toString())==1){
                isCheck=true;
            }else{
                isUnCheck=true;
            }
        }
        if(isCheck==true&&isUnCheck==false){//????????????,?????????
            for(int i=0;i<listmap.size();i++){
                map.put(listmap.get(i).get("id"),0);
            }
            all_chekbox.setChecked(false);
        }else if(isCheck==true && isUnCheck==true){//????????????,?????????
            for(int i=0;i<listmap.size();i++){
                map.put(listmap.get(i).get("id"),1);
            }
            all_chekbox.setChecked(true);
        }else if(isCheck==false && isUnCheck==true){//????????????,?????????
            for(int i=0;i<listmap.size();i++){
                map.put(listmap.get(i).get("id"),1);
            }
            all_chekbox.setChecked(true);
        }
        priceControl(map);
        adapter.setPitchOnMap(map);
        adapter.notifyDataSetChanged();
    }

}