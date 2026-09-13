import {copyFile, mkdir} from 'node:fs/promises';
import path from 'node:path';
import {fileURLToPath} from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const workspace = path.resolve(__dirname, '..');

const source = path.resolve(workspace, '../../../..');
const destination = path.resolve(workspace, '.generated');

for (const file of [
  'branding/favicon.svg',
  'branding/logo-color-on-dark.svg',
]) {
  const destinationFile = path.join(destination, file);

  await mkdir(path.dirname(destinationFile), { recursive: true });

  await copyFile(
    path.join(source, file),
    destinationFile,
  );
}

console.log(`Copied shared assets to ${path.relative(workspace, destination)} folder`);
