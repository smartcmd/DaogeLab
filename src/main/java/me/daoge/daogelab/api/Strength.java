package me.daoge.daogelab.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author daoge_cmd
 */
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
}
