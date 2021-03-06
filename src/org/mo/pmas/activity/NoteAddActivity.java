package org.mo.pmas.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import org.mo.common.activity.BaseFramgmentActivity;
import org.mo.pmas.bmob.entity.MyUser;
import org.mo.pmas.bmob.entity.Note;
import org.mo.pmas.bmob.entity.NoteGroup;

import java.util.List;

/**
 * Created by moziqi on 2015/1/6 0006.
 */
public class NoteAddActivity extends BaseFramgmentActivity implements View.OnClickListener {
    private final static String TAG = NoteAddActivity.class.getSimpleName();
    private EditText m_et_note_add_title;
    private EditText m_et_note_add_group;
    private EditText m_et_note_add_content;
    private Button m_btn_note_add_save;
    private final static int MAX_COUNT = 140;
    private NoteGroup mNoteGroup;
    private Note mNote;
    private MyUser myUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        myUser = MyUser.getCurrentUser(this, MyUser.class);
        init();
    }

    @Override
    protected void toInitUI() {

    }

    @Override
    protected void toUIOper() {

    }

    void init() {
        m_et_note_add_title = (EditText) findViewById(R.id.et_note_add_title);
        m_et_note_add_group = (EditText) findViewById(R.id.et_note_add_group);
        m_et_note_add_content = (EditText) findViewById(R.id.et_note_add_content);
        m_btn_note_add_save = (Button) findViewById(R.id.btn_note_add_save);
        m_et_note_add_group.setInputType(InputType.TYPE_NULL);
        m_et_note_add_group.setOnClickListener(this);
        m_btn_note_add_save.setOnClickListener(this);
        //监听字数
        m_et_note_add_content.addTextChangedListener(new TextWatcher() {

            private int editStart;
            private int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = m_et_note_add_content.getSelectionStart();
                editEnd = m_et_note_add_content.getSelectionEnd();
                // 先去掉监听器，否则会出现栈溢出
                m_et_note_add_content.removeTextChangedListener(this);

                // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
                // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
                while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                    s.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
                // mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
                m_et_note_add_content.setSelection(editStart);
                // 恢复监听器
                m_et_note_add_content.addTextChangedListener(this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_note_add_group:
                toNoteAddGroup();
                break;
            case R.id.btn_note_add_save:
                findNoteInfoByTitle();
                break;
        }
    }


    //记事类型数组
    String arrayNoteGroup[];

    /**
     * 添加记事类型
     */
    private void toNoteAddGroup() {
        findNoteGroup();
    }

    /**
     * 查找记事类型
     */
    private void findNoteGroup() {
        BmobQuery<NoteGroup> query = new BmobQuery<NoteGroup>();
        query.setLimit(1000);
        query.order("createdAt");
        query.addWhereEqualTo("user", myUser);//查询当前用户
        Log.e(NoteAddActivity.this.getClass().getSimpleName(), myUser.getUsername());
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);// 先从缓存获取数据，如果没有，再从网络获取。
        query.findObjects(NoteAddActivity.this, new FindListener<NoteGroup>() {
            @Override
            public void onSuccess(List<NoteGroup> noteGroups) {
                showNoteGroupDialog(noteGroups);
            }

            @Override
            public void onError(int i, String s) {
                //showErrorIms(i);
            }
        });
    }

    /**
     * 显示记事类型对话框
     *
     * @param noteGroups
     */
    private void showNoteGroupDialog(List<NoteGroup> noteGroups) {
        if (noteGroups.size() == 0) {
            arrayNoteGroup = new String[]{"新增"};
        } else {
            arrayNoteGroup = new String[noteGroups.size() + 1];
            for (int i = 0; i < noteGroups.size(); i++) {
                arrayNoteGroup[i] = new String(noteGroups.get(i).getName());
            }
            arrayNoteGroup[noteGroups.size()] = "新增";
        }
        new AlertDialog.Builder(this).setTitle("记事类型").setItems(arrayNoteGroup, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("新增".equals(arrayNoteGroup[which])) {
                    Intent intent = new Intent(NoteAddActivity.this, NoteGroupAddActivity.class);
                    startActivity(intent);
                } else {
                    m_et_note_add_group.setText(arrayNoteGroup[which]);
                }
            }
        }).show();
    }

    /**
     * 查询是否存在已存在的Note名字
     */
    private void findNoteInfoByTitle() {
        if (TextUtils.isEmpty(m_et_note_add_title.getText().toString().trim())) {
            ShowToast("标题不能为空");
            return;
        }
        if (TextUtils.isEmpty(m_et_note_add_content.getText().toString().trim())) {
            ShowToast("内容不能为空");
            return;
        }
        BmobQuery<Note> notes = new BmobQuery<Note>();
        notes.addWhereRelatedTo("notes", new BmobPointer(myUser));
        notes.addWhereEqualTo("title", m_et_note_add_title.getText().toString());
        notes.findObjects(this, new FindListener<Note>() {
            @Override
            public void onSuccess(List<Note> notes) {
                if (notes.size() == 0) {
                    saveNoteInfo();
                } else {
                    ShowToast("已存在\"" + m_et_note_add_title.getText().toString() + "\"");
                }
            }

            @Override
            public void onError(int i, String s) {
                showErrorIms(i);
            }
        });
    }

    /**
     * 保存记事
     */
    private void saveNoteInfo() {
        String group = m_et_note_add_group.getText().toString();
        if (TextUtils.isEmpty(group)) {
            mNote = new Note();
            mNote.setTitle(m_et_note_add_title.getText().toString().trim());
            mNote.setContent(m_et_note_add_content.getText().toString().trim());
            mNote.setUser(myUser);
            mNote.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    addNoteToUser();
                }

                @Override
                public void onFailure(int i, String s) {
                    showErrorIms(i);
                }
            });
        } else {
            BmobQuery<NoteGroup> query = new BmobQuery<NoteGroup>();
            query.setLimit(1000);
            query.addWhereEqualTo("user", myUser);//查询当前用户
            query.addWhereEqualTo("name", m_et_note_add_group.getText().toString().trim());
            query.findObjects(NoteAddActivity.this, new FindListener<NoteGroup>() {
                @Override
                public void onSuccess(List<NoteGroup> noteGroups) {
                    saveNoteWithNoteGroup(noteGroups);
                }

                @Override
                public void onError(int i, String s) {
                    showErrorIms(i);
                }
            });
        }
    }

    private void saveNoteWithNoteGroup(List<NoteGroup> noteGroups) {
        mNoteGroup = noteGroups.get(0);
        mNote = new Note();
        mNote.setTitle(m_et_note_add_title.getText().toString().trim());
        mNote.setContent(m_et_note_add_content.getText().toString().trim());
        mNote.setNoteGroup(mNoteGroup);
        mNote.setUser(myUser);
        mNote.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                addNoteToUser();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void addNoteToUser() {
        BmobRelation notes = new BmobRelation();
        notes.add(mNote);
        myUser.setNotes(notes);
        myUser.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                BmobQuery.clearAllCachedResults(getApplicationContext());
                ShowToast("保存\"" + m_et_note_add_title.getText().toString() + "\"成功");
            }

            @Override
            public void onFailure(int i, String s) {
                showErrorIms(i);
            }
        });
    }

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    /**
     * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
     */
    private void setLeftCount() {
        m_et_note_add_content.setText(String.valueOf((MAX_COUNT - getInputCount())));
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength(m_et_note_add_content.getText().toString());
    }

}