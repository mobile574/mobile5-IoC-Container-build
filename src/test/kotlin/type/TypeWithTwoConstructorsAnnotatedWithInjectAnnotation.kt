@file:Suppress("UNUSED_PARAMETER")

package type

import javax.inject.Inject

class TypeWithTwoConstructorsAnnotatedWithInjectAnnotation {
    @Inject
    constructor(a:String)

    @Inject
    constructor(a: Int)
}
