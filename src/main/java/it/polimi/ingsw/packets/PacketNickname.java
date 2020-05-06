package it.polimi.ingsw.packets;

import java.io.Serializable;

public class PacketNickname implements Serializable {

    private static final long serialVersionUID = 206113723449190436L;

    private final String nickname;

    public PacketNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
