@file:Suppress("UNUSED_PARAMETER")

package type

import javax.inject.Inject

class TypeWithSecondaryConstructorAnnotatedWithInject constructor(param1: Int) {
    @Inject constructor(param1: Int, param2:String): this(param1)
}
