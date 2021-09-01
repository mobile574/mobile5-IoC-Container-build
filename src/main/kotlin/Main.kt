fun main() {
    val container = DependencyContainer.getInstance()
    container.registerBinding(Foo::class, FooImpl::class)
    container.registerBinding(Bar::class)

    val instance = container.resolveBinding<Bar>()
    println(instance) // Bar
}

interface Foo
class FooImpl : Foo
class Bar(val foo: FooImpl)
