---
title: Backup workflow
nav_order: 3
---

The following diagram illustrates the tedious manual process that users typically face when trying to preserve their
game libraries:

```mermaid
sequenceDiagram
    actor User
    box transparent Game Provider
        participant GameProviderAuth as Game Provider <br> Authentication Service
        participant GameProviderLibrary as Game Provider Library
    end
    box transparent Backup Target
        participant FileSystem as File System
    end

    loop for each Game Provider
        User->>GameProviderAuth: Authenticate
        GameProviderAuth-->>User: Provide access to Library

        User->>GameProviderLibrary: Request list of owned Games
        GameProviderLibrary-->>User: Return list of Games

        loop For each Game
            User->>GameProviderLibrary: Request list of Game Files
            GameProviderLibrary-->>User: Return list of Game Files

            loop For each Game File
                User->>FileSystem: Check if Game File is already backed up
                FileSystem-->>User: Return whether Game File already backed up
                alt if Game File not backed up
                    User->>GameProviderLibrary: Request file download
                    GameProviderLibrary->>FileSystem: Stream file data
                    FileSystem-->>User: Confirm storage
                    User->>FileSystem: Verify file integrity
                    FileSystem-->>User: Return whether file not corrupted
                end
            end
        end
    end
```

This workflow includes several pain points:
- Manual tracking of already backed-up files
- Time-consuming file-by-file verification
- No automation of recurring tasks

Backity can transform this manual process into an automated workflow:

```mermaid
sequenceDiagram
    actor User
    participant Backity
    box transparent Game Provider
        participant GameProviderAuth as Game Provider <br> Authentication Service
        participant GameProviderLibrary as Game Provider Library
    end
    box transparent Backup Target
        participant FileSystem as File System
    end

    User->>Backity: Configure

    loop for each Game Provider
        User->>GameProviderAuth: Authenticate
        GameProviderAuth-->>Backity: Provide access to Library

        loop continuous
            Backity->>GameProviderLibrary: Request list of owned Games
            GameProviderLibrary-->>Backity: Return list of Games

            loop For each Game
                Backity->>GameProviderLibrary: Request list of Game Files
                GameProviderLibrary-->>Backity: Return list of Game Files

                loop For each Game File
                    Backity->>FileSystem: Check if Game File is already backed up
                    FileSystem-->>Backity: Return whether Game File already backed up
                    alt if Game File not backed up
                        Backity->>GameProviderLibrary: Request file download
                        GameProviderLibrary->>FileSystem: Stream file data
                        FileSystem-->>Backity: Confirm storage
                        Backity->>FileSystem: Verify file integrity
                        FileSystem-->>Backity: Return whether file not corrupted
                    end
                end
            end
        end
    end

    Backity-->>User: Report backup outcome
```

At the same time, if complete automation is not desired, the user can manually choose which games or files they want
backed up. The process then continues automatically.