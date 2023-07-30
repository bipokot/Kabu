package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.integration.resolver.common.RewiringConflictResolver

/**
 * Resolves conflict between FixedTypeNode-s
 */
class FixedTypeAndFixedTypeConflictResolver(private val integrator: Integrator) :
    RewiringConflictResolver(integrator)
