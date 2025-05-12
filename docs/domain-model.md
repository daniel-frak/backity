---
title: Domain Vision Statement
parent: Getting started
nav_order: 3
---

{: .important }
This document is a work in progress.

The domain centers around automated preservation of digital game libraries. Its core elements are:

- **Game Providers**: Sources of games and their files,
- **Games**: Digital products containing downloadable files,
- **Game Files**: Actual content that needs to be preserved,
- **Backup Target / File System**: Storage system maintaining preserved files.

The model ensures reliable automation of the preservation process while maintaining clear traceability
between provider files and their backups.
It must be flexible enough to accommodate various Game Providers and Backup Targets (or File Systems).
Unique authentication mechanisms should be supported for every Game Provider.
Game Files should be directly connected to their Games, and their backup status should be clearly represented.

A standard workflow for backing up game files is quite tedious and, at a high level, looks something like this:

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

Backity automates a lot of the busywork, only requiring the user to authenticate with the Game Providers,
while the backups are done continuously in the background:

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