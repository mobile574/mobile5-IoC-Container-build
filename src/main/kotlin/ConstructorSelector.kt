import extension.isEqualOrLessThan
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.hasAnnotation

class ConstructorSelector {
    operator fun invoke(type: KClass<*>): KFunction<Any> {
        require(!type.isInterface) {
            "Type is an interface ${type.qualifiedName}"
        }

        require(!type.isAbstract) {
            "Class is abstract ${type.qualifiedName}"
        }

        require(type.hasAtLeastOneAccessibleConstructor) {
            "Class has only private or protected constructors ${type.qualifiedName}"
        }

        require(type.hasAtMostOneInjectAnnotatedConstructor) {
            "Class has multiple constructors with Inject annotation ${type.qualifiedName}"
        }

        val accessibleConstructors = type.constructors
            .filter { it.visibility == KVisibility.PUBLIC || it.visibility == KVisibility.INTERNAL }

        return if (accessibleConstructors.size == 1) {
            accessibleConstructors.first()
        } else {
            val selectedConstructor = accessibleConstructors.firstOrNull { it.hasAnnotation<Inject>() }
                ?: accessibleConstructors.firstOrNull { it.isParameterless }

            requireNotNull(selectedConstructor) {
                "Unable to select constructor ${type.qualifiedName}"
            }

            selectedConstructor
        }
    }
}

private val KFunction<Any>.isParameterless
    get() = parameters.isEmpty()

private val KClass<*>.hasAtMostOneInjectAnnotatedConstructor: Boolean
    get() = constructors
        .flatMap { it.annotations }
        .count { it.annotationClass == Inject::class }
        .isEqualOrLessThan(1)

private val KClass<*>.hasAtLeastOneAccessibleConstructor: Boolean
    get() = constructors.any { it.visibility == KVisibility.PUBLIC || it.visibility == KVisibility.INTERNAL }

private val KClass<*>.isInterface get() = java.isInterface
