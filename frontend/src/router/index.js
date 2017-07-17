import Vue from 'vue';
import Router from 'vue-router';
import Home from '@/components/Home';
import Simulationview from '@/components/Simulationview';
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
      path: '/simulation',
      name: 'simulation',
      component: Simulationview,
    },
    {
      path: '/trades',
      name: 'trades',
      component: Tradeview,
    },
  ],
});
