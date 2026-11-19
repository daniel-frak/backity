export const AI_MARKERS = [
    // LLMs add attribution in several ways and there is no guaranteed standard.
    // Therefore, we rely mostly on matching specific LLM identifiers:
    { tool: 'Claude Code', pattern: /noreply@anthropic\.com|generated with \[claude code]|^[ \t]*claude-session:/im },
    { tool: 'OpenAI Codex', pattern: /noreply@openai\.com|codex@openai\.com/i },
    { tool: 'Cursor', pattern: /cursoragent@cursor\.com|^[ \t]*made-with: cursor/im },
    { tool: 'GitHub Copilot', pattern: /\+copilot@users\.noreply\.github\.com|copilot@github\.com|copilot-swe-agent\[bot]|^[ \t]*copilot-session:/im },
    { tool: 'Google Jules', pattern: /google-labs-jules\[bot]/i },
    { tool: 'Aider', pattern: /aider@aider\.chat|noreply@aider\.chat|^[ \t]*aider: |\(aider\)/im },

    // Generic:
    { tool: 'Assisted-by trailer', pattern: /^[ \t]*assisted-by:/im },
];

export function findAiMarkers(text) {
    return AI_MARKERS.filter(({ pattern }) => pattern.test(text)).map(({ tool }) => tool);
}