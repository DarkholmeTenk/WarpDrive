package cr0s.warpdrive.data;

import net.minecraft.entity.Entity;

public class MovingEntity {
    public final Entity entity;
    public final double oldX;
    public final double oldY;
    public final double oldZ;

    public MovingEntity(Entity parEntity) {
        entity = parEntity;
        oldX = parEntity.posX;
        oldY = parEntity.posY;
        oldZ = parEntity.posZ;
    }
}
