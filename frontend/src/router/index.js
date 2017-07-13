import Vue from 'vue';
import Router from 'vue-router';
import Home from '@/components/Home';
import Tradegraph from '@/components/Tradegraph';

import Hello from '@/components/Hello';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/trades',
      name: 'trades',
      component: Tradegraph,
    },
    {
      path: '/hello',
      name: 'hello',
      component: Hello,
    },
  ],
});
