package org.mo.pmas.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mo.common.activity.BaseFramgmentActivity;
import org.mo.common.util.ConfigContract;
import org.mo.common.util.EncryptUtils;
import org.mo.common.util.HttpURLTools;
import org.mo.pmas.activity.application.PmasAppliaction;
import org.mo.pmas.activity.fragment.ScoreSearchFragment;
import org.mo.pmas.activity.fragment.ScoreShowFragment;
import org.mo.pmas.activity.fragment.listview.XListView;
import org.mo.pmas.ext.entity.Score;
import org.mo.znyunxt.entity.CodeUtil;
import org.mo.znyunxt.entity.JsonToObjectUtil;
import org.mo.znyunxt.entity.Semester;
import org.mo.znyunxt.entity.UserDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moziqi on 2015/2/4 0004.
 */
public class ScoreActivity extends BaseFramgmentActivity {

    private AsyncHttpClient instance;
    private SharedPreferences preferences;
    private String username;

    private int page = 1;//当前页
    private int rows = 10;//每页多少条记录

    private String studentId = null;//学生id
    private String departId = null;//用户所在部门id
    private String rolename;
    private String semesterId;
    private List<Semester> semesterList;

    private Spinner sp_semester;
    private Spinner sp_exam_type;
    private XListView list_score;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PmasAppliaction.getInstance().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        instance = HttpURLTools.getInstance();
        username = preferences.getString(ConfigContract.USERNAME, null);

        sp_semester = (Spinner) findViewById(R.id.sp_semester);
        sp_exam_type = (Spinner) findViewById(R.id.sp_exam_type);
        list_score = (XListView) findViewById(R.id.list_score);
    }

    @Override
    protected void toInitUI() {
    }

    @Override
    protected void toUIOper() {
        String url = null;
        RequestParams params = null;
        if (username != null) {
            try {
                String encrypt3DES = EncryptUtils.Encrypt3DES(username, ConfigContract.CODE);
                url = ConfigContract.SERVICE_SCHOOL + "loginController.do?getUserInfo";
                params = new RequestParams();
                params.put("loginname", encrypt3DES);
                instance.post(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        showErrorIms(i + "--" + s);
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        Log.e(ConfigContract.CMD, s);
                        if (i == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String attributes = jsonObject.getString("attributes");
                                UserDetail userDetail = new UserDetail(attributes);
                                Log.e(ConfigContract.CMD, userDetail.toString());
                                if (userDetail != null) {
                                    studentId = userDetail.getId();
                                    departId = userDetail.getDepartid();
                                    rolename = userDetail.getRolename();
                                    if ("学生".equals(rolename)) {

                                    } else {

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(ConfigContract.CMD, e.getMessage());
                            }
                        } else {
                            showErrorIms(ConfigContract.GET_USER_INFO_ERROR);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        String url = null;
                        RequestParams params = null;
                        url = ConfigContract.SERVICE_SCHOOL + ConfigContract.TB_SEMESTER_CONTROLLER_URL2;
                        params = new RequestParams();
                        params.put(ConfigContract.filed, "id,year,semester,name");
                        params.put(ConfigContract.PAGE, page);
                        params.put(ConfigContract.ROWS, rows);
                        instance.post(url, params, new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                            }

                            @Override
                            public void onSuccess(int i, Header[] headers, String s) {
                                try {
                                    semesterList = new ArrayList<Semester>();
                                    JSONArray jsonArray = JsonToObjectUtil.getJSONArray(s);
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        Semester semester = new Semester(jsonArray.getString(j));
                                        semesterList.add(semester);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                String[] arrays = new String[semesterList.size()];
                                for (int i = 0; i < semesterList.size(); i++) {
                                    arrays[i] = semesterList.get(i).getName();
                                }
                                ArrayAdapter arrayAdapter = new ArrayAdapter(ScoreActivity.this, android.R.layout.simple_spinner_item, arrays);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_semester.setAdapter(arrayAdapter);
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ShowToast(ConfigContract.GET_USER_INFO_ERROR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_score_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RequestParams params = null;
        String url = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.score_search_actions:

                return true;
            case R.id.kaoshixinxi:

                return true;
            case R.id.xueshengchengji:

                return true;
            case R.id.xueqiliebiao1:
                url = ConfigContract.SERVICE_SCHOOL + ConfigContract.TB_SEMESTER_CONTROLLER_URL;
                params = new RequestParams();
                instance.post(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        showErrorIms(s);
                    }
                });
                return true;
            case R.id.xueqiliebiao2:

                return true;
            case R.id.kaoshiliebiao1:
                url = ConfigContract.SERVICE_SCHOOL + ConfigContract.TB_EXAM_CONTROLLER_URL1;
                params = new RequestParams();
                Semester semester = semesterList.get(sp_semester.getSelectedItemPosition());
                params.put(ConfigContract.filed, "id,semesterid,examType,name,createDatetime");
                params.put(ConfigContract.SEMESTER_ID, semester.getId());
//                params.put(ConfigContract.EXAM_TYPE, sp_exam_type.getSelectedItemPosition());
                params.put(ConfigContract.PAGE, page);
                params.put(ConfigContract.ROWS, rows);
                showErrorIms("考试列表参数：" + params.toString());
                instance.post(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        try {
                            JSONArray jsonArray = JsonToObjectUtil.getJSONArray(s);
                            showErrorIms(s);
//                            for (int j = 0; j < jsonArray.length(); j++) {
//                                Semester semester = new Semester(jsonArray.getString(j));
//                                showErrorIms(semester.toString());
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            case R.id.kaoshiliebiao2:
                url = ConfigContract.SERVICE_SCHOOL + ConfigContract.TB_EXAM_CONTROLLER_URL2;
                params = new RequestParams();
                Semester semester2 = semesterList.get(sp_semester.getSelectedItemPosition());
                params.put(ConfigContract.SEMESTER_ID, semester2.getId());
                params.put(ConfigContract.userDid, departId);
                showErrorIms("考试列表参数：" + params.toString());
                instance.post(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        showErrorIms(i+s);
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        try {
                            JSONArray jsonArray = JsonToObjectUtil.getJSONArray(s);
                            showErrorIms(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            case R.id.genjukaoshihuoquchengjibiebiao:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}