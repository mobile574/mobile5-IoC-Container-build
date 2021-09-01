
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.Test
import type.AbstractClass
import type.TypeThatIsInterface
import type.TypeWithOneInternalConstructor
import type.TypeWithOnePrivateConstructor
import type.TypeWithOneProtectedConstructor
import type.TypeWithOnePublicConstructor
import type.TypeWithPrimaryConstructorAnnotatedWithInject
import type.TypeWithSecondaryConstructorAnnotatedWithInject
import type.TypeWithTwoConstructorsAnnotatedWithInjectAnnotation
import kotlin.reflect.full.primaryConstructor

internal class ConstructorSelectorTest {
    private val subject = ConstructorSelector()

    @Test
    fun `return constructor for type with public constructor`() {
        // given
        val type = TypeWithOnePublicConstructor::class

        // when
        val result = subject(type)

        // then
        result shouldBeEqualTo TypeWithOnePublicConstructor::class.constructors.first()
    }

    @Test
    fun `return constructor for type with internal constructor`() {
        // given
        val type = TypeWithOneInternalConstructor::class

        // when
        val result = subject(type)

        // then
        result shouldBeEqualTo TypeWithOneInternalConstructor::class.constructors.first()
    }

    @Test
    fun `throw error when type has only private constructors`() {
        // given
        val type = TypeWithOnePrivateConstructor::class

        // when
        val result = { subject(type) }

        // then
        val message = "Class has only private or protected constructors type.TypeWithOnePrivateConstructor"
        invoking(result) shouldThrow IllegalArgumentException::class withMessage message
    }

    @Test
    fun `throw error when type has only protected constructors`() {
        // given
        val type = TypeWithOneProtectedConstructor::class

        // when
        val result = { subject(type) }

        // then
        val message = "Class has only private or protected constructors type.TypeWithOneProtectedConstructor"
        invoking(result) shouldThrow IllegalArgumentException::class withMessage message
    }

    @Test
    fun `throw error when class is abstract`() {
        // given
        val type = AbstractClass::class

        // when
        val result = { subject(type) }

        // then
        val message = "Class is abstract type.AbstractClass"
        invoking(result) shouldThrow IllegalArgumentException::class withMessage message
    }

    @Test
    fun `throw error when type is interface`() {
        // given
        val type = TypeThatIsInterface::class

        // when
        val result = { subject(type) }

        // then
        val message = "Type is an interface type.TypeThatIsInterface"
        invoking(result) shouldThrow IllegalArgumentException::class withMessage message
    }

    @Test
    fun `return primary constructor annotated with Inject`() {
        // given
        val type = TypeWithPrimaryConstructorAnnotatedWithInject::class

        // when
        val result = subject(type)

        // then
        result shouldBeEqualTo TypeWithPrimaryConstructorAnnotatedWithInject::class.primaryConstructor
    }

    @Test
    fun `return secondary constructor annotated with Inject`() {
        // given
        val type = TypeWithSecondaryConstructorAnnotatedWithInject::class

        // when
        val result = subject(type)

        // then
        result shouldBeEqualTo TypeWithSecondaryConstructorAnnotatedWithInject::class.constructors.toList()[0]
    }

    @Test
    fun `throw error when more then one constructor is annotated with InjectConstructor annotation`() {
        // given
        val type = TypeWithTwoConstructorsAnnotatedWithInjectAnnotation::class

        // when
        val result = { subject(type) }

        // then
        val message = "Class has multiple constructors with Inject annotation type.TypeWithTwoConstructorsAnnotatedWithInjectAnnotation"
        invoking(result) shouldThrow IllegalArgumentException::class withMessage message
    }
}
