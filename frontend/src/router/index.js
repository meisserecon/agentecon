import Vue from 'vue';
import Router from 'vue-router';
import Home from '@/components/Home';
import Simulationview from '@/components/Simulationview';
import Tradeview from '@/components/Tradeview';
import Dragndrop from '@/components/Dragndrop';

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
    {
      path: '/dragndrop',
      name: 'dragndrop',
      component: Dragndrop,
    },
  ],
});
