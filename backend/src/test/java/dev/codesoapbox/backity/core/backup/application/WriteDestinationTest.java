package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WriteDestinationTest {

    @Test
    void shouldCompareWriteDestinationsWithDifferentIdsAndPaths() {
        var id1 = new StorageSolutionId("1");
        var id2 = new StorageSolutionId("2");
        var filePath1 = "testFilePath1";
        var filePath2 = "testFilePath2";

        var destination1 = new WriteDestination(id1, filePath1);
        var destination2 = new WriteDestination(id1, filePath2);
        var destination3 = new WriteDestination(id2, filePath1);
        var destination4 = new WriteDestination(id2, filePath2);

        List<WriteDestination> actual = Stream.of(destination4, destination3, destination2, destination1)
                .sorted()
                .toList();

        List<WriteDestination> expected = List.of(destination1, destination2, destination3, destination4);
        assertThat(actual).isEqualTo(expected);
    }
}