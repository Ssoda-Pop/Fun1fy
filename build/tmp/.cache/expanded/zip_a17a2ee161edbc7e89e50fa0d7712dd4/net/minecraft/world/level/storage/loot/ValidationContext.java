package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

/**
 * Context for validating loot tables. Loot tables are validated recursively by checking that all functions, conditions,
 * etc. (implementing {@link LootContextUser}) are valid according to their LootTable's {@link LootContextParamSet}.
 */
public class ValidationContext {
   private final Multimap<String, String> problems;
   private final Supplier<String> context;
   private final LootContextParamSet params;
   private final LootDataResolver resolver;
   private final Set<LootDataId<?>> visitedElements;
   @Nullable
   private String contextCache;

   public ValidationContext(LootContextParamSet p_279447_, LootDataResolver p_279446_) {
      this(HashMultimap.create(), () -> {
         return "";
      }, p_279447_, p_279446_, ImmutableSet.of());
   }

   public ValidationContext(Multimap<String, String> p_279202_, Supplier<String> p_279184_, LootContextParamSet p_279485_, LootDataResolver p_279476_, Set<LootDataId<?>> p_279392_) {
      this.problems = p_279202_;
      this.context = p_279184_;
      this.params = p_279485_;
      this.resolver = p_279476_;
      this.visitedElements = p_279392_;
   }

   private String getContext() {
      if (this.contextCache == null) {
         this.contextCache = this.context.get();
      }

      return this.contextCache;
   }

   /**
    * Report a problem to this ValidationContext.
    */
   public void reportProblem(String pProblem) {
      this.problems.put(this.getContext(), pProblem);
   }

   /**
    * Create a new ValidationContext with {@code childName} being added to the context.
    */
   public ValidationContext forChild(String pChildName) {
      return new ValidationContext(this.problems, () -> {
         return this.getContext() + pChildName;
      }, this.params, this.resolver, this.visitedElements);
   }

   public ValidationContext enterElement(String p_279180_, LootDataId<?> p_279438_) {
      ImmutableSet<LootDataId<?>> immutableset = ImmutableSet.<LootDataId<?>>builder().addAll(this.visitedElements).add(p_279438_).build();
      return new ValidationContext(this.problems, () -> {
         return this.getContext() + p_279180_;
      }, this.params, this.resolver, immutableset);
   }

   public boolean hasVisitedElement(LootDataId<?> p_279178_) {
      return this.visitedElements.contains(p_279178_);
   }

   /**
    * Get all problems that have been recorded. The resulting Multimap maps the {@linkplain #getContext context} at the
    * time the problem occured to the problems.
    */
   public Multimap<String, String> getProblems() {
      return ImmutableMultimap.copyOf(this.problems);
   }

   /**
    * Validate the given LootContextUser.
    */
   public void validateUser(LootContextUser pLootContextUser) {
      this.params.validateUser(this, pLootContextUser);
   }

   public LootDataResolver resolver() {
      return this.resolver;
   }

   /**
    * Create a new ValidationContext with the given LootContextParamSet.
    */
   public ValidationContext setParams(LootContextParamSet pParams) {
      return new ValidationContext(this.problems, this.context, pParams, this.resolver, this.visitedElements);
   }
}