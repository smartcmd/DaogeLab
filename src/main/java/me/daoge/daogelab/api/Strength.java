package me.daoge.daogelab.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Strength {
    protected int aCurrentStrength;
    protected int bCurrentStrength;
    protected int aMaxStrength;
    protected int bMaxStrength;

    public void clear() {
        this.aCurrentStrength = 0;
        this.bCurrentStrength = 0;
        this.aMaxStrength = 0;
        this.bMaxStrength = 0;
    }

    public int getCurrentStrength(ChannelType channelType) {
        return switch (channelType) {
            case A -> aCurrentStrength;
            case B -> bCurrentStrength;
        };
    }

    public int getMaxStrength(ChannelType channelType) {
        return switch (channelType) {
            case A -> aMaxStrength;
            case B -> bMaxStrength;
        };
    }
}
