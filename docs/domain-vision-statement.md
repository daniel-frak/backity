---
title: Domain Vision Statement
nav_order: 2
---

The domain model ensures reliable automation of the game preservation process
while maintaining clear traceability between source files and their backups.

Specific game providers and backup targets are abstracted away from the core,
so that adding new ones is straightforward.

Tracking mechanisms offer clear visibility into every file's status,
making the backup process transparent at each stage.

Files are directly linked to their games yet remain accessible for independent browsing when needed.

Since simultaneous use of multiple backup targets is not expected to be required,
the model is streamlined to support only one at a time.
At the same time, the folder structure within it is configurable so that it can be tailored to the user's needs.