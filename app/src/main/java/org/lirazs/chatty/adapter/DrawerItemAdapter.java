package org.lirazs.chatty.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.lirazs.chatty.R;
import org.lirazs.chatty.model.DrawerItem;
import org.lirazs.chatty.model.DrawerOption;

import java.util.List;

/**
 * Created by Liraz on 31/05/2017.
 */

public class DrawerItemAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DrawerItem> drawerItems;

    private OnItemClickListener listener;
    private int selectedPosition;

    public DrawerItemAdapter() {
        selectedPosition = -1;
    }

    public void setDrawerItems(List<DrawerItem> drawerItems) {
        this.drawerItems = drawerItems;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (DrawerItem.Type.values()[viewType]) {
            case DIVIDER:
                View dividerRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_divider, parent, false);
                return new DividerViewHolder(dividerRootView);
            case OPTION:
                View optionRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_option, parent, false);
                return new OptionViewHolder(optionRootView);
            case SUB_OPTION:
                View subOptionRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_sub_option, parent, false);
                return new OptionViewHolder(subOptionRootView);
            case SUB_OPTIONS_TITLE:
                View subOptionTitleRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_sub_option_title, parent, false);
                return new OptionViewHolder(subOptionTitleRootView);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onClick(position);
            }
        });
        DrawerItem drawerItem = drawerItems.get(position);

        DrawerOption drawerOption;
        OptionViewHolder optionViewHolder;

        switch (drawerItem.getType()) {
            case OPTION:
                drawerOption = (DrawerOption) drawerItem;
                optionViewHolder = (OptionViewHolder) holder;

                optionViewHolder.nameTextView.setText(drawerOption.getNameRes());
                if (selectedPosition == position) {
                    optionViewHolder.itemView.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.selected_background, null));
                    optionViewHolder.nameTextView.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.primary_selected_text, null));
                    Drawable selectedIcon = context.getResources().getDrawable(drawerOption.getSelectedIconRes());
                    selectedIcon.mutate().setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.selected_icon, null), PorterDuff.Mode.SRC_IN);
                    optionViewHolder.iconImageView.setImageDrawable(selectedIcon);
                } else {
                    optionViewHolder.itemView.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.unselected_background, null));
                    optionViewHolder.nameTextView.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.primary_text, null));
                    optionViewHolder.iconImageView.setImageResource(drawerOption.getUnselectedIconRes());
                }
                break;
            case SUB_OPTION:
                drawerOption = (DrawerOption) drawerItem;
                optionViewHolder = (OptionViewHolder) holder;

                optionViewHolder.nameTextView.setText(drawerOption.getNameRes());
                optionViewHolder.nameTextView.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.primary_text, null));
                optionViewHolder.iconImageView.setImageResource(drawerOption.getUnselectedIconRes());
                break;
            case SUB_OPTIONS_TITLE:
                drawerOption = (DrawerOption) drawerItem;
                optionViewHolder = (OptionViewHolder) holder;

                optionViewHolder.nameTextView.setText(drawerOption.getNameRes());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return drawerItems.get(position).getType().ordinal();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    private static class DividerViewHolder extends RecyclerView.ViewHolder {

        public DividerViewHolder(View rootView) {
            super(rootView);
        }
    }

    private static class OptionViewHolder extends RecyclerView.ViewHolder {

        private ImageView iconImageView;
        private TextView nameTextView;

        public OptionViewHolder(View rootView) {
            super(rootView);
            iconImageView = (ImageView) rootView.findViewById(R.id.icon);
            nameTextView = (TextView) rootView.findViewById(R.id.name);
        }
    }
}