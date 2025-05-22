package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

// @TODO Implement this
class FileCopyJpaRepositoryAbstractIT {

    // @TODO Sort this out
//    @Test
//    void saveShouldPublishEventsAfterCommitting() {
//        GameFile gameFile = TestGameFile.discovered();
//        gameFile.markAsInProgress();
//        gameFileJpaRepository.save(gameFile);
//
//        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
//
//        verify(domainEventPublisher).publish(any(FileBackupStartedEvent.class));
//    }
//
//    @Test
//    void saveShouldClearEvents() {
//        GameFile gameFile = TestGameFile.discovered();
//        gameFile.markAsInProgress();
//        gameFileJpaRepository.save(gameFile);
//
//        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
//
//        assertThat(gameFile.getDomainEvents()).isEmpty();
//    }
//
//    @Test
//    void shouldFindAllProcessed() {
//        populateDatabase(GAME_FILES.getAll());
//        var pagination = new Pagination(0, 2);
//        Page<GameFile> result = gameFileJpaRepository.findAllProcessed(pagination);
//
//        List<GameFile> expectedResult = List.of(
//                GAME_FILES.SUCCESSFUL_FOR_GAME_1.get(),
//                GAME_FILES.FAILED_FOR_GAME_2.get()
//        );
//        assertThat(result.content())
//                .usingRecursiveComparison()
//                .ignoringFields("dateCreated", "dateModified")
//                .isEqualTo(expectedResult);
//    }
//
//    @Test
//    void shouldFindAllDiscovered() {
//        populateDatabase(GAME_FILES.getAll());
//        var pagination = new Pagination(0, 2);
//        Page<GameFile> result = gameFileJpaRepository.findAllDiscovered(pagination);
//
//        List<GameFile> expectedResult = singletonList(GAME_FILES.DISCOVERED_FOR_GAME_1.get());
//        assertThat(result.content())
//                .usingRecursiveComparison()
//                .ignoringFields("dateCreated", "dateModified")
//                .isEqualTo(expectedResult);
//    }
//
//    @Test
//    void shouldFindAllWaitingForDownload() {
//        populateDatabase(GAME_FILES.getAll());
//        var pagination = new Pagination(0, 2);
//        Page<GameFile> result = gameFileJpaRepository.findAllWaitingForDownload(pagination);
//
//        Page<GameFile> expectedResult = new Page<>(
//                List.of(GAME_FILES.ENQUEUED_FOR_GAME_1.get(), GAME_FILES.ENQUEUED_FOR_GAME_2.get()),
//                2, 1, 2, 2, 0);
//        assertThat(result).usingRecursiveComparison()
//                .ignoringFields("content")
//                .isEqualTo(expectedResult);
//        assertThat(result.content())
//                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("dateCreated", "dateModified")
//                .isEqualTo(expectedResult.content());
//    }
}