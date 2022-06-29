package com.example.xiaomaibu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.coorchice.library.SuperTextView;
import com.obs.services.ObsClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import kotlin.UByteArray;

public class community_Adapter extends RecyclerView.Adapter<community_Adapter.MyViewHolder> {
    private List<Map<String,Object>> data;
    private Context context;
    private View inflater;
    //标记被展开的item
    private int opened = -1;
    /*构造函数*/
    public community_Adapter(Context context, List<Map<String,Object>> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        inflater= LayoutInflater.from(context).inflate(R.layout.community_item,parent,false);
        MyViewHolder holder = new MyViewHolder(inflater);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        try {
            bindView(holder,position);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //通过点击改变状态
        holder.communityBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.setChoose_community_name((String) data.get(holder.getBindingAdapterPosition()).get("comm_name"));
                Data.setChoose_community_Image((Bitmap)data.get(holder.getBindingAdapterPosition()).get("community_image"),(Bitmap)data.get(holder.getBindingAdapterPosition()).get("background"));
                Intent intent=new Intent();
                intent.setClass(context.getApplicationContext(), communityInfoActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //返回数据总条数
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SuperTextView communityBackground,communityImage,communityName,communityIntroduceLittle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            communityBackground=itemView.findViewById(R.id.community_background);
            communityImage=itemView.findViewById(R.id.community_image);
            communityName=itemView.findViewById(R.id.community_name);
            communityIntroduceLittle=itemView.findViewById(R.id.community_introduce_little);
        }
    }

    //自定义方法，用于绑定数据
    public void bindView(@NonNull MyViewHolder holder, int position) throws IOException {
        Bitmap backgroundString = (Bitmap)data.get(position).get("background");
        Bitmap ImageString = (Bitmap)data.get(position).get("community_image");
        if(!backgroundString.equals("") && backgroundString!=null) {
            holder.communityBackground.setDrawable(backgroundString);
        }
        if(!ImageString.equals("") && ImageString!=null) {
            holder.communityImage.setDrawable(ImageString);
        }
        holder.communityName.setText((String)data.get(position).get("comm_name"));
        holder.communityIntroduceLittle.setText((String)data.get(position).get("comm_intro_little"));
    }
}
