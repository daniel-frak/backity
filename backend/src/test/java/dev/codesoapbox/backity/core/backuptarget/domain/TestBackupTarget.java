package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import lombok.Builder;

@Builder(builderClassName = "Builder", builderMethodName = "localFolderBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public class TestBackupTarget {

    @lombok.Builder.Default
    private BackupTargetId id = new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76");

    @lombok.Builder.Default
    private String title = "Local folder";

    @lombok.Builder.Default
    private StorageSolutionId storageSolutionId = new StorageSolutionId("storageSolution1");

    @lombok.Builder.Default
    private String pathTemplate = "games/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}";

    public static BackupTarget localFolder() {
        return localFolderBuilder().build();
    }

    public static BackupTarget s3Bucket() {
        return s3BucketBuilder()
                .build();
    }

    private static Builder s3BucketBuilder() {
        return localFolderBuilder()
                .withId(new BackupTargetId("ac74de14-ee63-446d-b97d-d152ab846cad"))
                .withTitle("S3 bucket")
                .withStorageSolutionId(new StorageSolutionId("storageSolution2"));
    }

    public static class Builder {

        public BackupTarget build() {
            TestBackupTarget temp = internalBuilder();
            return new BackupTarget(temp.id, temp.title, temp.storageSolutionId, temp.pathTemplate);
        }
    }

}