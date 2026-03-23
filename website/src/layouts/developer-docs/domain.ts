export type DevDocNode = {
    id: string;
    parent?: string;
    title: string;
    url: string;
    order?: number;
    items?: DevDocNode[];
    class?: string;
};