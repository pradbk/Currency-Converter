package com.prad.rateconversion.values;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pradeepbk on 3/31/14.
 */
public class ConversionValue {

    public static List<ConversionData> ITEMS = new ArrayList<ConversionData>();

    public static void addItem(ConversionData item) {
        ITEMS.add(item);
    }

    public static class ConversionData {
        public String content;

        public ConversionData(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
