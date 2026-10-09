import { before, after, beforeEach, test } from 'node:test';
import { readFile } from 'node:fs/promises';
import { initializeTestEnvironment, assertSucceeds, assertFails } from '@firebase/rules-unit-testing';

let environment;
before(async () => {
  const rules = await readFile(new URL('../database.rules.json', import.meta.url), 'utf8');
  environment = await initializeTestEnvironment({
    projectId: 'demo-spontaniius', database: { host: '127.0.0.1', port: 19000, rules }
  });
});
after(async () => { if (environment) await environment.cleanup(); });
beforeEach(async () => { await environment.clearDatabase(); });
const message = (senderUid = 'alice') => ({
  senderUid, messageText: 'A fictional test message', messageUser: 'Test user', messageTime: Date.now()
});
const database = (uid) => (uid ? environment.authenticatedContext(uid) : environment.unauthenticatedContext()).database();
const seed = () => environment.withSecurityRulesDisabled(async context => {
  await context.database().ref('chats/1/original').set(message());
});

test('unauthenticated clients cannot read or write', async () => {
  await seed();
  await assertFails(database().ref('chats/1').once('value'));
  await assertFails(database().ref('chats/1/new').set(message()));
});
test('authenticated clients can create own messages and read the event chat', async () => {
  await assertSucceeds(database('alice').ref('chats/1/new').set(message()));
  await assertSucceeds(database('bob').ref('chats/1').once('value'));
});
test('forged sender identities and legacy untagged writes are denied', async () => {
  await assertFails(database('bob').ref('chats/1/new').set(message('alice')));
  const legacy = message(); delete legacy.senderUid;
  await assertFails(database('alice').ref('chats/1/legacy').set(legacy));
});
test('neither owner nor another user can edit or delete existing messages', async () => {
  await seed();
  for (const uid of ['alice', 'bob']) {
    await assertFails(database(uid).ref('chats/1/original/messageText').set('Changed'));
    await assertFails(database(uid).ref('chats/1/original/senderUid').set(uid));
    await assertFails(database(uid).ref('chats/1/original').remove());
  }
});
test('root/event reads and bulk deletes follow least privilege', async () => {
  await seed();
  await assertFails(database('alice').ref().once('value'));
  await assertFails(database('alice').ref('chats').once('value'));
  await assertFails(database('alice').ref().remove());
  await assertFails(database('alice').ref('chats/1').remove());
});
test('malformed, excessive and unexpected content is denied', async () => {
  for (const payload of [
    { ...message(), messageText: '' },
    { ...message(), messageText: 'x'.repeat(10001) },
    { ...message(), messageUser: 'x'.repeat(101) },
    { ...message(), messageTime: 'not a number' },
    { ...message(), messageTime: Date.now() + 600000 },
    { ...message(), additionalField: 'unexpected' }
  ]) await assertFails(database('alice').ref('chats/1/new').set(payload));
});
test('privileged cleanup can remove an attributable message', async () => {
  await seed();
  await environment.withSecurityRulesDisabled(async context => {
    await assertSucceeds(context.database().ref('chats/1/original').remove());
  });
});

test('account deletion blocks reject cached-token chat access without affecting others', async () => {
  await seed();
  await environment.withSecurityRulesDisabled(async context => {
    await context.database().ref('accountDeletionBlocks/alice').set({ blocked: true });
  });
  await assertFails(database('alice').ref('chats/1').once('value'));
  await assertFails(database('alice').ref('chats/1/new').set(message()));
  await assertFails(database('alice').ref('accountDeletionBlocks/alice').remove());
  await assertFails(database('bob').ref('accountDeletionBlocks').once('value'));
  await assertSucceeds(database('bob').ref('chats/1').once('value'));
  await assertSucceeds(database('bob').ref('chats/1/new').set(message('bob')));
});
