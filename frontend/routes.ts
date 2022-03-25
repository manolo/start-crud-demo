import { Flow } from '@vaadin/flow-frontend';
import { Route } from '@vaadin/router';
import './views/main-layout';

const { serverSideRoutes } = new Flow({
  imports: () => import('../target/frontend/generated-flow-imports'),
});

export type ViewRoute = Route & {
  title?: string;
  icon?: string;
  children?: ViewRoute[];
};

export const views: ViewRoute[] = [
  // place routes below (more info https://vaadin.com/docs/latest/fusion/routing/overview)
  {
    path: 'crud3',
    component: 'crud-hilla-view',
    icon: 'la la-edit',
    title: 'Crud Hilla',
    action: async (_context, _command) => {
      await import('./views/crudhilla/crud-hilla-view');
      return;
    },
  },
  {
    path: 'master-detail3',
    component: 'master-detail-hilla-view',
    icon: 'la la-columns',
    title: 'Master-Detail Hilla',
    action: async (_context, _command) => {
      await import('./views/masterdetailhilla/master-detail-hilla-view');
      return;
    },
  },
];
export const routes: ViewRoute[] = [
  {
    path: '',
    component: 'main-layout',
    children: [
      ...views,
      // for server-side, the next magic line sends all unmatched routes:
      ...serverSideRoutes, // IMPORTANT: this must be the last entry in the array
    ],
  },
];
