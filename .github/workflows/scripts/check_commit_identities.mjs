import { readFileSync } from 'node:fs';
import { findAiMarkers } from '../../../forbidden-commit-markers.mjs';

// Read input from job
// (expects one line per commit: "<sha> <author name> <author email> <committer name> <committer email>"):
const lines = readFileSync(0, 'utf8')
    .split('\n')
    .filter(line => line !== '');

if (lines.length === 0) {
    // A PR always has at least one commit, so receiving none means something went wrong earlier
    // (e.g. the workflow ran on an event that isn't a pull request and `git log` printed nothing).
    console.log('::error::No commits received on stdin');
    process.exitCode = 2;
} else {
    for (const line of lines) {
        const hits = findAiMarkers(line);
        if (hits.length > 0) {
            // Print only the SHA, not names/emails, which are untrusted input.
            console.log(`::error title=AI identity in commit::${line.slice(0, 12)} author/committer matches AI markers (${hits.join(', ')})`);
            process.exitCode = 1;
        }
    }
}