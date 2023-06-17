package dev.codesoapbox.backity.testing.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

public class AdapterPackagesOnlyAccessedByTheirConfigCondition extends ArchCondition<JavaClass> {

    private final String adaptersPackageName;
    private final String configPackageName;

    public AdapterPackagesOnlyAccessedByTheirConfigCondition(String adaptersPackageName, String configPackageName) {
        super("only be accessed from their own adapter or config package");
        this.adaptersPackageName = adaptersPackageName;
        this.configPackageName = configPackageName;
    }

    @Override
    public void check(JavaClass item, ConditionEvents events) {
        item.getAccessesToSelf().forEach(access -> {
            String originPackageName = access.getOrigin().getOwner().getPackageName();
            String targetPackageName = access.getTarget().getOwner().getPackageName();
            String adaptersPackage = getAdaptersPackage(targetPackageName);
            String targetPackageSuffix = getTargetPackageSuffix(targetPackageName);

            String expectedAdapterPackage = getExpectedAdapterPackage(adaptersPackage, targetPackageSuffix);
            String expectedConfigPackage = getExpectedConfigPackage(adaptersPackage);

            boolean satisfied = originPackageName.startsWith(expectedAdapterPackage)
                    || originPackageName.startsWith(expectedConfigPackage)
                    || targetPackageName.contains("shared.adapters");

            events.add(new SimpleConditionEvent(access, satisfied, access.getDescription()));
        });
    }

    private String getAdaptersPackage(String targetPackageName) {
        return targetPackageName.substring(0,
                targetPackageName.indexOf(this.adaptersPackageName) + this.adaptersPackageName.length());
    }

    private String getTargetPackageSuffix(String targetPackageName) {
        return targetPackageName.substring(
                targetPackageName.indexOf(this.adaptersPackageName) + (this.adaptersPackageName + ".").length());
    }

    private String getExpectedAdapterPackage(String adaptersPackage, String targetPackageSuffix) {
        int adapterSubpackageIndex = targetPackageSuffix.indexOf(".", targetPackageSuffix.indexOf(".") + 1);
        if (adapterSubpackageIndex == -1) {
            return adaptersPackage + "." + targetPackageSuffix;
        }
        return adaptersPackage + "." + targetPackageSuffix.substring(0,
                targetPackageSuffix.indexOf(".", targetPackageSuffix.indexOf(".") + 1));
    }

    private String getExpectedConfigPackage(String adaptersPackage) {
        return adaptersPackage.substring(0,
                adaptersPackage.indexOf(this.adaptersPackageName)) + configPackageName;
    }
}
