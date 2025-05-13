---
title: Domain Vision Statement
nav_order: 2
---

The domain model ensures reliable automation of the game preservation process
while maintaining clear traceability between source files and their backups.

Game provider and backup target implementations are abstracted away from the core,
so that adding new ones is straightforward.

Each game provider may have a unique authentication mechanism, which will be supported by generic subdomains for each.

Tracking mechanisms offer clear visibility into every file's status,
making the backup process transparent at each stage.

Files are directly linked to their games yet remain accessible for independent browsing when needed.

Since multiple backup targets are not expected to be required, the model is streamlined to support only one at a time,
though the folder structure within it is configurable so that it can be tailored to the user's needs.