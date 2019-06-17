package me.dm7.barcodescanner.zbar;

import net.sourceforge.zbar.Symbol;

import java.util.List;
import java.util.ArrayList;

public class BarcodeFormat {
    private int mId;
    private String mName;

    public static final BarcodeFormat NONE = new BarcodeFormat(Symbol.NONE, "NONE");
    public static final BarcodeFormat CODE39 = new BarcodeFormat(Symbol.CODE39, "CODE39");
    public static final BarcodeFormat CODE128 = new BarcodeFormat(Symbol.CODE128, "CODE128");

    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList<BarcodeFormat>();

    static {
        ALL_FORMATS.add(BarcodeFormat.CODE39);
        ALL_FORMATS.add(BarcodeFormat.CODE128);
    }

    public BarcodeFormat(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public static BarcodeFormat getFormatById(int id) {
        for(BarcodeFormat format : ALL_FORMATS) {
            if(format.getId() == id) {
                return format;
            }
        }
        return BarcodeFormat.NONE;
    }
}