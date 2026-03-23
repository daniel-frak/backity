import {defineCollection} from 'astro:content';
import {glob} from 'astro/loaders';
import {z} from 'astro/zod';

const DOCS_PATH = '../docs/dev';
const ADRS_FOLDER = `adrs`;

const adrs = defineCollection({
    loader: glob({
        base: `${DOCS_PATH}/${ADRS_FOLDER}`,
        pattern: '**/*.{md,mdx}'
    }),
    schema: z.object({
        title: z.string(),
        status: z.string(),
        scope: z.string(),
        tags: z.array(z.string()).optional()
    }),
});

const devDocs = defineCollection({
    loader: glob({
        base: `${DOCS_PATH}`,
        pattern: [
            '**/*.{md,mdx}',
            `!${ADRS_FOLDER}/**`
        ],
    }),
    schema: z.object({
        title: z.string(),
        parent: z.string().optional(),
        order: z.number().optional()
    }),
});

export const collections = {adrs: adrs, devDocs: devDocs};