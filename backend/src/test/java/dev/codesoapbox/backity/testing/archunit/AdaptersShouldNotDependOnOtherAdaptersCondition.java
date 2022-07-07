package dev.codesoapbox.backity.testing.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

public class AdaptersShouldNotDependOnOtherAdaptersCondition extends ArchCondition<JavaClass> {

    private final String rootPackage;
    private final String domainPackageName;

    public AdaptersShouldNotDependOnOtherAdaptersCondition(String rootPackage, String domainPackageName) {
        super("not depend on other adapters");
        this.rootPackage = rootPackage;
        this.domainPackageName = domainPackageName;
    }

    @Override
    public void check(JavaClass item, ConditionEvents events) {
        if (!isAnAdapter(item)) {
            return;
        }

        item.getAccessesFromSelf().forEach(access -> {
            boolean targetIsAnAdapter = isAnAdapter(access.getTargetOwner());
            boolean satisfied = access.getOriginOwner().equals(access.getTargetOwner())
                    || !access.getTargetOwner().getPackageName().startsWith(rootPackage)
                    || !targetIsAnAdapter;
            events.add(new SimpleConditionEvent(access, satisfied, access.getDescription()));
        });
    }

    private boolean isAnAdapter(JavaClass item) {
        return isDrivenAdapter(item) || isDrivingAdapter(item);
    }

    private boolean isDrivenAdapter(JavaClass item) {
        for (JavaClass implementedInterface : item.getAllRawInterfaces()) {
            if (implementedInterface.getPackageName().contains(domainPackageName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDrivingAdapter(JavaClass item) {
        return item.getSimpleName().endsWith("Controller");
    }
}
