package comp5216.sydney.edu.au.timefiles.blackList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.model.AppInfo;
import comp5216.sydney.edu.au.timefiles.utils.AppInMachine;

public class ChoseBlackListAdapter extends RecyclerView.Adapter<ChoseBlackListAdapter.MyViewHolder>{
    Context context;
    ArrayList<AppInfo> list;

    public ChoseBlackListAdapter(Context context, ArrayList<AppInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override

    public ChoseBlackListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.one_app_view_for_app_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChoseBlackListAdapter.MyViewHolder holder, int position) {

        AppInfo app = list.get(position);

        // set icon from AppInfo to recycler view imageView
        Glide.with(this.context).load(AppInMachine.getIcon(app.getPackageName(), context)).into(holder.appIcon);

        // set app name from AppInfo to recycler view textView
        holder.appName.setText(app.getAppName());
        holder.checkBox.setChecked(app.getBlack());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!compoundButton.isPressed()){
                    return;
                }

                Log.e("black", "set black" + app.getAppName() + "  " + b);
                app.setBlack(b);
            }
        });

    }

    public void selectAll(){
        Log.e("onClickSelectAll","yes");
        for (AppInfo a : list) {
            a.setBlack(true);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.imageView);
            appName = itemView.findViewById(R.id.appName);
            checkBox = itemView.findViewById(R.id.checkBox);

        }

    }
}
