package me.daoge.daogelab.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import lombok.SneakyThrows;
import org.allaymc.api.container.FullContainerType;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.item.data.ItemLockMode;
import org.allaymc.api.item.interfaces.ItemAirStack;
import org.allaymc.api.item.interfaces.ItemFilledMapStack;
import org.allaymc.api.item.type.ItemTypes;
import org.allaymc.api.pdc.PersistentDataType;
import org.allaymc.api.utils.Identifier;
import org.allaymc.api.utils.TextFormat;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author daoge_cmd
 */
public class QRCodeUtils {

    protected static final Identifier IS_DGLAB_QR_CODE = new Identifier("daogelab:is_dglab_qr_code");

    public static boolean showQRCode(EntityPlayer player, String text) {
        var container = player.getContainer(FullContainerType.OFFHAND);
        if (!container.isEmpty() && !container.getOffhand().getPersistentDataContainer().get(IS_DGLAB_QR_CODE, PersistentDataType.BOOLEAN)) {
            player.sendTr(TextFormat.RED + "%daogelab:offhand_not_empty");
            return false;
        }

        var mapItem = ItemTypes.FILLED_MAP.createItemStack();
        mapItem.setCustomName(TextFormat.YELLOW + "DgLab QR Code");
        mapItem.setImage(generateQRCode(text, 128, 128));
        mapItem.getPersistentDataContainer().set(IS_DGLAB_QR_CODE, PersistentDataType.BOOLEAN, true);
        mapItem.setLockMode(ItemLockMode.LOCK_IN_SLOT);
        container.setOffhand(mapItem);

        return true;
    }

    public static void clearQRCode(EntityPlayer player) {
        var container = player.getContainer(FullContainerType.OFFHAND);
        if (container.getOffhand() instanceof ItemFilledMapStack mapItem &&
            mapItem.getPersistentDataContainer().get(IS_DGLAB_QR_CODE, PersistentDataType.BOOLEAN)) {
            container.setOffhand(ItemAirStack.AIR_STACK);
        }
    }

    @SneakyThrows
    protected static BufferedImage generateQRCode(String text, int width, int height) {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Create QR code maker
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        // Return BufferedImage
        return toBufferedImage(bitMatrix);
    }

    protected static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        int onColor = 0xFF000000;
        int offColor = 0xFFFFFFFF;
        int[] rowPixels = new int[width];
        BitArray row = new BitArray(width);
        for (int y = 0; y < height; y++) {
            row = matrix.getRow(y, row);
            for (int x = 0; x < width; x++) {
                rowPixels[x] = row.get(x) ? onColor : offColor;
            }
            image.setRGB(0, y, width, 1, rowPixels, 0, width);
        }
        return image;
    }
}
