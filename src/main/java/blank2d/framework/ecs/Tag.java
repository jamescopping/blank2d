package blank2d.framework.ecs;

public enum Tag {
    UNTAGGED("untagged"),
    PLAYER("player"),
    ENEMY("enemy"),
    CAMERA("camera"),
    RESPAWN("respawn"),
    FINISH("finish"),
    DEBUG("debug");

    Tag(String untagged) {}
}
