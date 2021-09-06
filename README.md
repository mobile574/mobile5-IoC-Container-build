# What is it

Basic IoC container written in pure Kotlin.

# How to run project

Checkout project and open it in InteliJ IDEA. Open [Main.kt](src/main/kotlin/Main.kt) file to see a sample application.

To run tests open Gradle View and run `Tasks - Verification - test` task.

# Features

- Constructor injection
- Nested dependency resolution
- Dependency verification
- Circular dependency detection
- Create instance of type with parameterless constructor (without need to register)
- Supports `@Inject` annotation
- Supports Kotlin Objects
- Supports `@Singleton` annotation
- Error handling (around access modifiers, abstract classes and interfaces, etc.)  

# Examples

Getting an instance of the Container:

```kotlin
val container = DependencyContainer.getInstance()
```

Register instance:

```kotlin
container.registerBinding(Foo::class, FooImpl::class)
```

Resolve class with parameterless constructor:

```kotlin
class FooImpl: Foo

val container = DependencyContainer.getInstance()

// No registration in contaier is required for class with parameterless constructor
val instance = container.resolveBinding<FooImpl>()
println(instance) // FooImpl
```

Resolve binding for the interface:

```kotlin
interface Foo
class FooImpl: Foo

val container = DependencyContainer.getInstance()
// Given interface Foo return FooImpl instance
container.registerBinding(Foo::class, FooImpl::class)

val instance = container.resolveBinding<Foo>()
println(instance) // FooImpl
```

Retrieve class with nested dependency:

```kotlin
interface Foo
class FooImpl: Foo
class Bar(val foo:FooImpl)

val container = DependencyContainer.getInstance()
container.registerBinding(Foo::class, FooImpl::class)

val instance = container.resolveBinding<Bar>()
println(instance) // Bar
```

Inject singleton:

```kotlin
@Singleton
class Foo

val instance1 = container.resolveBinding<Foo>()
val instance2 = container.resolveBinding<Foo>()

insatnce1 === instance2
```

More examples can be found in [DependencyContainerTest](src/test/kotlin/DependencyContainerTest.kt) class.

## Dependency verification

It is possible to verify all bindings in the container. This option is useful to check if all bindings can be resolved 
correctly.

```kotlin
val container = DependencyContainer.getInstance()
...
container.verifyBindings()

```


