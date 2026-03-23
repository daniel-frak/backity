export const devDocsRoot = "developer-documentation";
export const adrPath = "architecture-decision-records"

export const scopeLabels: Record<string, string> = {
    project: "Project",
    backend: "Backend",
    frontend: "Frontend",
};

export const statuses: Record<string, { label: string; class: string }> = {
    accepted: { label: "Accepted", class: "status-accepted" },
    superseded: { label: "Superseded", class: "status-superseded" },
    deprecated: { label: "Deprecated", class: "status-deprecated" },
};

export const scopeOrder = ["project", "backend", "frontend"];
export const statusOrder = ["accepted", "superseded", "deprecated"];