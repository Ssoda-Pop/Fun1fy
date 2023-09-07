package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.phys.AABB;

public class LevelEntityGetterAdapter<T extends EntityAccess> implements LevelEntityGetter<T> {
   private final EntityLookup<T> visibleEntities;
   private final EntitySectionStorage<T> sectionStorage;

   public LevelEntityGetterAdapter(EntityLookup<T> pVisibleEntities, EntitySectionStorage<T> pSectionStorage) {
      this.visibleEntities = pVisibleEntities;
      this.sectionStorage = pSectionStorage;
   }

   @Nullable
   public T get(int pId) {
      return this.visibleEntities.getEntity(pId);
   }

   @Nullable
   public T get(UUID pUuid) {
      return this.visibleEntities.getEntity(pUuid);
   }

   public Iterable<T> getAll() {
      return this.visibleEntities.getAllEntities();
   }

   public <U extends T> void get(EntityTypeTest<T, U> p_261718_, AbortableIterationConsumer<U> p_262009_) {
      this.visibleEntities.getEntities(p_261718_, p_262009_);
   }

   public void get(AABB pBoundingBox, Consumer<T> pConsumer) {
      this.sectionStorage.getEntities(pBoundingBox, AbortableIterationConsumer.forConsumer(pConsumer));
   }

   public <U extends T> void get(EntityTypeTest<T, U> p_261696_, AABB p_261693_, AbortableIterationConsumer<U> p_261719_) {
      this.sectionStorage.getEntities(p_261696_, p_261693_, p_261719_);
   }
}