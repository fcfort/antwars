package dk.antwars.core.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dk.antwars.core.ant.Ant;
import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.ant.species.QueenSpecies;
import dk.antwars.core.ant.species.Species;
import dk.antwars.core.game.gui.GameWindow;
import dk.antwars.core.game.mechanics.World;
import dk.antwars.core.game.mechanics.external.*;
import dk.antwars.core.game.mechanics.internal.GameEntity;
import dk.antwars.core.game.mechanics.internal.GameLocation;
import dk.antwars.core.game.mechanics.internal.GameTeam;
import dk.antwars.core.game.mechanics.internal.actions.*;
import dk.antwars.core.util.CoordinatesUtils;
import dk.antwars.core.util.RandomUtils;
import dk.antwars.core.util.WorldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class Game {

	private Logger logger = LoggerFactory.getLogger(Game.class);

    private int time;
    private int idGenerator;

	private final GameConfiguration gameConfiguration;
	private final World world;

    private Set<GameEntity> gameEntities;
    private Map<Entity, GameEntity> gameEntityMap;

    private final List<GameTeam> gameTeams;

    private boolean closeRequested;
    private GameWindow gameWindow;

    private final Semaphore gameSemaphore = new Semaphore(0);
    private final Semaphore paintSemaphore = new Semaphore(0);

    public Game(final GameConfiguration gameConfiguration) {
		this.gameConfiguration = gameConfiguration;
		this.world = WorldUtils.createWorldDefinition(this, gameConfiguration);
		this.gameTeams = Lists.newArrayList();
        this.gameEntities = Sets.newHashSet();
        this.gameEntityMap = Maps.newHashMap();
    }

	public World getWorld() {
		return this.world;
	}

    private void initializeTeams(GameConfiguration configuration) throws IllegalAccessException, InstantiationException {
        final List<Class<Ant>> antRepository = configuration.getAntRepository();
        for (int i = 0; i < antRepository.size(); i++) {
            final Class<Ant> antClass = antRepository.get(i);
            final Ant ant = antClass.newInstance();
            final Color selectedColor;
            if (colorIsInUse(getGameTeams(), ant.getPreferredColor())) {
                selectedColor = createRandomColor();
            } else {
                selectedColor = ant.getPreferredColor();
            }
            getGameTeams().add(new GameTeam(antClass, selectedColor));
        }
        for (final GameTeam team: getGameTeams()) {
            final GameEntity gameEntity = createEntity(team.getAntClass(), new QueenSpecies(), team);
            addGameEntity(gameEntity, WorldUtils.getRandomCoordinate(getWorld()));
            gameEntity.getGameLocation().getFoodAtomic().addAndGet(10);
        }
    }

    private Color createRandomColor() {
        int red = RandomUtils.getInstance().nextInt(255);
        int green = RandomUtils.getInstance().nextInt(255);
        int blue = RandomUtils.getInstance().nextInt(255);
        return new Color(red, green, blue);
    }

    private boolean colorIsInUse(List<GameTeam> teams, Color preferredColor) {
        for (final GameTeam team: teams) {
            if (team.getColor().equals(preferredColor)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWinnerDetermined() {
        return time >= 10000;
    }

    public void startGame() {
        try {
            initializeTeams(gameConfiguration);
            this.gameWindow = new GameWindow(this);
            while (!isWinnerDetermined()) {
                if (closeRequested) {
                    break;
                }
                processTurn();
            }
            logger.info("Game finished after " + time + " turns");
            for (final GameTeam team: getGameTeams()) {
                logger.info(team.toString());
            }
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
		System.exit(0);
	}

    private void evolveWorld() {
        final Set<Integer> generatedFood = WorldUtils.generateFood(gameConfiguration);
        for (final Integer food: generatedFood) {
            final Coordinates randomCoordinate = WorldUtils.getRandomCoordinate(getWorld());
            getWorld().getGameLocation(randomCoordinate).getFoodAtomic().addAndGet(food);
        }
    }

    private void processTurn() {
        time++;
        if (time % 5 == 0) {
            evolveWorld();
        }
        final List<GameEntity> gameEntitiesForProcessing = Lists.newArrayList(getGameEntities());
        for (final GameEntity gameEntity: gameEntitiesForProcessing) {
            gameEntity.resetActionPoints();
        }
        while (!gameEntitiesForProcessing.isEmpty()) {
            final Iterator<GameEntity> gameEntitiesForProcessingIterator = gameEntitiesForProcessing.iterator();
            while (gameEntitiesForProcessingIterator.hasNext()) {
                final GameEntity gameEntity = gameEntitiesForProcessingIterator.next();
                if (gameEntity.getHealth() > 0) {
                    final boolean actionPerformed = processAction(gameEntity, processEntity(gameEntity));
                    if (actionPerformed) {
                        gameEntity.useActionPoint();
                        if (!gameEntity.hasActionPointsLeft()) {
                            gameEntitiesForProcessingIterator.remove();
                        }
                    } else {
                        gameEntitiesForProcessingIterator.remove();
                    }
                } else {
                    gameEntitiesForProcessingIterator.remove();
                }
            }
            getPaintSemaphore().release();
            gameWindow.update();
            getGameSemaphore().acquireUninterruptibly();
        }
    }

    private Action processEntity(final GameEntity gameEntity) {
        return gameEntity.getAnt().act(gameEntity.getSelf(), extractArea(gameEntity));
    }

    private Area extractArea(final GameEntity gameEntity) {
        final Coordinates centerCoordinates = gameEntity.getGameLocation().getCoordinates();
        final int sightDistance = gameEntity.getSpecies().getSight();
        final Set<Coordinates> visibleCoordinates = collectVisibleCoordinates(centerCoordinates, sightDistance);
        final Map<Coordinates, Location> coordinatesLocationMap = Maps.newHashMap();
        for (final Coordinates coordinates: visibleCoordinates) {
            final Coordinates relativeCoordinates = CoordinatesUtils.calculateRelativeCoordinates(centerCoordinates, coordinates, getWorld());
            coordinatesLocationMap.put(relativeCoordinates, getWorld().getGameLocation(coordinates).getLocation(gameEntity));
        }
        return new Area(coordinatesLocationMap);
    }

    private Set<Coordinates> collectVisibleCoordinates(Coordinates centerCoordinates, int sightDistance) {
        final Set<Coordinates> visibleCoordinates = Sets.newHashSet();
        for (int longitude = -sightDistance; longitude <= sightDistance; longitude++) {
            for (int latitude = -sightDistance; latitude <= sightDistance; latitude++) {
                final int longitudeCoordinate = CoordinatesUtils.correctCoordinate(centerCoordinates.getLongitude() + longitude, getWorld().getNumberOfWorldLongitudes());
                final int latitudeCoordinate = CoordinatesUtils.correctCoordinate(centerCoordinates.getLatitude() + latitude, getWorld().getNumberOfWorldLatitudes());
                visibleCoordinates.add(new Coordinates(longitudeCoordinate, latitudeCoordinate));
            }
        }
        return visibleCoordinates;
    }

    private boolean processAction(final GameEntity gameEntity, final Action action) {
        if (action != null) {
            if (action instanceof Move) {
                return processMove(gameEntity, (Move) action);
            } else if (action instanceof Drag) {
                return processDrag(gameEntity, (Drag) action);
            } else if (action instanceof Bite) {
                return processBite(gameEntity, (Bite) action);
            } else if (action instanceof Eat) {
                return processEat(gameEntity, (Eat) action);
            } else if (action instanceof Hatch) {
                return processHatch(gameEntity, (Hatch) action);
            } else if (action instanceof Mark) {
                return processMark(gameEntity, (Mark) action);
            }
        }
        return false;
    }

    private boolean processMark(GameEntity gameEntity, Mark action) {
        if (action.getScent() != null) {
            gameEntity.getGameLocation().addPheromone(new Pheromone(action.getScent(), this.getTime(), 100));
            return true;
        }
        return false;
    }

    private boolean processHatch(GameEntity gameEntity, Hatch action) {
        if (action.getSpecies() != null) {
            final int hatchlingCost = action.getSpecies().getCost();
            final GameLocation gameLocation = gameEntity.getGameLocation();
            final Coordinates coordinates = gameLocation.getCoordinates();
            final int foodAtLocation = gameLocation.getFoodAtomic().get();
            if (foodAtLocation >= hatchlingCost) {
                gameLocation.getFoodAtomic().set(foodAtLocation - hatchlingCost);
                final GameEntity newGameEntity = createEntity((Class<Ant>) gameEntity.getAnt().getClass(), action.getSpecies(), gameEntity.getGameTeam());
                addGameEntity(newGameEntity, coordinates);
                return true;
            }
        }
        return false;
    }

    private boolean processEat(final GameEntity gameEntity, final Eat action) {
        final GameLocation gameLocation = gameEntity.getGameLocation();
        final int foodAtLocation = gameLocation.getFoodAtomic().get();
        if (foodAtLocation > 0) {
            gameLocation.getFoodAtomic().set(foodAtLocation - 1);
            gameEntity.heal(5);
            return true;
        }
        return false;
    }

    private boolean processBite(final GameEntity gameEntity, final Bite action) {
        if (action.getTargetEntity() != null) {
            final GameEntity targetGameEntity = getGameEntityMap().get(action.getTargetEntity());
            final Coordinates gameEntityCoordinates = gameEntity.getGameLocation().getCoordinates();
            final Coordinates targetGameEntityCoordinates = targetGameEntity.getGameLocation().getCoordinates();
            final int targetDistance = CoordinatesUtils.calculateDistance(gameEntityCoordinates, targetGameEntityCoordinates, getWorld());
            if (targetDistance <= 1) {
                final int attackDamage = gameEntity.getSpecies().getStrength();
                targetGameEntity.damage(attackDamage);
                if (targetGameEntity.getHealth() <= 0) {
                    targetGameEntity.getGameLocation().getFoodAtomic().getAndAdd(targetGameEntity.getSpecies().getCost() / 2);
                    removeEntity(targetGameEntity);
                }
                return true;
            }
        }
        return false;
    }

    private boolean processMove(final GameEntity gameEntity, final Move action) {
        if (action.getDirection() != null && action.getDirection().isValid()) {
            moveEntity(gameEntity, action.getDirection());
            return true;
        }
        return false;
    }

    private boolean processDrag(final GameEntity gameEntity, final Drag action) {
        if (action.getDirection() != null && action.getDirection().isValid()) {
            final GameLocation oldGameLocation = gameEntity.getGameLocation();
            final Coordinates oldCoordinates = oldGameLocation.getCoordinates();
            final Coordinates newCoordinates = CoordinatesUtils.calculateCoordinates(oldCoordinates, action.getDirection(), getWorld());
            final GameLocation newGameLocation = getWorld().getGameLocation(newCoordinates);

            final int foodAtOldLocation = oldGameLocation.getFoodAtomic().get();
            final int foodAtNewLocation = newGameLocation.getFoodAtomic().get();
            final int delta = Math.min(gameEntity.getSpecies().getStrength(), foodAtOldLocation);
            if (delta > 0) {
                oldGameLocation.getFoodAtomic().set(foodAtOldLocation - delta);
                newGameLocation.getFoodAtomic().set(foodAtNewLocation + delta);
            }

            moveEntity(gameEntity, action.getDirection());
            return true;
        }
        return false;
    }

    private GameEntity createEntity(Class<Ant> antClass, final Species species, final GameTeam team) {
        try {
            return new GameEntity(idGenerator++, antClass.newInstance(), species, team);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addGameEntity(GameEntity gameEntity, Coordinates coordinates) {
        final GameLocation newGameLocation = getWorld().getGameLocation(coordinates);
        newGameLocation.getGameEntities().add(gameEntity);
        gameEntity.setGameLocation(newGameLocation);
        gameEntity.getGameTeam().addGameEntity(gameEntity);
        getGameEntities().add(gameEntity);
        getGameEntityMap().put(gameEntity.getHostileEntity(), gameEntity);
        getGameEntityMap().put(gameEntity.getFriendlyEntity(), gameEntity);
    }

    private void removeEntity(GameEntity gameEntity) {
        gameEntity.getGameLocation().getGameEntities().remove(gameEntity);
        gameEntity.getGameTeam().removeGameEntity(gameEntity);
        getGameEntities().remove(gameEntity);
        getGameEntityMap().remove(gameEntity.getHostileEntity());
        getGameEntityMap().remove(gameEntity.getFriendlyEntity());
    }

    public void closeRequested() {
		this.closeRequested = true;
	}

    private void moveEntity(GameEntity gameEntity, Direction direction) {
        final GameLocation currentGameLocation = gameEntity.getGameLocation();
        final Coordinates currentCoordinates = currentGameLocation.getCoordinates();
        final Coordinates newCoordinates = CoordinatesUtils.calculateCoordinates(currentCoordinates, direction, getWorld());
        removeEntity(gameEntity);
        addGameEntity(gameEntity, newCoordinates);

        currentGameLocation.setScentColor(gameEntity.getGameTeam().getColor());

/*
            final Pheromone pheromone = new Pheromone(Pheromone.Scent.Trace, 1, this, gameEntity);
            currentGameLocation.addPheromone(pheromone, gameEntity.getGameTeam());
*/

    }

    public int getTime() {
        return time;
    }

    public List<GameTeam> getGameTeams() {
        return gameTeams;
    }

    public Set<GameEntity> getGameEntities() {
        return gameEntities;
    }

    public Map<Entity, GameEntity> getGameEntityMap() {
        return gameEntityMap;
    }

    public Semaphore getGameSemaphore() {
        return gameSemaphore;
    }

    public Semaphore getPaintSemaphore() {
        return paintSemaphore;
    }
}