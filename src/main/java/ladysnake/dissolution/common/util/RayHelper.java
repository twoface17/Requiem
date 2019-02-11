/*
 * Blink
 * Copyright (C) 2019-2019 GlassPane
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses>.
 */
package ladysnake.dissolution.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Some raytracing utilities
 *
 * @author UpcraftLP
 */
public class RayHelper {

    /**
     * Raytraces using an entity's position as source, and its look vector as direction
     *
     * @param entity        the raytrace source
     * @param range         the maximum range of the raytrace
     * @param shapeType     <b>COLLIDER</b> for collision raytracing, <b>OUTLINE</b> for tracing the block outline shape (render bounding box)
     * @param fluidHandling how to handle fluids
     * @param tickDeltaTime the delta tick time (partial render tick)
     */
    @Nonnull
    public static HitResult rayTraceEntity(Entity entity, double range, RayTraceContext.ShapeType shapeType, RayTraceContext.FluidHandling fluidHandling, float tickDeltaTime) {
        Vec3d startPoint = entity.getCameraPosVec(tickDeltaTime);
        Vec3d lookVec = entity.getRotationVec(tickDeltaTime);
        Vec3d endPoint = startPoint.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        return rayTrace(entity.world, entity, startPoint, endPoint, shapeType, fluidHandling);
    }

    /**
     *
     * @param world         the world
     * @param source        the entity to be used for determining block bounding boxes
     * @param start         the start point
     * @param end           the end point, if no result was found
     * @param shapeType     <b>COLLIDER</b> for collision raytracing, <b>OUTLINE</b> for tracing the block outline shape (render bounding box)
     * @param fluidHandling how to handle fluids
     */
    @Nonnull
    public static HitResult rayTrace(World world, Entity source, Vec3d start, Vec3d end, RayTraceContext.ShapeType shapeType, RayTraceContext.FluidHandling fluidHandling) {
        return world.rayTrace(new RayTraceContext(start, end, shapeType, fluidHandling, source));
    }

    /**
     * Finds a suitable blink position for an entity based on its look
     *
     * @param entity    the source of the raytrace
     * @param deltaTime the delta tick time (partial render tick)
     * @param range     the maximum range that the entity can target
     * @return the position targeted by <code>entity</code>
     */
    public static Vec3d findBlinkPos(Entity entity, float deltaTime, double range) {
        World world = entity.world;
        HitResult trace = rayTraceEntity(entity, range, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.SOURCE_ONLY, deltaTime);
        boolean secondPass;
        if (trace.getType() == HitResult.Type.NONE) {
            trace = rayTrace(world, entity, trace.getPos(), trace.getPos().subtract(0, 1, 0), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.SOURCE_ONLY);
            secondPass = false;
        } else {
            secondPass = true;
        }
        Vec3d pos = trace.getPos();
        if (trace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult result = (BlockHitResult) trace;
            switch (result.getSide()) {
                case DOWN:
                    pos = pos.subtract(0, entity.getHeight(), 0);
                    break;
                case UP:
                    secondPass = false;
                    break;
                default:
                    Vec3d entityPos = entity.getCameraPosVec(deltaTime);
                    Vec3d toTarget = pos.subtract(entityPos);
                    if (pos.y - (int) pos.y >= 0.5D) {
                        BlockPos testPos;
                        switch (result.getSide()) {
                            case EAST:
                                testPos = new BlockPos(pos.x - 1, pos.y + 1, pos.z);
                                break;
                            case WEST:
                            case NORTH:
                                testPos = new BlockPos(pos.x, pos.y + 1, pos.z);
                                break;
                            case SOUTH:
                                testPos = new BlockPos(pos.x, pos.y + 1, pos.z - 1);
                                break;
                            default: //should never happen, but better safe than sorry
                                throw new RaytraceException("hit result had wrong value: " + result.getSide());
                        }
                        if (!world.isEntityColliding(null, entity.getBoundingBox().offset(testPos.getX() - entity.x, testPos.getY() - entity.y, testPos.getZ() - entity.z))) {
                            toTarget = toTarget.multiply(Math.max((toTarget.length() + 0.8D) / toTarget.length(), 1.0D));
                            pos = new Vec3d(entityPos.x + toTarget.x, testPos.getY() + 0.1D, entityPos.z + toTarget.z);
                            HitResult result1 = rayTrace(world, entity, pos, pos.subtract(0.0D, 1.0D, 0.0D), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.SOURCE_ONLY);
                            pos = result1.getPos();
                            secondPass = false;
                        }
                    }
                    if (secondPass) {
                        toTarget = toTarget.multiply((toTarget.length() - (entity.getWidth() * 1.3F)) / toTarget.length());
                        pos = entityPos.add(toTarget);
                        HitResult result1 = rayTrace(world, entity, pos, pos.subtract(0.0D, 1.0D, 0.0D), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.SOURCE_ONLY);
                        pos = result1.getPos();
                    }
            }
        }
        if (secondPass) {
            Vec3d tempPos = pos.subtract(0.0D, 0.0001D, 0.0D);
            HitResult flagTrace = rayTrace(world, entity, tempPos, pos.add(0.0D, entity.getHeight(), 0.0D), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.SOURCE_ONLY);
            if (flagTrace.getPos().y - tempPos.y < entity.getHeight()) {
                pos = flagTrace.getPos().subtract(0, entity.getHeight(), 0);
            }
        }
        return pos;
    }

    public static class RaytraceException extends RuntimeException {
        public RaytraceException(String message) {
            super(message);
        }
    }
}