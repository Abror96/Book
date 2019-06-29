package com.book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.book.databinding.ActivitySettingsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySettingsBinding binding;
//    private String selected_iso = "";
    private int selected_quality_pos = -1;
    private int selected_focus_pos = -1;
    private int[] qualities = {
            CamcorderProfile.QUALITY_2160P,
            CamcorderProfile.QUALITY_1080P,
            CamcorderProfile.QUALITY_720P,
            CamcorderProfile.QUALITY_480P
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        binding.isoRecycler.setLayoutManager(layoutManager);
//        binding.isoRecycler.setAdapter(new IsoAdapter(iso_values_arr));

        // quality btns
        Log.d("LOGGERR", "onCreate: " + CamcorderProfile.hasProfile(qualities[0]) + " " + CamcorderProfile.hasProfile(qualities[1]) + " " + CamcorderProfile.hasProfile(qualities[2]) + " " + CamcorderProfile.hasProfile(qualities[3]));
        if (!CamcorderProfile.hasProfile(qualities[0])) {
            binding.tvUhd.setVisibility(View.GONE);
        }
        binding.tvUhd.setOnClickListener(this);
        if (!CamcorderProfile.hasProfile(qualities[1])) {
            binding.tvFhd.setVisibility(View.GONE);
        }
        binding.tvFhd.setOnClickListener(this);
        if (!CamcorderProfile.hasProfile(qualities[2])) {
            binding.tvHd.setVisibility(View.GONE);
        }
        binding.tvHd.setOnClickListener(this);
        if (!CamcorderProfile.hasProfile(qualities[3])) {
            binding.tvNeHd.setVisibility(View.GONE);
        }
        binding.tvNeHd.setOnClickListener(this);

        // focus btns
        binding.topLeft.setOnClickListener(this);
        binding.topRight.setOnClickListener(this);
        binding.bottomLeft.setOnClickListener(this);
        binding.bottomRight.setOnClickListener(this);
        binding.center.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_uhd:
                selected_quality_pos = 0;
                binding.tvUhd.setBackgroundColor(Color.parseColor("#dadada"));
                binding.tvFhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvHd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvNeHd.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_fhd:
                selected_quality_pos = 1;
                binding.tvUhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvFhd.setBackgroundColor(Color.parseColor("#dadada"));
                binding.tvHd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvNeHd.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_hd:
                selected_quality_pos = 2;
                binding.tvUhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvFhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvHd.setBackgroundColor(Color.parseColor("#dadada"));
                binding.tvNeHd.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_ne_hd:
                selected_quality_pos = 3;
                binding.tvUhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvFhd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvHd.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.tvNeHd.setBackgroundColor(Color.parseColor("#dadada"));
                break;
                // focus buttons
            case R.id.top_left:
                selected_focus_pos = 0;
                binding.topLeft.setBackgroundColor(Color.parseColor("#dadada"));
                binding.topRight.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.bottomLeft.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.bottomRight.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.center.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.top_right:
                selected_focus_pos = 1;
                binding.topLeft.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.topRight.setBackgroundColor(Color.parseColor("#dadada"));
                binding.bottomLeft.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.bottomRight.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.center.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.bottom_left:
                selected_focus_pos = 2;
                binding.topLeft.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.topRight.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.bottomLeft.setBackgroundColor(Color.parseColor("#dadada"));
                binding.bottomRight.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.center.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.bottom_right:
                selected_focus_pos = 3;
                binding.topLeft.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.topRight.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.bottomLeft.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.bottomRight.setBackgroundColor(Color.parseColor("#dadada"));
                binding.center.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.center:
                selected_focus_pos = 4;
                binding.topLeft.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.topRight.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.bottomLeft.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.bottomRight.setBackgroundColor(Color.parseColor("#ffffff"));
                binding.center.setBackgroundColor(Color.parseColor("#dadada"));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (/*!selected_iso.equals("") && */selected_quality_pos != -1 && !binding.seconds.getText().toString().isEmpty() && selected_focus_pos != -1) {
                    int duration = Integer.valueOf(binding.seconds.getText().toString())*1000;
                    if (duration >= 10000 && duration <= 30000) {
//                        Log.d("LOGGERR", "save: " + selected_iso + " " + selected_quality_pos);
                        Intent intent = new Intent();
//                        intent.putExtra("iso", selected_iso);
                        intent.putExtra("quality", selected_quality_pos);
                        intent.putExtra("duration", duration);
                        intent.putExtra("focus", selected_focus_pos);
                        setResult(3030, intent);
                        finish();
                    } else {
                        Snackbar.make(binding.mainView, "Продолжительность должна быть от 10 до 60 секунд", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(binding.mainView, "Все параметры обязательны", Snackbar.LENGTH_LONG).show();
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    class IsoAdapter extends RecyclerView.Adapter<IsoAdapter.IsoViewHolder> {
//
//        private ArrayList<String> isoList;
//        private int selectedPosition = -1;
//
//        public IsoAdapter(ArrayList<String> isoList) {
//            this.isoList = isoList;
//        }
//
//        @NonNull
//        @Override
//        public IsoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new IsoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.iso_item, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull IsoViewHolder holder, int position) {
//            holder.textView.setText(getItem(position));
//            if (selectedPosition != position) {
//                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//            } else {
//                holder.itemView.setBackgroundColor(Color.parseColor("#DADADA"));
//            }
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    selected_iso = getItem(position);
//                    selectedPosition = position;
//                    notifyDataSetChanged();
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return isoList.size();
//        }
//
//        private String getItem(int position) {
//            return isoList.get(position);
//        }
//
//        class IsoViewHolder extends RecyclerView.ViewHolder {
//
//            TextView textView;
//
//            public IsoViewHolder(@NonNull View itemView) {
//                super(itemView);
//                textView = itemView.findViewById(R.id.tv_iso);
//            }
//        }
//    }
}
