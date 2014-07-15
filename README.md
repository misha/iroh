[![Stories in Ready](https://badge.waffle.io/msoliter/iroh.png?label=ready&title=Ready)](https://waffle.io/msoliter/iroh)
# Iroh [![Build Status](https://travis-ci.org/msoliter/iroh.svg?branch=master)](https://travis-ci.org/msoliter/iroh) [![Coverage Status](https://img.shields.io/coveralls/msoliter/iroh.svg)](https://coveralls.io/r/msoliter/iroh?branch=master)

Iroh is a lightweight, principled dependency injection container. Its system is dead simple, employing only two Spring-inspired annotations with only the most basic options. Iroh is well-tested, comes with samples, provides a complete sample project, and implements optional **lazy injection** for injected components, a rare feature among DI containers.

Iroh is implemented using AspectJ. Check out the example POM [here](https://github.com/msoliter/iroh/blob/master/example/pom.xml) for details on how to integrate with Maven.

## Usage

```xml
<dependency>
    <groupId>com.github.msoliter</groupId>
    <artifactId>iroh</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Example

```java
/**
 * A service we wish test.
 */
@Component
public class NetworkServiceImpl implements NetworkService {

    /* A network-based API object that accesses production resources. */
    @Autowired
    private NetworkAPI api;

    public Object receive() throws IOException {
        return api.receive();
    }
}
```

```java
/**
 * A test configuration that renders NetworkService implementations unit testable.
 */
@Component
public class NetworkServiceTestConfiguration {

    /**
     * Overrides an existing NetworkAPI component just for unit testing. (The
     * production version of the type exists, just not in this brief example.)
     *
     * @return An instance of a stubbed NetworkAPI, for unit testing.
     */
    @Component(override = true)
    public NetworkAPI stubNetworkApi() {
        return new NetworkAPI() {           
            
            @Override
            public Object receive() {
                return new DummyResponse();
            }
        };
    }
}
```

```java
/**
 * A unit test that can inject a NetworkService for unit testing without 
 * accessing production resources.
 */
public class NetworkServiceTest {

    /* A network service, using the stubbed version of the NetworkAPI type. */
    @Autowired
    private NetworkService service;
    
    @Test
    public void test() throws IOException {
        Assert.assertEquals(new DummyResponse(), service.receive());
    }
}
```

## Annotations

### `@Component`

You may use the `@Component` annotation to register any *concrete* type as a template for dependency injection. All annotated types must have a zero-argument constructor.

Similarly, third-party types may be adapted into Iroh with an `@Component` placed on a method, indicating the method is a factory for the method's return type. Only `@Component`-annotated types may have `@Component`-annotated methods. Otherwise, they will be ignored by Iroh. There is no limitation on the return type of an `@Component`-annotated method.

Here's a copy-paste of the class definition, which explicitly describes the available options:

```java
/**
 * States that the annotated type should be injectable via its zero argument
 * constructor, or that the annotated method produces an object that should
 * be injectable by calling that method with no parameters.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    
    /* This component's qualifier, if its supertype has many implementations. */
    public String qualifier() default "";

    /* The scope of the component - 'singleton' or 'prototype'. */
    public Scope scope() default Scope.SINGLETON;
    
    /* Whether or not this component should override an existing definition of
     * a component with the same type. Primarily used for unit testing. */
    public boolean override() default false;
}
```

### `@Autowired`

You may use the `@Autowired` annotation on any field. The type containing dependencies doesn't need to have an `@Component` annotation. `@Autowired` annotations on constructors are on the drawing board, and will be implemented on request.

Due to its aspect-oriented nature, Iroh can also inject dependencies into any types with `@Autowired` annotations instantiated using normal Java allocation (the `new` keyword). Similarly, Iroh will happily inject static fields annotated with `@Autowired` upon the first instantiation of a type. This differs from some other containers, which forbid injection of static fields because their object graphs exclude the Java type system itself.

Here's a copy-paste of the class definition, which explicitly describes the available options:

```java
/**
 * Tells Iroh that the annotated field should be injected with an instance.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

    /* A qualifier value for disambiguating a field in the case of multiple
     * available implementations. */
    public String qualifier() default "";
    
    /* Whether or not this autowiring should be done lazily or not. If true, 
     * the annotated field won't be injected until the first time it's touched.
     * Otherwise, the annotated field will be injected on construction. */
    public boolean lazy() default false;
}
```

## Testing

Check out the repository, run ```mvn clean test```. Note that Iroh will never reach 100% code coverage because it throws exceptions during JVM initialization if certain errors are detected ([dependency cycles](https://github.com/msoliter/iroh/blob/master/src/test/java/com/github/msoliter/iroh/tests/DependencyCycleTest.java), [duplicate qualifiers](https://github.com/msoliter/iroh/blob/master/src/test/java/com/github/msoliter/iroh/tests/DuplicateQualifierTest.java), etc.) making it impossible to write unit tests for. But rest assured, the unit tests are written, the testing framework is simply insufficiently powerful enough to drive them.

## Background

I wrote Iroh after six months of learning and developing with dependency injection. I believe that it's a beautiful tool for software engineering, allowing for an elegant organization of dependencies without the management hassle. I learned a bit about Java's reflection library, aspect-oriented programming, and licensing/releasing open source software in the process.

**Should you use Iroh for work?** Probably not. I'm just one person, and cannot support a company if you choose to adopt Iroh to perform some heavy lifting.

**Should you use Iroh to learn dependency injection, and then move to a more stable container?** Absolutely. Iroh's no-frills approach will greatly accelerate your learning process.

**Can I contribute?** There's not much *to* contribute, aside from more examples and tests. All the features I wanted to implement are implemented. If you find a bug, feel free to report it, [open an issue](https://github.com/msoliter/iroh/issues/new), or better yet, write a test for it and open a pull request.

**Iroh?** My favorite TLA character. Learning dependency injection should be as easy as a cup of tea.

## Questions, Comments

If you have questions or comments, feel free to e-mail [me](https://github.com/msoliter).

