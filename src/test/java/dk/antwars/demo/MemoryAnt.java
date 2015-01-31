/*
package dk.antwars.demo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dk.antwars.core.ant.Ant;
import dk.antwars.core.ant.Memory;
import dk.antwars.core.ant.Self;
import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.ant.species.Antennas;
import dk.antwars.core.ant.species.Body;
import dk.antwars.core.ant.species.Brain;
import dk.antwars.core.ant.species.Eyes;
import dk.antwars.core.ant.species.Head;
import dk.antwars.core.ant.species.Jaws;
import dk.antwars.core.ant.species.Legs;
import dk.antwars.core.ant.species.QueenSpecies;
import dk.antwars.core.ant.species.Species;
import dk.antwars.core.game.mechanics.external.Area;
import dk.antwars.core.game.mechanics.external.Coordinates;
import dk.antwars.core.game.mechanics.external.Direction;
import dk.antwars.core.game.mechanics.external.Entity;
import dk.antwars.core.game.mechanics.external.FriendlyEntity;
import dk.antwars.core.game.mechanics.external.GeographicCoordinate;
import dk.antwars.core.game.mechanics.external.HostileEntity;
import dk.antwars.core.game.mechanics.external.Location;
import dk.antwars.core.game.mechanics.external.Pheromone;
import dk.antwars.core.game.mechanics.internal.actions.Action;
import dk.antwars.core.game.mechanics.internal.actions.Bite;
import dk.antwars.core.game.mechanics.internal.actions.Drag;
import dk.antwars.core.game.mechanics.internal.actions.Hatch;
import dk.antwars.core.game.mechanics.internal.actions.Mark;
import dk.antwars.core.game.mechanics.internal.actions.Move;
import dk.antwars.core.util.RandomUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MemoryAnt implements Ant {

    private static final Pheromone.Scent QUEEN_SCENT = Pheromone.Scent.Yellow;
    private static final Pheromone.Scent ENEMY_SCENT = Pheromone.Scent.Red;
    private static final Pheromone.Scent FOOD_SCENT = Pheromone.Scent.Green;
    private static final Pheromone.Scent EGG_SCENT = Pheromone.Scent.Blue;

    private static final char MEMORY_SCOUT      = 0x00; //0x0<Direction> scout direction
    private static final char MEMORY_FLEE       = 0x10; //0x1<Direction> flee direction
    private static final char MEMORY_RETURN     = 0x20; //0x2<Direction> return direction
    private static final char MEMORY_DRAG       = 0x30; //0x3<Direction> drag direction
    private static final char MEMORY_ATTACK     = 0x40; //0x4<Direction> attack direction

    private static final char MEMORY_MARK_FRIENDLY_QUEEN = 0x50;
    private static final char MEMORY_MARK_HOSTILE_QUEEN = 0x51;
    private static final char MEMORY_MARK_ENEMY = 0x52;
    private static final char MEMORY_MARK_FOOD  = 0x53;
    private static final char MEMORY_MARK_EGG   = 0x54;

    private static final char BIRTH   = 0xFFFF;

    private static final Coordinates CENTER_COORDINATES = new Coordinates(0,0);

    @Override
    public dk.antwars.core.game.mechanics.internal.actions.Action act(final Self self, Area visibleArea) {
        final Memory memory = self.getMemory();
        maintainMemory(memory);
        if (self.getSpecies().isQueen()) {
            return actAsQueen(self, visibleArea);
        } else {
            handleQueenMark(memory, visibleArea);
            if (!visibleArea.getLocationsWithHostiles().isEmpty()) {
                if (shouldMarkWithEnemyScent(self, visibleArea)) {
                    return new Mark(ENEMY_SCENT);
                } else {
                    if (shouldStayAndFight(self, visibleArea)) {
                        final Entity targetEntity = determineTargetEntity(self, visibleArea);
                        if (targetEntity != null) {
                            final Direction attackDirection = determineAttackDirection(visibleArea, targetEntity);
                            if (attackDirection != null) {
                                memory.push(getMemoryFragment(MEMORY_ATTACK, attackDirection));
                            }
                            return new Bite(targetEntity);
                        }
                    } else {
                        final Direction fleeDirection = determineFleeDirection(self, visibleArea);
                        if (fleeDirection != null) {
                            memory.push(getMemoryFragment(MEMORY_FLEE, fleeDirection));
                            if (isStandingOnFood(visibleArea)) {
                                return new Drag(fleeDirection);
                            } else {
                                return new Move(fleeDirection);
                            }
                        }
                    }
                }
            }
            if (!isStandingOnQueen(visibleArea) && isStandingOnFood(visibleArea)) {
                final Coordinates coordinatesToLatestQueenMemoryFragmentMark = determineCoordinatesToClosestMemoryFragment(memory, MEMORY_MARK_FRIENDLY_QUEEN);
                if (coordinatesToLatestQueenMemoryFragmentMark != null) {
                    if (calculateDistance(CENTER_COORDINATES, coordinatesToLatestQueenMemoryFragmentMark) > 0) {
                        final Direction dragDirection = determineImmediateDirection(coordinatesToLatestQueenMemoryFragmentMark);
                        memory.push(getMemoryFragment(MEMORY_DRAG, dragDirection));
                        return new Drag(dragDirection);
                    } else {
                        System.out.println("Send distress, lost!");
                        //TODO: Send distress, lost! Maybe try to prevent getting lost
                    }
                }
            }
            final Direction moveDirection = determineApproachFoodDirection(visibleArea);
            if (moveDirection != null) {
                memory.push(getMemoryFragment(MEMORY_SCOUT, moveDirection));
                return new Move(moveDirection);
            }
            final Direction scoutDirection = getScoutDirection(self, visibleArea);
            if (scoutDirection != null) {
                memory.push(getMemoryFragment(MEMORY_SCOUT, scoutDirection));
                return new Move(scoutDirection);
            }
        }
        System.out.print("Marking White!");
        return new Mark(Pheromone.Scent.White);
    }

    private Coordinates determineCoordinatesToClosestMemoryFragment(final Memory memory, final char memoryMark) {
        final Map<Character, Set<Coordinates>> coordinatesToMemoryFragments = recallCoordinatesToSpecificMemoryFragments(memory, memoryMark);
        final Set<Coordinates> coordinatesToFriendlyQueen = coordinatesToMemoryFragments.get(MEMORY_MARK_FRIENDLY_QUEEN);
        if (coordinatesToFriendlyQueen != null && !coordinatesToFriendlyQueen.isEmpty()) {
            return determineClosestCoordinates(coordinatesToFriendlyQueen);
        }
        return null;
    }

    private Direction determineApproachFoodDirection(Area visibleArea) {
        for (final Location locationWithFood : visibleArea.getLocationsWithFood()) {
            if (!locationWithFood.getCoordinates().equals(CENTER_COORDINATES)) {
                if (locationWithFood.getFood() > locationWithFood.getEntitiesOnLocation().size()) {
                    return determineImmediateDirection(locationWithFood.getCoordinates());
                }
            }
        }
        return null;
    }

    private Direction getScoutDirection(Self self, Area visibleArea) {
        final Set<Direction> possibleDirections = Direction.getPossibleDirections();
        for (Location location: visibleArea.getLocationsWithEntities()) {
            possibleDirections.remove(determineImmediateDirection(location.getCoordinates()));
        }
        for (final Location location: visibleArea.getLocationsWithPheromones()) {
            possibleDirections.remove(determineImmediateDirection(location.getCoordinates()));
        }
        final Coordinates coordinatesToClosestQueen = determineCoordinatesToClosestMemoryFragment(self.getMemory(), MEMORY_MARK_FRIENDLY_QUEEN);
        final Direction directionOfClosestQueen = determineImmediateDirection(coordinatesToClosestQueen);
        possibleDirections.remove(directionOfClosestQueen);
        if (!possibleDirections.isEmpty()) {
            return RandomUtils.getRandomDirection(possibleDirections.toArray(new Direction[possibleDirections.size()]));
        } else {
            return directionOfClosestQueen.getOppositeDirection();
        }
    }

    public Direction determineFleeDirection(Self self, Area visibleArea) {
        final Set<Direction> optimalAvoidDirections = determineOptimalAvoidDirections(self, visibleArea);
        if (optimalAvoidDirections.size() == 1) {
            return optimalAvoidDirections.iterator().next();
        } else {
            final Direction returnDirection = determineReturnDirection(self);
            if (returnDirection != null) {
                if (optimalAvoidDirections.isEmpty() || optimalAvoidDirections.contains(returnDirection)) {
                    return returnDirection;
                } else {
                    final Set<Direction> adjacentReturnDirections = returnDirection.getAdjacentDirections();
                    for (Direction adjacentReturnDirection : adjacentReturnDirections) {
                        if (optimalAvoidDirections.contains(returnDirection)) {
                            return adjacentReturnDirection;
                        }
                    }
                }
            }
        }
        return optimalAvoidDirections.iterator().next();
    }

    private Direction determineReturnDirection(final Self self) {
        final Map<Character, Set<Coordinates>> coordinatesToMemoryFragments = recallCoordinatesToSpecificMemoryFragments(self.getMemory(), MEMORY_MARK_FRIENDLY_QUEEN);
        final Set<Coordinates> coordinatesToFriendlyQueen = coordinatesToMemoryFragments.get(MEMORY_MARK_FRIENDLY_QUEEN);
        final Coordinates closestCoordinates = determineClosestCoordinates(coordinatesToFriendlyQueen);
        return determineImmediateDirection(closestCoordinates);
    }

    private Coordinates determineClosestCoordinates(final Set<Coordinates> possibleCoordinates) {
        int shortestDistance = 0;
        Coordinates closestCoordinates = null;
        for (final Coordinates coordinates: possibleCoordinates) {
            final int distanceToCoordinates = calculateDistance(CENTER_COORDINATES, coordinates);
            if (closestCoordinates == null || distanceToCoordinates < shortestDistance) {
                shortestDistance = distanceToCoordinates;
                closestCoordinates = coordinates;
            }
        }
        return closestCoordinates;
    }

    private boolean isStandingOnFood(Area visibleArea) {
        int food = visibleArea.getCurrentLocation().getFood();
        return food > 0;
    }

    private void handleQueenMark(final Memory memory, final Area visibleArea) {
        markQueenCoordinates(memory, getCoordinatesOnFriendlyQueens(visibleArea), MEMORY_MARK_FRIENDLY_QUEEN);
        markQueenCoordinates(memory, getCoordinatesOnHostileQueens(visibleArea), MEMORY_MARK_HOSTILE_QUEEN);
    }

    private void markQueenCoordinates(Memory memory, Set<Coordinates> coordinatesOnFriendlyQueens, char memoryFragment) {
        if (coordinatesOnFriendlyQueens != null && !coordinatesOnFriendlyQueens.isEmpty()) {
            for (final Coordinates coordinatesOnFriendlyQueen: coordinatesOnFriendlyQueens) {
                if (!coordinatesOnFriendlyQueen.equals(CENTER_COORDINATES)) {
                    rememberMovement(memory, CENTER_COORDINATES, coordinatesOnFriendlyQueen);
                }
                memory.push(memoryFragment);
                if (!coordinatesOnFriendlyQueen.equals(CENTER_COORDINATES)) {
                    rememberMovement(memory, coordinatesOnFriendlyQueen, CENTER_COORDINATES);
                }
            }
        }
    }

    private Coordinates calculateRelativeCoordinates(Coordinates from, Coordinates to) {
        return new Coordinates(to.getLongitude()-from.getLongitude(), to.getLatitude()-from.getLatitude());
    }

    private int calculateDistance(Coordinates sourceCoordinates, Coordinates targetCoordinates) {
        return Math.max(Math.abs(targetCoordinates.getLatitude()-sourceCoordinates.getLatitude()),Math.abs(targetCoordinates.getLongitude()-sourceCoordinates.getLongitude()));
    }

    public void rememberMovement(Memory memory, Coordinates from, Coordinates to) {
        final Coordinates coordinates = calculateRelativeCoordinates(from, to);
        int latitude = coordinates.getLatitude(), longitude = coordinates.getLongitude();
        while(Math.abs(latitude) > 0 || Math.abs(longitude) > 0) {
            final Direction immediateDirection = determineImmediateDirection(longitude, latitude);
            memory.push(getMemoryFragment(MEMORY_SCOUT, immediateDirection));
            if (latitude != 0) {
                if (latitude > 0) {
                    latitude--;
                } else {
                    latitude++;
                }
            }
            if (longitude != 0) {
                if (longitude > 0) {
                    longitude--;
                } else {
                    longitude++;
                }
            }
        }
    }

//    public Map<Character, Set<Coordinates>> recallCoordinatesToNonMovementMemoryFragments(final Memory memory) {
//        final Map<Character, Set<Coordinates>> nonMovementMemoryFragmentMarks = Maps.newLinkedHashMap();
//        int latitude = 0;
//        int longitude = 0;
//        char[] memoryFragments = memory.getMemoryFragments();
//        if (memoryFragments != null) {
//            for (final char memoryFragment: memoryFragments) {
//                if (memoryFragment > 0x4F) {
//                    final Coordinates coordinatesToFragment = new Coordinates(-longitude, -latitude);
//                    if (nonMovementMemoryFragmentMarks.containsKey(memoryFragment)) {
//                        nonMovementMemoryFragmentMarks.get(memoryFragment).add(coordinatesToFragment);
//                    } else {
//                        final Set<Coordinates> coordinates = Sets.newHashSet(coordinatesToFragment);
//                        nonMovementMemoryFragmentMarks.put(memoryFragment, coordinates);
//                    }
//                } else {
//                    if (isMoveFragment(getMemoryFragmentType(memoryFragment))) {
//                        final char direction = (char) (memoryFragment & 0xF);
//                        if ((direction & Direction.EAST) == Direction.EAST) longitude++; else if ((direction & Direction.WEST) == Direction.WEST) longitude--;
//                        if ((direction & Direction.NORTH) == Direction.NORTH) latitude++; else if ((direction & Direction.SOUTH) == Direction.SOUTH) latitude--;
//                    }
//                }
//            }
//        }
//        return null;
//    }

//    public Map<Coordinates, Set<Character>> recallCoordinates(final Memory memory) {
//        final Map<Coordinates, Set<Character>> recalledCoordinates = Maps.newLinkedHashMap();
//        int latitude = 0;
//        int longitude = 0;
//        char[] memoryFragments = memory.getMemoryFragments();
//        if (memoryFragments != null) {
//            for (final char memoryFragment: memoryFragments) {
//                if (memoryFragment > 0x4F) {
//                    final Coordinates coordinatesToFragment = new Coordinates(-longitude, -latitude);
//                    if (recalledCoordinates.containsKey(coordinatesToFragment)) {
//                        recalledCoordinates.get(coordinatesToFragment).add(memoryFragment);
//                    } else {
//                        final Set<Character> memorizedFragments = Sets.newHashSet(memoryFragment);
//                        recalledCoordinates.put(coordinatesToFragment, memorizedFragments);
//                    }
//                } else {
//                    if (isMoveFragment(getMemoryFragmentType(memoryFragment))) {
//                        final char direction = (char) (memoryFragment & 0xF);
//                        if ((direction & Direction.EAST) == Direction.EAST) longitude++; else if ((direction & Direction.WEST) == Direction.WEST) longitude--;
//                        if ((direction & Direction.NORTH) == Direction.NORTH) latitude++; else if ((direction & Direction.SOUTH) == Direction.SOUTH) latitude--;
//                    }
//                }
//            }
//        }
//        return recalledCoordinates;
//    }

    public Map<GeographicCoordinate, Set<Character>> recallCoordinates(final Memory memory) {
        final Map<GeographicCoordinate, Set<Character>> recalledCoordinates = Maps.newLinkedHashMap();
        final GeographicCoordinate coordinatesToFragment = new GeographicCoordinate();
        while (memory.getNumberOfMemoryFragments() > 0) {
            final char memoryFragment = memory.pop();
            if (memoryFragment > 0x4F) {
                if (recalledCoordinates.containsKey(coordinatesToFragment)) {
                    recalledCoordinates.get(coordinatesToFragment).add(memoryFragment);
                } else {
                    final Set<Character> memorizedFragments = Sets.newHashSet(memoryFragment);
                    recalledCoordinates.put(coordinatesToFragment, memorizedFragments);
                }
            } else {
                coordinatesToFragment.move(getDirectionFromMemoryFragment(memoryFragment));
            }
        }
        return recalledCoordinates;
    }

    public Map<Character, Set<Coordinates>> recallCoordinatesToSpecificMemoryFragments(final Memory memory, Character... memoryFragmentsArray) {
        final Map<Character, Set<Coordinates>> nonMovementMemoryFragmentMarks = Maps.newLinkedHashMap();
        int latitude = 0;
        int longitude = 0;
        final Set<Character> memoryFragments = Sets.newHashSet(memoryFragmentsArray);
        if (memoryFragments != null && !memoryFragments.isEmpty()) {
            for (final char memoryFragment: memory.getMemoryFragments()) {
                if (memoryFragments.contains(memoryFragment)) {
                    final Coordinates coordinatesToFragment = new Coordinates(-longitude, -latitude);
                    if (nonMovementMemoryFragmentMarks.containsKey(memoryFragment)) {
                        nonMovementMemoryFragmentMarks.get(memoryFragment).add(coordinatesToFragment);
                    } else {
                        final Set<Coordinates> coordinates = Sets.newHashSet(coordinatesToFragment);
                        nonMovementMemoryFragmentMarks.put(memoryFragment, coordinates);
                    }
                } else {
                    if (isMoveFragment(getMemoryFragmentType(memoryFragment))) {
                        final char direction = (char) (memoryFragment & 0xF);
                        if ((direction & Direction.EAST) == Direction.EAST) longitude++; else if ((direction & Direction.WEST) == Direction.WEST) longitude--;
                        if ((direction & Direction.NORTH) == Direction.NORTH) latitude++; else if ((direction & Direction.SOUTH) == Direction.SOUTH) latitude--;
                    }
                }
            }
        }
        return nonMovementMemoryFragmentMarks;
    }

    private boolean isStandingOnQueen(Area visibleArea) {
        final Set<FriendlyEntity> friendlyEntitiesOnLocation = visibleArea.getCurrentLocation().getFriendlyEntitiesOnLocation();
        for (final FriendlyEntity friendlyEntity: friendlyEntitiesOnLocation) {
            if (friendlyEntity.getSpecies() instanceof QueenSpecies) {
                return true;
            }
        }
        return false;
    }

    private Set<Coordinates> getCoordinatesOnFriendlyQueens(Area visibleArea) {
        final Set<Coordinates> coordinatesOnFriendlyQueens = Sets.newHashSet();
        for (final Location locationWithEntity: visibleArea.getLocationsWithEntities()) {
            for (final FriendlyEntity entity: locationWithEntity.getFriendlyEntitiesOnLocation()) {
                if (entity.getSpecies() instanceof QueenSpecies) {
                    coordinatesOnFriendlyQueens.add(locationWithEntity.getCoordinates());
                }
            }
        }
        return coordinatesOnFriendlyQueens;
    }

    private Set<Coordinates> getCoordinatesOnHostileQueens(Area visibleArea) {
        final Set<Coordinates> coordinatesOnHostileQueens = Sets.newHashSet();
        for (final Location locationWithEntity: visibleArea.getLocationsWithEntities()) {
            for (final HostileEntity entity: locationWithEntity.getHostileEntitiesOnLocation()) {
                if (entity.getSpecies() instanceof QueenSpecies) {
                    coordinatesOnHostileQueens.add(locationWithEntity.getCoordinates());
                }
            }
        }
        return coordinatesOnHostileQueens;
    }

    private void maintainMemory(final Memory memory) {
        if (memory.getNumberOfMemoryFragments() == 0) {
            memory.push(BIRTH);
        } else if (memory.getMemorySize() == memory.getNumberOfMemoryFragments()) {
            final Map<GeographicCoordinate, Set<Character>> memorizedCoordinates = recallCoordinates(memory);
            final GeographicCoordinate geographicCoordinate = new GeographicCoordinate();
            for (final Map.Entry<GeographicCoordinate, Set<Character>> memorizedCoordinateEntry: memorizedCoordinates.entrySet()) {
                while (!geographicCoordinate.equals(memorizedCoordinateEntry.getKey())) {
                    final Direction immediateDirection = determineImmediateDirection(geographicCoordinate, memorizedCoordinateEntry.getKey());
                    geographicCoordinate.move(immediateDirection);
                    memory.push(getMemoryFragment(MEMORY_SCOUT, immediateDirection));
                }
                final Set<Character> memoryFragments = memorizedCoordinateEntry.getValue();
                for (final Character memoryFragment: memoryFragments) {
                    memory.push(memoryFragment.charValue());
                }
            }
            final GeographicCoordinate centerCoordinates = new GeographicCoordinate();
            while (!geographicCoordinate.equals(centerCoordinates)) {
                final Direction immediateDirection = determineImmediateDirection(geographicCoordinate, centerCoordinates);
                geographicCoordinate.move(immediateDirection);
                memory.push(getMemoryFragment(MEMORY_SCOUT, immediateDirection));
            }
        }
    }

    private Direction getDirectionFromMemoryFragment(char memoryFragment) {
        return new Direction((byte) (memoryFragment & 0xF));
    }

    private boolean isMoveFragment(char memoryFragmentType) {
        return memoryFragmentType == MEMORY_SCOUT || memoryFragmentType == MEMORY_DRAG || memoryFragmentType == MEMORY_FLEE || memoryFragmentType == MEMORY_RETURN;
    }

    private Action actAsQueen(final Self self, Area visibleArea) {
        final char[] memoryFragments = self.getMemory().getMemoryFragments();
        if (memoryFragments.length < 20) {
            return new Hatch(new Species(new Head(new Brain(0), new Eyes(2), new Jaws(0), new Antennas(0)), new Body(new Legs(0), pheromoneGlands), 1, 0));
        }
        return new Move(new Direction(Direction.CENTER));
    }

    private Direction determineAttackDirection(Area visibleArea, Entity targetEntity) {
        if (visibleArea.getCurrentLocation().getHostileEntitiesOnLocation().contains(targetEntity)) {
            return determineImmediateDirection(visibleArea.getCurrentLocation().getCoordinates());
        }
        for (final Location locationsWithHostiles: visibleArea.getLocationsWithHostiles()) {
            if (locationsWithHostiles.getHostileEntitiesOnLocation().contains(targetEntity)) {
                return determineImmediateDirection(locationsWithHostiles.getCoordinates());
            }
        }
        return null;
    }

    private Direction determineImmediateDirection(GeographicCoordinate sourceCoordinates, GeographicCoordinate targetCoordinates) {
        return determineImmediateDirection(sourceCoordinates.getLongitude()-targetCoordinates.getLongitude(), sourceCoordinates.getLatitude()-targetCoordinates.getLatitude());
    }

    private Direction determineImmediateDirection(Coordinates relativeCoordinates) {
        return determineImmediateDirection(relativeCoordinates, false);
    }
    private Direction determineImmediateDirection(final Coordinates relativeCoordinates, boolean reverse) {
        final Direction direction = determineImmediateDirection(relativeCoordinates.getLongitude(), relativeCoordinates.getLatitude());
        if (reverse) {
            return direction.getOppositeDirection();
        } else {
            return direction;
        }
    }
    private Direction determineImmediateDirection(final int longitude, final int latitude) {
        byte direction = Direction.CENTER;
        if (latitude > 0) direction |= Direction.NORTH; else if (latitude < 0) direction |= Direction.SOUTH;
        if (longitude > 0) direction |= Direction.EAST; else if (longitude < 0) direction |= Direction.WEST;
        return new Direction(direction);
    }

    private char getMemoryFragmentType(char memoryFragment) {
        return (char) (memoryFragment & 0xF0);
    }
    private char getMemoryFragment(char memoryType, Direction direction) {
        return (char) (memoryType | direction.getDirection());
    }

    public Set<Direction> determineOptimalAvoidDirections(Self self, Area visibleArea) {
        final Map<Direction, Set<HostileEntity>> avoidDirections = Maps.newHashMap();
        for (Location locationWithHostile : visibleArea.getLocationsWithHostiles()) {
            if (!locationWithHostile.getCoordinates().equals(CENTER_COORDINATES)) {
                final Direction avoidDirection = determineImmediateDirection(locationWithHostile.getCoordinates(), true);
                final List<Direction> possibleAvoidDirections = Lists.newArrayList();
                possibleAvoidDirections.add(avoidDirection);
                possibleAvoidDirections.addAll((avoidDirection.getAdjacentDirections()));
                for (final Direction possibleAvoidDirection: possibleAvoidDirections) {
                    if (avoidDirections.containsKey(possibleAvoidDirection)) {
                        avoidDirections.get(possibleAvoidDirection).addAll(locationWithHostile.getHostileEntitiesOnLocation());
                    } else {
                        avoidDirections.put(possibleAvoidDirection, locationWithHostile.getHostileEntitiesOnLocation());
                    }
                }
            }
        }
        final Set<Direction> priorityDirections = Sets.newHashSet();
        int directionPriority = 0;
        for (final Map.Entry<Direction, Set<HostileEntity>> avoidDirectionsEntry: avoidDirections.entrySet()) {
            if (avoidDirectionsEntry.getValue().size() > directionPriority) {
                directionPriority = avoidDirectionsEntry.getValue().size();
                priorityDirections.clear();
            }
            if (avoidDirectionsEntry.getValue().size() == directionPriority) {
                priorityDirections.add(avoidDirectionsEntry.getKey());
            }
        }
        return priorityDirections;
    }

    private boolean shouldMarkWithEnemyScent(Self self, Area visibleArea) {
        //TODO: make
        return false;
    }
    private Entity determineTargetEntity(Self self, Area visibleArea) {
        //TODO: make
        if (!visibleArea.getCurrentLocation().getHostileEntitiesOnLocation().isEmpty()) {
            return visibleArea.getCurrentLocation().getHostileEntitiesOnLocation().iterator().next();
        }
        for (final Location locationWithHostileEntity: visibleArea.getLocationsWithHostiles()) {
            for (final HostileEntity hostileEntity: locationWithHostileEntity.getHostileEntitiesOnLocation()) {
                return hostileEntity;
            }
        }
        return null;
    }
    private boolean shouldStayAndFight(Self self, Area visibleArea) {
        //TODO: make
        return true;
    }

    @Override
    public Color getPreferredColor() {
        return new Color(0,255,0);
    }

}
*/
