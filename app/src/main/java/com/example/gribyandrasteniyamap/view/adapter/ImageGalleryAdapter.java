package com.example.gribyandrasteniyamap.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.databse.entity.Plant;
import com.example.gribyandrasteniyamap.service.PlantService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MViewHolder> {

    @Inject
    PlantService plantService;

    @Inject
    public ImageGalleryAdapter() {
    }

    private Context mContext;
    private List<Plant> plants;
    private final List<Integer> selectedPositions = new ArrayList<>();

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageGalleryAdapter.MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.gallery_item, parent, false);
        return new MViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGalleryAdapter.MViewHolder holder, int position) {
        Plant plant = plants.get(position);
        ImageView imageView = holder.mPhotoImageView;

        holder.mNameTextView.setText(plant.getName());

        holder.checkBox.setChecked(selectedPositions.contains(position));
        holder.checkBox.setVisibility(selectedPositions.contains(position) ? View.VISIBLE : View.GONE);

        Glide.with(mContext)
                .load(plant.filePath)
                .into(imageView);

        logMemory();
    }

    private void logMemory() {
        Log.i("log", String.format("Total memory = %s",
                (int) (Runtime.getRuntime().totalMemory() / 1024)));
    }

    private boolean isSelectionMode() {
        return !selectedPositions.isEmpty();
    }

    private int getSelectedCount() {
        return selectedPositions.size();
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView mPhotoImageView;
        public TextView mNameTextView;
        public CheckBox checkBox;

        @SuppressLint("CheckResult")
        public MViewHolder(View itemView) {
            super(itemView);
            mPhotoImageView = itemView.findViewById(R.id.iv_photo);
            mNameTextView = itemView.findViewById(R.id.tv_name);
            checkBox = itemView.findViewById(R.id.checkBox);

            int[][] states = {{android.R.attr.state_checked}, {}};
            int[] colors = {Color.WHITE, Color.BLACK};
            CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            setOnCancelListener();
            setOnDeleteListener();
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (isSelectionMode()) {
                    processClick(position);
                }
            }
        }


        @Override
        public boolean onLongClick(View v) {
            processClick(getAdapterPosition());
            return true;
        }

        private void processClick(int position) {
            if (position != RecyclerView.NO_POSITION) {
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove((Integer) position);
                    checkBox.setChecked(false);
                    checkBox.setVisibility(View.GONE);
                } else {
                    selectedPositions.add(position);
                    checkBox.setChecked(true);
                    checkBox.setVisibility(View.VISIBLE);
                }

                changeTopPanelVisible();

                TextView textView = ((Activity) mContext).findViewById(R.id.selectedCount);
                String s = "Выбрано: " + getSelectedCount();
                textView.setText(s);
            }
        }

        private void changeTopPanelVisible() {
            LinearLayout deletePanel = ((Activity) mContext).findViewById(R.id.delete_panel);
            LinearLayout galleryPanel = ((Activity) mContext).findViewById(R.id.galleryPanel);

            deletePanel.setVisibility(isSelectionMode() ? View.VISIBLE : View.GONE);
            galleryPanel.setVisibility(isSelectionMode() ? View.GONE : View.VISIBLE);
        }

        private void setOnCancelListener() {
            ImageButton cancelBtn = ((Activity) mContext).findViewById(R.id.cancelButton);
            cancelBtn.setOnClickListener(v -> {
                selectedPositions.clear();
                changeTopPanelVisible();
                notifyDataSetChanged();
            });
        }

        private void setOnDeleteListener() {
            ImageButton deleteBtn = ((Activity) mContext).findViewById(R.id.deleteButton);
            deleteBtn.setOnClickListener(v -> {
                List<Integer> copyList = new ArrayList<>(selectedPositions);
                copyList.forEach(s -> {
                    Plant plant = plants.get(s);
                    new File(plant.getFilePath()).delete();
                    Observable.fromCallable(() -> plantService.delete(plant))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(r -> {
                                selectedPositions.remove((Integer) s);
                                plants.remove(plant);
                                notifyItemRemoved(s);
                                notifyItemRangeChanged(s, selectedPositions.size());
                                changeTopPanelVisible();
                            });
                    Toast.makeText(mContext, "Удалено: " + getSelectedCount() + " элементов", Toast.LENGTH_SHORT).show();
                });
            });
        }


    }
}
