@file:Suppress("UNUSED_PARAMETER")

package type

import javax.inject.Inject

class TypeWithPrimaryConstructorAnnotatedWithInject @Inject constructor(param1: Int) {
    constructor(param1: Int, param2:String): this(param1)
}
