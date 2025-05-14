---
title: Domain Vision Statement
nav_order: 2
---

The domain model ensures reliable automation of the game preservation process
while allowing clear traceability between source files and their backups.

Specific game providers and storage solutions are abstracted away from the core and designed as supporting subdomains, 
so that adding support for new ones is straightforward.

In addition to predefined game providers, custom ones can be added by the user, supporting manual file upload
so that all backups can be stored in the same place.

Multiple backup targets can be used simultaneously to ensure redundancy and can be added at any time by the user.
Each backup target can be configured to use a different storage solution and folder structure.

Tracking mechanisms offer clear visibility into every file's status across all relevant backup targets,
making the backup process transparent at each stage. 
"Integrity verified" is considered the final status for each file, in the context of a backup target.

Files are directly linked to their games while remaining accessible for independent browsing when needed.
Multiple versions of the same file can be stored at the same time
(such as installers for multiple versions of the same game).
