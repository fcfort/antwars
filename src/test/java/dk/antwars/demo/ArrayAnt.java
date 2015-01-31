//package dk.antwars.demo;
//
//import dk.antwars.core.ant.Ant;
//import dk.antwars.core.ant.presentation.Color;
//import dk.antwars.core.ant.Self;
//import dk.antwars.core.game.mechanics.external.Area;
//import dk.antwars.core.game.mechanics.internal.actions.Action;
//import dk.antwars.core.game.mechanics.internal.actions.Move;
//import dk.antwars.core.game.mechanics.external.Direction;
//import dk.antwars.core.util.RandomUtils;
//
//public class ArrayAnt implements Ant {
//
//    final static int LONGITUDE_BYTE_INDEX1 = 0;
//    final static int LONGITUDE_BYTE_INDEX2 = 1;
//    final static int LATITUDE_BYTE_INDEX1  = 2;
//    final static int LATITUDE_BYTE_INDEX2  = 3;
//
//    final static int QUEEN_LONGITUDE_BYTE_INDEX1 = 4;
//    final static int QUEEN_LONGITUDE_BYTE_INDEX2 = 5;
//    final static int QUEEN_LATITUDE_BYTE_INDEX1  = 6;
//    final static int QUEEN_LATITUDE_BYTE_INDEX2  = 7;
//
//    final static int TARGET_LONGITUDE_BYTE_INDEX1 = 8;
//    final static int TARGET_LONGITUDE_BYTE_INDEX2 = 9;
//    final static int TARGET_LATITUDE_BYTE_INDEX1  = 10;
//    final static int TARGET_LATITUDE_BYTE_INDEX2  = 11;
//
//    //Memory 1 byte latitude  | 255
//    //Memory 2 byte longitude | 255
//
//    @Override
//    public Action act(final Self self, Area visibleArea) {
//        int sourceLongitude = extractIntFromMemory(self, LONGITUDE_BYTE_INDEX1, LONGITUDE_BYTE_INDEX2);
//        int sourceLatitude = extractIntFromMemory(self, LATITUDE_BYTE_INDEX1, LATITUDE_BYTE_INDEX2);
//        int targetLongitude = extractIntFromMemory(self, TARGET_LONGITUDE_BYTE_INDEX1, TARGET_LONGITUDE_BYTE_INDEX2);
//        int targetLatitude = extractIntFromMemory(self, TARGET_LATITUDE_BYTE_INDEX1, TARGET_LATITUDE_BYTE_INDEX2);
//
//        System.out.println("Position: " + sourceLatitude + "," + sourceLongitude);
//        if (sourceLongitude == targetLongitude && sourceLatitude == targetLatitude) {
//            targetLatitude = (RandomUtils.getInstance().nextBoolean() ? 1 : -1) * RandomUtils.getInstance().nextInt(500);
//            targetLongitude = (RandomUtils.getInstance().nextBoolean() ? 1 : -1) * RandomUtils.getInstance().nextInt(500);
//            insertIntIntoMemory(self, TARGET_LONGITUDE_BYTE_INDEX1, TARGET_LONGITUDE_BYTE_INDEX2, targetLongitude);
//            insertIntIntoMemory(self, TARGET_LATITUDE_BYTE_INDEX1, TARGET_LATITUDE_BYTE_INDEX2, targetLatitude);
//            System.out.println("Target: " + targetLatitude + "," + targetLongitude);
//        }
//
//        final int moveDirection = calculateDirectMoveDirection(sourceLongitude, sourceLatitude, targetLongitude, targetLatitude);
//        if (moveDirection != Direction.CENTER) {
//            final int newLongitude = calculateNewLongitude(sourceLongitude, moveDirection);
//            final int newLatitude = calculateNewLatitude(sourceLatitude, moveDirection);
//            insertIntIntoMemory(self, TARGET_LONGITUDE_BYTE_INDEX1, TARGET_LONGITUDE_BYTE_INDEX2, newLongitude);
//            insertIntIntoMemory(self, TARGET_LATITUDE_BYTE_INDEX1, TARGET_LATITUDE_BYTE_INDEX2, newLatitude);
//            return new Move(moveDirection);
//        }
//        return null;
//    }
//
//    @Override
//    public Color getPreferredColor() {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    private void insertIntIntoMemory(Self self, int byteIndex1, int byteIndex2, int value) {
//        final byte[] bytes = convertToBytes(value);
//        self.getMemory().getShortTermMemory()[byteIndex1] = bytes[0];
//        self.getMemory().getShortTermMemory()[byteIndex2] = bytes[1];
//    }
//
//    private int extractIntFromMemory(final Self self, final int byteIndex1, final int byteIndex2) {
//        int value = 0;
//        value += self.getMemory().getShortTermMemory()[byteIndex1];
//        value += self.getMemory().getShortTermMemory()[byteIndex2] << 8;
//        return value;
//    }
//
//    private byte[] convertToBytes(int value) {
//        final byte lowByte = (byte) value;
//        final byte highByte = (byte) (value >>> 8);
//        return new byte[]{lowByte, highByte};
//    }
//
//    private int calculateNewLongitude(final int sourceLongitude, final int direction) {
//        final boolean north = (direction & Direction.NORTH) == Direction.NORTH;
//        final boolean south = (direction & Direction.SOUTH) == Direction.SOUTH;
//        if (north) return sourceLongitude + 1;
//        if (south) return sourceLongitude - 1;
//        return sourceLongitude;
//    }
//
//    private int calculateNewLatitude(final int sourceLatitude, final int direction) {
//        final boolean east = (direction & Direction.EAST) == Direction.EAST;
//        final boolean west = (direction & Direction.WEST) == Direction.WEST;
//        if (east) return sourceLatitude + 1;
//        if (west) return sourceLatitude - 1;
//        return sourceLatitude;
//    }
//
//    private int calculateDirectMoveDirection(final int sourceLongitude, final int sourceLatitude, final int targetLongitude, final int targetLatitude) {
//        final int longitudeDistance = targetLongitude - sourceLongitude;
//        final int latitudeDistance = targetLatitude - sourceLatitude;
//        if (longitudeDistance > 0) {
//            if (latitudeDistance > 0) {
//                return Direction.NORTHEAST;
//            } else if (latitudeDistance < 0) {
//                return Direction.NORTHWEST;
//            }
//            return Direction.NORTH;
//        } else if (longitudeDistance < 0) {
//             if (latitudeDistance > 0) {
//                return Direction.SOUTHEAST;
//            } else if (latitudeDistance < 0) {
//                return Direction.SOUTHWEST;
//            }
//            return Direction.SOUTH;
//        } else {
//            if (latitudeDistance > 0) {
//                return Direction.EAST;
//            } else if (latitudeDistance < 0) {
//                return Direction.WEST;
//            }
//            return Direction.STAY;
//        }
//    }
//
//    @Override
//    public Color getColor() {
//        return new Color(0,255,0);
//    }
//
//}
