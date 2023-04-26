package me.earth.crystalaura.module.crystalaura.util;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PlayerUtil
        implements Globals {
    private static final Map<Integer, EntityOtherPlayerMP> fakePlayers = new HashMap<Integer, EntityOtherPlayerMP>();

    public static EntityOtherPlayerMP createFakePlayerAndAddToWorld(GameProfile profile) {
        EntityOtherPlayerMP fakePlayer = PlayerUtil.createFakePlayer(profile);
        int randomID = -1000;
        while (fakePlayers.containsKey(randomID) || PlayerUtil.mc.world.getEntityByID(randomID) != null) {
            randomID = ThreadLocalRandom.current().nextInt(-100000, -100);
        }
        fakePlayers.put(randomID, fakePlayer);
        PlayerUtil.mc.world.addEntityToWorld(randomID, fakePlayer);
        return fakePlayer;
    }

    public static EntityOtherPlayerMP createFakePlayer(GameProfile profile) {
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(PlayerUtil.mc.world, profile);
        fakePlayer.inventory.copyInventory(PlayerUtil.mc.player.inventory);
        fakePlayer.setPositionAndRotation(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.getEntityBoundingBox().minY, PlayerUtil.mc.player.posZ, PlayerUtil.mc.player.rotationYaw, PlayerUtil.mc.player.rotationPitch);
        fakePlayer.rotationYawHead = PlayerUtil.mc.player.rotationYawHead;
        fakePlayer.onGround = PlayerUtil.mc.player.onGround;
        fakePlayer.setSneaking(PlayerUtil.mc.player.isSneaking());
        fakePlayer.setHealth(PlayerUtil.mc.player.getHealth());
        return fakePlayer;
    }

    public static void removeFakePlayer(EntityOtherPlayerMP fakePlayer) {
        if (fakePlayer != null && fakePlayers.containsKey(fakePlayer.getEntityId())) {
            fakePlayers.remove(fakePlayer.getEntityId());
            if (PlayerUtil.mc.world != null) {
                PlayerUtil.mc.world.removeEntity(fakePlayer);
            }
        }
    }

    public static boolean isFakePlayer(Entity entity) {
        return entity != null && fakePlayers.containsKey(entity.getEntityId());
    }

    public static boolean isOtherFakePlayer(Entity entity) {
        return entity != null && entity.getEntityId() < 0;
    }

    public static boolean isCreative(EntityPlayer player) {
        return player != null && (player.isCreative() || player.capabilities.isCreativeMode);
    }

    public static boolean isFeetPlaceable(EntityPlayer player, boolean ignoreCrystals, boolean noBoost2, double range) {
        return PlayerUtil.getFeetPos(player, ignoreCrystals, noBoost2, range) != null;
    }

    public static boolean isFootBlock(EntityPlayer player, BlockPos pos) {
        ArrayList<BlockPos> posList = new ArrayList<BlockPos>();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            posList.add(PositionUtil.getPosition(player).offset(facing));
        }
        return posList.contains(pos);
    }

    public static BlockPos getFeetPos(EntityPlayer player, boolean ignoreCrystals, boolean noBoost2, double range) {
        BlockPos off1;
        BlockPos off;
        BlockPos origin = new BlockPos(player).down();
        List<Object> valid = new ArrayList();
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.DOWN || face == EnumFacing.UP) continue;
            off = origin.offset(face);
            off1 = origin.offset(face).offset(face);
            if (PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.OBSIDIAN || PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.ENDER_CHEST || PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.BEDROCK) {
                return null;
            }
            if (BlockUtil.canPlaceCrystal(off, ignoreCrystals, noBoost2)) {
                valid.add(off);
            }
            if (!BlockUtil.canPlaceCrystal(off1, ignoreCrystals, noBoost2) || PlayerUtil.mc.world.getBlockState(off.up()).getBlock() == Blocks.BEDROCK)
                continue;
            valid.add(off1);
        }
        if (!(valid = valid.stream().filter(pos -> PlayerUtil.mc.player.getDistanceSq((BlockPos) pos) <= range * range).sorted().sorted(Comparator.comparing(pos -> Float.valueOf(DamageUtil.calculate((BlockPos) pos) * -1.0f))).collect(Collectors.toList())).isEmpty()) {
            return (BlockPos) valid.get(0);
        }
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.DOWN || face == EnumFacing.UP) continue;
            off = origin.offset(face);
            off1 = origin.offset(face).offset(face);
            if (BlockUtil.canPlaceCrystalFuture(off, ignoreCrystals, noBoost2)) {
                valid.add(off);
            }
            if (!BlockUtil.canPlaceCrystalFuture(off1, ignoreCrystals, noBoost2) || PlayerUtil.mc.world.getBlockState(off.up()).getBlock() == Blocks.BEDROCK)
                continue;
            valid.add(off1);
        }
        return (valid = valid.stream().filter(pos -> PlayerUtil.mc.player.getDistanceSq((BlockPos) pos) <= range * range).sorted().sorted(Comparator.comparing(pos -> Float.valueOf(DamageUtil.calculate((BlockPos) pos) * -1.0f))).collect(Collectors.toList())).isEmpty() ? null : (BlockPos) valid.get(0);
    }

    public static boolean isFootPlace(BlockPos pos, EntityPlayer player, boolean ignoreCrystals, boolean noBoost2, double range) {
        BlockPos origin = new BlockPos(player).down();
        ArrayList<BlockPos> valid = new ArrayList<BlockPos>();
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.DOWN || face == EnumFacing.UP) continue;
            BlockPos off = origin.offset(face);
            BlockPos off1 = origin.offset(face).offset(face);
            if (PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.OBSIDIAN || PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.ENDER_CHEST || PlayerUtil.mc.world.getBlockState(origin.up()).getBlock() == Blocks.BEDROCK) {
                return false;
            }
            if (BlockUtil.canPlaceCrystal(off, ignoreCrystals, noBoost2) || BlockUtil.canPlaceCrystalFuture(off, ignoreCrystals, noBoost2)) {
                valid.add(off);
            }
            if (!BlockUtil.canPlaceCrystal(off1, ignoreCrystals, noBoost2) && !BlockUtil.canPlaceCrystalFuture(off, ignoreCrystals, noBoost2) || PlayerUtil.mc.world.getBlockState(off.up()).getBlock() == Blocks.BEDROCK)
                continue;
            valid.add(off1);
        }
        return valid.contains(pos);
    }

    public static boolean isSafe(float maxDamage) {
        return BlockUtil.sphere(6.0, blockPos -> DamageUtil.calculate(blockPos) >= maxDamage);
    }

    public static BlockPos getBestPlace(BlockPos pos, EntityPlayer player) {
        EnumFacing facing = PlayerUtil.getSide(player, pos);
        if (facing == EnumFacing.UP) {
            Block block = PlayerUtil.mc.world.getBlockState(pos).getBlock();
            Block block2 = PlayerUtil.mc.world.getBlockState(pos.offset(EnumFacing.UP)).getBlock();
            if (block2 instanceof BlockAir && (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK)) {
                return pos;
            }
        } else {
            BlockPos blockPos = pos.offset(facing);
            Block block = PlayerUtil.mc.world.getBlockState(blockPos).getBlock();
            BlockPos blockPos2 = blockPos.down();
            Block block2 = PlayerUtil.mc.world.getBlockState(blockPos2).getBlock();
            if (block instanceof BlockAir && (block2 == Blocks.OBSIDIAN || block2 == Blocks.BEDROCK)) {
                return blockPos2;
            }
        }
        return null;
    }

    public static EnumFacing getSide(EntityPlayer player, BlockPos blockPos) {
        BlockPos playerPos = PositionUtil.getPosition(player);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (!playerPos.offset(facing).equals(blockPos)) continue;
            return facing;
        }
        if (playerPos.offset(EnumFacing.UP).offset(EnumFacing.UP).equals(blockPos)) {
            return EnumFacing.UP;
        }
        return EnumFacing.DOWN;
    }

    public static boolean isValidFootCrystal(Entity crystal, EntityPlayer player) {
        AxisAlignedBB bb = player.getEntityBoundingBox().contract(0.0, 1.0, 0.0).expand(2.0, 0.0, 2.0).expand(-2.0, 0.0, -2.0);
        return PlayerUtil.mc.world.getEntitiesWithinAABB(Entity.class, bb).contains(crystal) && player.onGround;
    }

    public static boolean isInHole(EntityPlayer player) {
        BlockPos position = PositionUtil.getPosition(player);
        int count = 0;
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.UP || face == EnumFacing.DOWN || BlockUtil.isReplaceable(position.offset(face)))
                continue;
            ++count;
        }
        return count >= 3;
    }

    public static boolean willBlockBlockCrystal(BlockPos pos, BlockPos crystalPos) {
        return new Vec3d(pos).equals(new Vec3d(crystalPos.up()));
    }

    public static EnumFacing getOppositePlayerFace(EntityPlayer player, BlockPos pos) {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos off = pos.offset(face);
            AxisAlignedBB bb = PlayerUtil.mc.world.getBlockState(off).getBoundingBox(PlayerUtil.mc.world, off);
            if (!PlayerUtil.mc.world.getEntitiesWithinAABB(Entity.class, bb).contains(player)) continue;
            return face;
        }
        return null;
    }

    public static EnumFacing getOppositePlayerFaceBetter(EntityPlayer player, BlockPos pos) {
        for (EnumFacing face : EnumFacing.HORIZONTALS) {
            BlockPos off = pos.offset(face);
            BlockPos off1 = pos.offset(face).offset(face);
            BlockPos playerOff = PositionUtil.getPosition(player);
            if (!new Vec3d(off).equals(new Vec3d(playerOff)) && !new Vec3d(off1).equals(new Vec3d(off1)))
                continue;
            return face.getOpposite();
        }
        return null;
    }

    public static List<BlockPos> crystalPosFromLegBlock(EntityPlayer player, BlockPos pos) {
        ArrayList<BlockPos> valid = new ArrayList<BlockPos>();
        EnumFacing facing = Objects.requireNonNull(PlayerUtil.getOppositePlayerFaceBetter(player, pos)).getOpposite();
        EnumFacing rotated = facing.rotateY();
        EnumFacing inverse = facing.rotateY().rotateY().rotateY();
        if (BlockUtil.canPlaceCrystal(pos.offset(facing).offset(rotated).down(), true, false)) {
            valid.add(pos.offset(facing).offset(rotated).down());
        }
        if (BlockUtil.canPlaceCrystal(pos.offset(facing).offset(inverse).down(), true, false)) {
            valid.add(pos.offset(facing).offset(inverse).down());
        }
        return valid;
    }
}

