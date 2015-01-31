package dk.antwars.demo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dk.antwars.core.ant.Ant;
import dk.antwars.core.ant.Self;
import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.ant.species.*;
import dk.antwars.core.game.mechanics.external.*;
import dk.antwars.core.game.mechanics.internal.actions.*;
import dk.antwars.core.util.RandomUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimplAnt implements Ant {

    private static final Pheromone.Scent QUEEN_SCENT = Pheromone.Scent.Yellow;
    private static final Pheromone.Scent ENEMY_SCENT = Pheromone.Scent.Red;
    private static final Pheromone.Scent FOOD_SCENT = Pheromone.Scent.Green;
    private static final Pheromone.Scent EGG_SCENT = Pheromone.Scent.Blue;

    private static final char MEMORY_SCOUT      = 0x00; //0x0<Direction> scout direction
    private static final char MEMORY_FLEE       = 0x10; //0x1<Direction> flee direction
    private static final char MEMORY_RETURN     = 0x20; //0x2<Direction> return direction
    private static final char MEMORY_DRAG       = 0x30; //0x3<Direction> drag direction
    private static final char MEMORY_ATTACK     = 0x40; //0x4<Direction> attack direction

    private static final char MEMORY_MARK_FRIENDLY_QUEEN    = 0x50;
    private static final char MEMORY_MARK_HOSTILE_QUEEN     = 0x51;
    private static final char MEMORY_MARK_ENEMY             = 0x52;
    private static final char MEMORY_MARK_FOOD              = 0x53;
    private static final char MEMORY_MARK_EGG               = 0x54;

    private static final Coordinates CENTER_COORDINATES = new Coordinates(0,0);

    private final static int positionLongitudeMemoryIndex = 0;
    private final static int positionLatitudeMemoryIndex = 1;
    private final static int foodLongitudeMemoryIndex = 2;
    private final static int foodLatitudeMemoryIndex = 3;

    @Override
    public Action act(final Self self, Area visibleArea) {
        if (self.getSpecies().isQueen()) {
            return actAsQueen(self, visibleArea);
        } else {
            final boolean standingOnQueen = isStandingOnQueen(visibleArea);
            if (standingOnQueen) {
                resetCoordinates(self);
            }
            final Coordinates myCoordinates = getMyCoordinates(self);
            final Action action = actAsDrone(self, visibleArea, myCoordinates);
            if (action != null) {
                if (isMovementAction(action)) {
                    final Direction movementDirection = getMovementDirection(action);
                    adjustCoordinates(self, myCoordinates, movementDirection);
                }
                return action;
            }
        }
        return new Mark(Pheromone.Scent.White);
    }

    private void adjustCoordinates(final Self self, final Coordinates myCoordinates, final Direction movementDirection) {
        int longitude = myCoordinates.getLongitude();
        int latitude = myCoordinates.getLatitude();
        final byte direction = movementDirection.getDirection();
        if ((direction & Direction.EAST) == Direction.EAST) longitude++; else if ((direction & Direction.WEST) == Direction.WEST) longitude--;
        if ((direction & Direction.NORTH) == Direction.NORTH) latitude++; else if ((direction & Direction.SOUTH) == Direction.SOUTH) latitude--;
        self.getMemories()[positionLongitudeMemoryIndex] = longitude;
        self.getMemories()[positionLatitudeMemoryIndex] = latitude;
    }

    private Direction getMovementDirection(Action action) {
        if (action instanceof Move) {
            return ((Move) action).getDirection();
        }
        if (action instanceof Drag) {
            return ((Drag) action).getDirection();
        }
        return null;
    }

    private boolean isMovementAction(Action action) {
        return (action instanceof Move || action instanceof Drag);
    }

    private Action actAsDrone(Self self, Area visibleArea, Coordinates myCoordinates) {
        if (visibleArea.getLocationsWithHostiles().size() > 0) {
            final Entity targetEntity = determineTargetEntity(self, visibleArea);
            if (targetEntity != null) {
                final Direction attackDirection = determineAttackDirection(visibleArea, targetEntity);
                if (attackDirection.getDirection() == Direction.CENTER) {
                    return new Bite(targetEntity);
                } else {
                    return new Move(attackDirection);
                }
            }
        }
        if (isStandingOnFood(visibleArea) && !isStandingOnQueen(visibleArea)) {
            final Direction returnDirection = determineReturnDirection(self, visibleArea, myCoordinates);
            return new Drag(returnDirection);
        } else {
            final Coordinates bestFoodCoordinates = determineBestFoodCoordinates(self, visibleArea, myCoordinates);
            if (bestFoodCoordinates != null) {
                final Direction moveDirection = determineImmediateDirection(bestFoodCoordinates);
                return new Move(moveDirection);
            }
            final Direction scoutDirection = determineScoutDirection(self, visibleArea, myCoordinates);
            if (scoutDirection != null) {
                return new Move(scoutDirection);
            }
        }
        return null;
    }

    private int calculateDistanceBetweenCoordinates(Coordinates sourceCoordinates, Coordinates targetCoordinates) {
        final int latitudeDistance = targetCoordinates.getLatitude()-sourceCoordinates.getLatitude();
        final int longitudeDistance = targetCoordinates.getLongitude()-sourceCoordinates.getLongitude();
        return longitudeDistance + latitudeDistance;
    }

    private void resetCoordinates(Self self) {
        final int[] memories = self.getMemories();
        memories[positionLongitudeMemoryIndex] = 0;
        memories[positionLatitudeMemoryIndex] = 0;
    }

    public Coordinates getMyCoordinates(final Self self) {
        final int[] memories = self.getMemories();
        final int longitude = memories[positionLongitudeMemoryIndex];
        final int latitude = memories[positionLatitudeMemoryIndex];
        return new Coordinates(longitude, latitude);
    }

    public Coordinates getFoodCoordinates(final Self self) {
        final int[] memories = self.getMemories();
        final int longitude = memories[foodLongitudeMemoryIndex];
        final int latitude = memories[foodLatitudeMemoryIndex];
        return new Coordinates(longitude, latitude);
    }

    private Direction determineAttackDirection(Area visibleArea, Entity targetEntity) {
        if (visibleArea.getCurrentLocation().getHostileEntities().contains(targetEntity)) {
            return determineImmediateDirection(visibleArea.getCurrentLocation().getCoordinates());
        }
        for (final Location locationsWithHostiles: visibleArea.getLocationsWithHostiles()) {
            if (locationsWithHostiles.getHostileEntities().contains(targetEntity)) {
                return determineImmediateDirection(locationsWithHostiles.getCoordinates());
            }
        }
        return null;
    }

    private Coordinates determineBestFoodCoordinates(final Self self, final Area visibleArea, final Coordinates myCoordinates) {
        final Coordinates foodCoordinates = getFoodCoordinates(self);
        for (final Location locationWithFood : visibleArea.getLocationsWithFood()) {
            final Coordinates locationWithFoodCoordinates = locationWithFood.getCoordinates();
            final Coordinates coordinatesOfLocationWithFood = calculateAdditiveCoordinates(myCoordinates, locationWithFoodCoordinates);
            if (!CENTER_COORDINATES.equals(coordinatesOfLocationWithFood)) {
                if (locationWithFood.getFood() > locationWithFood.getFriendlyEntities().size()) {
                    if (calculateDistanceBetweenCoordinates(CENTER_COORDINATES, coordinatesOfLocationWithFood) > calculateDistanceBetweenCoordinates(CENTER_COORDINATES, foodCoordinates)) {
                        self.getMemories()[foodLongitudeMemoryIndex] = coordinatesOfLocationWithFood.getLongitude();
                        self.getMemories()[foodLatitudeMemoryIndex] = coordinatesOfLocationWithFood.getLatitude();
                    }
                    return locationWithFoodCoordinates;
                }
            }
        }
        if (myCoordinates.equals(foodCoordinates)) {
            self.getMemories()[foodLongitudeMemoryIndex] = CENTER_COORDINATES.getLongitude();
            self.getMemories()[foodLatitudeMemoryIndex] = CENTER_COORDINATES.getLatitude();
            return null;
        }
        if (!foodCoordinates.equals(CENTER_COORDINATES)) {
            final Coordinates coordinatesOfLocationWithFood = calculateRelativeCoordinates(myCoordinates, foodCoordinates);
            return coordinatesOfLocationWithFood;
        }
        return null;
    }

    private Direction determineScoutDirection(Self self, Area visibleArea, final Coordinates myCoordinates) {

        byte[] directionsCount = new byte[0xF];
        final int sightDistance = self.getSpecies().getSight();
        if (sightDistance > 0) {
            for (int longitude = -sightDistance; longitude <= sightDistance; longitude+=sightDistance) {
                for (int latitude = -sightDistance; latitude <= sightDistance; latitude+=sightDistance) {
                    final Coordinates coordinates = new Coordinates(longitude, latitude);
                    if (!coordinates.equals(CENTER_COORDINATES)) {
                        final Location locationByCoordinates = visibleArea.getLocationByCoordinates(coordinates);
                        if (locationByCoordinates.getScent() == null) {
                            if (coordinates.getLongitude() > 0) {
                                directionsCount[Direction.EAST]++;
                            } else if (coordinates.getLongitude() < 0){
                                directionsCount[Direction.WEST]++;
                            }
                            if (coordinates.getLatitude() > 0) {
                                directionsCount[Direction.NORTH]++;
                            } else if (coordinates.getLatitude() < 0){
                                directionsCount[Direction.SOUTH]++;
                            }
                        }
                    }
                }
            }
            final Set<Direction> possibleScoutDirections = Sets.newHashSet();
            for (byte i = 0x0; i < 0xF; i++) {
                if (directionsCount[i] > 0) {
                    final Direction suggestedDirection = new Direction(i);
                    possibleScoutDirections.add(suggestedDirection);
                    possibleScoutDirections.addAll(suggestedDirection.getAdjacentDirections());
                }
            }

            if (possibleScoutDirections.size() > 0) {
                return RandomUtils.getRandomDirection(possibleScoutDirections.toArray(new Direction[possibleScoutDirections.size()]));
            }
        }

        {
            final Set<Direction> nonReturningDirections = Sets.newHashSet();
            final Direction returnDirection = determineReturnDirection(self, visibleArea, myCoordinates);
            if (returnDirection.getDirection() == Direction.CENTER) {
                nonReturningDirections.addAll(Direction.getPossibleDirections());
            } else {
                final Direction nonReturningDirection = returnDirection.getOppositeDirection();
                nonReturningDirections.add(nonReturningDirection);
                final Set<Direction> adjacentDirections = nonReturningDirection.getAdjacentDirections();
                for (final Direction adjacentDirection: adjacentDirections) {
                    nonReturningDirections.add(adjacentDirection);
                    nonReturningDirections.addAll(adjacentDirection.getAdjacentDirections());
                }
            }
            return RandomUtils.getRandomDirection(nonReturningDirections.toArray(new Direction[nonReturningDirections.size()]));
        }

/*
        final Set<Direction> unexploredDirections = Sets.newHashSet(Direction.getPossibleDirections());

        for (final Direction direction : Direction.getPossibleDirections()) {
            final Coordinates coordinates = calculateCoordinate(direction);
            final Location locationByCoordinates = visibleArea.getLocationByCoordinates(coordinates);
            if (locationByCoordinates != null) {
                if (locationByCoordinates.isMarkedBy(self.getTeam())) {
                    unexploredDirections.remove(direction);
                }
            }
        }

        if (unexploredDirections.size() > 0) {
            for (Location location: visibleArea.getLocationsWithEntities()) {
                unexploredDirections.remove(determineImmediateDirection(location.getCoordinates()));
            }
        }

        if (unexploredDirections.size() > 0) {
            return RandomUtils.getRandomDirection(unexploredDirections.toArray(new Direction[unexploredDirections.size()]));
        } else {
            final Set<Direction> nonReturningDirections = Sets.newHashSet();
            final Direction returnDirection = determineReturnDirection(self, visibleArea, myCoordinates);
            if (returnDirection.getDirection() == Direction.CENTER) {
                nonReturningDirections.addAll(Direction.getPossibleDirections());
            } else {
                final Direction nonReturningDirection = returnDirection.getOppositeDirection();
                nonReturningDirections.add(nonReturningDirection);
                nonReturningDirections.addAll(nonReturningDirection.getAdjacentDirections());
            }
            return RandomUtils.getRandomDirection(nonReturningDirections.toArray(new Direction[nonReturningDirections.size()]));
        }
    */
    }

    private Coordinates calculateCoordinate(Direction direction) {
        int longitude = 0, latitude = 0;
        final byte directionByte = direction.getDirection();
        if ((directionByte & Direction.EAST) == Direction.EAST) longitude++; else if ((directionByte & Direction.WEST) == Direction.WEST) longitude--;
        if ((directionByte & Direction.NORTH) == Direction.NORTH) latitude++; else if ((directionByte & Direction.SOUTH) == Direction.SOUTH) latitude--;
        return new Coordinates(longitude, latitude);
    }

    private boolean hasScent(final Set<Pheromone> pheromones, final Pheromone.Scent scent) {
        for (final Pheromone pheromone : pheromones) {
            if (pheromone.getScent().equals(scent)) {
                return true;
            }
        }
        return false;
    }

    private Direction determineReturnDirection(final Self self, final Area visibleArea, Coordinates myCoordinates) {
        final Coordinates targetCoordinates = calculateRelativeCoordinates(myCoordinates, CENTER_COORDINATES);
        return determineImmediateDirection(targetCoordinates);
    }

    private Coordinates calculateAdditiveCoordinates(Coordinates originCoordinates, Coordinates relativeCoordinates) {
        return new Coordinates(originCoordinates.getLongitude()+relativeCoordinates.getLongitude(), originCoordinates.getLatitude()+relativeCoordinates.getLatitude());
    }

    private Coordinates calculateRelativeCoordinates(Coordinates from, Coordinates to) {
        return new Coordinates(to.getLongitude()-from.getLongitude(), to.getLatitude()-from.getLatitude());
    }

    private boolean isStandingOnFood(Area visibleArea) {
        return visibleArea.getCurrentLocation().getFood() > 0;
    }

    private boolean isStandingOnQueen(Area visibleArea) {
        final Set<FriendlyEntity> friendlyEntitiesOnLocation = visibleArea.getCurrentLocation().getFriendlyEntities();
        for (final FriendlyEntity friendlyEntity: friendlyEntitiesOnLocation) {
            if (friendlyEntity.getSpecies() instanceof QueenSpecies) {
                return true;
            }
        }
        return false;
    }

    private final DroneSpecies standardDroneSpecies = new DroneSpecies(1, 1, 1);
    private Action actAsQueen(final Self self, Area visibleArea) {
        if (self.getDamage() > 5 && visibleArea.getCurrentLocation().getFood() > 0) {
            return new Eat();
        }
        if (visibleArea.getCurrentLocation().getFood() >= standardDroneSpecies.getCost()) {
            return new Hatch(standardDroneSpecies);
        }
        return new Sleep();
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

    private Entity determineTargetEntity(Self self, Area visibleArea) {
        if (!visibleArea.getCurrentLocation().getHostileEntities().isEmpty()) {
            return visibleArea.getCurrentLocation().getHostileEntities().iterator().next();
        }
        for (final Location locationWithHostileEntity: visibleArea.getLocationsWithHostiles()) {
            for (final HostileEntity hostileEntity: locationWithHostileEntity.getHostileEntities()) {
                return hostileEntity;
            }
        }
        return null;
    }

    @Override
    public Color getPreferredColor() {
        return new Color(0,255,0);
    }

}
