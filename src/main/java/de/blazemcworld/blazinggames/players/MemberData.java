package de.blazemcworld.blazinggames.players;

import java.util.UUID;

public class MemberData {
    public MemberData(String name) {
        this.name = name;
    }

    public String name; // also acts as a unique identifier
    public String displayName;
    public String pronouns;
    public Integer color;
    public String proxyStart;
    public String proxyEnd;
    public UUID skin;
}