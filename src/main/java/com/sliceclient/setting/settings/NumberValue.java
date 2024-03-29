package com.sliceclient.setting.settings;

import com.sliceclient.setting.Setting;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NumberValue extends Setting {
    private Number value, min, max;
    private Type type;

    public NumberValue(String name, Number value, Number min, Number max, Type type) {
        super(name);
        this.value = value;
        this.min = min;
        this.max = max;
        this.type = type;
    }

    public void setValue(Number value) {
        this.value = value;

        if(value.doubleValue() > max.doubleValue()) this.value = max;
        else if(value.doubleValue() < min.doubleValue()) this.value = min;
//        try {
//            if (Slice.INSTANCE.getSaver() != null)
//                Slice.INSTANCE.getSaver().save();
//        }catch (Exception ignored){}
    }

    public enum Type {
        DOUBLE,
        FLOAT,
        INTEGER,
        LONG
    }
}
