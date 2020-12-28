package com.example.capstone.Forms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;

public class DataAdapter extends RecyclerView.Adapter implements Filterable {
    List<Recycler> contacts;
    List<Recycler> copylist;
    private Context context;

    public DataAdapter(List<Recycler> contacts,Context context)
    {
        this.contacts=contacts;
        copylist=new ArrayList<>(contacts);
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout,parent,false);
        ViewHolderClass viewHolderClass=new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass viewHolderClass=(ViewHolderClass)holder;
        Recycler recycler=contacts.get(position);
        viewHolderClass.tv1.setText("Name: "+recycler.getFname()+" "+recycler.getLname());
        viewHolderClass.tv2.setText("EmailID: "+recycler.getEmail());
        viewHolderClass.tv3.setText("Phone No: "+String.valueOf(recycler.getPhone()));
        viewHolderClass.tv4.setText("Company: "+recycler.getCompany());

        viewHolderClass.callButton.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (PermissionChecker.checkSelfPermission(context, CALL_PHONE)
                        == PermissionChecker.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + contacts.get(position).getPhone()));
                    context.startActivity(intent);
                } else {
                    ((Activity) context).requestPermissions(new String[]{CALL_PHONE}, 401);
                }
            }
        });

        viewHolderClass.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Phone Number: "+contacts.get(position).getPhone();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }


        });


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public Filter getFilter() {
        return copyFilter;
    }

    private Filter copyFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recycler> filteredList=new ArrayList<>();
            if(constraint==null||constraint.length()==0)
            {
                filteredList.addAll(copylist);
            }
            else{
                String filteredPattern=constraint.toString().toLowerCase().trim();
                for(Recycler item:copylist)
                {
                    if(item.getFname().toLowerCase().contains(filteredPattern)||item.getLname().toLowerCase().contains(filteredPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contacts.clear();
            contacts.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };

    public class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView tv1,tv2,tv3,tv4;
        ImageView shareButton, callButton;
        public ViewHolderClass(@ NonNull View itemView)
        {
            super(itemView);
            {
                tv1=itemView.findViewById(R.id.textView1);
                tv2=itemView.findViewById(R.id.textView2);
                tv3=itemView.findViewById(R.id.textView3);
                tv4=itemView.findViewById(R.id.textView4);
                shareButton = itemView.findViewById(R.id.share_btn);
                callButton = itemView.findViewById(R.id.call_btn);


            }
        }

    }
}
