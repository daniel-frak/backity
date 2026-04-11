package dev.codesoapbox.backity.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import dev.codesoapbox.backity.e2e.actions.Repeat;
import dev.codesoapbox.backity.e2e.backend.BackupTargetsApi;

public class SettingsPage {

    private final Page page;
    private final BackupTargetsApi backupTargetsApi;
    private final Locator addBackupTargetBtn;
    private final Locator deleteBackupTargetBtns;
    private final Locator confirmDeleteBtn;
    private final Locator backupTargetNameInput;
    private final Locator submitNewBackupTargetBtn;
    private final Locator loader;

    public SettingsPage(Page page, BackupTargetsApi backupTargetsApi) {
        this.page = page;
        this.backupTargetsApi = backupTargetsApi;
        this.addBackupTargetBtn = page.getByTestId("add-backup-target-btn");
        this.deleteBackupTargetBtns = page.locator("[data-testid^='delete-backup-target-btn-']");
        this.confirmDeleteBtn = page.getByTestId("confirmation-modal-yes-btn");
        this.backupTargetNameInput = page.getByTestId("name-input");
        this.submitNewBackupTargetBtn = page.getByTestId("submit-new-backup-target-btn");
        this.loader = page.getByTestId("loader");
    }

    public void visit() {
        page.navigate("/settings");
        waitUntilLoaderDisappears();
    }

    private void waitUntilLoaderDisappears() {
        loader.waitFor(isHidden());
    }

    private Locator.WaitForOptions isHidden() {
        return new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN);
    }

    public void deleteAllBackupTargets() {
        Repeat.on(page)
                .action(() -> {
                    /*
                    The frontend might not have the latest state of Backup Targets by the time we navigate here.
                    We must refresh to pull the latest state from the backend.
                     */
                    page.reload();
                    waitUntilLoaderDisappears();

                    if (deleteBackupTargetBtns.count() <= 0) {
                        return;
                    }

                    Locator currentDeleteButton = deleteBackupTargetBtns.first();
                    currentDeleteButton.click();
                    confirmDeleteBtn.click();
                    waitUntilLoaderDisappears();
                    currentDeleteButton.waitFor(isHidden());
                })
                .expectingResponse(backupTargetsApi::isDeleted)
                .until(() -> {
                    waitUntilLoaderDisappears();
                    return deleteBackupTargetBtns.count() == 0;
                });
    }

    public void createBackupTarget(String name) {
        addBackupTargetBtn.click();
        backupTargetNameInput.fill(name);
        page.waitForResponse(backupTargetsApi::isCreated, submitNewBackupTargetBtn::click);
    }
}
