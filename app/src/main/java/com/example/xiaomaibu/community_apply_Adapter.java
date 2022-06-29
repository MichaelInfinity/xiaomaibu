package com.example.xiaomaibu;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;

import java.sql.Connection;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

public class community_apply_Adapter extends RecyclerView.Adapter<community_apply_Adapter.MyViewHolder> {
    private List<Map<String,Object>> data;
    private Context context;
    private View inflater;
    //标记被展开的item
    private int opened = -1;
    private int flag=0;
    /*构造函数*/
    public community_apply_Adapter(Context context, List<Map<String,Object>> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        inflater= LayoutInflater.from(context).inflate(R.layout.community_apply_item,parent,false);
        MyViewHolder holder = new MyViewHolder(inflater);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        bindView(holder,position);
        //通过点击改变状态
        holder.Ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(1,holder.communityName.getText().toString(),holder.communityUserCareer.getText().toString());
                data.remove(holder.getBindingAdapterPosition());
                notifyItemRemoved(holder.getBindingAdapterPosition());
                notifyItemRangeChanged(0,data.size());
            }
        });
        holder.No_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(0,holder.communityName.getText().toString(),holder.communityUserCareer.getText().toString());
                data.remove(holder.getBindingAdapterPosition());
                notifyItemRemoved(holder.getBindingAdapterPosition());
                notifyItemRangeChanged(0,data.size());
            }
        });
    }

    void getData(int okOrno, String username, String career) {
        mysql_minecraft mysqlMinecraft = new mysql_minecraft();
        Message msg = Message.obtain();
        msg.what = 0;
        Bundle bundle = new Bundle();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Connection conn = mysqlMinecraft.sql_connect();  // 获得数据的连接
                    flag=mysqlMinecraft.Ok_or_No(conn,username,career,okOrno);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //bundle.putString("key", "123");
                //msg.setData(bundle);
                //inithandler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        //返回数据总条数
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        SuperTextView communityImage,communityName,communityUserCareer,Ok_button,No_button;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            communityImage=itemView.findViewById(R.id.community_image_apply);
            communityName=itemView.findViewById(R.id.community_name_apply);
            communityUserCareer=itemView.findViewById(R.id.community_career_apply);
            Ok_button=itemView.findViewById(R.id.Ok_button);
            No_button=itemView.findViewById(R.id.No_button);
        }
    }

    //自定义方法，用于绑定数据
    public void bindView(@NonNull MyViewHolder holder, int position){
        Bitmap ImageB64 = (Bitmap)data.get(position).get("picture");
        if(ImageB64!=null) {
            holder.communityImage.setDrawable(ImageB64);
        }
        holder.communityName.setText((String)data.get(position).get("username"));
        holder.communityUserCareer.setText((String)data.get(position).get("career"));
    }
}
