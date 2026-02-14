package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(builderClassName = "Builder", builderMethodName = "localFolderBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public class TestBackupTarget {

    @lombok.Builder.Default
    private BackupTargetId id = new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76");

    @lombok.Builder.Default
    private LocalDateTime dateCreated = LocalDateTime.parse("2022-04-29T14:15:53");

    @lombok.Builder.Default
    private LocalDateTime dateModified = LocalDateTime.parse("2023-04-29T14:15:53");

    @lombok.Builder.Default
    private String name = "Local folder";

    @lombok.Builder.Default
    private StorageSolutionId storageSolutionId = new StorageSolutionId("storageSolution1");

    @lombok.Builder.Default
    private PathTemplate pathTemplate = new PathTemplate("games/{GAME_PROVIDER_ID}/{GAME_TITLE}/{FILENAME}");

    public static BackupTarget localFolder() {
        return localFolderBuilder().build();
    }

    public static BackupTarget s3Bucket() {
        return s3BucketBuilder()
                .build();
    }

    public static Builder s3BucketBuilder() {
        return localFolderBuilder()
                .withId(new BackupTargetId("ac74de14-ee63-446d-b97d-d152ab846cad"))
                .withName("S3 bucket")
                .withStorageSolutionId(new StorageSolutionId("storageSolution2"));
    }

    public static class Builder {

        public BackupTarget build() {
            TestBackupTarget temp = internalBuilder();
            return new BackupTarget(temp.id, temp.dateCreated, temp.dateModified, temp.storageSolutionId, temp.name,
                    temp.pathTemplate);
        }
    }

}