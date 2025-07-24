package me.daoge.daogelab.api;

import lombok.Getter;

/**
 * @author daoge_cmd
 */
@Getter
public enum ChannelType {
    A(1),
    B(2);
    private final int typeNumber;

    ChannelType(int typeNumber) {
        this.typeNumber = typeNumber;
    }
}
