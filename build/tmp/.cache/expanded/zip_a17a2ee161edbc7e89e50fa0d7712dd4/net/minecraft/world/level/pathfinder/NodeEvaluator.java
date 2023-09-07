package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;

public abstract class NodeEvaluator {
   protected PathNavigationRegion level;
   protected Mob mob;
   protected final Int2ObjectMap<Node> nodes = new Int2ObjectOpenHashMap<>();
   protected int entityWidth;
   protected int entityHeight;
   protected int entityDepth;
   protected boolean canPassDoors;
   protected boolean canOpenDoors;
   protected boolean canFloat;
   protected boolean canWalkOverFences;

   public void prepare(PathNavigationRegion pLevel, Mob pMob) {
      this.level = pLevel;
      this.mob = pMob;
      this.nodes.clear();
      this.entityWidth = Mth.floor(pMob.getBbWidth() + 1.0F);
      this.entityHeight = Mth.floor(pMob.getBbHeight() + 1.0F);
      this.entityDepth = Mth.floor(pMob.getBbWidth() + 1.0F);
   }

   /**
    * This method is called when all nodes have been processed and PathEntity is created.
    * {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
    * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
    */
   public void done() {
      this.level = null;
      this.mob = null;
   }

   protected Node getNode(BlockPos pPos) {
      return this.getNode(pPos.getX(), pPos.getY(), pPos.getZ());
   }

   /**
    * Returns a mapped point or creates and adds one
    */
   protected Node getNode(int pX, int pY, int pZ) {
      return this.nodes.computeIfAbsent(Node.createHash(pX, pY, pZ), (p_77332_) -> {
         return new Node(pX, pY, pZ);
      });
   }

   public abstract Node getStart();

   public abstract Target getGoal(double pX, double pY, double pZ);

   protected Target getTargetFromNode(Node p_230616_) {
      return new Target(p_230616_);
   }

   public abstract int getNeighbors(Node[] p_77353_, Node p_77354_);

   public abstract BlockPathTypes getBlockPathType(BlockGetter p_77337_, int p_77338_, int p_77339_, int p_77340_, Mob p_77341_);

   /**
    * Returns the node type at the specified postion taking the block below into account
    */
   public abstract BlockPathTypes getBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ);

   public void setCanPassDoors(boolean pCanEnterDoors) {
      this.canPassDoors = pCanEnterDoors;
   }

   public void setCanOpenDoors(boolean pCanOpenDoors) {
      this.canOpenDoors = pCanOpenDoors;
   }

   public void setCanFloat(boolean pCanSwim) {
      this.canFloat = pCanSwim;
   }

   public void setCanWalkOverFences(boolean p_255862_) {
      this.canWalkOverFences = p_255862_;
   }

   public boolean canPassDoors() {
      return this.canPassDoors;
   }

   public boolean canOpenDoors() {
      return this.canOpenDoors;
   }

   public boolean canFloat() {
      return this.canFloat;
   }

   public boolean canWalkOverFences() {
      return this.canWalkOverFences;
   }
}