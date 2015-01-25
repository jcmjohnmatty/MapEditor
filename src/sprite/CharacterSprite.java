package sprite;

public class CharacterSprite extends Sprite {
    public CharacterSprite(String ip, int startX, int startY, float sz, int ra, boolean iss) {
        super(ip, startX, startY, sz, ra, iss);
    }

    public CharacterSprite(String ip, int startX, int startY, float sz, boolean iss) {
        super(ip, startX, startY, sz, iss);
    }

    public String getType() {
        return "CharacterSprite";
    }

    public String saveData() {
        return new String("CharacterSprite|"+imgPath+"|"+X+"|"+Y+"|"+scale+"|"+rotangle+"|"+isSolid);
    }
}

