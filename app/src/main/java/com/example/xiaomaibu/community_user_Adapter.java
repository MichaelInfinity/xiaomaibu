package com.example.xiaomaibu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class community_user_Adapter extends RecyclerView.Adapter<community_user_Adapter.MyViewHolder> {
    private List<Map<String,String>> data;
    private Context context;
    private View inflater;
    //标记被展开的item
    private int opened = -1;
    /*构造函数*/
    public community_user_Adapter(Context context, List<Map<String,String>> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder，绑定每一项的布局为item
        inflater= LayoutInflater.from(context).inflate(R.layout.community_info_item,parent,false);
        MyViewHolder holder = new MyViewHolder(inflater);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        bindView(holder,position);
        //通过点击改变状态
    }

    @Override
    public int getItemCount() {
        //返回数据总条数
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SuperTextView communityImage,communityName,communityUserCareer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            communityImage=itemView.findViewById(R.id.community_user_image);
            communityName=itemView.findViewById(R.id.community_user_name);
            communityUserCareer=itemView.findViewById(R.id.community_user_career);
        }
    }

    //自定义方法，用于绑定数据
    public void bindView(@NonNull MyViewHolder holder, int position){
        Bitmap ImageMap;
        String ImageB64 = data.get(position).get("picture");
        byte[] bytearray;
        if(!ImageB64.equals("")) {
            ImageB64=ImageB64.replaceAll("\\n|\\t","");
            bytearray = Base64.getDecoder().decode(ImageB64);
            ImageMap = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
            holder.communityImage.setDrawable(ImageMap);
        }
        holder.communityName.setText(data.get(position).get("username"));
        holder.communityUserCareer.setText(data.get(position).get("career"));
    }
}
