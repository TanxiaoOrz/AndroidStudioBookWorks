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
                    Toast.makeText(this,"请选择要付款的商品~",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"付款成功",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**
     * 数据
     */
    private void initDate() {
        //创建集合
        goodsList = new ArrayList<>();
        //数据库
        userDao = Myapplication.getInstances().getDaoSession().getUserDao();
        userDao.deleteAll();
        //数据源
        for (int i = 0; i < 10; i++) {
            //向数据库存放数据
            User user = new User((long) i,
                    "购物车里的第" + (i + 1) + "件商品",
                    (i + 20) + "码",
                    "10",
                    "10");
            userDao.insert(user);
        }
        //从数据库中把数据放到集合中
        goodsList=userDao.loadAll();
        //把结合中的数据放到HashMap集合中
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
     * 控制价格展示总价
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
        price.setText(" ¥ "+totalPrice);
        tv_go_to_pay.setText("付款("+totalCount+")");
    }

    /**
     * 删除 控制价格展示总价
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
     *全选或反选
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
        if(isCheck==true&&isUnCheck==false){//已经全选,做反选
            for(int i=0;i<listmap.size();i++){
                map.put(listmap.get(i).get("id"),0);
            }
            all_chekbox.setChecked(false);
        }else if(isCheck==true && isUnCheck==true){//部分选择,做全选
            for(int i=0;i<listmap.size();i++){
                map.put(listmap.get(i).get("id"),1);
            }
            all_chekbox.setChecked(true);
        }else if(isCheck==false && isUnCheck==true){//一个没选,做全选
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