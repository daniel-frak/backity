const base = import.meta.env.BASE_URL
    .replace(/\/$/, ''); // strip trailing slash

export function withBase(path: string): string {
    const baseAlreadyAdded: boolean = (path === base) || path.startsWith(`${base}/`);
    if (baseAlreadyAdded) {
        return path;
    }
    return `${base}${path.startsWith('/') ? path : '/' + path}`;
}

export function isSameUrl(path1: string, path2: string) {
    return normalizeUrl(withBase(path1)) === normalizeUrl(withBase(path2));
}

const normalizeUrl = (url: string) => url.replace(/\/+$/, '');

export function urlStartsWith(url: string, prefix: string) {
    return withBase(url).startsWith(withBase(prefix));
}