/**
 * Syntax:
 *
 *   ```text
 *   :::note
 *   > Some blockquote content
 *   ```
 *
 * A `:::type` paragraph followed by consecutive blockquote nodes is replaced
 * by a `<blockquote>` with `callout` and the `type` added to its class list.
 */

const OPEN_RE = /^:::[ \t]*([a-zA-Z][\w-]*)[ \t]*$/

function isOpenFence(node) {
    return (
        node.type === 'paragraph' &&
        node.children.length === 1 &&
        node.children[0].type === 'text' &&
        OPEN_RE.test(node.children[0].value.trim())
    )
}

export default function remarkCalloutPlugin() {
    return function ({children}) {
        let i = 0

        while (i < children.length) {
            if (!isOpenFence(children[i])) {
                i++
                continue
            }

            const [, type] = children[i].children[0].value.trim().match(OPEN_RE)

            const rest = children.slice(i + 1)
            const end = rest.findIndex((node) => node.type !== 'blockquote')
            const blockquotes = rest.slice(0, end === -1 ? undefined : end)

            blockquotes.forEach((node) => {
                node.data ??= {}
                const existingClasses = node.data.hProperties?.className ?? []
                node.data.hProperties = {
                    ...node.data.hProperties,
                    className: [
                        ...(Array.isArray(existingClasses)
                            ? existingClasses
                            : [existingClasses]),
                        'callout', type.toLowerCase()
                    ]
                }
            })

            children.splice(i, 1)
            i += blockquotes.length
        }
    }
}