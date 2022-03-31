import {registerPlugin} from '@capacitor/core';

import type {CapacitorCodeUpdatePlugin} from './definitions';

const CapacitorCodeUpdate = registerPlugin<CapacitorCodeUpdatePlugin>(
    'CapacitorCodeUpdate',
    {
        web: () => import('./web').then(m => new m.CapacitorCodeUpdateWeb()),
    },
);


export * from './definitions';
export {CapacitorCodeUpdate};
