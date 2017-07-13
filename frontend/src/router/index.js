import Vue from 'vue';
import Router from 'vue-router';
import Home from '@/components/Home';
import Tradeview from '@/components/Tradeview';

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
      component: Tradeview,
    },
  ],
});
