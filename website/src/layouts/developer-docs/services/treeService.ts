import type {DevDocNode} from "../domain";

/** Build a tree from a flat list */
export const buildTree: (docs: DevDocNode[]) => DevDocNode[] = (docs: DevDocNode[]): DevDocNode[] => {
    const nodesById = new Map<string, DevDocNode>(
        docs.map(doc => [doc.id, {...doc}])
    );

    nodesById.forEach(doc => {
        if (!doc.parent) {
            return;
        }

        const parent = nodesById.get(doc.parent);
        if (parent) {
            parent.items = [...(parent.items ?? []), doc];
        }
    });

    return [...nodesById.values()];
};

/** Recursively sort tree */
export const sortTree: (items: DevDocNode[], getOrder: (doc: DevDocNode) => number) => DevDocNode[] = (
    items: DevDocNode[],
    getOrder: (doc: DevDocNode) => number
): DevDocNode[] => {
    return items
        .toSorted((a, b) => getOrder(a) - getOrder(b))
        .map(item => ({
            ...item,
            items: item.items ? sortTree(item.items, getOrder) : undefined
        }));
};