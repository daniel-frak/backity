import type {DevDocNode} from "../domain";
import {adrPath, devDocsRoot, scopeLabels, statuses} from "../config";

export const createAdrNodes = (adrDocs: any[], adrRootId: string): DevDocNode[] => {
    const scopes: Record<string, DevDocNode> = {};

    adrDocs.forEach((doc: any) => {
        const {scope, status} = doc.data;
        if (!scope || !status) {
            console.warn(`ADR document ${doc.url} is missing scope or status`);
            return;
        }

        if (!scopes[scope]) {
            scopes[scope] = asScopeNode(adrRootId, scope);
        }
        const scopeNode = scopes[scope];

        let statusNode = scopeNode.items
            ?.find((s) => s.id === `${scopeNode.id}-${status}`);
        if (!statusNode) {
            statusNode = asStatusNode(status, scopeNode);
            scopeNode.items?.push(statusNode);
        }

        statusNode.items?.push(asAdrNode(doc, statusNode));
    });

    return Object.values(scopes);
};

function asScopeNode(adrRootId: string, scope: string) {
    return {
        id: `${adrRootId}-${scope}`,
        title: scopeLabels[scope] ?? scope,
        url: "#",
        parent: adrRootId,
        items: [],
    };
}

function asStatusNode(status: string, scopeNode: DevDocNode) {
    const statusInfo = statuses[status] ?? {label: status, class: ""};
    return {
        id: `${scopeNode.id}-${status}`,
        title: statusInfo.label,
        url: "#",
        parent: scopeNode.id,
        class: statusInfo.class,
        items: [],
    };
}

function asAdrNode(doc: any, statusNode: DevDocNode) {
    let url: string = '/' + devDocsRoot + '/' + adrPath + '/' + doc.id;
    return {
        id: url,
        title: formatAdrTitleWithoutScope(doc),
        url: url,
        parent: statusNode.id,
        order: doc.data.order
    };
}

function formatAdrTitleWithoutScope(doc: any) {
    return formatAdrTitle(doc, 1);
}

function formatAdrTitle(doc: any, sliceIndex: number = 1) {
    const filename = doc.id.split('/').pop();
    const parts = filename.split('-').slice(sliceIndex, 2);
    const prefix = parts.join('-').toUpperCase();
    return `${prefix} - ${doc.data.title}`;
}

export const formatAdrTitleWithScope = (doc: any) => {
    return formatAdrTitle(doc, 0);
}

export const getDevDocsPath = () => {
    return `/${devDocsRoot}`;
}

export const getAdrPath = () => {
    return `/${devDocsRoot}/${adrPath}`;
}