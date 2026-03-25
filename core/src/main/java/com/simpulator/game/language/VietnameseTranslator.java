package com.simpulator.game.language;

import com.simpulator.game.trading.Item;

public class VietnameseTranslator implements Translator {

    @Override
    public String translateItemName(Item item) {
        switch (item) {
            case TRINKET:
                return "Đồ trang trí";
            case ROPE:
                return "Dây thừng";
            case CLOTH:
                return "Vải";
            case CANDLE:
                return "Nến";
            case FISH:
                return "Cá khô";
            case PENDANT:
                return "Mặt dây chuyền";
            case COMPASS:
                return "La bàn";
            case SPICE:
                return "Lọ gia vị";
            case MAP:
                return "Bản đồ cũ";
            case LENS:
                return "Thấu kính";
            case GEMSTONE:
                return "Đá quý";
            case IDOL:
                return "Tượng vàng";
            case SEXTANT:
                return "Lục phân nghi";
            case CHALICE:
                return "Chén vàng";
            case ASTROLABE:
                return "Thiên văn bàn";
            case DRAGON_SCALE:
                return "Vảy rồng";
            case CROWN:
                return "Vương miện cổ đại";
            case ORB:
                return "Cầu thần bí";
            default:
                return item.toString();
        }
    }
}
