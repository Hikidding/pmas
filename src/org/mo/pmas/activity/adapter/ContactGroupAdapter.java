package org.mo.pmas.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import org.mo.common.util.ToastUtil;
import org.mo.pmas.activity.R;
import org.mo.pmas.entity.ContactGroup;
import org.mo.pmas.resolver.ContactGroupResolver;

import java.util.List;

/**
 * Created by moziqi on 2015/1/11 0011.
 */
public class ContactGroupAdapter extends BaseAdapter {
    private List<ContactGroup> mContactGroupLists;
    private Context mContext;

    public ContactGroupAdapter(Context mContext, List<ContactGroup> mContactGroupLists) {
        this.mContext = mContext;
        this.mContactGroupLists = mContactGroupLists;
    }

    @Override
    public int getCount() {
        return mContactGroupLists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactGroup contactGroup = mContactGroupLists.get(position);
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_contact_group_list_item, null);
            mViewHolder.bt_contact_group_list_item_update =
                    (Button) convertView.findViewById(R.id.bt_contact_group_list_item_update);
            mViewHolder.bt_contact_group_list_item_remove =
                    (Button) convertView.findViewById(R.id.bt_contact_group_list_item_remove);
            mViewHolder.tv_contact_group_list_item_text =
                    (TextView) convertView.findViewById(R.id.tv_contact_group_list_item_text);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.tv_contact_group_list_item_text.setText(contactGroup.getName());
        mViewHolder.bt_contact_group_list_item_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showLongToast(mContext, contactGroup.getName() + "将给我delete");
                notifyDataSetChanged();
            }
        });
        mViewHolder.bt_contact_group_list_item_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showLongToast(mContext, contactGroup.getName() + "将给我更新");
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    final static class ViewHolder {
        Button bt_contact_group_list_item_update;
        Button bt_contact_group_list_item_remove;
        TextView tv_contact_group_list_item_text;
    }
}
