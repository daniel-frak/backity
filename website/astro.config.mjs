// @ts-check
import {defineConfig} from 'astro/config';
import path from 'node:path';
import remarkToc from 'remark-toc';
import rehypeShiftHeading from "rehype-shift-heading";
import mermaid from 'astro-mermaid';
import pagefind from "astro-pagefind";
import remarkCalloutPlugin from "./remark-plugins/remark-callout-plugin.js";

// https://astro.build/config
export default defineConfig({
    site: 'https://daniel-frak.github.io',
    base: '/backity',

    vite: {
        resolve: {
            alias: {
                '@branding': path.resolve('../branding'),
                '@docs': path.resolve('../docs'),
            },
        },
    },

    integrations: [
        // https://github.com/joesaby/astro-mermaid
        mermaid({
            theme: 'dark',
            autoTheme: true
        }),

        // https://github.com/shishkin/astro-pagefind
        pagefind(),
    ],

    markdown: {
        syntaxHighlight: {
            type: 'shiki',
            excludeLangs: ['mermaid', 'math'],
        },

        // https://github.com/remarkjs/remark-toc
        remarkPlugins: [
            [remarkToc, {heading: 'Contents'}],
            remarkCalloutPlugin
        ],

        // https://github.com/rehypejs/rehype-shift-heading
        rehypePlugins: [
            [rehypeShiftHeading, {shift: 1}],
        ]
    }
});