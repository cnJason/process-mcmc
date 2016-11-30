import Vue from 'vue';
import ElementUI from 'element-ui';
import VueRouter from 'vue-router';
import Vuex from 'vuex';
import NProgress from 'nprogress'; // 页面顶部进度条

import 'element-ui/lib/theme-default/index.css';
import 'nprogress/nprogress.css';

import store from './vuex/store';
import App from './App';
import Login from './components/Login';
import Home from './components/Home';
import Main from './components/Main';
import Table from './components/nav1/Table';
import Form from './components/nav1/Form';
import Page3 from './components/nav1/Page3';
import Page4 from './components/nav2/Page4';
import Page5 from './components/nav2/Page5';
import Page6 from './components/nav3/Page6';

Vue.use(ElementUI);
Vue.use(VueRouter);
Vue.use(Vuex);

const routes = [
  { path: '/login', component: Login },
  {
    path: '/',
    component: Home,
    name: '导航一',
    children: [
      { path: '/main', component: Main },
      { path: '/table', component: Table, name: 'Table' },
      { path: '/form', component: Form, name: 'Form' },
      { path: '/page3', component: Page3, name: '页面3' },
    ],
  },
  {
    path: '/',
    component: Home,
    name: '导航二',
    children: [
      { path: '/page4', component: Page4, name: '页面4' },
      { path: '/page5', component: Page5, name: '页面5' },
    ],
  },
  {
    path: '/',
    component: Home,
    name: '导航三',
    children: [
      { path: '/page6', component: Page6, name: '' },
    ],
  },
];

const router = new VueRouter({
  routes,
});

router.beforeEach((to, from, next) => {
  NProgress.start();
  next();
});

router.afterEach(() => {
  NProgress.done();
});

new Vue({
  el: '#app',
  template: '<App/>',
  router,
  store,
  components: { App },
  // render: h => h(Login)
}).$mount('#app');

router.replace('/login');
