package com.sanginfo.intripper.libraryproject.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.sanginfo.intripper.libraryproject.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by Sang.05 on 2/16/2018.
 */

public class EmptyTileProvider implements TileProvider{

    private final int mTileWidth;
    private final int mTileHeight;
    private Context context;

    public EmptyTileProvider(Context context){
        this.mTileWidth = 265;
        this.mTileHeight = 265;
        this.context=context;
    }

    public TileOverlayOptions createTileOverlayOptions(){
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions().tileProvider(this);
        try {
            Class.forName("com.google.android.gms.maps.model.TileOverlayOptions")
                    .getMethod("fadeIn", boolean.class)
                    .invoke(tileOverlayOptions, true);
        } catch (Exception e) {
        }
        return tileOverlayOptions;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        byte[] tileImage = getTileImage(x,y,zoom);
        if (tileImage!=null){
            return new Tile(mTileWidth/2,mTileHeight/2,tileImage);
        }
        return NO_TILE;
    }

    private byte[] getTileImage(int x, int y, int zoom){
        ByteArrayOutputStream outputStream=null;
        try{
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_small);
            outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        }
        catch (Exception ex){
        }
        finally {
            try{
                if (outputStream!=null){
                    outputStream.close();
                }
            }
            catch (Exception ex1){

            }
        }
        return null;
    }


}
