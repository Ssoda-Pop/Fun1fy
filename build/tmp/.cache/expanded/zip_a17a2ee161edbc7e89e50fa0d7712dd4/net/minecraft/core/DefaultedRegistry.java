package net.minecraft.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public interface DefaultedRegistry<T> extends Registry<T> {
   /**
    * @return the name used to identify the given object within this registry or {@code null} if the object is not
    * within this registry
    */
   @Nonnull
   ResourceLocation getKey(T pValue);

   @Nonnull
   T get(@Nullable ResourceLocation pName);

   @Nonnull
   T byId(int pId);

   ResourceLocation getDefaultKey();
}