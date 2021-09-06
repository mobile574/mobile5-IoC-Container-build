
import exception.CircularDependencyException
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation

class DependencyContainer private constructor(
    private val constructorSelector: ConstructorSelector
) {
    init {
        instance = this
    }

    private val bindings = mutableMapOf<KClass<*>, KClass<*>?>()
    private val singletonInstances = mutableMapOf<KClass<*>, Any>()

    /**
     * Register new binding.
     *
     * @param sourceType type to be resolved
     * @param targetType optional type that source type will be mapped to eg. sourceType can be an interface and
     * targetType is a concrete class.
     */
    fun registerBinding(sourceType: KClass<*>, targetType: KClass<*>? = null) {
        require(!bindings.contains(sourceType)) { "Binding already exists: ${sourceType.qualifiedName}" }

        bindings[sourceType] = targetType
    }

    /**
     * Returns instance of specific type passed as generic type argument
     */
    inline fun <reified T : Any> resolveBinding() = resolveBinding(T::class) as T

    /**
     * Returns instance of specific type
     *
     * @param sourceType type to be resolved
     */
    fun resolveBinding(sourceType: KClass<*>): Any {
        val targetType = bindings[sourceType] ?: sourceType

        require(!targetType.java.isInterface) { "No binding found for interface: ${targetType.qualifiedName}" }

        if (sourceType.objectInstance != null) {
            return sourceType.objectInstance as Any
        }

        if(singletonInstances.containsKey(targetType)) {
            return singletonInstances[targetType] as Any
        }

        val constructor = constructorSelector(targetType)

        return if (constructor.parameters.isEmpty()) {
            constructor
                .call()
                .also { saveSingletonInstanceIfNeeded(targetType, it) }
        } else {
            try {
                val parameterInstances = constructor.parameters
                    .map { it.type.classifier as KClass<*> }
                    .map { resolveBinding(it) }

                constructor
                    .call(*parameterInstances.toTypedArray())
                    .also { saveSingletonInstanceIfNeeded(targetType, it) }
            } catch (error: StackOverflowError) {
                throw CircularDependencyException("Type ${sourceType.qualifiedName}")
            }
        }
    }

    private fun saveSingletonInstanceIfNeeded(targetType: KClass<*>, instance: Any) {
        if(targetType.hasAnnotation<Singleton>()) {
            singletonInstances[targetType] = instance
        }
    }

    companion object {
        private lateinit var instance: DependencyContainer

        /**
         * Returns DependencyContainer instance
         */
        fun getInstance(): DependencyContainer = if (this::instance.isInitialized) {
            instance
        } else {
            DependencyContainer(ConstructorSelector())
        }

        internal fun getNewInstance(constructorSelector: ConstructorSelector) =
            DependencyContainer(ConstructorSelector())
    }

    /**
     * Verifies is all binding can be resolved. This method is expensive, so it should only be used in debug
     * mode (not in production code).
     */
    fun verifyBindings() {
        bindings.forEach { resolveBinding(it.key) }
    }
}
