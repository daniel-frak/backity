import {findAiMarkers} from './forbidden-commit-markers.mjs';

// commitlint loads the default export, so it must stay:
// noinspection JSUnusedGlobalSymbols
export default {
    extends: ['@commitlint/config-conventional'],

    plugins: [{
        rules: {
            // Ignores the `when` setting: this rule only supports 'always':
            'no-ai-markers': ({ raw }) => {
                // Note that due to `defaultIgnores: true`, Commitlint will ignore commits like "Revert ...", etc.
                // The full list of ignored commits is available here:
                // https://github.com/conventional-changelog/commitlint/blob/master/%40commitlint/is-ignored/src/defaults.ts
                //
                // `defaultIgnores: false` would break Conventional Commits checks.
                // To include these commits in the AI check,
                // we would have to extract the check into a separate workflow step.
                const hits = findAiMarkers(raw ?? '');
                return [hits.length === 0, `commit message contains AI markers (${hits.join(', ')})`];
            },
        },
    }],

    // Severity: 2 = error; 1 = warning; 0 = off.
    // When: must be 'always' (see above).
    rules: { 'no-ai-markers': [2, 'always'] },
};