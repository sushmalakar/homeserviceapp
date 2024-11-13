package com.sushmitamalakar.homeserviceadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sushmitamalakar.homeserviceadmin.R;
import com.sushmitamalakar.homeserviceadmin.model.User;
import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<User> userList;
    private OnUserClickListener editListener;
    private OnUserClickListener deleteListener;

    public UsersAdapter(Context context, ArrayList<User> userList, OnUserClickListener editListener, OnUserClickListener deleteListener) {
        this.context = context;
        this.userList = userList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_items_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userRecyclerTitle.setText(user.getFullName());
        holder.userMobileTextView.setText("Mobile: " + user.getMobileNo());

        holder.editIcon.setOnClickListener(v -> editListener.onUserClick(user));
        holder.deleteIcon.setOnClickListener(v -> deleteListener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userRecyclerTitle, userMobileTextView;
        ImageView editIcon, deleteIcon;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userRecyclerTitle = itemView.findViewById(R.id.userRecyclerTitle);
            userMobileTextView = itemView.findViewById(R.id.userMobileTextView);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }
}
