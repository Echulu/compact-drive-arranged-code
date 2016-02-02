package com.compactdrive.AppUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.compactdrive.R;

import java.util.List;

public class FileAdapter extends ArrayAdapter {



    public FileAdapter(Context context, List<CDFileObject> temp){
        super(context, R.layout.content_home,temp);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater tem = LayoutInflater.from(getContext());
            View conView= tem.inflate(R.layout.each_row, parent, false);
            TextView l = (TextView) conView.findViewById(R.id.ItemName);
            ImageView i = (ImageView) conView.findViewById(R.id.ItemIcon);
            CDFileObject ob =(CDFileObject) getItem(position);
            l.setText(ob.getTitle());
            if (ob.isFolder) {
                i.setImageResource(R.drawable.foldericon);
            }else {
                i.setImageResource(R.drawable.fileicon);
            }

        return conView;
    }

}
