package com.sanginfo.intripper.libraryproject.common;

import com.sanginfo.intripper.model.MapArea;

import java.util.ArrayList;

/**
 * Created by mosesafonso on 05/02/18.
 */

public class AmenitiesDataModal {

    private String amenityName;

    private ArrayList<MapArea> amenityData;

    public AmenitiesDataModal()
    {

    }

    public AmenitiesDataModal(String amenityName, ArrayList<MapArea> amenityData)
    {
        this.amenityName = amenityName;
        this.amenityData = amenityData;
    }

    public void setAmenityData(ArrayList<MapArea> amenityData)
    {
        this.amenityData = amenityData;
    }

    public ArrayList<MapArea> getAmenityData() {
        return amenityData;
    }

    public void setAmenityName(String amenityName)
    {
        this.amenityName = amenityName;
    }

    public String getAmenityName() {
        return amenityName;
    }
}
