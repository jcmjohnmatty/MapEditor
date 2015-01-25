package sprite;

public class BorderSprite extends Sprite {
    public BorderSprite(String ip, int startX, int startY, float sz, int ra, boolean iss) {
        super(ip, startX, startY, sz, ra, iss);
    }

    public BorderSprite(String ip, int startX, int startY, float sz, boolean iss) {
        super(ip, startX, startY, sz, iss);
    }

    public String getType() {
        return "BorderSprite";
    }

    public String saveData() {
        return new String("BorderSprite|"+imgPath+"|"+X+"|"+Y+"|"+scale+"|"+rotangle+"|"+isSolid);
    }
}

