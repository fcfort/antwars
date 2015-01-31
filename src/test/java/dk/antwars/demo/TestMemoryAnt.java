//package dk.antwars.demo;
//
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import dk.antwars.core.ant.Ant;
//import dk.antwars.core.ant.species.QueenSpecies;
//import dk.antwars.core.game.mechanics.external.Entity;
//import dk.antwars.core.ant.Self;
//import dk.antwars.core.game.mechanics.internal.GameTeam;
//import dk.antwars.core.game.mechanics.external.Area;
//import dk.antwars.core.game.mechanics.external.Coordinates;
//import dk.antwars.core.game.mechanics.external.Pheromone;
//import dk.antwars.core.game.mechanics.WorldObject;
//import dk.antwars.core.game.mechanics.internal.GameEntity;
//import dk.antwars.core.game.mechanics.external.Direction;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.Map;
//import java.util.Set;
//
//public class TestMemoryAnt {
//
//    @Test
//    public void TestDetermineFleeDirection1() {
//
//        final MemoryAnt memoryAnt = new MemoryAnt();
//        final GameEntity gameEntity = new GameEntity(0, memoryAnt, new QueenSpecies(), new GameTeam(Ant.class, memoryAnt.getPreferredColor()));
//
//        final Set<Entity> entities = Sets.newHashSet();
//
//        final Entity entity1 = new Entity(new GameEntity(1, memoryAnt, new QueenSpecies(), new GameTeam(Ant.class, memoryAnt.getPreferredColor())));
//        final Entity entity2 = new Entity(new GameEntity(2, memoryAnt, new QueenSpecies(), new GameTeam(Ant.class, memoryAnt.getPreferredColor())));
//        entities.add(entity1);
//        entities.add(entity2);
//
//        //North
//        Coordinates coordinates1 = new Coordinates(1, 0);
//
//        //East
//        Coordinates coordinates2 = new Coordinates(0, 1);
//
//        final Map<Coordinates, Set<WorldObject>> worldObjects = Maps.newHashMap();
//        worldObjects.put(coordinates1, Sets.<WorldObject>newHashSet(entity1));
//        worldObjects.put(coordinates2, Sets.<WorldObject>newHashSet(entity2));
//        final Map<Coordinates, Set<Pheromone>> pheromones = Maps.newHashMap();
//        final Area area = new Area(gameEntity, worldObjects, pheromones);
//
//        final Self self = new Self(gameEntity);
//        final Direction direction = memoryAnt.determineFleeDirection(self, area, entities);
//        Assert.assertTrue(direction.getDirection() == Direction.SOUTHWEST);
//    }
//
//    @Test
//    public void TestDetermineFleeDirection2() {
//
//        final MemoryAnt memoryAnt = new MemoryAnt();
//        final GameEntity gameEntity = new GameEntity(0, memoryAnt, new QueenSpecies(), new GameTeam(Ant.class, memoryAnt.getPreferredColor()));
//
//        final Set<Entity> entities = Sets.newHashSet();
//
//        final Entity entity1 = new Entity(new GameEntity(1, memoryAnt, new QueenSpecies(), new GameTeam(Ant.class, memoryAnt.getPreferredColor())));
//        final Entity entity2 = new Entity(new GameEntity(2, memoryAnt, new QueenSpecies(), new GameTeam(Ant.class, memoryAnt.getPreferredColor())));
//        final Entity entity3 = new Entity(new GameEntity(3, memoryAnt, new QueenSpecies(), new GameTeam(Ant.class, memoryAnt.getPreferredColor())));
//        entities.add(entity1);
//        entities.add(entity2);
//        entities.add(entity3);
//
//        //North
//        Coordinates coordinates1 = new Coordinates(1, 0);
//
//        //East
//        Coordinates coordinates2 = new Coordinates(0, 1);
//
//        //West
//        Coordinates coordinates3 = new Coordinates(0, -1);
//
//        final Map<Coordinates, Set<WorldObject>> worldObjects = Maps.newHashMap();
//        worldObjects.put(coordinates1, Sets.<WorldObject>newHashSet(entity1));
//        worldObjects.put(coordinates2, Sets.<WorldObject>newHashSet(entity2));
//        worldObjects.put(coordinates3, Sets.<WorldObject>newHashSet(entity3));
//        final Map<Coordinates, Set<Pheromone>> pheromones = Maps.newHashMap();
//        final Area area = new Area(gameEntity, worldObjects, pheromones);
//
//        final Self self = new Self(gameEntity);
//        final Direction direction = memoryAnt.determineFleeDirection(self, area, entities);
//        Assert.assertTrue(direction.getDirection() == Direction.SOUTHWEST || direction.getDirection() == Direction.SOUTHEAST);
//    }
//
//}
