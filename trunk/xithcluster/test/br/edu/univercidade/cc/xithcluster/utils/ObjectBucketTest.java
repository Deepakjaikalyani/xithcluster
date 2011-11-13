package br.edu.univercidade.cc.xithcluster.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ObjectBucketTest {
	
	public abstract static class Pet {
		
		private String name;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public abstract String getOnomatopoeia();
		
		public boolean equals(Object o) {
			if (o == null)
				return false;
			
			if (!(o instanceof Pet))
				return false;
			
			Pet otherPet = (Pet) o;
			
			if (this.name == null) {
				if (otherPet.name != null) {
					return false;
				} else {
					return true;
				}
			}
			
			return this.name.equals(otherPet.name);
		}
	}
	
	public static class Dog extends Pet {
		
		@Override
		public String getOnomatopoeia() {
			return "bark";
		}
	}
	
	public static class Cat extends Pet {
		
		@Override
		public String getOnomatopoeia() {
			return "meow";
		}
	}
	
	private ObjectBucket<String, Pet> animalBucket;
	private Cat gorbi;
	private Dog flash;
	
	@Before
	public void setUp() throws Exception {
		animalBucket = new ObjectBucket<String, Pet>();
		
		animalBucket.register("bark", Dog.class);
		animalBucket.register("meow", Cat.class);
		
		flash = (Dog) animalBucket.retrieveFromBucket("bark");
		flash.setName("Flash");
		animalBucket.returnToBucket(flash);
		
		gorbi = (Cat) animalBucket.retrieveFromBucket("meow");
		gorbi.setName("Gorbi");
		animalBucket.returnToBucket(gorbi);
	}
	
	@Test(expected = RuntimeException.class)
	public void testRetrieveAnUnregisteredKey() {
		animalBucket.retrieveFromBucket("whinny");
	}
	
	@Test(expected = RuntimeException.class)
	public void testReturnAnObjectThatWasntRetrievedFromTheBucket() {
		Cat cat = new Cat();
		cat.setName("Charlie");
		
		animalBucket.returnToBucket(cat);
	}
	
	@Test
	public void testRetrieveANewObject() {
		Dog flash = (Dog) animalBucket.retrieveFromBucket("bark");
		Dog aBrandNewDog = (Dog) animalBucket.retrieveFromBucket("bark");
		
		assertFalse(flash.equals(aBrandNewDog));
	}
	
	@Test
	public void testReturnARetrievedObject() {
		Cat gorbi = (Cat) animalBucket.retrieveFromBucket("meow");
		animalBucket.returnToBucket(gorbi);
		
		gorbi = (Cat) animalBucket.retrieveFromBucket("meow");
		assertTrue("Gorbi".equals(gorbi.getName()));
	}
	
}
