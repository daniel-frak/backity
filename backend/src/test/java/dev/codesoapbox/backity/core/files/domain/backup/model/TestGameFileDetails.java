package dev.codesoapbox.backity.core.files.domain.backup.model;

import dev.codesoapbox.backity.core.files.domain.game.GameId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestGameFileDetails {

    public static Supplier<GameFileDetails> GAME_FILE_DETAILS_1 = () -> new GameFileDetails(
            new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
            new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")),
            new SourceFileDetails(
                    "someSourceId1",
                    "someOriginalGameTitle1",
                    "someFileTitle1",
                    "someVersion1",
                    "someUrl1",
                    "someOriginalFileName1",
                    "5 KB"
            ),
            new BackupDetails(
                    FileBackupStatus.ENQUEUED,
                    null,
                    null
            ),
            LocalDateTime.parse("2022-04-29T14:15:53"),
            LocalDateTime.parse("2023-04-29T14:15:53")
    );

    public static Supplier<GameFileDetails> GAME_FILE_DETAILS_2 = () -> new GameFileDetails(
            new GameFileDetailsId(UUID.fromString("a6adc122-df20-4e2c-a975-7d4af7104704")),
            new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")),
            new SourceFileDetails(
                    "someSourceId2",
                    "someOriginalGameTitle2",
                    "someFileTitle2",
                    "someVersion2",
                    "someUrl2",
                    "someOriginalFileName2",
                    "5 KB"
            ),
            new BackupDetails(
                    FileBackupStatus.SUCCESS,
                    null,
                    null
            ),
            LocalDateTime.parse("2022-04-29T14:15:53"),
            LocalDateTime.parse("2023-04-29T14:15:53")
    );

    public static Supplier<GameFileDetails> GAME_FILE_DETAILS_3 = () -> new GameFileDetails(
            new GameFileDetailsId(UUID.fromString("0d4d181c-9a77-4146-bbd6-40f7d4453b5f")),
            new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")),
            new SourceFileDetails(
                    "someSourceId3",
                    "someOriginalGameTitle3",
                    "someFileTitle3",
                    "someVersion3",
                    "someUrl3",
                    "someOriginalFileName3",
                    "5 KB"
            ),
            new BackupDetails(
                    FileBackupStatus.ENQUEUED,
                    null,
                    null
            ),
            LocalDateTime.parse("2022-04-29T14:15:53"),
            LocalDateTime.parse("2023-04-29T14:15:53")
    );

    public static Supplier<GameFileDetails> GAME_FILE_DETAILS_4 = () -> new GameFileDetails(
            new GameFileDetailsId(UUID.fromString("568afe65-018b-40fc-a8b4-481ded571ff8")),
            new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")),
            new SourceFileDetails(
                    "someSourceId4",
                    "someOriginalGameTitle4",
                    "someFileTitle4",
                    "someVersion4",
                    "someUrl4",
                    "someOriginalFileName4",
                    "5 KB"
            ),
            new BackupDetails(
                    FileBackupStatus.FAILED,
                    null,
                    null
            ),
            LocalDateTime.parse("2022-04-29T14:15:54"),
            LocalDateTime.parse("2023-04-29T14:15:54")
    );

    public static Supplier<GameFileDetails> GAME_FILE_DETAILS_5 = () -> new GameFileDetails(
            new GameFileDetailsId(UUID.fromString("0a2a4b8d-f02e-4e3e-a3da-f47e1ea6aa30")),
            new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")),
            new SourceFileDetails(
                    "someSourceId5",
                    "someOriginalGameTitle5",
                    "someFileTitle5",
                    "someVersion5",
                    "someUrl5",
                    "someOriginalFileName5",
                    "5 KB"
            ),
            new BackupDetails(
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    null
            ),
            LocalDateTime.parse("2022-04-29T14:15:55"),
            LocalDateTime.parse("2023-04-29T14:15:55")
    );

    public static Supplier<GameFileDetails> GAME_FILE_DETAILS_6 = () -> new GameFileDetails(
            new GameFileDetailsId(UUID.fromString("3d65af79-a558-4f23-88bd-3c04e977e63f")),
            new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")),
            new SourceFileDetails(
                    "someSourceId6",
                    "someOriginalGameTitle6",
                    "someFileTitle6",
                    "someVersion6",
                    "someUrl6",
                    "someOriginalFileName6",
                    "5 KB"
            ),
            new BackupDetails(
                    FileBackupStatus.DISCOVERED,
                    null,
                    null
            ),
            LocalDateTime.parse("2022-04-29T14:15:56"),
            LocalDateTime.parse("2023-04-29T14:15:56")
    );
    public static Supplier<GameFileDetails> FULL_GAME_FILE_DETAILS = () -> new GameFileDetails(
            new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
            new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")),
            new SourceFileDetails(
                    "someSourceId",
                    "someOriginalGameTitle",
                    "someFileTitle",
                    "someVersion",
                    "someUrl",
                    "someOriginalFileName",
                    "5 KB"
            ),
            new BackupDetails(
                    FileBackupStatus.DISCOVERED,
                    "someFailedReason",
                    "someFilePath"
            ),
            LocalDateTime.parse("2022-04-29T14:15:53"),
            LocalDateTime.parse("2023-04-29T14:15:53")
    );
}
