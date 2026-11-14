---
title: 'Verifying repository configuration'
order: 20
---

# Contents

# Running Renovate locally

To perform a [dry run](https://docs.renovatebot.com/self-hosted-configuration/#dryrun) of Renovate, use the command:

```shell
# From repository root:
docker compose -f docker/renovate/docker-compose.yml run --rm renovate
```

This will run Renovate in `extract` mode, which performs a package file scan to identify the extracted dependencies.

:::warning
> Renovate uses `git ls-files` to discover files.
> Any newly added files will be ignored until they are staged or committed.