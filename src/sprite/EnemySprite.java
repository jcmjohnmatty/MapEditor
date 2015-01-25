package sprite;

public class EnemySprite extends Sprite {
    public EnemySprite(String ip, int startX, int startY, float sz, int ra, boolean iss) {
        super(ip, startX, startY, sz, ra, iss);
    }

    public EnemySprite(String ip, int startX, int startY, float sz, boolean iss) {
        super(ip, startX, startY, sz, iss);
    }

    public String getType() {
        return "EnemySprite";
    }

    public String saveData() {
        return new String("EnemySprite|"+imgPath+"|"+X+"|"+Y+"|"+scale+"|"+rotangle+"|"+isSolid);
    }
}

