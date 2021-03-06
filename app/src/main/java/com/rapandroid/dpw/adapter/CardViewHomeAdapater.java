package com.rapandroid.dpw.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rapandroid.dpw.FormActivity;
import com.rapandroid.dpw.HomeActivity;
import com.rapandroid.dpw.R;
import com.rapandroid.dpw.model.HomeModel;

import java.util.ArrayList;

public class CardViewHomeAdapater extends RecyclerView.Adapter<CardViewHomeAdapater.HomeViewHolder> {
    private final ArrayList<HomeModel> list;
    private Context context;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;


    public CardViewHomeAdapater(Context context, ArrayList<HomeModel> list) {
        this.list = list;
        this.context = context;
//        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
//        String uId = mUser.getUid();
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("daftar-produk-warung").child(uId);
        mStorage = FirebaseStorage.getInstance().getReference();
    }


    @NonNull
    @Override
    public CardViewHomeAdapater.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_list, viewGroup, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardViewHomeAdapater.HomeViewHolder holder, int position) {
        final HomeModel homeModel = list.get(position);

        holder.tvName.setText(homeModel.getName());
        holder.tvPrice.setText("Rp. "  + String.valueOf(homeModel.getPrice()));
        holder.tvDesc.setText(homeModel.getDesc());
//        holder.tvId.setText(homeModel.getId());
        getImage("Images/" + homeModel.getId() +".jpg", holder.ivProductList);
        holder.cvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FormActivity.class);
                i.putExtra("name", homeModel.getName());
                i.putExtra("price", String.valueOf(homeModel.getPrice()));
                i.putExtra("desc", homeModel.getDesc());
                i.putExtra("id", homeModel.getId() );
                i.putExtra("img", "Images/" + homeModel.getId() +".jpg");
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder{
        CardView cvProduct;
        TextView tvName, tvPrice, tvDesc, tvId;
        ImageView ivProductList;

        public HomeViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvPrice = view.findViewById(R.id.tv_price);
            tvDesc = view.findViewById(R.id.tv_desc);
//            tvId = view.findViewById(R.id.tv_id);
            ivProductList = view.findViewById(R.id.iv_product_list);
            cvProduct = view.findViewById(R.id.cv_product);
        }
    }

    public void getImage(String data, final ImageView f){
        mStorage.child(data).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(f);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}
