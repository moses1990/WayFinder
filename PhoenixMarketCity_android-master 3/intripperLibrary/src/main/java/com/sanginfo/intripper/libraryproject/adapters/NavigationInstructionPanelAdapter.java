package com.sanginfo.intripper.libraryproject.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.libraryproject.common.CommonMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NavigationInstructionPanelAdapter extends RecyclerView.Adapter< NavigationInstructionPanelAdapter.DataObjectHolder > {
    private JSONArray steps;
    private int color;

    public NavigationInstructionPanelAdapter(JSONArray steps, int color) {
        this.steps = steps;
        this.color = color;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.si_in_lb__navigation_instruction_item, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        try {
            CommonMethods.setFontHelvetica(holder.step);
            CommonMethods.setFontHelvetica(holder.distanceMtrs);
            if (position == 0) {
                holder.step.setText("Your location");
                holder.distanceMtrs.setText("");
                holder.navigationIcon.setImageResource(R.drawable.ic_turn_location_charcoal_grey);
                holder.distanceMtrs.setVisibility(View.GONE);
                return;
            }
            JSONObject object = ((JSONObject) steps.get(position - 1)).getJSONObject("properties");
            String instruction = object.getString("instruction");
            Pattern storeName = Pattern.compile(object.getString("ac"));

            holder.distanceMtrs.setVisibility(View.VISIBLE);
            holder.distanceMtrs.setText((int) object.getDouble("distance") + " m ");
            String direction = object.getString("direction");
            int icon;
            if (direction.equalsIgnoreCase("S")) {
                icon = R.drawable.ic_turn_straight_charcoal_grey;
            } else if (direction.equalsIgnoreCase("R")) {
                icon = R.drawable.ic_turn_right_charcoal_grey;
            } else if (direction.equalsIgnoreCase("SR")) {
                icon = R.drawable.ic_turn_slight_right_charcoal_grey;
            } else if (direction.equalsIgnoreCase("SL")) {
                icon = R.drawable.ic_turn_slight_left_charcoal_grey;
            } else if (direction.equalsIgnoreCase("L")) {
                icon = R.drawable.ic_turn_left_charcoal_grey;
            } else {
                icon = R.drawable.ic_turn_destination_charcoal_grey;
            }
            if (position == steps.length()) {
                icon = R.drawable.ic_turn_destination_charcoal_grey;
                if (!instruction.toLowerCase().contains("destination")) {
                    instruction = instruction + " and arrive at your destination";
                }
            }

            if (instruction.toLowerCase().contains("escalator")) {
                icon = R.drawable.ic_escalat_charcoal_grey_opac;
            }

            int startIndex = 0, endIndex = 0;
            Matcher matcher = storeName.matcher(instruction);
            if (matcher.find()) {
                startIndex = matcher.start();
                endIndex = matcher.end();
            }
            //instruction = instruction.replace(object.getString( "ac" ),"<b>" + object.getString( "ac" )+ "</b>");

            Spannable WordtoSpan = new SpannableString(instruction);
            WordtoSpan.setSpan(new ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            WordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.step.setText(WordtoSpan);
            holder.navigationIcon.setImageResource(icon);
        } catch (Exception e) {
            e.toString();
        }

    }

    @Override
    public int getItemCount() {
        return steps.length() + 1;
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        ImageView navigationIcon;
        TextView step;
        TextView distanceMtrs;

        public DataObjectHolder(View itemView) {
            super(itemView);
            step = (TextView) itemView.findViewById(R.id.step);
            distanceMtrs = (TextView) itemView.findViewById(R.id.distance_mtr);
            navigationIcon = (ImageView) itemView.findViewById(R.id.navigation_icon);
        }
    }
}
