/// Currently, Spring Modulith is only used for event outboxing,
/// so all modules are declared open to disable IDE warnings.

@ApplicationModule(type = ApplicationModule.Type.OPEN)
package dev.codesoapbox.backity.core;

import org.springframework.modulith.ApplicationModule;