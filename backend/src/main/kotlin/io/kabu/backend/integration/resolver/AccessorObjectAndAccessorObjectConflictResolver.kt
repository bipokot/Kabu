package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.integration.resolver.common.RewiringConflictResolver

/**
 * Resolves conflict when integrating a AccessorObject
 */
class AccessorObjectAndAccessorObjectConflictResolver(integrator: Integrator) :
    RewiringConflictResolver(integrator)
