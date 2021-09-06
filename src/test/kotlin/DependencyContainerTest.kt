
import exception.CircularDependencyException
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import type.BarImpl
import type.Chicken
import type.Egg
import type.Foo
import type.FooImpl
import type.KotlinObject
import type.TypeMarkedWithSingletonAnnotation
import type.TypeNotMarkedWithSingletonAnnotation
import type.TypeWithOneInterfaceDependency
import type.TypeWithOneNestedInterfaceDependency
import kotlin.reflect.full.primaryConstructor

@ExtendWith(MockitoExtension::class)
internal class DependencyContainerTest {

    private val constructorSelector = mock<ConstructorSelector>()

    private val subject = DependencyContainer.getNewInstance(constructorSelector)

    @Test
    fun `register fails when registering existing type`() {
        // given
        subject.registerBinding(FooImpl::class, Foo::class)

        // when
        val register = { subject.registerBinding(FooImpl::class, Foo::class) }

        // then
        invoking(register) shouldThrow IllegalArgumentException::class withMessage "Binding already exists: type.FooImpl"
    }

    @Test
    fun `resolve binding for registered type with parametrized constructor`() {
        // given
        subject.registerBinding(TypeWithOneInterfaceDependency::class)
        subject.registerBinding(Foo::class, FooImpl::class)
        whenever(constructorSelector.invoke(TypeWithOneInterfaceDependency::class)).thenReturn(TypeWithOneInterfaceDependency::class.primaryConstructor)
        whenever(constructorSelector.invoke(FooImpl::class)).thenReturn(FooImpl::class.primaryConstructor)

        // when
        val instance = subject.resolveBinding<TypeWithOneInterfaceDependency>()

        // then
        instance shouldBeInstanceOf TypeWithOneInterfaceDependency::class
    }

    @Test
    fun `resolve binding for non registered type with parameterless constructor`() {
        // given
        whenever(constructorSelector.invoke(FooImpl::class)).thenReturn(FooImpl::class.primaryConstructor)

        // when
        val instance = subject.resolveBinding<FooImpl>()

        // then
        instance shouldBeInstanceOf FooImpl::class
    }

    @Test
    fun `resolve binding for the registered interface`() {
        // given
        subject.registerBinding(Foo::class, FooImpl::class)

        // when
        val instance = subject.resolveBinding<Foo>()

        // then
        instance shouldBeInstanceOf FooImpl::class
    }

    @Test
    fun `resolve instance for registered nested binding`() {
        // given
        subject.registerBinding(Foo::class, FooImpl::class)
        whenever(constructorSelector.invoke(TypeWithOneNestedInterfaceDependency::class)).thenReturn(TypeWithOneNestedInterfaceDependency::class.primaryConstructor)
        whenever(constructorSelector.invoke(TypeWithOneInterfaceDependency::class)).thenReturn(TypeWithOneInterfaceDependency::class.primaryConstructor)
        whenever(constructorSelector.invoke(FooImpl::class)).thenReturn(FooImpl::class.primaryConstructor)

        // when
        val instance= subject.resolveBinding<TypeWithOneNestedInterfaceDependency>()

        // then
        instance shouldBeInstanceOf TypeWithOneNestedInterfaceDependency::class
    }

    @Test
    fun `resolve binding of Kotlin object`() {
        // given
        subject.registerBinding(KotlinObject::class)

        // when
        val instance = subject.resolveBinding<KotlinObject>()

        // then
        instance shouldBeEqualTo KotlinObject
    }

    @Test
    fun `resolve binding fails to resolve instance for unknown binding`() {
        // when
        val getInstance = { subject.resolveBinding<TypeWithOneInterfaceDependency>() }

        // then
        invoking(getInstance) shouldThrow IllegalArgumentException::class withMessage "No binding found for interface: type.Foo"
    }

    @Test
    fun `resolve binding fails to resolve instance for non-registered nested binding`() {
        // given
        subject.registerBinding(BarImpl::class, Foo::class)
        whenever(constructorSelector.invoke(TypeWithOneNestedInterfaceDependency::class)).thenReturn(TypeWithOneNestedInterfaceDependency::class.primaryConstructor)
        whenever(constructorSelector.invoke(TypeWithOneInterfaceDependency::class)).thenReturn(TypeWithOneInterfaceDependency::class.primaryConstructor)

        // when
        val getInstance = { subject.resolveBinding<TypeWithOneNestedInterfaceDependency>() }

        // then
        invoking(getInstance) shouldThrow IllegalArgumentException::class withMessage "No binding found for interface: type.Foo"
    }

    @Test
    fun `resolve binding fails to resolve interface instance`() {
        // given
        subject.registerBinding(Foo::class, Foo::class)

        // when
        val getInstance = { subject.resolveBinding<Foo>() }

        // then
        invoking(getInstance) shouldThrow IllegalArgumentException::class withMessage "No binding found for interface: type.Foo"
    }

    @Test
    fun `resolve fails to circular dependency`() {
        // given
        subject.registerBinding(Egg::class)
        subject.registerBinding(Chicken::class)

        // when
        val getInstance = { subject.resolveBinding<Egg>() }

        // then
        invoking(getInstance) shouldThrow CircularDependencyException::class
    }

    @Test
    fun `resolve the same instances for type marked with Singleton annotation`() {
        // when
        val instance1 = subject.resolveBinding<TypeMarkedWithSingletonAnnotation>()
        val instance2 = subject.resolveBinding<TypeMarkedWithSingletonAnnotation>()

        // then
        instance1 shouldBeEqualTo instance2
    }

    @Test
    fun `resolve the different instances for type not marked with Singleton annotation`() {
        // when
        val instance1 = subject.resolveBinding<TypeNotMarkedWithSingletonAnnotation>()
        val instance2 = subject.resolveBinding<TypeNotMarkedWithSingletonAnnotation>()

        // then
        instance1 shouldNotBeEqualTo instance2
    }

    @Test
    fun `verify bindings succeeds for empty bindings`() {
        // when
        subject.verifyBindings()
    }

    @Test
    fun `verify bindings succeeds for correct binding`() {
        // when
        subject.registerBinding(Foo::class, FooImpl::class)

        //then
        subject.verifyBindings()
    }
}
