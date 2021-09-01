import kotlin.reflect.KClass

data class Binding(
    var sourceType: KClass<*>,
    var targetType: KClass<*>
)
