package com.simpulator.game.language;

import com.simpulator.game.trading.Item;

public class ChineseTranslator implements Translator {

    @Override
    public String translateItemName(Item item) {
        switch (item) {
            case TRINKET:
                return "小饰品";
            case ROPE:
                return "绳子";
            case CLOTH:
                return "布料";
            case CANDLE:
                return "蜡烛";
            case FISH:
                return "干鱼";
            case PENDANT:
                return "吊坠";
            case COMPASS:
                return "指南针";
            case SPICE:
                return "香料罐";
            case MAP:
                return "旧地图";
            case LENS:
                return "玻璃镜片";
            case GEMSTONE:
                return "宝石";
            case IDOL:
                return "黄金神像";
            case SEXTANT:
                return "六分仪";
            case CHALICE:
                return "圣杯";
            case ASTROLABE:
                return "星盘";
            case DRAGON_SCALE:
                return "龙鳞";
            case CROWN:
                return "古代王冠";
            case ORB:
                return "神秘宝珠";
            default:
                return item.toString();
        }
    }
}
